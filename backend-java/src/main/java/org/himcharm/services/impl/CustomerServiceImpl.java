package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.entities.Customer;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.CustomerRepository;
import org.himcharm.services.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final int PAGE_SIZE = 30;

    private final CustomerRepository customerRepository;
    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.findByPhone(customer.getPhone())
                .orElseGet(() -> Customer.builder().phone(customer.getPhone()).build());
        savedCustomer.setName(customer.getName());
        savedCustomer.setEmail(customer.getEmail());
        savedCustomer.setDateOfBirth(customer.getDateOfBirth());
        savedCustomer.setMaritalStatus(customer.getMaritalStatus());
        savedCustomer.setSpouseName(customer.getSpouseName());
        savedCustomer.setAnniversaryDate(customer.getAnniversaryDate());
        return customerRepository.save(savedCustomer);
    }

    @Override
    @Transactional
    public Customer getOrCreateCustomerByPhone(String phone, String name) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseGet(() -> Customer.builder().phone(phone).name(name).build());
        if ((customer.getName() == null || customer.getName().isBlank()) && name != null && !name.isBlank()) {
            customer.setName(name);
        }
        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> getCustomers(int page, LocalDate fromDate, LocalDate toDate, String phone) {
        if (page < 0) {
            throw new IllegalStateException("Page number cannot be negative");
        }
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalStateException("From date cannot be after to date");
        }

        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();
        String phoneFilter = phone == null || phone.isBlank() ? null : phone.trim();
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        return customerRepository.findAllByFilters(fromDateTime, toDateExclusive, phoneFilter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return findCustomer(id);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer customer = findCustomer(id);
        customer.setName(updatedCustomer.getName());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setDateOfBirth(updatedCustomer.getDateOfBirth());
        customer.setMaritalStatus(updatedCustomer.getMaritalStatus());
        customer.setSpouseName(updatedCustomer.getSpouseName());
        customer.setAnniversaryDate(updatedCustomer.getAnniversaryDate());
        return customerRepository.save(customer);
    }

    private Customer findCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

}
