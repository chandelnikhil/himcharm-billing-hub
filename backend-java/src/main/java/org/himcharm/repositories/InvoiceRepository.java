package org.himcharm.repositories;

import org.himcharm.entities.Invoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Override
    @EntityGraph(attributePaths = {"store", "customer", "items", "items.product"})
    Optional<Invoice> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"store", "customer", "items", "items.product"})
    List<Invoice> findAll();
}
