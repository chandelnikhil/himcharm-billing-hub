package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.entities.Product;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.ProductRepository;
import org.himcharm.services.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    @Override
    @Transactional
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return findProduct(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByIds(Collection<Long> ids) {
        return productRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = findProduct(id);
        product.setName(updatedProduct.getName());
        product.setBrand(updatedProduct.getBrand());
        product.setDefaultPrice(updatedProduct.getDefaultPrice());
        return productRepository.save(product);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

}
