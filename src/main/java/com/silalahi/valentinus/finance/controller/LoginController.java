package com.silalahi.valentinus.finance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping(value = "/login")
	public String login(Model model) {
		model.addAttribute("loginError", true);
		return "login";
	}

}
