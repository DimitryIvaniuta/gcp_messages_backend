package com.messages.engine.service;

import com.messages.engine.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Handler for incoming chat messages. This class delegates the processing of
 * the message to the {@link MessageTypeProcessor} and logs the outcome.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageHandler {

    private final MessageTypeProcessor messageTypeProcessor;

    /**
     * Processes the given chat message asynchronously by delegating to the
     * {@link MessageTypeProcessor} and returns a CompletableFuture that will be completed
     * with the processing outcome.
     *
     * @param chatMessage the chat message to process.
     * @return a CompletableFuture containing the result string describing the outcome.
     */
    public CompletableFuture<String> handleChatMessage(ChatMessage chatMessage) {
        return messageTypeProcessor.processMessageType(chatMessage)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error processing message of type {}: {}", chatMessage.type(), ex.getMessage(), ex);
                    } else {
                        log.info("Processed message of type {}: {}", chatMessage.type(), result);
                    }
                });
    }

}
