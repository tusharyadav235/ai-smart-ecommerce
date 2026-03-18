package com.example.ai_smart_ecommerce.service;

import com.example.ai_smart_ecommerce.entity.Product;
import com.example.ai_smart_ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiRecommendationService {

    @Autowired
    private GrokAiService grokAiService;

    @Autowired
    private ProductRepository productRepository;

    public List<Product> recommend(String prompt) {

        String aiResponse = grokAiService.getOutfitSuggestion(prompt);

        String keyword = aiResponse.toLowerCase();

        return productRepository
                .findTop5ByNameContainingIgnoreCase(keyword);
    }
}
