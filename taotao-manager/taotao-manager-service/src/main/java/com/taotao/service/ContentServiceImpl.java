package com.taotao.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.EUDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.utils.HttpClientUtil;

/**
 * 管理内容Service
 * 
 * @author a07
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${REST_CONTENT_SYNC_URL}")
	private String REST_CONTENT_SYNC_URL;
	
	@Override
	public EUDataGridResult getContentList(Long categoryId, int page, int rows) {
		
		PageHelper.startPage(page, rows);
		//创建查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		//添加查询条件
		criteria.andCategoryIdEqualTo(categoryId);
		//以categoryId为条件查询,返回list
		List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
		//获取记录总条数
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		
		//返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public TaotaoResult insertContent(TbContent content) {
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insert(content);
		
		try {
			//添加缓存同步逻辑
			HttpClientUtil.doGet(REST_BASE_URL + REST_CONTENT_SYNC_URL + content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return TaotaoResult.ok();
	}

}
