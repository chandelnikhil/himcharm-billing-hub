package org.himcharm.services;

import org.himcharm.entities.Customer;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Customer getOrCreateCustomerByPhone(String phone, String name);

    Page<Customer> getCustomers(int page, LocalDate fromDate, LocalDate toDate, String phone);

    Customer getCustomerById(Long id);

    Customer updateCustomer(Long id, Customer updatedCustomer);
}
