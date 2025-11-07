package com.example.smily_bot.service.external.stickergallery;

import com.example.smily_bot.config.StickerGalleryApiProperties;
import com.example.smily_bot.exception.BotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Client for interacting with the Sticker Gallery API.
 */
@Component
public class StickerGalleryApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StickerGalleryApiClient.class);

    private final RestTemplate restTemplate;
    private final StickerGalleryApiProperties properties;

    public StickerGalleryApiClient(RestTemplate restTemplate, StickerGalleryApiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @SuppressWarnings("null")
    public StickerSetResponse createStickerSet(StickerSetCreateRequest request) {
        String url = properties.getCreateStickerSetUrl();
        String title = Objects.requireNonNull(request.getTitle(), "Sticker set title must not be null");
        String name = Objects.requireNonNull(request.getName(), "Sticker set name must not be null");
        LOGGER.info("🌐 Создание стикерсета через Sticker Gallery API: userId={}, title='{}', name='{}'",
                request.getUserId(), title, name);

        try {
            ResponseEntity<StickerSetResponse> response = restTemplate.postForEntity(url, request, StickerSetResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                LOGGER.info("✅ Стикерсет '{}' создан через Sticker Gallery API", request.getName());
                return response.getBody();
            }

            int statusCode = response.getStatusCode().value();
            LOGGER.error("❌ Sticker Gallery API вернул неожиданный ответ: status={}, body={}", statusCode, response.getBody());
            throw new BotException("Sticker Gallery API responded with status " + statusCode);
        } catch (RestClientResponseException e) {
            int statusCode = e.getStatusCode().value();
            LOGGER.error("❌ Ошибка ответа Sticker Gallery API: status={}, body={}", statusCode, e.getResponseBodyAsString());
            throw new BotException("Sticker Gallery API responded with error status " + statusCode, e);
        } catch (RestClientException e) {
            LOGGER.error("❌ Сетевая ошибка Sticker Gallery API: {}", e.getMessage());
            throw new BotException("Failed to call Sticker Gallery API", e);
        }
    }
}

