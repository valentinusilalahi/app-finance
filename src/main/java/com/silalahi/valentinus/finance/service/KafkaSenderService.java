package com.silalahi.valentinus.finance.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.silalahi.valentinus.finance.dao.TagihanDao;
import com.silalahi.valentinus.finance.dao.VirtualAccountDao;
import com.silalahi.valentinus.finance.dto.NotifikasiPembayaranRequest;
import com.silalahi.valentinus.finance.dto.NotifikasiTagihanRequest;
import com.silalahi.valentinus.finance.dto.PembayaranTagihan;
import com.silalahi.valentinus.finance.dto.TagihanResponse;
import com.silalahi.valentinus.finance.dto.VaRequest;
import com.silalahi.valentinus.finance.entity.Pembayaran;
import com.silalahi.valentinus.finance.entity.StatusNotifikasi;
import com.silalahi.valentinus.finance.entity.Tagihan;
import com.silalahi.valentinus.finance.entity.VaStatus;
import com.silalahi.valentinus.finance.entity.VirtualAccount;
import com.silalahi.valentinus.finance.helper.VirtualAccountNumberGenerator;

@Service @Transactional
public class KafkaSenderService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSenderService.class);
	private static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	private static final Integer NOTIFICATION_BATCH_SIZE = 50;
	
	@Value("${kafka.topic.notification.request}") private String kafkaTopicNotificationRequest;
	@Value("${kafka.topic.va.request}") private String kafkaTopicVaRequest;
	@Value("${kafka.topic.debitur.response}") private String kafkaTopicDebiturResponse;
	@Value("${kafka.topic.tagihan.response}") private String kafkaTopicTagihanResponse;
	@Value("${kafka.topic.tagihan.payment}") private String kafkaTopicPembayaranTagihan;
	
	@Value("${notifikasi.delay.menit}") private Integer delayNotifikasi;
	
	@Value("${notifikasi.konfigurasi.tagihan}") private String konfigurasiTagihan;
	@Value("${notifikasi.konfigurasi.pembayaran}") private String konfigurasiPembayaran;
	@Value("${notifikasi.contactinfo}") private String contactinfo;
	@Value("${notifikasi.contactinfoFull}") private String contactinfoFull;
	@Value("${notifikasi.email.finance}") private String financeEmail;
	@Value("${notifikasi.email.finance.send}") private Boolean sendFinanceEmail;
	@Value("${notifikasi.email.it}") private String itEmail;
	@Value("${notifikasi.email.it.send}") private Boolean sendItEmail;
	@Value("${notifikasi.email.marketing}") private String marketingEmail;
	@Value("${notifikasi.email.marketing.send}") private Boolean sendMarketingEmail;
	
	@Value("#{'${jenis.biaya.marketing}'.split(',')}") 
	private List<String> jenisBiayaMarketing;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	private VirtualAccountDao virtualAccountDao;
	
	@Autowired
	private TagihanDao tagihanDao;
	
	@Scheduled(fixedDelay = 1000)
	public void processVaBaru() {
		processVa(VaStatus.CREATE);
	}
	
	@Scheduled(fixedDelay = 1000)
	public void processVaUpdate() {
		processVa(VaStatus.UPDATE);
	}
	
	@Scheduled(fixedDelay = 1000)
	public void processVaDelete() {
		processVa(VaStatus.DELETE);
	}
	
	@Scheduled(fixedDelay = 60 * 1000)
	public void processNotifikasiTagihan() {
		tagihanDao.findByStatusNotifikasi(StatusNotifikasi.BELUM_TERKIRIM, 
				PageRequest.of(0, NOTIFICATION_BATCH_SIZE))
				.getContent().stream().filter(tagihan -> punyaVaAktif(tagihan))
				.forEachOrdered(tagihan -> sendNotifikasiTagihan(tagihan));

	}

	private boolean punyaVaAktif(Tagihan tagihan) {
		// TODO Auto-generated method stub
		if (LocalDateTime.now().isBefore(tagihan.getUpdateAt().plusMinutes(delayNotifikasi))) {
			return false;
		}

		Iterator<VirtualAccount> daftarVa = virtualAccountDao.findByTagihan(tagihan).iterator();
		while (daftarVa.hasNext()) {
			VirtualAccount va = daftarVa.next();
			if (VaStatus.AKTIF.equals(va.getVaStatus())) {
				return true;
			}
		}
		return false;
	}
	
	public void sendNotifikasiTagihan(Tagihan tagihan) {
		try {
			StringBuilder rekening = new StringBuilder("");
			StringBuilder rekeningFull = new StringBuilder("<ul>");
			
			for(VirtualAccount va : virtualAccountDao.findByTagihan(tagihan)) {
				if(!VaStatus.AKTIF.equals(va.getVaStatus())) {
					continue;
				}
				if(rekening.length()>0) {
					rekening.append("/");
				}
				rekening.append(va.getBank().getNama()+" "+va.getNomor());
				rekeningFull.append("<li>"+va.getBank().getNama()+" "+va.getNomor()+"</li>");
			}
			rekeningFull.append("</ul>");
			
			String email = tagihan.getDebitur().getEmail();
			sendNotifikasiTagihan(email,tagihan,
					rekening.toString(),rekeningFull.toString());
			
			LOGGER.debug("Send Marketing Email : {}", sendMarketingEmail);
			LOGGER.debug("Marketing Email : {}", marketingEmail);
			LOGGER.debug("Jenis Biaya Marketing : {}", jenisBiayaMarketing.size());
			LOGGER.debug("Jenis Biaya Tagihan : {}", tagihan.getJenisTagihan().getId());
			LOGGER.debug("Jenis Biaya : {}", jenisBiayaMarketing.contains(tagihan.getJenisTagihan().getId()));
			
			if(sendMarketingEmail 
					&& jenisBiayaMarketing.contains(tagihan.getJenisTagihan().getId())) {
				LOGGER.debug("Kirim email tagihan ke marketing");
				sendNotifikasiTagihan(marketingEmail, tagihan, 
						rekening.toString(), rekeningFull.toString());
			}
			
			tagihan.setStatusNotifikasi(StatusNotifikasi.SUDAH_TERKIRIM);
			tagihanDao.save(tagihan);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn(e.getMessage(), e);
		}
	}
	
	private void sendNotifikasiTagihan(String email, Tagihan tagihan, String rekeningText, String rekeningHtml)
			throws JsonProcessingException {
		String hp = tagihan.getDebitur().getNoHp();

		Map<String, Object> notifikasi = new LinkedHashMap<>();
		notifikasi.put("email", email);
		notifikasi.put("mobile", hp);
		notifikasi.put("konfigurasi", konfigurasiTagihan);

		NotifikasiTagihanRequest requestData = NotifikasiTagihanRequest.builder()
				.jumlah(tagihan.getNilaiTagihan())
				.keterangan(tagihan.getJenisTagihan().getNama())
				.nama(tagihan.getDebitur().getNama())
				.email(tagihan.getDebitur().getEmail())
				.noHp(tagihan.getDebitur().getNoHp())
				.nomorTagihan(tagihan.getNomor())
				.rekening(rekeningText).rekeningFull(rekeningHtml)
				.tanggalTagihan(tagihan.getTanggalTagihan().format(DateTimeFormatter.ISO_LOCAL_DATE))
				.contactinfo(contactinfo)
				.contactinfoFull(contactinfoFull)
				.build();

		notifikasi.put("data", requestData);
		kafkaTemplate.send(kafkaTopicNotificationRequest, objectMapper.writeValueAsString(notifikasi));
	}
	
	private void sendNotifikasiPembayaran(Pembayaran pembayaran) {
		sendPembayaranTagihan(pembayaran);
		
		try {
			sendNotifikasiPembayaran(pembayaran.getTagihan().getDebitur().getEmail(),
					pembayaran.getTagihan().getDebitur().getNoHp(), pembayaran);
			
			if(sendFinanceEmail) {
				sendNotifikasiPembayaran(financeEmail, null, pembayaran);
			}
			
			if(sendItEmail) {
				sendNotifikasiPembayaran(itEmail, null, pembayaran);
			}
			
			if(sendMarketingEmail && jenisBiayaMarketing.contains(pembayaran.getTagihan().getJenisTagihan().getId())) {
				LOGGER.debug("kirim email tagihan ke marketing");
				sendNotifikasiPembayaran(marketingEmail, null, pembayaran);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn(e.getMessage(), e);
		}
		
	}
	
	private void sendNotifikasiPembayaran(String email, String mobile, Pembayaran pembayaran) throws JsonProcessingException {
		NotifikasiPembayaranRequest request = NotifikasiPembayaranRequest.builder()
				.contactinfo(contactinfo)
				.contactinfoFull(contactinfoFull)
				.keterangan(pembayaran.getTagihan().getJenisTagihan().getNama())
				.nomorTagihan(pembayaran.getTagihan().getNomor())
				.nama(pembayaran.getTagihan().getDebitur().getNama())
				.noHp(pembayaran.getTagihan().getDebitur().getNoHp())
				.tanggalTagihan(pembayaran.getTagihan().getTanggalTagihan().format(DateTimeFormatter.ISO_LOCAL_DATE))
				.nilaiPembayaran(pembayaran.getJumlah())
				.nilaiTagihan(pembayaran.getTagihan().getNilaiTagihan())
				.rekening(pembayaran.getBank().getNama())
				.waktu(pembayaran.getWaktuTransaksi().format(DateTimeFormatter.ofPattern(FORMAT_DATETIME)))
				.referensi(pembayaran.getReferensi())
				.build();
		Map<String, Object> notifikasi = new LinkedHashMap<>();
		notifikasi.put("email", email);
		if(mobile != null) {
			notifikasi.put("mobile", mobile);
		}
		notifikasi.put("konfigurasi", konfigurasiPembayaran);
		notifikasi.put("data", request);
		kafkaTemplate.send(kafkaTopicNotificationRequest, objectMapper.writeValueAsString(notifikasi));
	}
	
	private void sendTagihanResponse(TagihanResponse tagihanResponse) {
		try {
			String message = objectMapper.writeValueAsString(tagihanResponse);
			LOGGER.debug("Kirim tagihan response : {}", message);
			kafkaTemplate.send(kafkaTopicTagihanResponse, message);
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn(e.getMessage(), e);
		}
	}
	
	private void sendDebiturResponse(Map<String, Object> data) {
		try {
			kafkaTemplate.send(kafkaTopicDebiturResponse, objectMapper.writeValueAsString(data));
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn(e.getMessage(), e);
		}
	}

	private void sendPembayaranTagihan(Pembayaran p) {
		PembayaranTagihan pt = PembayaranTagihan.builder()
				.bank(p.getBank().getId())
				.jenisTagihan(p.getTagihan().getJenisTagihan().getId())
				.kodeBiaya(p.getTagihan().getJenisTagihan().getId())
				.nomorTagihan(p.getTagihan().getNomor())
				.nomorDebitur(p.getTagihan().getDebitur().getNomorDebitur())
				.namaDebitur(p.getTagihan().getDebitur().getNama())
				.keteranganTagihan(p.getTagihan().getKeterangan())
				.statusTagihan(p.getTagihan().getStatusTagihan().toString())
				.nilaiTagihan(p.getTagihan().getNilaiTagihan())
				.nilaiPembayaran(p.getJumlah())
				.nilaiAkumulasiPembayaran(p.getTagihan().getJumlahPembayaran())
				.refereniPembayaran(p.getReferensi())
				.waktuPembayaran(p.getWaktuTransaksi().format(DateTimeFormatter.ofPattern(FORMAT_DATETIME)))
				.tanggalTagihan(p.getTagihan().getTanggalJatuhTempo().format(DateTimeFormatter.ISO_LOCAL_DATE))
				.build();
		
		try {
			kafkaTemplate.send(kafkaTopicPembayaranTagihan, objectMapper.writeValueAsString(pt));
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn(e.getMessage(), e);
		}
	}

	private void processVa(VaStatus status) {
		// TODO Auto-generated method stub
		Iterator<VirtualAccount> daftarVa = virtualAccountDao.findByVaStatus(status).iterator();
		try {
			if (daftarVa.hasNext()) {
				VirtualAccount va = daftarVa.next();
				if (!VaStatus.CREATE.equals(status) && !StringUtils.hasText(va.getNomor())) {
					LOGGER.warn("VA Request {} untuk no tagihan {} tidak ada nomor VA-nya", status,
							va.getTagihan().getNomor());
					return;
				}
				VaRequest vaRequest = createRequest(va, status);
				String json = objectMapper.writeValueAsString(vaRequest);
				LOGGER.debug("VA request : {}", json);
				kafkaTemplate.send(kafkaTopicVaRequest, json);
				va.setVaStatus(VaStatus.SEDANG_PROSES);
				virtualAccountDao.save(va);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.warn(e.getMessage(), e);
		}

	}

	private VaRequest createRequest(VirtualAccount va, VaStatus requestType) {
		// TODO Auto-generated method stub
		VaRequest vaRequest = VaRequest.builder()
				.accountType(va.getTagihan().getJenisTagihan().getTipePembayaran())
				.requestType(requestType)
				.amount(va.getTagihan().getNilaiTagihan().subtract(va.getTagihan().getJumlahPembayaran()))
				.description(va.getTagihan().getKeterangan()).email(va.getTagihan().getDebitur().getEmail())
				.phone(va.getTagihan().getDebitur().getNoHp())
				.expireDate(va.getTagihan().getTanggalJatuhTempo().format(DateTimeFormatter.ISO_LOCAL_DATE))
				.expireDate(va.getTagihan().getTanggalJatuhTempo().format(DateTimeFormatter.ISO_LOCAL_DATE))
				.invoiceNumber(va.getTagihan().getNomor()).nama(va.getTagihan().getDebitur().getNama())
				.bankId(va.getBank().getId()).build();

		if (VaStatus.CREATE.equals(requestType)) {
			vaRequest.setAccountNumber(VirtualAccountNumberGenerator.generateVirtualAccountNumber(
					va.getTagihan().getJenisTagihan().getKode() + va.getTagihan().getDebitur().getNomorDebitur(),
					va.getBank().getJumlahDigitVirtualAccount()));
		} else {
			vaRequest.setAccountNumber(va.getNomor().substring(va.getBank().getJumlahDigitPrefix()));
		}
		return vaRequest;
	}

}
