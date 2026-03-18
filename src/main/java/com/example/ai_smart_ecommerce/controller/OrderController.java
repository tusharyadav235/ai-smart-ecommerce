package com.example.ai_smart_ecommerce.controller;

import com.example.ai_smart_ecommerce.dto.OrderResponseDTO;
import com.example.ai_smart_ecommerce.entity.Order;
import com.example.ai_smart_ecommerce.repository.OrderRepository;
import com.example.ai_smart_ecommerce.security.CustomUserDetails;
import com.example.ai_smart_ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Order placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<Long, Integer> products) {

        return orderService.placeOrder(
                userDetails.getUser(),
                products
        );
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponseDTO> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return orderService.getAllOrders(pageable);
    }


    @GetMapping("/user/orders")
    @PreAuthorize("hasRole('USER')")
    public List<OrderResponseDTO> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return orderService.getOrdersForUser(userDetails.getUser());
    }


}
