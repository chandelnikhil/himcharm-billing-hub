package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.dtos.ProductRequestDTO;
import org.himcharm.dtos.ProductResponseDTO;
import org.himcharm.entities.Product;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.ProductRepository;
import org.himcharm.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO request) {
        Product product = modelMapper.map(request, Product.class);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProduct(Long id) {
        return toResponse(findProduct(id));
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        Product product = findProduct(id);
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDefaultPrice(request.getDefaultPrice());
        return toResponse(productRepository.save(product));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponseDTO toResponse(Product product) {
        return modelMapper.map(product, ProductResponseDTO.class);
    }
}
