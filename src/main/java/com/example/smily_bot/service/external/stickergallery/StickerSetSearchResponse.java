package com.example.smily_bot.service.external.stickergallery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response DTO for sticker set search endpoint.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StickerSetSearchResponse {

    private boolean exists;

    public StickerSetSearchResponse() {
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}

