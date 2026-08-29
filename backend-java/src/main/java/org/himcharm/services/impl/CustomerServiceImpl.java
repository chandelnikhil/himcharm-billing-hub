package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.entities.Customer;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.CustomerRepository;
import org.himcharm.services.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

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
    public Customer getOrCreateCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone)
                .orElseGet(() -> customerRepository.save(Customer.builder().phone(phone).build()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
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
