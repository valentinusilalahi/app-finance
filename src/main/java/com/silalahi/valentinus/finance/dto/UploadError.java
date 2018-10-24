package com.silalahi.valentinus.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadError {

	public Integer baris;
	public String keterangan;
	public String data;

}
