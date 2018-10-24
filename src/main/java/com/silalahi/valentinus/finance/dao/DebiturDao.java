package com.silalahi.valentinus.finance.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.silalahi.valentinus.finance.entity.Debitur;

public interface DebiturDao extends PagingAndSortingRepository<Debitur, String> {

	Debitur findByNomorDebitur(String nomor);

	Page<Debitur> findByNomorDebiturOrNamaContainingIgnoreCase(String nomor, String nama, Pageable pageable);

}
