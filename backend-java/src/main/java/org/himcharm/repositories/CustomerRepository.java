package org.himcharm.repositories;

import org.himcharm.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    @Query("""
            SELECT customer
            FROM Customer customer
            WHERE customer.dateOfBirth IS NOT NULL
              AND MONTH(customer.dateOfBirth) = :month
              AND DAY(customer.dateOfBirth) = :day
            """)
    List<Customer> findCustomersByBirthday(
            @Param("month") int month,
            @Param("day") int day
    );

    @Query("""
            SELECT customer
            FROM Customer customer
            WHERE customer.anniversaryDate IS NOT NULL
              AND MONTH(customer.anniversaryDate) = :month
              AND DAY(customer.anniversaryDate) = :day
            """)
    List<Customer> findCustomersByAnniversary(
            @Param("month") int month,
            @Param("day") int day
    );

    @Query("""
            SELECT customer
            FROM Customer customer
            WHERE (:fromDate IS NULL OR customer.createdAt >= :fromDate)
              AND (:toDateExclusive IS NULL OR customer.createdAt < :toDateExclusive)
              AND (:phone IS NULL OR customer.phone LIKE CONCAT('%', :phone, '%'))
            """)
    Page<Customer> findAllByFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive,
            @Param("phone") String phone,
            Pageable pageable
    );
}
