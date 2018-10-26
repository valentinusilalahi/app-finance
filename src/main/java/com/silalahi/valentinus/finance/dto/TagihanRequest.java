package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TagihanRequest {

	private String jenisTagihan;
	private String kodeBiaya;
	private String debitur;
	private BigDecimal nilaiTagihan;
	private LocalDate tanggalJatuhTempo;
	private String keterangan;

}
