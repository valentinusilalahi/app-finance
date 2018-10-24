package com.silalahi.valentinus.finance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class Bank {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@NotNull
	@NotEmpty
	@Column(unique = true)
	private String kode;

	@NotNull
	@NotEmpty
	private String nama;

	@NotNull
	@NotEmpty
	private String nomorRekening;

	@NotNull
	@NotEmpty
	private String namaRekening;

	@NotNull
	@Min(0)
	private Integer jumlahDigitVirtualAccount = 0;

	@NotNull
	@Min(0)
	private Integer jumlahDigitPrefix = 0;

}
