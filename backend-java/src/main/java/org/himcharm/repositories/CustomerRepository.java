package org.himcharm.repositories;

import org.himcharm.entities.Customer;
import org.himcharm.repositories.projections.CustomerActivityProjection;
import org.himcharm.repositories.projections.CustomerFrequencyProjection;
import org.himcharm.repositories.projections.CustomerSpecialDatesProjection;
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

    @Query(value = """
            SELECT COUNT(*) AS totalCustomers,
                   COALESCE(SUM(CASE WHEN visits.lastVisit >= :threeMonthsAgo THEN 1 ELSE 0 END), 0)
                       AS activeInThreeMonths,
                   COALESCE(SUM(CASE WHEN visits.lastVisit >= :sixMonthsAgo
                                      AND visits.lastVisit < :threeMonthsAgo THEN 1 ELSE 0 END), 0)
                       AS dormantThreeToSixMonths,
                   COALESCE(SUM(CASE WHEN visits.lastVisit >= :twelveMonthsAgo
                                      AND visits.lastVisit < :sixMonthsAgo THEN 1 ELSE 0 END), 0)
                       AS dormantSixToTwelveMonths,
                   COALESCE(SUM(CASE WHEN visits.lastVisit < :twelveMonthsAgo
                                      OR visits.lastVisit IS NULL THEN 1 ELSE 0 END), 0)
                       AS dormantTwelvePlusMonths
            FROM customers customer
            LEFT JOIN (
                SELECT invoice.customer_id, MAX(invoice.invoice_date) AS lastVisit
                FROM invoices invoice
                GROUP BY invoice.customer_id
            ) visits ON visits.customer_id = customer.id
            """, nativeQuery = true)
    CustomerActivityProjection getCustomerActivity(
            @Param("threeMonthsAgo") LocalDateTime threeMonthsAgo,
            @Param("sixMonthsAgo") LocalDateTime sixMonthsAgo,
            @Param("twelveMonthsAgo") LocalDateTime twelveMonthsAgo
    );

    @Query(value = """
            SELECT COALESCE(SUM(CASE WHEN visits.visitCount = 1 THEN 1 ELSE 0 END), 0) AS oneTimeVisit,
                   COALESCE(SUM(CASE WHEN visits.visitCount = 2 THEN 1 ELSE 0 END), 0) AS twoTimesVisits,
                   COALESCE(SUM(CASE WHEN visits.visitCount = 3 THEN 1 ELSE 0 END), 0) AS threeTimesVisits,
                   COALESCE(SUM(CASE WHEN visits.visitCount = 4 THEN 1 ELSE 0 END), 0) AS fourTimesVisits,
                   COALESCE(SUM(CASE WHEN visits.visitCount BETWEEN 5 AND 9 THEN 1 ELSE 0 END), 0)
                       AS fivePlusTimesVisits,
                   COALESCE(SUM(CASE WHEN visits.visitCount >= 10 THEN 1 ELSE 0 END), 0) AS tenPlusTimesVisits
            FROM (
                SELECT customer.id, COUNT(invoice.id) AS visitCount
                FROM customers customer
                LEFT JOIN invoices invoice ON invoice.customer_id = customer.id
                GROUP BY customer.id
            ) visits
            """, nativeQuery = true)
    CustomerFrequencyProjection getCustomerFrequency();

    @Query("""
            SELECT customer.dateOfBirth AS dateOfBirth,
                   customer.anniversaryDate AS anniversaryDate
            FROM Customer customer
            WHERE customer.dateOfBirth IS NOT NULL OR customer.anniversaryDate IS NOT NULL
            """)
    List<CustomerSpecialDatesProjection> findCustomerSpecialDates();

    @Query("""
            SELECT COUNT(customer)
            FROM Customer customer
            WHERE customer.name IS NOT NULL
              AND TRIM(customer.name) <> ''
              AND customer.dateOfBirth IS NOT NULL
              AND customer.maritalStatus IS NOT NULL
              AND (customer.maritalStatus <> org.himcharm.enums.MaritalStatus.MARRIED
                   OR customer.anniversaryDate IS NOT NULL)
            """)
    long countCompletedProfiles();
}
