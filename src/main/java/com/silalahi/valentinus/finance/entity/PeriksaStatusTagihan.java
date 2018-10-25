package com.silalahi.valentinus.finance.entity;

import java.time.LocalDateTime;

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
public class PeriksaStatusTagihan {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_virtual_account")
	private VirtualAccount virtualAccount;

	@NotNull
	private LocalDateTime waktuPeriksa;

	@NotNull
	@Enumerated(EnumType.STRING)
	private StatusPemeriksaanTagihan statusPemeriksaanTagihan = StatusPemeriksaanTagihan.BARU;

}
