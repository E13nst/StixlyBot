package com.example.stixly_bot.service.ai;

public interface AIService {
    String completion(String conversationId, String message, String prompt, Integer memWindow);
}
