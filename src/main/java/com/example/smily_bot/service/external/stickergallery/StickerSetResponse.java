package com.example.smily_bot.service.external.stickergallery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * Response representation of a sticker set returned by Sticker Gallery API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StickerSetResponse {

    private Long id;
    private Long userId;
    private String title;
    private String name;
    private LocalDateTime createdAt;

    public StickerSetResponse() {
        // Jackson constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

