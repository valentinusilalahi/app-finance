package com.silalahi.valentinus.finance.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.silalahi.valentinus.finance.entity.JenisTagihan;

public interface JenisTagihanDao extends PagingAndSortingRepository<JenisTagihan, String> {

	List<JenisTagihan> findByAktifOrderByKode(Boolean aktif);

}
