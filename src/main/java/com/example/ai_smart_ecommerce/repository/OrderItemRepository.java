package com.example.ai_smart_ecommerce.repository;

import com.example.ai_smart_ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
