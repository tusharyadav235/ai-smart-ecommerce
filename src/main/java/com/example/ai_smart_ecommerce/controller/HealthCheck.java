package com.example.ai_smart_ecommerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class HealthCheck {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public String Health() {
        return "Fine";
    }
}
