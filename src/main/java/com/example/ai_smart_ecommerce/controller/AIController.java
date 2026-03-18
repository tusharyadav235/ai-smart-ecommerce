package com.example.ai_smart_ecommerce.controller;

import com.example.ai_smart_ecommerce.dto.AiRequest;
import com.example.ai_smart_ecommerce.service.GrokAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private GrokAiService grokAiService;


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/outfit")
    public String outfit(@RequestBody AiRequest request) {
        return grokAiService.getOutfitSuggestion(request.getPrompt());
    }

}
