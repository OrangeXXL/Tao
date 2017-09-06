package com.taotao.service;

import java.util.List;

import com.taotao.pojo.EUTreeNode;
import com.taotao.pojo.TaotaoResult;

public interface ContentCategoryService {

	List<EUTreeNode> getCategoryList(Long parentId);
	TaotaoResult insertContentCategory(Long parentId, String name);
	TaotaoResult deleteContentCategory(Long parentId, Long id);
	TaotaoResult updateContentCateName(Long id, String name);
}
