package org.himcharm.repositories;

import org.himcharm.entities.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @EntityGraph(attributePaths = {"store", "customer", "feedback", "items", "items.product"})
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Override
    @EntityGraph(attributePaths = {"store", "customer", "items", "items.product"})
    Optional<Invoice> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"store", "customer"})
    Page<Invoice> findAll(
            Specification<Invoice> specification,
            Pageable pageable
    );

    @Query("""
            SELECT invoice
            FROM Invoice invoice
            JOIN FETCH invoice.customer customer
            WHERE invoice.invoiceDate >= :fromDate
              AND invoice.invoiceDate < :toDateExclusive
            ORDER BY invoice.invoiceDate ASC
            """)
    List<Invoice> findAllForDashboardAcrossStores(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive
    );

    @Query("""
            SELECT invoice
            FROM Invoice invoice
            JOIN FETCH invoice.customer customer
            WHERE invoice.invoiceDate >= :fromDate
              AND invoice.invoiceDate < :toDateExclusive
              AND invoice.store.id = :storeId
            ORDER BY invoice.invoiceDate ASC
            """)
    List<Invoice> findAllForDashboardByStore(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive,
            @Param("storeId") Long storeId
    );

    @Query("""
            SELECT MIN(invoice.id)
            FROM Invoice invoice
            WHERE invoice.customer.id IN :customerIds
            GROUP BY invoice.customer.id
            """)
    List<Long> findFirstInvoiceIdsByCustomerIds(@Param("customerIds") Collection<Long> customerIds);
}
