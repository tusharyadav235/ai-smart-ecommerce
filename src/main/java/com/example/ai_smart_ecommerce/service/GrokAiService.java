package com.example.ai_smart_ecommerce.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GrokAiService {

    private final ChatClient chatClient;


    public GrokAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String getOutfitSuggestion(String userPrompt) {
        return this.chatClient.prompt()
                .user("Provide exactly one keyword for this outfit: " + userPrompt)
                .call()
                .content(); // Extracts just the message content as a String
    }
}