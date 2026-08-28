package org.himcharm.services;

import org.himcharm.dtos.ProductRequestDTO;
import org.himcharm.dtos.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO addProduct(ProductRequestDTO request);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProduct(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);
}
