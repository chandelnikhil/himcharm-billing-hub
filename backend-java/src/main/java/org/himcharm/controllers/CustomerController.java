package org.himcharm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.CustomerRequestDTO;
import org.himcharm.dtos.CustomerResponseDTO;
import org.himcharm.entities.Customer;
import org.himcharm.services.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<ApiResponse> createCustomer(@Valid @RequestBody CustomerRequestDTO request) {
        Customer customer = customerService.createCustomer(modelMapper.map(request, Customer.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Customer created successfully", toResponse(customer))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Customers fetched successfully", customers)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = toResponse(customerService.getCustomerById(id));
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Customer fetched successfully", customer)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO request
    ) {
        Customer customer = customerService.updateCustomer(id, modelMapper.map(request, Customer.class));
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Customer updated successfully", toResponse(customer))
        );
    }

    private CustomerResponseDTO toResponse(Customer customer) {
        return modelMapper.map(customer, CustomerResponseDTO.class);
    }
}
