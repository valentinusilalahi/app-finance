package com.silalahi.valentinus.finance.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.silalahi.valentinus.finance.dao.BankDao;
import com.silalahi.valentinus.finance.entity.Bank;

@Transactional
@Controller
public class BankController {

	@Autowired
	private BankDao bankDao;

	@GetMapping("/api/client/bank")
	@ResponseBody
	public Page<Bank> findAll(Pageable pageable) {
		return bankDao.findAll(pageable);
	}
	
	@GetMapping("/bank/list")
	public ModelMap listBank() {
		return new ModelMap()
				.addAttribute("daftarBank", bankDao.findAll(new Sort(Sort.Direction.ASC, "kode")))
				.addAttribute("pageTitle", "Daftar Bank");
	}

}
