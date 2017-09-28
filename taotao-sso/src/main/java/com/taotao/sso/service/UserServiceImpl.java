package com.taotao.sso.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.dao.JedisClient;
import com.taotao.utils.CookieUtils;
import com.taotao.utils.JsonUtils;

/**
 * 用户管理service
 * 
 * @author a07
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private int SSO_SESSION_EXPIRE;

	/**
	 * 用户校验
	 */
	@Override
	public TaotaoResult checkData(String content, Integer type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		if (1 == type) {
			criteria.andUsernameEqualTo(content);
		} else if (2 == type) {
			criteria.andPhoneEqualTo(content);
		} else {
			criteria.andEmailEqualTo(content);
		}
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return TaotaoResult.ok(true);
		}
		return TaotaoResult.ok(false);
	}

	/**
	 * 用户注册
	 */
	@Override
	public TaotaoResult createUser(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(new Date());
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		userMapper.insert(user);
		return TaotaoResult.ok();
	}

	/**
	 * 用户登录
	 */
	@Override
	public TaotaoResult userLogin(String username, String password, HttpServletRequest req, HttpServletResponse resp) {

		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		criteria.andPasswordEqualTo(DigestUtils.md5DigestAsHex(password.getBytes()));
		List<TbUser> list = userMapper.selectByExample(example);
		
		if (null == list || list.size() == 0) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		user.setPassword(null);
		//生成token
		String token = UUID.randomUUID().toString();
		jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token, JsonUtils.objectToJson(user));
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
		//写入cookie
		CookieUtils.setCookie(req, resp, "TT_TOKEN", token);
		return TaotaoResult.ok(token);
	}

	/**
	 * 根据token查询用户信息
	 */
	@Override
	public TaotaoResult getUserByToken(String token) {
		String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
		if(StringUtils.isBlank(json)){
			return TaotaoResult.build(400, "过期，请重新登录");
		}
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);
		return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
	}

	/**
	 * 用户退出
	 */
	@Override
	public TaotaoResult userLogout(String token) {
		jedisClient.del(REDIS_USER_SESSION_KEY + ":" + token);
		return TaotaoResult.ok("退出成功");
	}
}
