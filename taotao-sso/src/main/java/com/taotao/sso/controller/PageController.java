package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面跳转Controller
 * @author a07
 *
 */
@Controller
@RequestMapping("/page")
public class PageController {

	/**
	 * 注册页面
	 * @return
	 */
	@RequestMapping("/register")
	public String showRegister(){
		return "register";
	}
	
	/**
	 * 登录页面
	 * @return
	 */
	@RequestMapping("/login")
	public String showLogin(String redirect, Model model){
		model.addAttribute("redirect", redirect);
		return "login";
	}
}
