package com.taotao.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.CachingResourceTransformer;

import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.EUTreeNode;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
/**
 * 内容分类管理service
 * @author a07
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EUTreeNode> getCategoryList(Long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		List<EUTreeNode> resultList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EUTreeNode node = new EUTreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public TaotaoResult insertContentCategory(Long parentId, String name) {
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//1正常，2删除
		contentCategory.setStatus(1);
		contentCategory.setSortOrder(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		contentCategory.setIsParent(false);
		//添加记录
		contentCategoryMapper.insert(contentCategory);
		//查看父节点parentId是否为true
		TbContentCategory parentCate = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parentCate.getIsParent()){
			parentCate.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parentCate);
		}
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult deleteContentCategory(Long parentId, Long id) {
		//删除节点
		contentCategoryMapper.deleteByPrimaryKey(id);	
		//创建父节点查询条件
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//查询父节点下的子节点
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		//如果为空，将父节点的isparent设置为false
		if(list.size() == 0){
			TbContentCategory parentCate = contentCategoryMapper.selectByPrimaryKey(parentId);
			parentCate.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parentCate);
		}
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult updateContentCateName(Long id, String name) {
		TbContentCategory category = contentCategoryMapper.selectByPrimaryKey(id);
		category.setName(name);
		contentCategoryMapper.updateByPrimaryKey(category);
		return TaotaoResult.ok();
	}

}
