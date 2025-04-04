package com.messages.engine.controller;

import com.messages.engine.dto.ChatMessage;
import com.messages.engine.service.ChatMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for handling incoming chat messages.
 * <p>
 * When a message is received on the "/app/chat.sendMessage" endpoint, the
 * message is delegated to the {@link ChatMessageHandler} for processing. The
 * handler performs message type–based logic (persisting messages, managing
 * JOIN/LEAVE events, etc.) and publishes the processed message via RabbitMQ.
 * The actual broadcast to subscribers is handled asynchronously by a RabbitMQ listener.
 * </p>
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    /** Handler that processes incoming chat messages. */
    private final ChatMessageHandler chatMessageHandler;

    /**
     * Receives a chat message from a WebSocket client and processes it.
     * <p>
     * The method does not block; processing is offloaded to a custom TaskExecutor.
     * </p>
     *
     * @param chatMessage the incoming chat message; its type is an enum {@code ChatMessageType}.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        log.info("Received chat message: {}", chatMessage);
        // Fire and forget asynchronous processing.
        chatMessageHandler.handleChatMessage(chatMessage);
    }

}
