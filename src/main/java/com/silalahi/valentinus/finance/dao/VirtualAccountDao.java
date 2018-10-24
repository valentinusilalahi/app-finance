package com.silalahi.valentinus.finance.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.silalahi.valentinus.finance.entity.Tagihan;
import com.silalahi.valentinus.finance.entity.VaStatus;
import com.silalahi.valentinus.finance.entity.VirtualAccount;

public interface VirtualAccountDao extends PagingAndSortingRepository<VirtualAccount, String> {

	Iterable<VirtualAccount> findByVaStatus(VaStatus vaStatus);

	List<VirtualAccount> findByVaStatusAndTagihanNomor(VaStatus vaStatus, String nomor);

	Page<VirtualAccount> findByTagihan(Tagihan tagihan, Pageable pageable);

	Iterable<VirtualAccount> findByTagihan(Tagihan tagihan);

	VirtualAccount findByVaStatusAndTagihanNomorAndBankId(VaStatus status, String invoiceNumber, String bankId);

}
