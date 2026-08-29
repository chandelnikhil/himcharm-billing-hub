package org.himcharm.repositories;

import org.himcharm.entities.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
            SELECT invoice
            FROM Invoice invoice
            WHERE (:fromDate IS NULL OR invoice.invoiceDate >= :fromDate)
              AND (:toDateExclusive IS NULL OR invoice.invoiceDate < :toDateExclusive)
              AND (:storeId IS NULL OR invoice.store.id = :storeId)
            """)
    Page<Invoice> findAllByInvoiceDateRange(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive,
            @Param("storeId") Long storeId,
            Pageable pageable
    );
}
