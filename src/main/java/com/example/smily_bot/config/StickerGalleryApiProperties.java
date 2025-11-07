package com.example.smily_bot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Configuration holder for Sticker Gallery API integration.
 */
@Component
public class StickerGalleryApiProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(StickerGalleryApiProperties.class);

    private final String baseUrl;

    public StickerGalleryApiProperties(@Value("${STICKER_GALLERY_API_URL}") String rawBaseUrl) {
        if (!StringUtils.hasText(rawBaseUrl)) {
            throw new IllegalStateException("STICKER_GALLERY_API_URL is required");
        }

        String sanitized = sanitizeBaseUrl(rawBaseUrl.trim());
        LOGGER.info("🌐 Sticker Gallery API base URL configured: {}", sanitized);
        this.baseUrl = sanitized;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getCreateStickerSetUrl() {
        return baseUrl + "/stickersets";
    }

    public String getSearchStickerSetUrl() {
        return baseUrl + "/stickersets/search";
    }

    public String getUserStickerSetsUrl(Long userId) {
        return baseUrl + "/stickersets/user/" + userId;
    }

    private String sanitizeBaseUrl(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}

