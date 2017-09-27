package com.taotao.portal.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.pojo.TbUser;
import com.taotao.portal.service.UserServiceImpl;
import com.taotao.utils.CookieUtils;

public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private UserServiceImpl userService;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// 返回modelandview之后处理，响应用户之后
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// handler执行之后，返回modelandview之前处理

	}

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object arg2) throws Exception {
		// handler执行之前处
		// 判断用户是否登录
		// 从cookie中取token
		String token = CookieUtils.getCookieValue(req, "TT_TOKEN");
		// 根据token换取用户信息(调用sso接口)
		TbUser user = userService.getUserByToken(token);
		// 取不到用户信息
		if (null == user) {
			// 跳转到登录页面，并将当前url作为参数传递并返回false
			resp.sendRedirect(userService.SSO_BASE_URL + userService.SSO_PAGE_URL + "?redirect=" + req.getRequestURL());
			return false;
		}
		// 取到用户信息，放行
		// 返回值决定handler是否执行。true执行，false不执行
		return true;
	}

}
