package com.example.ai_smart_ecommerce.repository;

import com.example.ai_smart_ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrder_User_Id(Long userId);

}
