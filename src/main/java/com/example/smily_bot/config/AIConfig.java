package com.example.smily_bot.config;

import com.example.smily_bot.service.memory.InMemoryChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    @Bean
    public InMemoryChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, InMemoryChatMemory chatMemory) {
        return chatClientBuilder
                .defaultAdvisors(new PromptChatMemoryAdvisor(chatMemory))
                .build();
    }
} 