package com.example.ai_smart_ecommerce.service;

import com.example.ai_smart_ecommerce.entity.Product;
import com.example.ai_smart_ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setStock(updatedProduct.getStock());

        return productRepository.save(product);
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> smartSearch(String userQuery) {

        String[] keywords = userQuery.toLowerCase().split("\\s+");

        Map<Long, Product> matchedProducts = new LinkedHashMap<>();

        for (String keyword : keywords) {

            List<Product> results = productRepository.searchByKeyword(keyword);

            for (Product product : results) {
                matchedProducts.putIfAbsent(product.getId(), product);
            }
        }

        return new ArrayList<>(matchedProducts.values());
    }

}
