package com.silalahi.valentinus.finance.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.silalahi.valentinus.finance.dao.KodeBiayaDao;
import com.silalahi.valentinus.finance.dao.PeriksaStatusTagihanDao;
import com.silalahi.valentinus.finance.dao.TagihanDao;

@Controller
@RequestMapping("/tagihan")
public class TagihanController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TagihanController.class);
	
	private static final String ATTR_JENIS_TAGIHAN = "jenisTagihan";
	private static final String ATTR_LIST_TAGIHAN = "listTagihan";
	private static final String ATTR_TAGIHAN = "tagihan";
	private static final String REDIRECT_LIST_VIEW = "redirect:list";
	
	@Autowired
	private KodeBiayaDao kodeBiayaDao;
	
	@Autowired
	private TagihanDao tagihanDao;
	
	@Autowired
	private PeriksaStatusTagihanDao periksaStatusTagihanDao;

}
