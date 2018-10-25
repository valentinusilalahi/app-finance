package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotifikasiPembayaranRequest {

	private String nomorTagihan;
	private String tanggalTagihan;
	private String nama;
	private BigDecimal nilaiTagihan;
	private BigDecimal nilaiPembayaran;
	private String noHp;
	private String rekening;
	private String keterangan;
	private String contactinfo;
	private String contactinfoFull;
	private String waktu;
	private String referensi;

}
