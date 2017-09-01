package com.taotao.rest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.CatResult;
/**
 * 商品分类列表
 * @author a07
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public CatResult getItemCatList() {
		CatResult catResult = new CatResult();
		catResult.setData(getCatList(0));
		return catResult;
	}
	
	private List<?> getCatList(long parentId){
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		List resultList = new ArrayList<>();
		//向list中添加节点
		for(TbItemCat itemCat : list){
			if(itemCat.getIsParent()){
				CatNode catNode = new CatNode();
				if(parentId == 0){
					catNode.setName("<a href='/products/"+itemCat.getId()+".html'>"+itemCat.getName()+"</a>");
				}else{
					catNode.setName(itemCat.getName());
				}
				catNode.setUrl("/products/"+itemCat.getId()+".html");
				catNode.setItem(getCatList(itemCat.getId()));
				resultList.add(catNode);
			}else{
				resultList.add("/products/"+itemCat.getId()+".html|" + itemCat.getName());
			}
			
		}
		return resultList;
	}

}
