package com.taotao.service;

import com.taotao.pojo.EUDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {

	EUDataGridResult getContentList(Long categoryId, int page, int rows);
	TaotaoResult insertContent(TbContent content);
}
