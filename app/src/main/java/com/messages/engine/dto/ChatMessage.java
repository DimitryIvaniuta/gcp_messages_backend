package com.messages.engine.dto;

/**
 * Immutable Data Transfer Object for chat messages sent over WebSocket and RabbitMQ.
 *
 * @param type           The type of the message (e.g., CHAT, JOIN, LEAVE)..
 * @param content        The content of the chat message.
 * @param userId         The ID of the user sending the message.
 * @param conversationId The ID of the conversation to which the message belongs.
 */
public record ChatMessage(ChatMessageType type, String content, Long userId, Long conversationId) { }
