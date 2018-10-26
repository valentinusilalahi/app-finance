package com.silalahi.valentinus.finance.dao;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import com.silalahi.valentinus.finance.entity.RunningNumber;

public interface RunningNumberDao extends CrudRepository<RunningNumber, String> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	RunningNumber findByPemakaianAndPrefix(String pemakaian, String prefix);

}
