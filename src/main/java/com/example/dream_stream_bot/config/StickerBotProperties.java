package com.example.dream_stream_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Centralizes runtime configuration for the single sticker bot instance.
 * Values are sourced from environment variables to avoid any database dependency.
 */
@Component
public class StickerBotProperties {

    private final String displayName;
    private final String username;
    private final String token;
    private final String prompt;
    private final Integer memWindow;
    private final List<String> triggers;
    private final String miniApp;

    public StickerBotProperties(
            @Value("${TELEGRAM_BOT_NAME}") String botName,
            @Value("${TELEGRAM_API_TOKEN}") String botToken,
            @Value("${TELEGRAM_BOT_PROMPT:}") String botPrompt,
            @Value("${TELEGRAM_BOT_MEM_WINDOW:100}") Integer memWindow,
            @Value("${TELEGRAM_BOT_TRIGGERS:}") String rawTriggers,
            @Value("${TELEGRAM_BOT_MINIAPP:}") String miniApp
    ) {
        if (!StringUtils.hasText(botName)) {
            throw new IllegalStateException("TELEGRAM_BOT_NAME is required");
        }
        if (!StringUtils.hasText(botToken)) {
            throw new IllegalStateException("TELEGRAM_API_TOKEN is required");
        }

        this.displayName = botName;
        this.username = botName;
        this.token = botToken;
        this.prompt = StringUtils.hasText(botPrompt) ? botPrompt : null;
        this.memWindow = memWindow != null ? memWindow : 100;
        this.triggers = buildTriggers(rawTriggers, botName);
        this.miniApp = StringUtils.hasText(miniApp) ? miniApp : null;
    }

    public String getName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getPrompt() {
        return prompt;
    }

    public Integer getMemWindow() {
        return memWindow;
    }

    public List<String> getBotTriggersList() {
        return triggers;
    }

    public List<String> getBotAliasesList() {
        return triggers;
    }

    public String getMiniApp() {
        return miniApp;
    }

    public String getType() {
        return "sticker";
    }

    private List<String> buildTriggers(String rawTriggers, String botName) {
        if (StringUtils.hasText(rawTriggers)) {
            return Arrays.stream(rawTriggers.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(value -> value.toLowerCase(Locale.ROOT))
                    .distinct()
                    .toList();
        }

        String lowerName = botName.toLowerCase(Locale.ROOT);
        return Collections.unmodifiableList(Arrays.asList(
                lowerName,
                lowerName.replace("bot", ""),
                "sticker",
                "стикер"
        ));
    }
}


