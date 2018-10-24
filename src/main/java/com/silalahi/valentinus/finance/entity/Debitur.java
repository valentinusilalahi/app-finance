package com.silalahi.valentinus.finance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class Debitur {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@NotNull
	@NotEmpty
	@Column(unique = true)
	private String nomorDebitur;

	@NotNull
	@NotEmpty
	private String nama;

	@Email
	private String email;
	private String noHp;

}
