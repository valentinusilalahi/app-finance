package com.silalahi.valentinus.finance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class Pembayaran {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@NotNull
	private LocalDateTime waktuTransaksi;

	@NotNull
	@Enumerated(EnumType.STRING)
	private JenisPembayaran jenisPembayaran;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "id_tagihan")
	private Tagihan tagihan;

	@ManyToOne
	@JoinColumn(name = "id_bank")
	private Bank bank;

	@ManyToOne
	@JoinColumn(name = "id_virtual_account")
	private VirtualAccount virtualAccount;

	@NotNull
	@Min(1)
	private BigDecimal jumlah;

	@NotNull
	@NotEmpty
	private String referensi;
	private String keterangan;

}
