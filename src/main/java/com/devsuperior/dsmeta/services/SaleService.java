package com.devsuperior.dsmeta.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import com.devsuperior.dsmeta.dto.SaleReportDTO;
import com.devsuperior.dsmeta.dto.SaleSumaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devsuperior.dsmeta.dto.SaleMinDTO;
import com.devsuperior.dsmeta.entities.Sale;
import com.devsuperior.dsmeta.repositories.SaleRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

	@Autowired
	private SaleRepository repository;

	@Transactional(readOnly = true)
	public SaleMinDTO findById(Long id) {
		Optional<Sale> result = repository.findById(id);
		Sale entity = result.get();
		return new SaleMinDTO(entity);
	}

	@Transactional(readOnly = true)
	public Page<SaleReportDTO> getReport(Map<String, String> params, Pageable pageable) {
		LocalDate maxDate = getValidMaxDate(params.get("maxDate"));
		LocalDate minDate = getValidMinDate(params.get("minDate"), maxDate);
		String sellerName = getValidSellerName(params.get("name"));

		return repository.findReportBetweenDatesAndSellerName(
				minDate, maxDate, sellerName,
				pageable
		);
	}

	@Transactional(readOnly = true)
	public Page<SaleSumaryDTO> gerSumary(Map<String, String> params, Pageable pageable) {
		LocalDate maxDate = getValidMaxDate(params.get("maxDate"));
		LocalDate minDate = getValidMinDate(params.get("minDate"), maxDate);

		return repository.findSumaryBetweenDate(
				minDate, maxDate,
				pageable
		);
	}

	private LocalDate toLocalDate(String date) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(date, fmt);
	}

	private LocalDate getValidMaxDate(String dateStr) {
		return dateStr == null || dateStr.isBlank() ?
				LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault()) :
				toLocalDate(dateStr)
		;
	}

	private LocalDate getValidMinDate(String dateStr, LocalDate maxDate) {
		return dateStr == null || dateStr.isBlank() ?
				maxDate.minusYears(1) :
				toLocalDate(dateStr)
		;
	}

	private String getValidSellerName(String sellerName) {
		return sellerName == null || sellerName.isBlank() ?
				"" : sellerName;
	}
}
