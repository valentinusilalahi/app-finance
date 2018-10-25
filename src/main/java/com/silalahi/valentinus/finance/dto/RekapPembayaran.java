package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RekapPembayaran {

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date tanggal;
	private BigDecimal nilai;
	private Long jumlah;

}
