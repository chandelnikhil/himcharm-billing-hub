package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.DashboardMetricsResponseDTO;
import org.himcharm.dtos.DashboardResponseDTO;
import org.himcharm.dtos.DashboardTrendPointResponseDTO;
import org.himcharm.dtos.CustomerActivityResponseDTO;
import org.himcharm.dtos.CustomerDashboardResponseDTO;
import org.himcharm.dtos.CustomerFrequencyResponseDTO;
import org.himcharm.entities.Invoice;
import org.himcharm.repositories.CustomerRepository;
import org.himcharm.repositories.InvoiceRepository;
import org.himcharm.repositories.projections.CustomerActivityProjection;
import org.himcharm.repositories.projections.CustomerFrequencyProjection;
import org.himcharm.repositories.projections.CustomerSpecialDatesProjection;
import org.himcharm.services.DashboardService;
import org.himcharm.services.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final int DEFAULT_RANGE_DAYS = 30;
    private static final int MAX_RANGE_DAYS = 366;
    private static final int UPCOMING_DAYS = 30;

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final StoreService storeService;
    private final Clock applicationClock;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboard(LocalDate fromDate, LocalDate toDate, Long storeId) {
        LocalDate endDate = toDate == null ? LocalDate.now() : toDate;
        LocalDate startDate = fromDate == null
                ? endDate.minusDays(DEFAULT_RANGE_DAYS - 1L)
                : fromDate;
        validateRange(startDate, endDate);
        /**
         * Check whether valid store id or not
         */
        if (storeId != null) {
            storeService.getStoreById(storeId);
        }

        List<Invoice> invoices = invoiceRepository.findAllForDashboard(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                storeId
        );
        // Find the first-ever invoice for every customer present in the selected range.
        // "New" means the customer's first invoice across all time, not simply their
        // first invoice inside the selected dashboard date range.
        Set<Long> firstInvoiceIds = getFirstInvoiceIds(invoices);

        // Total revenue contains both new-customer and repeat-customer revenue.
        double revenue = 0.0;
        double newRevenue = 0.0;
        long newBills = 0L;
        Map<LocalDate, DailyTotals> dailyTotals = new HashMap<>();

        for (Invoice invoice : invoices) {
            double amount = value(invoice.getTotalAmount());
            revenue += amount;

            // Only a customer's first-ever invoice contributes to new revenue/new bills.
            // Every later invoice from that customer is repeat business.
            if (firstInvoiceIds.contains(invoice.getId())) {
                newRevenue += amount;
                newBills++;
            }
            LocalDate invoiceDate = invoice.getInvoiceDate().toLocalDate();
            dailyTotals.computeIfAbsent(invoiceDate, ignored -> new DailyTotals()).add(amount);
        }

        long bills = invoices.size();

        // Every bill is either new or repeat, so repeat values are the remaining totals.
        long repeatBills = bills - newBills;
        double repeatRevenue = revenue - newRevenue;
        DashboardMetricsResponseDTO metrics = DashboardMetricsResponseDTO.builder()
                .revenue(money(revenue))
                .bills(bills)
                .averageOrderValue(average(revenue, bills))
                .newRevenue(money(newRevenue))
                .repeatRevenue(money(repeatRevenue))
                .newBills(newBills)
                .repeatBills(repeatBills)
                .newAverageOrderValue(average(newRevenue, newBills))
                .repeatAverageOrderValue(average(repeatRevenue, repeatBills))
                .build();

        List<DashboardTrendPointResponseDTO> trend = new ArrayList<>();
        LocalDate currentDate = startDate;

        // Add one trend entry for every date from startDate through endDate.
        while (!currentDate.isAfter(endDate)) {
            DailyTotals totals = dailyTotals.getOrDefault(currentDate, new DailyTotals());
            DashboardTrendPointResponseDTO trendPoint = DashboardTrendPointResponseDTO.builder()
                    .date(currentDate)
                    .revenue(money(totals.revenue))
                    .bills(totals.bills)
                    .build();

            trend.add(trendPoint);
            currentDate = currentDate.plusDays(1);
        }

        return DashboardResponseDTO.builder()
                .fromDate(startDate)
                .toDate(endDate)
                .storeId(storeId)
                .metrics(metrics)
                .trend(trend)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDashboardResponseDTO getCustomerDashboardGraph() {
        LocalDate today = LocalDate.now(applicationClock);
        CustomerActivityProjection activity = customerRepository.getCustomerActivity(
                today.minusMonths(3).atStartOfDay(),
                today.minusMonths(6).atStartOfDay(),
                today.minusMonths(12).atStartOfDay()
        );
        CustomerFrequencyProjection frequency = customerRepository.getCustomerFrequency();

        long upcomingBirthdays = 0;
        long upcomingAnniversaries = 0;
        LocalDate upcomingThrough = today.plusDays(UPCOMING_DAYS);
        for (CustomerSpecialDatesProjection specialDates : customerRepository.findCustomerSpecialDates()) {
            if (isUpcoming(specialDates.getDateOfBirth(), today, upcomingThrough)) {
                upcomingBirthdays++;
            }
            if (isUpcoming(specialDates.getAnniversaryDate(), today, upcomingThrough)) {
                upcomingAnniversaries++;
            }
        }

        return new CustomerDashboardResponseDTO(
                new CustomerActivityResponseDTO(
                        activity.getTotalCustomers(),
                        activity.getActiveInThreeMonths(),
                        activity.getDormantThreeToSixMonths(),
                        activity.getDormantSixToTwelveMonths(),
                        activity.getDormantTwelvePlusMonths()
                ),
                new CustomerFrequencyResponseDTO(
                        frequency.getOneTimeVisit(),
                        frequency.getTwoTimesVisits(),
                        frequency.getThreeTimesVisits(),
                        frequency.getFourTimesVisits(),
                        frequency.getFivePlusTimesVisits(),
                        frequency.getTenPlusTimesVisits()
                ),
                customerRepository.countCompletedProfiles(),
                upcomingBirthdays,
                upcomingAnniversaries,
                LocalDateTime.now(applicationClock)
        );
    }

    private boolean isUpcoming(LocalDate eventDate, LocalDate today, LocalDate upcomingThrough) {
        if (eventDate == null) {
            return false;
        }
        MonthDay eventMonthDay = MonthDay.from(eventDate);
        LocalDate nextOccurrence = eventMonthDay.atYear(today.getYear());
        if (nextOccurrence.isBefore(today)) {
            nextOccurrence = eventMonthDay.atYear(today.getYear() + 1);
        }
        return !nextOccurrence.isAfter(upcomingThrough);
    }

    private Set<Long> getFirstInvoiceIds(List<Invoice> invoices) {
        // Only look up customers who have invoices in the requested dashboard range.
        Set<Long> customerIds = new HashSet<>();
        for (Invoice invoice : invoices) {
            customerIds.add(invoice.getCustomer().getId());
        }
        if (customerIds.isEmpty()) {
            return Set.of();
        }

        // The repository checks complete invoice history and returns one first invoice
        // ID per customer. A Set provides fast lookup while classifying each invoice.
        return new HashSet<>(invoiceRepository.findFirstInvoiceIdsByCustomerIds(customerIds));
    }

    private void validateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalStateException("From date cannot be after to date");
        }
        if (fromDate.plusDays(MAX_RANGE_DAYS - 1L).isBefore(toDate)) {
            throw new IllegalStateException("Dashboard date range cannot exceed 366 days");
        }
    }

    private double average(double amount, long count) {
        return count == 0 ? 0.0 : money(amount / count);
    }

    private double value(Double amount) {
        return amount == null ? 0.0 : amount;
    }

    private double money(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    private static class DailyTotals {
        private double revenue;
        private long bills;

        void add(double amount) {
            revenue += amount;
            bills++;
        }
    }
}
