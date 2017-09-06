package com.taotao.portal.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.portal.service.ContentService;

/**
 * 首页跳转
 * @author a07
 *
 */
@Controller
public class IndexController {

	@Autowired
	private ContentService contentService;
	@RequestMapping("/index")
	public String showIndex(Model model){
		String advJson = contentService.getContentList();
		model.addAttribute("ad1", advJson);
		return "index";
	}
}
