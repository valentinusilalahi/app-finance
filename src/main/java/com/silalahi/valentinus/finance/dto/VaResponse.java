package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;

import com.silalahi.valentinus.finance.entity.VaStatus;

import lombok.Data;

@Data
public class VaResponse {

	private VaStatus requestType;
	private VaRequestStatus requestStatus;
	private String accountNumber;
	private String invoiceNumber;
	private String name;
	private BigDecimal amount;
	private String bankId;

}
