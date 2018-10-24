package com.silalahi.valentinus.finance.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.silalahi.valentinus.finance.dao.DebiturDao;
import com.silalahi.valentinus.finance.dto.UploadError;
import com.silalahi.valentinus.finance.entity.Debitur;

@Transactional @Controller
public class DebiturController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DebiturController.class);
	
	@Autowired
	private DebiturDao debiturDao;
	
	@Autowired
	private Validator validator;
	
	@PreAuthorize("hasAuthority('VIEW_DEBITUR')")
	@GetMapping("/api/debitur")
	@ResponseBody
	public Page<Debitur> findAll(String search, Pageable pageable){
		return debiturDao.findByNomorDebiturOrNamaContainingIgnoreCase(search, search, pageable);
	}
	
	@PreAuthorize("hashAuthority('VIEW_DEBITUR')")
	@GetMapping("/debitur/list")
	public String daftarDebitur(ModelMap mm, @RequestParam(value="key", required=false) String key, 
			@PageableDefault(size=10) Pageable pageable) {
		Page<Debitur> result;
		if(key != null) {
			result = debiturDao.findByNomorDebiturOrNamaContainingIgnoreCase(key, key, pageable);
		}else {
			result=debiturDao.findAll(pageable);
		}
		
		mm.addAttribute("data", result);
		return "debitur/list";
	}
	
	@ModelAttribute("pageTitle")
	public String pageTitle() {
		return "Data Debitur";
	}
	
	@PreAuthorize("hasAuthority('EDIT_DEBITUR')")
	@GetMapping("/debitur/form")
	public ModelMap tampilkanForm(@RequestParam(value="id", required=false) String id) {
		Debitur debitur;
		if(id==null) {
			debitur= new Debitur();
		}else {
			debitur=debiturDao.findById(id).orElse(new Debitur());
		}
		return new ModelMap("debitur", debitur);
	}
	
	@PreAuthorize("hasAuthority('EDIT_DEBITUR')")
	@PostMapping("/debitur/form")
	public String processForm(@ModelAttribute @Valid Debitur debitur, BindingResult errors, SessionStatus status) {
		if(errors.hasErrors()) {
			return "debitur/form";
		}
		debiturDao.save(debitur);
		status.setComplete();
		return "redirect:list";
	}
	
	@PreAuthorize("hasAuthority('EDIT_DEBITUR')")
	@GetMapping("/debitur/delete{id}")
	public String deleteData(@RequestParam(value="id", required=false) String id) {
		debiturDao.deleteById(id);
		
		return "redirect:/debitur/list";
	}
	
	@GetMapping("/debitur/upload/form")
	public void displayFormUpload() {
		//display static html, tdk perlu kirim data
	}
	
	public String processFormUplad(@RequestParam(required=false) Boolean pakaiHeader,
			MultipartFile fileDebitur,
			RedirectAttributes redirectAttrs) {
		LOGGER.debug("Pakai Header : {}", pakaiHeader);
		LOGGER.debug("Nama File : {}", fileDebitur);
		LOGGER.debug("Ukuran File : {}", redirectAttrs);
		
		List<UploadError> errors = new ArrayList<>();
		Integer baris=0;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileDebitur.getInputStream()));
			String content;
			
			if(adaHeader(pakaiHeader)) {
				content = reader.readLine(); //NOSONAR
			}
			
			while((content = reader.readLine())!=null) {
				baris++;
				String[] data = content.split(",", -1);
				if(rowInvalid(data, errors, baris, content)) continue;
				
				Debitur d = new Debitur();
				d.setNomorDebitur(data[0]);
				d.setNama(data[1]);
				d.setEmail(data[2]);
				
				if(StringUtils.hasText(data[3])) {
					d.setNoHp(data[3]);
				}
				debiturDao.save(d);
			}
		} catch (IOException err) {
			LOGGER.warn(err.getMessage(), err);
			errors.add(new UploadError(0, "Format file Salah", err.getMessage()));
		}
		redirectAttrs
			.addFlashAttribute("jumlahBaris", baris)
			.addFlashAttribute("jumlahSukses", baris - errors.size())
			.addFlashAttribute("jumlahError", errors.size())
			.addFlashAttribute("errors", errors);
		
		return "redirect:hasil";
	}

	private boolean rowInvalid(String[] data, List<UploadError> errors, Integer baris, String content) {
		// TODO Auto-generated method stub
		if(data.length!=4) {
			errors.add(new UploadError(baris, "Format data salah", content));
			return true;
		}
		if(!StringUtils.hasText(data[0])) {
			errors.add(new UploadError(baris, "Nama debitur harus di isi", content));
			return true;
		}
		if(!StringUtils.hasText(data[1])) {
			errors.add(new UploadError(baris, "Nomor debitur "+data[0]+" sudah digunakan", content));
			return true;
		}
		
		if(StringUtils.hasText(data[2])) {
			Debitur d = new Debitur();
			d.setEmail(data[2]);
			BeanPropertyBindingResult binder = new BeanPropertyBindingResult(d, "debitur");
			validator.validate(d, binder);
			
			if(binder.hasFieldErrors("email")) {
				errors.add(new UploadError(baris, "Format email salah", content));
				return true;
			}
		}
		return false;
	}

	private boolean adaHeader(Boolean pakaiHeader) {
		// TODO Auto-generated method stub
		return pakaiHeader != null && pakaiHeader;
	}
	
	@GetMapping("/debitur/upload/hasil")
    public void hasilFormUpload() {
        // tidak perlu kirim data ke view
    }

}
