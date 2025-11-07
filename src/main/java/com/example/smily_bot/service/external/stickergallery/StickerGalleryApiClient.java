package com.example.smily_bot.service.external.stickergallery;

import com.example.smily_bot.config.StickerGalleryApiProperties;
import com.example.smily_bot.exception.BotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public StickerSetPageResponse getStickerSetsByUser(Long userId, int page, int size, String sort, String direction) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Objects.requireNonNull(properties.getUserStickerSetsUrl(userId)))
                .queryParam("page", page)
                .queryParam("size", size);
        if (sort != null && !sort.isBlank()) {
            builder.queryParam("sort", sort);
        }
        if (direction != null && !direction.isBlank()) {
            builder.queryParam("direction", direction);
        }

        String url = builder.toUriString();

        LOGGER.debug("📄 Получаем стикерсеты пользователя {}: page={}, size={}, sort={} {}, url={}",
                userId, page, size, sort, direction, url);

        try {
            ResponseEntity<StickerSetPageResponse> response = restTemplate.getForEntity(url, StickerSetPageResponse.class);

            StickerSetPageResponse body = response.getBody();
            if (response.getStatusCode().is2xxSuccessful() && body != null) {
                LOGGER.debug("✅ Получено {} стикерсетов пользователя {}", body.getContent().size(), userId);
                return body;
            }

            LOGGER.error("❌ Sticker Gallery API вернул неожиданный ответ при запросе списка пользователя {}: status={}, body={}",
                    userId, response.getStatusCode(), body);
            throw new BotException("Sticker Gallery API responded unexpectedly for user sticker sets");
        } catch (RestClientResponseException e) {
            LOGGER.error("❌ Ошибка Sticker Gallery API при запросе набора пользователя {}: status={}, body={}",
                    userId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new BotException("Sticker Gallery API responded with error status " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            LOGGER.error("❌ Сетевая ошибка Sticker Gallery API при запросе набора пользователя {}: {}", userId, e.getMessage());
            throw new BotException("Failed to call Sticker Gallery API", e);
        }
    }

    public boolean stickerSetExistsByName(String name) {
        return getStickerSetByName(name) != null;
    }

    public StickerSetResponse getStickerSetByName(String name) {
        String url = UriComponentsBuilder.fromHttpUrl(Objects.requireNonNull(properties.getSearchStickerSetUrl()))
                .queryParam("name", name)
                .toUriString();
        LOGGER.debug("🔍 Запрос данных стикерсета '{}' через Sticker Gallery API", name);

        try {
            ResponseEntity<StickerSetResponse> response = restTemplate.getForEntity(url, StickerSetResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            LOGGER.warn("⚠️ Sticker Gallery API вернул неожиданный ответ при запросе '{}': status={}, body={}",
                    name, response.getStatusCode(), response.getBody());
            return null;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.debug("ℹ️ Sticker Gallery API: стикерсет '{}' не найден", name);
                return null;
            }

            LOGGER.error("❌ Ошибка Sticker Gallery API при запросе '{}': status={}, body={}",
                    name, e.getStatusCode(), e.getResponseBodyAsString());
            throw new BotException("Sticker Gallery API responded with error status " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            LOGGER.error("❌ Сетевая ошибка Sticker Gallery API при запросе '{}': {}", name, e.getMessage());
            throw new BotException("Failed to call Sticker Gallery API", e);
        }
    }

    public StickerSetResponse getStickerSetById(Long id) {
        String url = Objects.requireNonNull(properties.getStickerSetByIdUrl(id));
        LOGGER.debug("🔍 Получаем стикерсет по ID {} через Sticker Gallery API", id);

        try {
            ResponseEntity<StickerSetResponse> response = restTemplate.getForEntity(url, StickerSetResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            LOGGER.warn("⚠️ Sticker Gallery API вернул неожиданный ответ при запросе ID {}: status={}, body={}",
                    id, response.getStatusCode(), response.getBody());
            return null;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.debug("ℹ️ Sticker Gallery API: стикерсет с ID {} не найден", id);
                return null;
            }

            LOGGER.error("❌ Ошибка Sticker Gallery API при запросе ID {}: status={}, body={}",
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            throw new BotException("Sticker Gallery API responded with error status " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            LOGGER.error("❌ Сетевая ошибка Sticker Gallery API при запросе ID {}: {}", id, e.getMessage());
            throw new BotException("Failed to call Sticker Gallery API", e);
        }
    }

    public StickerSetPageResponse getStickerSets(int page, int size, String sort, String direction,
                                                 String categoryKeys, Boolean officialOnly,
                                                 Long authorId, Boolean hasAuthorOnly, Boolean likedOnly) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Objects.requireNonNull(properties.getStickerSetsUrl()))
                .queryParam("page", page)
                .queryParam("size", size);

        if (sort != null && !sort.isBlank()) {
            builder.queryParam("sort", sort);
        }
        if (direction != null && !direction.isBlank()) {
            builder.queryParam("direction", direction);
        }
        if (categoryKeys != null && !categoryKeys.isBlank()) {
            builder.queryParam("categoryKeys", categoryKeys);
        }
        if (officialOnly != null) {
            builder.queryParam("officialOnly", officialOnly);
        }
        if (authorId != null) {
            builder.queryParam("authorId", authorId);
        }
        if (hasAuthorOnly != null) {
            builder.queryParam("hasAuthorOnly", hasAuthorOnly);
        }
        if (likedOnly != null) {
            builder.queryParam("likedOnly", likedOnly);
        }

        String url = builder.toUriString();
        LOGGER.debug("📄 Получаем список стикерсетов: {}", url);

        try {
            ResponseEntity<StickerSetPageResponse> response = restTemplate.getForEntity(url, StickerSetPageResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            LOGGER.error("❌ Sticker Gallery API вернул неожиданный ответ при запросе списка: status={}, body={}",
                    response.getStatusCode(), response.getBody());
            throw new BotException("Sticker Gallery API responded unexpectedly for sticker set list");
        } catch (RestClientResponseException e) {
            LOGGER.error("❌ Ошибка Sticker Gallery API при запросе списка стикерсетов: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new BotException("Sticker Gallery API responded with error status " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            LOGGER.error("❌ Сетевая ошибка Sticker Gallery API при запросе списка стикерсетов: {}", e.getMessage());
            throw new BotException("Failed to call Sticker Gallery API", e);
        }
    }
}

