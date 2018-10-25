package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PembayaranTagihan {

	private String jenisTagihan;
	private String kodeBiaya;
	private String nomorTagihan;
	private String nomorDebitur;
	private String namaDebitur;
	private String keteranganTagihan;
	private String tanggalTagihan;
	private String tanggalJatuhTempo;
	private String statusTagihan;
	private BigDecimal nilaiTagihan;
	private BigDecimal nilaiPembayaran;
	private BigDecimal nilaiAkumulasiPembayaran;
	private String bank;
	private String waktuPembayaran;
	private String refereniPembayaran;

}
