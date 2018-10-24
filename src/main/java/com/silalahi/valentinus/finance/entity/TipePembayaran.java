package com.silalahi.valentinus.finance.entity;

public enum TipePembayaran {
	OPEN("Jumlah Bebas"), 
	CLOSED("Jumlah Tertentu"), 
	INSTALLMENT("Cicilan");

	private final String label;

	TipePembayaran(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}
}
