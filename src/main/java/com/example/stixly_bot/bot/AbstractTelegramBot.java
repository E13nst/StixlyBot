package com.example.stixly_bot.bot;

import com.example.stixly_bot.config.StickerBotProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractTelegramBot extends TelegramLongPollingBot {
    protected final StickerBotProperties botProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTelegramBot.class);

    public AbstractTelegramBot(StickerBotProperties botProperties) {
        this.botProperties = botProperties;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public abstract void onUpdateReceived(Update update);

    protected String getConversationId(Long chatId) {
        return getBotUsername() + ":" + chatId;
    }

    protected void sendWithLogging(SendMessage message) {
        try {
            var result = execute(message);
            if (result != null) {
                LOGGER.info("✅ Message sent successfully | Chat: {} | MessageId: {} | Text: '{}'", 
                    message.getChatId(), result.getMessageId(), truncateText(message.getText(), 100));
            } else {
                LOGGER.warn("⚠️ Message sent but result is null | Chat: {} | Text: '{}'", 
                    message.getChatId(), truncateText(message.getText(), 100));
            }
        } catch (TelegramApiException e) {
            LOGGER.error("❌ Failed to send message | Chat: {} | Error: {}", 
                message.getChatId(), e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("❌ Unexpected error sending message | Chat: {} | Error: {}", 
                message.getChatId(), e.getMessage(), e);
        }
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return null;
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    // Пример для будущих ботов:
    // protected void handleReplyToBotMessage(Message message, String conversationId) {
    //     messageHandlerService.handleReplyToBotMessage(message, conversationId, botEntity);
    // }
    // protected void handlePersonalMessage(Message message, String conversationId) {
    //     messageHandlerService.handlePersonalMessage(message, conversationId, botEntity);
    // }

    // Можно добавить общие методы для всех ботов, например, логирование, отправку сообщений и т.д.
} 