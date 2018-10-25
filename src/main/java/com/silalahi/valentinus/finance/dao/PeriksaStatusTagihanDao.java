package com.silalahi.valentinus.finance.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.silalahi.valentinus.finance.entity.PeriksaStatusTagihan;
import com.silalahi.valentinus.finance.entity.StatusPemeriksaanTagihan;
import com.silalahi.valentinus.finance.entity.Tagihan;
import com.silalahi.valentinus.finance.entity.VirtualAccount;

public interface PeriksaStatusTagihanDao extends PagingAndSortingRepository<PeriksaStatusTagihan, String> {

	Page<PeriksaStatusTagihan> findByVirtualAccountTagihanOrderByWaktuPeriksaDesc(Tagihan tagihan, Pageable pageable);

	List<PeriksaStatusTagihan> findByVirtualAccountAndStatusPemeriksaanTagihan(VirtualAccount va,
			StatusPemeriksaanTagihan statusPemeriksaanTagihan);

}
