package com.example.ai_smart_ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AiSmartEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiSmartEcommerceApplication.class, args);
	}

}
