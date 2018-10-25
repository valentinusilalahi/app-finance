package com.silalahi.valentinus.finance.dao;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.silalahi.valentinus.finance.dto.RekapTagihan;
import com.silalahi.valentinus.finance.entity.Debitur;
import com.silalahi.valentinus.finance.entity.JenisTagihan;
import com.silalahi.valentinus.finance.entity.StatusNotifikasi;
import com.silalahi.valentinus.finance.entity.StatusTagihan;
import com.silalahi.valentinus.finance.entity.Tagihan;

public interface TagihanDao extends PagingAndSortingRepository<Tagihan, String> {
	
	Optional<Tagihan> findByNomorAndStatusTagihan(String nomor, StatusTagihan statusTagihan);
	Iterable<Tagihan> findByJenisTagihanAndStatusTagihanOrderByTanggalTagihan(JenisTagihan jenisTagihan, StatusTagihan statusTagihan);
	Page<Tagihan> findByJenisTagihanAndStatusTagihanOrderByTanggalTagihan(JenisTagihan jenisTagihan, StatusTagihan statusTagihan, Pageable pageable);
	Page<Tagihan> findbyDebiturAndStatusTagihanOrderByTanggalTagihan(Debitur debitur, StatusTagihan statusTagihan, Pageable pageable);
	Page<Tagihan> findByStatusTagihan(StatusTagihan statusTagihan, Pageable pageable);
	Page<Tagihan> findByStatusNotifikasi(StatusNotifikasi statusNotifikasi, PageRequest pageable);
	
	@Query(
			"select sum(t.nilaiTagihan) from Tagihan t where t.jenisTagihan = :jenisTagihan and t.statusTagihan = :statusTagihan"
		  )
	BigDecimal totalTagihanByJenisTagihanAndStatusTagihan(@Param("jenisTagihan") JenisTagihan jenisTagihan, 
			@Param("statusTagihan")StatusTagihan statusTagihan);
	
	@Query(
			"select count(t) from Tagihan t where t.jenisTagihan = :jenisTagihan and t.statusTagihan = :statusTagihan"
		  )
	Long jumlahTagihanByJenisTagihanAndStatusTagihan(@Param("jenisTagihan") JenisTagihan jenisTagihan, 
			@Param("statusTagihan") StatusTagihan statusTagihan);
	
	@Query(
			"select new com.silalahi.valentinus.finance.dto.RekapTagihan(t.jenisTagihan, t.statusPembayaran, "
			+ "count(t), sum(t.nilaiTagihan), sum(t.jumlahPembayaran))"
			+ "from Tagihan t "
			+ "where t.tanggalTagihan >= :mulai and t.tanggalTagihan <= sampai "
			+ "group by t.jenisTagihan, t.statusPembayaran"
		  )
	List<RekapTagihan> rekapTagihan(@Param("mulai") LocalDate mulai,
			@Param("sampai") LocalDate sampai);

}
