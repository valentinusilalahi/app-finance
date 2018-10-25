package com.silalahi.valentinus.finance.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.silalahi.valentinus.finance.entity.TipePembayaran;
import com.silalahi.valentinus.finance.entity.VaStatus;

import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class VaRequest {

	private String bankId;
	private String accountNumber;
	private String invoiceNumber;
	private String nama;
	private String description;
	private String email;
	private String phone;
	private BigDecimal amount;
	private String expireDate;
	@Builder.Default
	private TipePembayaran accountType = TipePembayaran.CLOSED;
	@Builder.Default
	private VaStatus requestType = VaStatus.CREATE;

}
