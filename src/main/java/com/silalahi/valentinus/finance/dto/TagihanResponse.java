package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagihanResponse {

	private Boolean sukses;
	private String error;
	private String debitur;
	private String jenisTagihan;
	private String kodeBiaya;
	private BigDecimal nilaiTagihan;
	private LocalDate tanggalTagihan;
	private LocalDate tanggalJatuhTempo;
	private String nomorTagihan;
	private String keterangan;

}
