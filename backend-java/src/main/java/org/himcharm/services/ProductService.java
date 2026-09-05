package org.himcharm.services;

import org.himcharm.entities.Product;

import java.util.Collection;
import java.util.List;

public interface ProductService {

    Product addProduct(Product product);

    List<Product> getAllProducts();

    Product getProduct(Long id);

    List<Product> getProductsByIds(Collection<Long> ids);

    Product updateProduct(Long id, Product updatedProduct);
}
