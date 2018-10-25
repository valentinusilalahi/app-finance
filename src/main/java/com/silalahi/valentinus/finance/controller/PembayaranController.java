package com.silalahi.valentinus.finance.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.silalahi.valentinus.finance.dao.JenisTagihanDao;
import com.silalahi.valentinus.finance.dao.PembayaranDao;
import com.silalahi.valentinus.finance.dto.RekapPembayaran;
import com.silalahi.valentinus.finance.entity.Debitur;
import com.silalahi.valentinus.finance.entity.JenisTagihan;
import com.silalahi.valentinus.finance.entity.Pembayaran;
import com.silalahi.valentinus.finance.entity.Tagihan;

@Controller
@RequestMapping("/pembayaran")
public class PembayaranController {
	
	@Autowired
	private PembayaranDao pembayaranDao;
	
	@Autowired
	private JenisTagihanDao jenisTagihanDao;
	
	@ModelAttribute("listjenistagihan")
	public Iterable<JenisTagihan> daftarJenisTagihan(){
		return jenisTagihanDao.findAll(new Sort(Sort.Direction.ASC, "kode"));
	}
	
	@PreAuthorize("hasAuthority('EDIT_PEMBAYARAN')")
	@GetMapping("/form")
	public ModelMap displayForm(@RequestParam(value="id", required=false)String id) {
		return new ModelMap()
				.addAttribute("pembayaran",StringUtils.hasText(id) ? 
						pembayaranDao.findById(id).orElse(new Pembayaran()) : new Pembayaran());
	}
	
	@PreAuthorize("hasAuthority('VIEW_PEMBAYARAN')")
	@GetMapping("/list")
	public ModelMap findAllHtml(@RequestParam(required=false) JenisTagihan jenisTagihan,
			@RequestParam(required=false) Tagihan tagihan,
			@RequestParam(required=false) Debitur debitur,
			@RequestParam(required=false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate mulai,
			@RequestParam(required=false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate sampai,
			@PageableDefault(sort="waktuTransaksi", direction=Sort.Direction.DESC) Pageable pageable) {
		
		if(tagihan != null) {
			return new ModelMap()
					.addAttribute("data", pembayaranDao.findByTagihanOrderByWaktuTransaksi(tagihan, pageable));
		}
		if(jenisTagihan != null) {
			if(mulai != null && sampai != null) {
				Page<Pembayaran> hasil = pembayaranDao
						.findByJenisTagihanAndWaktuTransaksi(jenisTagihan, mulai, sampai, pageable);
				return new ModelMap()
						.addAttribute("jenisTagihan", jenisTagihan)
						.addAttribute("data", hasil);
			}
			return new ModelMap()
					.addAttribute("jenisTagihan", jenisTagihan)
					.addAttribute("data", pembayaranDao.findByTagihanJenisTagihan(jenisTagihan, pageable));
		}
		if(mulai!= null && sampai != null) {
			return new ModelMap()
					.addAttribute("data", pembayaranDao
							.findByWaktuTransaksi(mulai, sampai, pageable));
		}
		if(debitur!=null) {
			return new ModelMap()
					.addAttribute("debitur", debitur)
					.addAttribute("data", pembayaranDao.findByTagihanDebiturOrderByWaktuTransaksi(debitur, pageable));
		}
		
		return new ModelMap()
				.addAttribute("data", pembayaranDao.findAll(pageable));
		
	}
	
	@ModelAttribute("pageTitle")
	public String pageTitle() {
		return "Data Pembayaran";
	}
	
	@ModelAttribute("awalBulan")
	public String awalBulan() {
		return LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
	
	@ModelAttribute("akhirBulan")
	public String akhirBulan() {
		return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
	
	@GetMapping("/rekap")
	@ResponseBody
	public List<RekapPembayaran> rekapPembayaranBulanan(){
		LocalDate sekarang = LocalDate.now();
		LocalDate mulai = sekarang.minusMonths(1);
		LocalDate sampai = sekarang;
		
		Map<String, RekapPembayaran> hasil = new LinkedHashMap<>();
		for(LocalDate date = sekarang.minusMonths(1);date.isBefore(sekarang); date= date.plusDays(1)) {
			RekapPembayaran rekap = new RekapPembayaran(java.sql.Date.valueOf(date), BigDecimal.ZERO, 0L);
			hasil.put(date.format(DateTimeFormatter.BASIC_ISO_DATE), rekap);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		for(RekapPembayaran rp : pembayaranDao.rekapPembayaran(mulai.atStartOfDay(), sampai.plusDays(1).atStartOfDay())) {
			rp.setJumlah(rp.getJumlah());
			hasil.put(formatter.format(rp.getTanggal()), rp);
		}
		return new ArrayList<>(hasil.values());
	}
	
	@GetMapping("/csv")
	public void rekapPembayaranCsv(@RequestParam JenisTagihan jenisTagihan,
			@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate mulai,
			@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate sampai,
			HttpServletResponse response) throws IOException {
		String fileName = "pembayaran-"
				+ mulai.format(DateTimeFormatter.BASIC_ISO_DATE) 
				+ "-"
				+ sampai.format(DateTimeFormatter.BASIC_ISO_DATE)
				+ ".csv";
		
		response.setHeader("Content-Disposition", "attachment;filename="+fileName);
		response.setContentType("text/csv");
		response.getWriter().println("No,NIM,Nama,Bank,Nominal,Tanggal Transfer,Waktu Transfer, Referensi");
		
		Iterable<Pembayaran>  dataPembayaran = pembayaranDao
				.findByTagihanJenisTagihanAndWaktuTransaksiBetweenOrderByWaktuTransaksi(jenisTagihan, 
						mulai.atStartOfDay(), sampai.plusDays(1).atStartOfDay());
		
		Integer baris=0;
		BigDecimal total = BigDecimal.ZERO;
		for(Pembayaran p : dataPembayaran) {
			baris++;
			total = total.add(p.getJumlah());
			response.getWriter().print(baris);
			response.getWriter().print(",");
			response.getWriter().print(p.getTagihan().getDebitur().getNomorDebitur());
			response.getWriter().print(",");
			response.getWriter().print(p.getTagihan().getDebitur().getNama());
			response.getWriter().print(",");
			response.getWriter().print(p.getBank().getNama());
			response.getWriter().print(",");
			response.getWriter().print(p.getJumlah().setScale(0).toPlainString());
			response.getWriter().print(",");
			response.getWriter().print(p.getWaktuTransaksi().format(DateTimeFormatter.ISO_LOCAL_DATE));
			response.getWriter().print(",");
			response.getWriter().print(p.getWaktuTransaksi().format(DateTimeFormatter.ISO_LOCAL_TIME));
			response.getWriter().print(",");
			response.getWriter().print(p.getReferensi());
			response.getWriter().println();
		}
		
		response.getWriter().print("-");
		response.getWriter().print(",");
		response.getWriter().print("-");
		response.getWriter().print(",");
		response.getWriter().print("Jumlah");
		response.getWriter().print(",");
		response.getWriter().print("-");
		response.getWriter().print(",");
		response.getWriter().print(total.setScale(0).toPlainString());
		response.getWriter().print(",");
		response.getWriter().print("-");
		response.getWriter().print(",");
		response.getWriter().print("-");
		response.getWriter().println();
		
		response.getWriter().flush();
	}

}
