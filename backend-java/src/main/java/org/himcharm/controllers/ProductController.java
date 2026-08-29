package org.himcharm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.ProductRequestDTO;
import org.himcharm.dtos.ProductResponseDTO;
import org.himcharm.entities.Product;
import org.himcharm.services.ProductService;
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
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<ApiResponse> addProduct(@Valid @RequestBody ProductRequestDTO request) {
        Product product = productService.addProduct(modelMapper.map(request, Product.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(HttpStatus.CREATED.value(), "Product added successfully", toResponse(product))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Products fetched successfully", products)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable Long id) {
        ProductResponseDTO product = toResponse(productService.getProduct(id));
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Product fetched successfully", product)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request
    ) {
        Product product = productService.updateProduct(id, modelMapper.map(request, Product.class));
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Product updated successfully", toResponse(product))
        );
    }

    private ProductResponseDTO toResponse(Product product) {
        return modelMapper.map(product, ProductResponseDTO.class);
    }
}
