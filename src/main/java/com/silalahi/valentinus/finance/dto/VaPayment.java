package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VaPayment {

	private String bankId;
	private String invoiceNumber;
	private String accountNumber;
	private String referensi;
	private BigDecimal amount;
	private BigDecimal cumulativeAmount;
	private LocalDateTime paymentTime;

}
