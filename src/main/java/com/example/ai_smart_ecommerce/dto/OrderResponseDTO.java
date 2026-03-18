package com.example.ai_smart_ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {
    private Long orderId;

    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private List<OrderItemResponseDTO> items;
}
