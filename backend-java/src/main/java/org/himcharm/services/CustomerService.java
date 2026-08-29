package org.himcharm.services;

import org.himcharm.entities.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Customer getOrCreateCustomerByPhone(String phone);

    List<Customer> getAllCustomers();

    Customer getCustomerById(Long id);

    Customer updateCustomer(Long id, Customer updatedCustomer);
}
