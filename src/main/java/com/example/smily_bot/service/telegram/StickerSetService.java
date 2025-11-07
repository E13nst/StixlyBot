package com.example.smily_bot.service.telegram;

import com.example.smily_bot.dto.PageRequest;
import com.example.smily_bot.dto.PageResponse;
import com.example.smily_bot.dto.StickerSetDto;
import com.example.smily_bot.model.telegram.StickerSet;
import com.example.smily_bot.service.external.stickergallery.StickerGalleryApiClient;
import com.example.smily_bot.service.external.stickergallery.StickerSetCreateRequest;
import com.example.smily_bot.service.external.stickergallery.StickerSetPageResponse;
import com.example.smily_bot.service.external.stickergallery.StickerSetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StickerSetService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StickerSetService.class);
    private final TelegramBotApiService telegramBotApiService;
    private final StickerGalleryApiClient stickerGalleryApiClient;
    
    @Autowired
    public StickerSetService(TelegramBotApiService telegramBotApiService,
                             StickerGalleryApiClient stickerGalleryApiClient) {
        this.telegramBotApiService = telegramBotApiService;
        this.stickerGalleryApiClient = stickerGalleryApiClient;
    }
    
    public StickerSet createStickerSet(Long userId, String title, String name) {
        StickerSetCreateRequest request = new StickerSetCreateRequest(userId, title, name, List.of());
        StickerSetResponse response = stickerGalleryApiClient.createStickerSet(request);

        StickerSet created = new StickerSet();
        created.setId(response.getId());
        created.setUserId(response.getUserId() != null ? response.getUserId() : userId);
        created.setTitle(response.getTitle() != null ? response.getTitle() : title);
        created.setName(response.getName() != null ? response.getName() : name);
        created.setCreatedAt(response.getCreatedAt());

        LOGGER.info("📦 Стикерпак создан через Sticker Gallery API: ID={}, Title='{}', Name='{}', UserId={}",
                created.getId(), created.getTitle(), created.getName(), created.getUserId());

        return created;
    }

    public boolean existsInStickerGallery(String name) {
        return stickerGalleryApiClient.stickerSetExistsByName(name);
    }

    @Nullable
    public StickerSet findByName(String name) {
        StickerSetResponse response = stickerGalleryApiClient.getStickerSetByName(name);
        return response != null ? mapToStickerSet(response) : null;
    }

    @Nullable
    public StickerSet findByTitle(String title) {
        StickerSetPageResponse response = stickerGalleryApiClient.getStickerSets(0, 100, "createdAt", "DESC",
                null, null, null, null, null);
        return response.getContent().stream()
                .map(this::mapToStickerSet)
                .filter(set -> set.getTitle() != null && set.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    public List<StickerSet> findByUserId(Long userId) {
        StickerSetPageResponse response = stickerGalleryApiClient.getStickerSetsByUser(userId, 0, 100, "createdAt", "DESC");
        return response.getContent().stream()
                .map(this::mapToStickerSet)
                .collect(Collectors.toList());
    }

    @Nullable
    @SuppressWarnings("null")
    public StickerSet findById(Long id) {
        StickerSetResponse response = stickerGalleryApiClient.getStickerSetById(id);
        return response != null ? mapToStickerSet(response) : null;
    }
    
    @SuppressWarnings("null")
    public List<StickerSet> findAll() {
        StickerSetPageResponse response = stickerGalleryApiClient.getStickerSets(0, 100, "createdAt", "DESC",
                null, null, null, null, null);
        return response.getContent().stream()
                .map(this::mapToStickerSet)
                .collect(Collectors.toList());
    }
    
    /**
     * Получить все стикерсеты с пагинацией и обогащением данных Bot API
     */
    @SuppressWarnings("null")
    public PageResponse<StickerSetDto> findAllWithPagination(PageRequest pageRequest) {
        LOGGER.debug("📋 Получение всех стикерсетов с пагинацией: page={}, size={}", 
                pageRequest.getPage(), pageRequest.getSize());
        StickerSetPageResponse response = stickerGalleryApiClient.getStickerSets(
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSort(),
                pageRequest.getDirection(),
                null,
                null,
                null,
                null,
                null);

        List<StickerSet> stickerSets = response.getContent().stream()
                .map(this::mapToStickerSet)
                .collect(Collectors.toList());
        List<StickerSetDto> enrichedDtos = enrichWithBotApiData(stickerSets);

        return new PageResponse<>(
                enrichedDtos,
                response.getPage(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast(),
                response.isHasNext(),
                response.isHasPrevious()
        );
    }
    
    /**
     * Получить стикерсеты пользователя с пагинацией и обогащением данных Bot API
     */
    public PageResponse<StickerSetDto> findByUserIdWithPagination(Long userId, PageRequest pageRequest) {
        LOGGER.debug("👤 Получение стикерсетов пользователя {} через Sticker Gallery API: page={}, size={}",
                userId, pageRequest.getPage(), pageRequest.getSize());

        StickerSetPageResponse response = stickerGalleryApiClient.getStickerSetsByUser(
                userId,
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSort(),
                pageRequest.getDirection());

        List<StickerSet> stickerSets = response.getContent().stream()
                .map(this::mapToStickerSet)
                .collect(Collectors.toList());
        List<StickerSetDto> dtos = enrichWithBotApiData(stickerSets);

        return new PageResponse<>(
                dtos,
                response.getPage(),
                response.getSize(),
                response.getTotalElements(),
                response.getTotalPages(),
                response.isFirst(),
                response.isLast(),
                response.isHasNext(),
                response.isHasPrevious()
        );
    }
    
    /**
     * Получить стикерсет по ID с обогащением данных Bot API
     * Если Bot API недоступен, возвращает стикерсет без обогащения
     */
    public StickerSetDto findByIdWithBotApiData(Long id) {
        LOGGER.debug("🔍 Получение стикерсета по ID {} с данными Bot API", id);
        
        StickerSet stickerSet = findById(id);
        if (stickerSet == null) {
            return null;
        }
        
        return enrichSingleStickerSetSafely(stickerSet);
    }
    
    /**
     * Получить стикерсет по имени с обогащением данных Bot API
     * Если Bot API недоступен, возвращает стикерсет без обогащения
     */
    public StickerSetDto findByNameWithBotApiData(String name) {
        LOGGER.debug("🔍 Получение стикерсета по имени '{}' с данными Bot API", name);
        
        StickerSet stickerSet = findByName(name);
        if (stickerSet == null) {
            return null;
        }
        
        return enrichSingleStickerSetSafely(stickerSet);
    }
    
    /**
     * Обогащает список стикерсетов данными из Bot API (параллельно)
     */
    private List<StickerSetDto> enrichWithBotApiData(List<StickerSet> stickerSets) {
        if (stickerSets.isEmpty()) {
            return List.of();
        }
        
        LOGGER.debug("🚀 Обогащение {} стикерсетов данными Bot API (параллельно)", stickerSets.size());
        
        // Создаем список CompletableFuture для параллельной обработки
        List<CompletableFuture<StickerSetDto>> futures = stickerSets.stream()
                .map(stickerSet -> CompletableFuture.supplyAsync(() -> enrichSingleStickerSetSafely(stickerSet)))
                .collect(Collectors.toList());
        
        // Ждем завершения всех запросов
        List<StickerSetDto> result = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        
        LOGGER.debug("✅ Обогащение завершено для {} стикерсетов", result.size());
        return result;
    }
    
    /**
     * Обогащает один стикерсет данными из Bot API (безопасно)
     * Если данные Bot API недоступны, возвращает DTO без обогащения, но не выбрасывает исключение
     */
    private StickerSetDto enrichSingleStickerSetSafely(StickerSet stickerSet) {
        StickerSetDto dto = StickerSetDto.fromEntity(stickerSet);
        
        try {
            String botApiData = telegramBotApiService.getStickerSetInfo(stickerSet.getName());
            dto.setTelegramStickerSetInfo(botApiData);
            LOGGER.debug("✅ Стикерсет '{}' обогащен данными Bot API", stickerSet.getName());
        } catch (Exception e) {
            LOGGER.warn("⚠️ Не удалось получить данные Bot API для стикерсета '{}': {} - пропускаем обогащение", 
                    stickerSet.getName(), e.getMessage());
            // Оставляем telegramStickerSetInfo = null, продолжаем обработку
            dto.setTelegramStickerSetInfo(null);
        }
        
        return dto;
    }

    private StickerSet mapToStickerSet(StickerSetResponse response) {
        StickerSet stickerSet = new StickerSet();
        stickerSet.setId(response.getId());
        stickerSet.setUserId(response.getUserId());
        stickerSet.setTitle(response.getTitle());
        stickerSet.setName(response.getName());
        stickerSet.setCreatedAt(response.getCreatedAt());
        return stickerSet;
    }
} 