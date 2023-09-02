package com.devsuperior.dsmeta.repositories;

import com.devsuperior.dsmeta.dto.SaleReportDTO;
import com.devsuperior.dsmeta.dto.SaleSumaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dsmeta.entities.Sale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("""
        SELECT new com.devsuperior.dsmeta.dto.SaleReportDTO(
            sa.id,
            sa.date,
            sa.amount,
            sa.seller.name
        )
        FROM Sale sa
        WHERE sa.date >= :minDate AND sa.date <= :maxDate
        AND UPPER(sa.seller.name) LIKE UPPER(CONCAT('%', :sellerName, '%'))
    """)
    Page<SaleReportDTO> findReportBetweenDatesAndSellerName(
            LocalDate minDate, LocalDate maxDate, String sellerName,
            Pageable pageable
    );

    @Query("""
           SELECT new com.devsuperior.dsmeta.dto.SaleSumaryDTO(sa.seller.name, SUM(sa.amount))
           FROM Sale sa
           WHERE sa.date >= :minDate AND sa.date <= :maxDate
           GROUP BY sa.seller.name
    """)
    Page<SaleSumaryDTO> findSumaryBetweenDate(
            LocalDate minDate, LocalDate maxDate,
            Pageable pageable
    );

}
