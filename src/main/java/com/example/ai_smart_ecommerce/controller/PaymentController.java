package com.example.ai_smart_ecommerce.controller;

import com.example.ai_smart_ecommerce.dto.OrderResponseDTO;
import com.example.ai_smart_ecommerce.entity.Payment;
import com.example.ai_smart_ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    // Simulate payment processing
    @PostMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDTO processPayment(
            @PathVariable Long orderId,
            @RequestParam boolean success
    ) {
        return orderService.processPayment(orderId, success);
    }

    @GetMapping("/history/{userId}")
    @PreAuthorize("hasRole('USER')")
    public List<Payment> getPaymentHistory(@PathVariable Long userId) {
        return orderService.getPaymentHistory(userId);
    }

}
