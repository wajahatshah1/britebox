package com.briteboxbackend.briterbox.service;

import com.briteboxbackend.briterbox.entities.Product;
import com.briteboxbackend.briterbox.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public String saveProduct(Product product) {
        try {
            Product savedProduct = productRepository.save(product);
            return "Product saved successfully with ID: " + savedProduct;
        } catch (Exception e) {
            // Handle any exceptions that might occur during the saving process
            e.printStackTrace();
            return "Failed to save product";
        }
    }

    public List<Product> getAllProducts() {
        try{
            return productRepository.findAll();
        }catch (Exception e) {
            // Handle any exceptions that might occur during the saving process
            e.printStackTrace();
            return null;
        }

    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Product updatedProduct) {
        return productRepository.save(updatedProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
