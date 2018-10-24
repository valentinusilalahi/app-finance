package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;

import com.silalahi.valentinus.finance.entity.JenisTagihan;
import com.silalahi.valentinus.finance.entity.StatusPembayaran;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RekapTagihan {

	private JenisTagihan jenisTagihan;
	private StatusPembayaran statusPembayaran;
	private Long jumlahTagihan;
	private BigDecimal nilaiTagihan;
	private BigDecimal nilaiPembayaran;

}
