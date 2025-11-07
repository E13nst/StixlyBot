package com.example.smily_bot.service.external.stickergallery;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Request payload for creating a sticker set via Sticker Gallery API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StickerSetCreateRequest {

    private Long userId;
    private String title;
    private String name;
    private List<String> categoryKeys;

    public StickerSetCreateRequest() {
        // Jackson constructor
    }

    public StickerSetCreateRequest(Long userId, String title, String name, List<String> categoryKeys) {
        this.userId = userId;
        this.title = title;
        this.name = name;
        setCategoryKeys(categoryKeys);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategoryKeys() {
        return categoryKeys;
    }

    public void setCategoryKeys(List<String> categoryKeys) {
        if (categoryKeys == null || categoryKeys.isEmpty()) {
            this.categoryKeys = Collections.emptyList();
        } else {
            this.categoryKeys = new ArrayList<>(categoryKeys);
        }
    }
}

