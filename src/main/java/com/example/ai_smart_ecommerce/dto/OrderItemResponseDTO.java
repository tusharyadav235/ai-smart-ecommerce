package com.example.ai_smart_ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseDTO {

    private Long productId;

    private String productName;

    private Integer quantity;

    private Double price;


}
