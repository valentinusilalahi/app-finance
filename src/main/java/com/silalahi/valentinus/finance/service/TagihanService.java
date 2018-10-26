package com.silalahi.valentinus.finance.service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.silalahi.valentinus.finance.dao.PeriksaStatusTagihanDao;
import com.silalahi.valentinus.finance.dao.TagihanDao;
import com.silalahi.valentinus.finance.dao.VirtualAccountDao;
import com.silalahi.valentinus.finance.dto.TagihanResponse;
import com.silalahi.valentinus.finance.entity.Bank;
import com.silalahi.valentinus.finance.entity.PeriksaStatusTagihan;
import com.silalahi.valentinus.finance.entity.StatusPemeriksaanTagihan;
import com.silalahi.valentinus.finance.entity.StatusTagihan;
import com.silalahi.valentinus.finance.entity.Tagihan;
import com.silalahi.valentinus.finance.entity.VaStatus;
import com.silalahi.valentinus.finance.entity.VirtualAccount;

@Service @Transactional
public class TagihanService {
	
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final String TIMEZONE = "GMT+07:00";
	
	@Autowired private RunningNumberService runningNumberService;
	@Autowired private TagihanDao tagihanDao;
	@Autowired private VirtualAccountDao virtualAccountDao;
	@Autowired private PeriksaStatusTagihanDao periksaStatusTagihanDao;
	@Autowired private KafkaSenderService kafkaSenderService;
	
	public void saveTagihan(Tagihan t) {
		t.setNilaiTagihan(t.getNilaiTagihan().setScale(0, RoundingMode.DOWN));

		// Tagihan Baru
		if (t.getId() == null) {
			String datePrefix = DATE_FORMAT.format(LocalDateTime.now(ZoneId.of(TIMEZONE)));
			Long runningNumber = runningNumberService.getNumber(datePrefix);
			String nomorTagihan = datePrefix + t.getJenisTagihan().getKode() + String.format("%06d", runningNumber);
			t.setNomor(nomorTagihan);
			tagihanDao.save(t);
			for (Bank b : t.getJenisTagihan().getDaftarBank()) {
				VirtualAccount va = new VirtualAccount();
				va.setBank(b);
				va.setTagihan(t);
				virtualAccountDao.save(va);
			}
		} else {
			for (VirtualAccount va : virtualAccountDao.findByTagihan(t)) {
				va.setVaStatus(StatusTagihan.AKTIF.equals(t.getStatusTagihan()) ? VaStatus.UPDATE : VaStatus.DELETE);
				virtualAccountDao.save(va);
			}
			tagihanDao.save(t);
		}
		TagihanResponse response = new TagihanResponse();
		response.setDebitur(t.getDebitur().getNomorDebitur());
		response.setJenisTagihan(t.getJenisTagihan().getId());
		response.setKodeBiaya(t.getKodeBiaya().getId());
		response.setKeterangan(t.getKeterangan());
		response.setNilaiTagihan(t.getNilaiTagihan());
		response.setSukses(true);
		response.setNomorTagihan(t.getNomor());
		response.setTanggalTagihan(t.getTanggalTagihan());
		response.setTanggalJatuhTempo(t.getTanggalJatuhTempo());
		kafkaSenderService.sendTagihanResponse(response);
	}

	public void periksaStatus(Tagihan tagihan) {

		for (VirtualAccount va : virtualAccountDao.findByTagihan(tagihan)) {
			PeriksaStatusTagihan p = new PeriksaStatusTagihan();
			p.setVirtualAccount(va);
			p.setWaktuPeriksa(LocalDateTime.now());
			p.setStatusPemeriksaanTagihan(StatusPemeriksaanTagihan.BARU);
			periksaStatusTagihanDao.save(p);

			va.setVaStatus(VaStatus.INQUIRY);
			virtualAccountDao.save(va);
		}
	}

}
