package com.silalahi.valentinus.finance.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.silalahi.valentinus.finance.dao.RunningNumberDao;
import com.silalahi.valentinus.finance.entity.RunningNumber;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RunningNumberService {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final String TIMEZONE = "GMT+07:00";

	@Autowired
	private RunningNumberDao runningNumberDao;

	public Long getNumber() {
		return getNumber(DATE_FORMAT.format(LocalDateTime.now(ZoneId.of(TIMEZONE))));
	}

	public Long getNumber(String prefix) {
		// TODO Auto-generated method stub
		return getNumber(RunningNumber.PEMAKAIAN_DEFAULT, prefix);
	}

	private Long getNumber(String pemakaian, String prefix) {
		// TODO Auto-generated method stub
		RunningNumber rn = runningNumberDao.findByPemakaianAndPrefix(pemakaian, prefix);
		if (rn == null) {
			rn = new RunningNumber();
			rn.setPrefix(prefix);
			rn.setLasNumber(0L);
		}
		Long last = rn.getLasNumber() + 1;
		rn.setLasNumber(last);
		runningNumberDao.save(rn);
		return last;
	}

}
