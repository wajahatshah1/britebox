package com.briteboxbackend.briterbox.controller;


import com.briteboxbackend.briterbox.entities.Product;
import com.briteboxbackend.briterbox.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;



    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            updatedProduct.setId(id); // Ensure the ID is set to the correct value
            Product savedProduct = productService.updateProduct(updatedProduct);
            return new ResponseEntity<>(savedProduct, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/submit")
    public String SaveProduct(@RequestBody Product product){
        Product newProduct = new Product();
        System.out.println("Received product data from Flutter:");
        System.out.println("Name: " + product.getName());
        System.out.println("Section: " + product.getSection());
        System.out.println("Active: " + product.isActive());
        System.out.println("Online: " + product.isOnline());
        System.out.println("Tax Exempt: " + product.isTaxExempt());
        System.out.println("Price: " + product.getPrice());
        System.out.println("Express Price: " + product.getExpressPrice());
        System.out.println("Image: " + product.getImage());
        return productService.saveProduct(product);

    }
    @GetMapping("/display")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

}
