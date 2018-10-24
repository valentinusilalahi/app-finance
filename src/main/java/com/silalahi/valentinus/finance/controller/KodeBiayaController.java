package com.silalahi.valentinus.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import com.silalahi.valentinus.finance.dao.KodeBiayaDao;
import com.silalahi.valentinus.finance.entity.KodeBiaya;

@Controller
@RequestMapping("/kode_biaya")
public class KodeBiayaController {

	@Autowired
	private KodeBiayaDao kodeBiayaDao;

	@GetMapping("/form")
	public ModelMap displayForm(@RequestParam(name = "id", required = false) KodeBiaya kodeBiaya) {
		if (kodeBiaya == null) {
			kodeBiaya = new KodeBiaya();
		}
		return new ModelMap("kodeBiaya", kodeBiaya);
	}

	@GetMapping("/list")
	public ModelMap daftarKodeBiaya(Pageable pageable) {
		return new ModelMap().addAttribute("daftarKodeBiaya", kodeBiayaDao.findAll());
	}

	public String processForm(@ModelAttribute KodeBiaya kodeBiaya, BindingResult errors, SessionStatus status) {
		if (errors.hasErrors()) {
			return "form";
		}
		kodeBiayaDao.save(kodeBiaya);
		status.setComplete();
		return "redirect:list";
	}

}
