package com.silalahi.valentinus.finance.entity;

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
public class RunningNumber {

	public static final String PEMAKAIAN_DEFAULT = "DEFAULT";

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@NotNull
	@NotEmpty
	private String prefix;

	@NotNull
	@NotEmpty
	private String pemakaian = PEMAKAIAN_DEFAULT;

	@NotNull
	@Min(0)
	private Long lasNumber = 0L;

}
