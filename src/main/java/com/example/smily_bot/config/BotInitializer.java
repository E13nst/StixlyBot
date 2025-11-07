package com.example.smily_bot.config;

import com.example.smily_bot.bot.AbstractTelegramBot;
import com.example.smily_bot.bot.StickerBot;
import com.example.smily_bot.service.telegram.StickerService;
import com.example.smily_bot.service.telegram.StickerSetService;
import com.example.smily_bot.service.telegram.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class BotInitializer {

    private final StickerBotProperties botProperties;
    private final UserStateService userStateService;
    private final StickerSetService stickerSetService;
    private final StickerService stickerService;
    private final Map<String, AbstractTelegramBot> botRegistry = new ConcurrentHashMap<>();

    public BotInitializer(StickerBotProperties botProperties,
                          UserStateService userStateService,
                          StickerSetService stickerSetService,
                          StickerService stickerService) {
        this.botProperties = botProperties;
        this.userStateService = userStateService;
        this.stickerSetService = stickerSetService;
        this.stickerService = stickerService;
    }

    @Bean
    public Map<String, AbstractTelegramBot> botRegistry() {
        return botRegistry;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("🚀 Application is ready! Starting single sticker bot initialization...");
        try {
            StickerBot stickerBot = new StickerBot(botProperties, userStateService, stickerSetService, stickerService);

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(stickerBot);

            botRegistry.put(botProperties.getUsername(), stickerBot);
            log.info("✅ Sticker bot '{}' registered successfully", botProperties.getUsername());
        } catch (TelegramApiException e) {
            log.error("❌ Failed to register sticker bot", e);
        }
    }
}
