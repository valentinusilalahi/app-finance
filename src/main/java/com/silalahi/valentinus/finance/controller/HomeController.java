package com.silalahi.valentinus.finance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import com.silalahi.valentinus.finance.dao.TagihanDao;
import com.silalahi.valentinus.finance.dto.LaporanTagihan;
import com.silalahi.valentinus.finance.dto.RekapTagihan;
import com.silalahi.valentinus.finance.entity.StatusPembayaran;

@Controller
public class HomeController {

	private static final String TANGGAL_LIVE = "20190101";

	@Value("classpath:sample/tagihan.csv")
	private Resource contohFileTagihan;
	
	@Value("classpath:sample/debitur.csv")
	private Resource contohFileDebitur;
	
	@Autowired
	private TagihanDao tagihanDao;
	
	@GetMapping("/home")
	public ModelMap home() {
		
		List<RekapTagihan> rekap = tagihanDao.rekapTagihan(LocalDate.parse(TANGGAL_LIVE, DateTimeFormatter.BASIC_ISO_DATE), 
				LocalDate.now());
		Map<String, LaporanTagihan> daftarLaporanTagihan = new LinkedHashMap<>();
		for(RekapTagihan rt : rekap) {
			LaporanTagihan laporanTagihan = daftarLaporanTagihan.get(rt.getJenisTagihan().getId());
			if(laporanTagihan == null) {
				laporanTagihan = new LaporanTagihan();
				laporanTagihan.setJenisTagihan(rt.getJenisTagihan());
				daftarLaporanTagihan.put(rt.getJenisTagihan().getId(), laporanTagihan);
			}
			
			if(!StatusPembayaran.LUNAS.equals(rt.getStatusPembayaran())) {
				laporanTagihan.setJumlahTagihanBelumLunas(
						laporanTagihan.getJumlahTagihanBelumLunas() 
						+ rt.getJumlahTagihan());
			}
			
			if(StatusPembayaran.LUNAS.equals(rt.getStatusPembayaran())) {
				laporanTagihan.setJumlahTagihanLunas(
						laporanTagihan.getJumlahTagihanLunas()
						+ rt.getJumlahTagihan());
			}
			
			laporanTagihan.setNilaiTagihan(
					laporanTagihan.getNilaiTagihan()
						.add(rt.getNilaiTagihan()));
			
			laporanTagihan.setNilaiPembayaran(
					laporanTagihan.getNilaiPembayaran()
						.add(rt.getNilaiPembayaran()));
		}
		
		return new ModelMap()
				.addAttribute("daftarLaporanTagihan", new ArrayList<>(daftarLaporanTagihan.values()))
				.addAttribute("pageTitle", "dashboard");
		
	}
	
	@GetMapping("/contoh/tagihan")
	public void downloadContohFileTagihan(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=contoh-tagihan.csv");
		FileCopyUtils.copy(contohFileTagihan.getInputStream(), response.getOutputStream());
		response.getOutputStream().flush();
	}
	
	public void downloadContohFileDebitur(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=contoh-debitur.csv");
		FileCopyUtils.copy(contohFileDebitur.getInputStream(), response.getOutputStream());
		response.getOutputStream().flush();
	}

}
