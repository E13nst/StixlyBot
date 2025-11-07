package com.example.dream_stream_bot.controller;

import com.example.dream_stream_bot.bot.AbstractTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class TelegramWebhookController {

    private final Map<String, AbstractTelegramBot> botRegistry;

    public TelegramWebhookController(Map<String, AbstractTelegramBot> botRegistry) {
        this.botRegistry = botRegistry;
    }

    @PostMapping("/{botUsername}")
    public ResponseEntity<String> handleWebhook(@PathVariable String botUsername, @RequestBody Update update) {
        AbstractTelegramBot bot = botRegistry.get(botUsername);
        if (bot != null) {
            bot.onUpdateReceived(update);
            log.info("✅ Update routed to bot: {}", botUsername);
            return ResponseEntity.ok("OK");
        }

        log.warn("❌ No bot found for username: {}", botUsername);
        return ResponseEntity.notFound().build();
    }
}