package com.silalahi.valentinus.finance.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class VirtualAccount {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "id_tagihan")
	private Tagihan tagihan;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "id_bank")
	private Bank bank;

	private String nomor;

	@NotNull
	@Enumerated(EnumType.STRING)
	private VaStatus vaStatus = VaStatus.CREATE;

}
