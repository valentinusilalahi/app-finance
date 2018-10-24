package com.silalahi.valentinus.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.silalahi.valentinus.finance.dao.VirtualAccountDao;
import com.silalahi.valentinus.finance.entity.Tagihan;
import com.silalahi.valentinus.finance.entity.VaStatus;
import com.silalahi.valentinus.finance.entity.VirtualAccount;

@Controller
@RequestMapping("/va")
public class VirtualAccountController {
	
	@Autowired
	private VirtualAccountDao virtualAccountDao;
	
	public ModelMap daftarVa(@RequestParam Tagihan tagihan, Pageable pageable) {
		return new ModelMap()
				.addAttribute("listVa",
						virtualAccountDao.findByTagihan(tagihan, pageable));
	}
	
	@PostMapping("/retry")
	public String retryCreateVa(@RequestParam(name="id") VirtualAccount virtualAccount) {
		if(virtualAccount == null) {
			return "redirect:/home";
		}
		virtualAccount.setVaStatus(VaStatus.CREATE);
		virtualAccountDao.save(virtualAccount);
		return "redirect:/va/list?tagihan="+virtualAccount.getTagihan().getId();
	}
	

}
