package com.example.ai_smart_ecommerce.controller;


import com.example.ai_smart_ecommerce.entity.Product;
import com.example.ai_smart_ecommerce.entity.User;
import com.example.ai_smart_ecommerce.repository.ProductRepository;
import com.example.ai_smart_ecommerce.service.ImageUploadService;
import com.example.ai_smart_ecommerce.service.OrderService;
import com.example.ai_smart_ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    // ADMIN
    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam String category,
            @RequestParam Integer stock,
            @RequestParam MultipartFile image


    ) {
        Map<String, String> uploadResult = imageUploadService.uploadImage(image);

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);


        product.setImageUrl(uploadResult.get("url"));
        product.setImagePublicId(uploadResult.get("publicId"));

        return productRepository.save(product);
    }



    // USER + ADMIN
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @PutMapping("/admin/products/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProductImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // delete old image if exists
        if (product.getImagePublicId() != null) {
            imageUploadService.deleteImage(product.getImagePublicId());
        }

        // upload new image
        Map<String, String> uploadResult = imageUploadService.uploadImage(image);

        product.setImageUrl(uploadResult.get("url"));
        product.setImagePublicId(uploadResult.get("publicId"));

        return productRepository.save(product);
    }


    // ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/recommend")
    @PreAuthorize("hasRole('USER')")
    public List<Product> recommendProducts(@AuthenticationPrincipal User user) {
        return orderService.recommendProducts(user);
    }

    @Cacheable(value = "productSearch", key = "#query")
    public List<Product> searchProducts(String query) {

        System.out.println("Fetching from DB...");
        return productRepository.searchByKeyword(query);
    }






}

