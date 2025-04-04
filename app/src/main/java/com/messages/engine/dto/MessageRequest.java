package com.messages.engine.dto;

import lombok.Builder;

/**
 * Immutable DTO for creating or updating a message.
 *
 * @param content        The content of the message.
 * @param userId         The ID of the user sending the message.
 * @param conversationId The ID of the conversation the message belongs to.
 */
public record MessageRequest(String content, Long userId, Long conversationId) { }
