package com.silalahi.valentinus.finance.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Data
public class JenisTagihan {

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
	@Column(unique = true)
	private String nama;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TipePembayaran tipePembayaran;

	@NotNull
	private Boolean aktif = Boolean.FALSE;

	@ManyToMany
	@JoinTable(
			name = "jenis_tagihan_bank", 
			joinColumns = @JoinColumn(name = "id_jenis_tagihan"), 
			inverseJoinColumns = @JoinColumn(name = "id_bank"))
	private Set<Bank> daftarBank = new HashSet<>();

}
