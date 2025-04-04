package com.messages.engine.controller;

import com.messages.engine.dto.ChatMessage;
import com.messages.engine.service.ChatMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for processing chat messages.
 * <p>
 * This controller exposes an endpoint that accepts chat messages via POST requests.
 * It delegates processing to the {@link ChatMessageHandler} which, in turn, calls the appropriate
 * business logic based on the {@link com.messages.engine.dto.ChatMessageType} of the message.
 * The outcome is then returned as a response.
 * </p>
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatMessageProcessingController {

    /**
     * Handler for processing chat messages.
     */
    private final ChatMessageHandler chatMessageHandler;

    /**
     * Processes an incoming chat message.
     *
     * @param chatMessage the chat message to process, containing type, content, userId, and conversationId.
     * @return a ResponseEntity with a string result describing the outcome of processing.
     */
    @PostMapping("/process")
    public CompletableFuture<ResponseEntity<String>> processChatMessage(@RequestBody ChatMessage chatMessage) {
        return chatMessageHandler.handleChatMessage(chatMessage)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error processing chat message: {}", ex.getMessage(), ex);
                    return ResponseEntity.status(500)
                            .body("Error processing message: " + ex.getMessage());
                });
    }
}
