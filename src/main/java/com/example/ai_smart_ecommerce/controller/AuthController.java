package com.example.ai_smart_ecommerce.controller;

import com.example.ai_smart_ecommerce.dto.LoginRequest;
import com.example.ai_smart_ecommerce.dto.RegisterRequest;
import com.example.ai_smart_ecommerce.service.AuthService;
import com.example.ai_smart_ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    private AuthService authService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public  ResponseEntity<String> login(
            @Valid @RequestBody LoginRequest request
            ){
        String token =authService.login(request);
        return ResponseEntity.ok(token);
    }
}