package com.example.smily_bot.model.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickerSet {

    private Long id;
    private Long userId;
    private String title;
    private String name;
    private LocalDateTime createdAt;
} 