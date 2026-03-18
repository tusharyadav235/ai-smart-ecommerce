package com.example.ai_smart_ecommerce.repository;

import com.example.ai_smart_ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop5ByCategory(String category);

    List<Product> findTop5ByNameContainingIgnoreCase(String keyword);

    @Query("""
       SELECT DISTINCT p FROM Product p
       WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    List<Product> findByNameContainingIgnoreCase(String keyword);


}
