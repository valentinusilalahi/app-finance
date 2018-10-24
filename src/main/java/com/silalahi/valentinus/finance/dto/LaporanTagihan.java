package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.silalahi.valentinus.finance.entity.JenisTagihan;

import lombok.Data;

@Data
public class LaporanTagihan {

	private JenisTagihan jenisTagihan;
	private Date mulai;
	private Date sampai;
	private Long jumlahTagihanLunas = 0L;
	private Long jumlahTagihanBelumLunas = 0L;
	private BigDecimal nilaiTagihan = BigDecimal.ZERO;
	private BigDecimal nilaiPembayaran = BigDecimal.ZERO;

}
