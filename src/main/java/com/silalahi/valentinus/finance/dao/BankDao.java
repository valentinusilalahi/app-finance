package com.silalahi.valentinus.finance.dao;

import java.util.Set;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.silalahi.valentinus.finance.entity.Bank;

public interface BankDao extends PagingAndSortingRepository<Bank, String> {

	Iterable<Bank> findByIdNotIn(Set<String> ids);

}
