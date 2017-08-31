package com.taotao.service;

import com.taotao.pojo.EUDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItemParam;

public interface ItemParamService {

	TaotaoResult getItemParamByCid(Long cid);
	TaotaoResult insertItemParam(TbItemParam itemParam);
	EUDataGridResult getItemList(Integer page, Integer rows);
}
