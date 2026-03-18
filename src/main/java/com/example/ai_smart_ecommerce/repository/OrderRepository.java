package com.example.ai_smart_ecommerce.repository;

import com.example.ai_smart_ecommerce.entity.Order;
import com.example.ai_smart_ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    Page<Order> findAll(Pageable pageable);
}
