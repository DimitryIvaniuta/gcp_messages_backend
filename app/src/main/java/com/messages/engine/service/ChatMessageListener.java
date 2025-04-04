package com.messages.engine.service;

import com.messages.engine.config.RabbitMQConfig;
import com.messages.engine.dto.ChatMessage;
import com.messages.engine.dto.MessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Listener service that receives chat messages from RabbitMQ,
 * persists them, performs additional processing, and forwards them via WebSocket.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener {

    /** Template used to send messages over WebSocket. */
    private final SimpMessagingTemplate messagingTemplate;

    /** Service used to persist messages to the database. */
    private final MessageService messageService;

    /**
     * Listens for chat messages on the RabbitMQ chat queue.
     * Persists the message and then broadcasts it to the WebSocket topic.
     *
     * @param message the received chat message.
     */
    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void receiveMessage(ChatMessage message) {
        // Persist the message by converting ChatMessage into a MessageRequest
        MessageRequest request =
                new MessageRequest(
                        message.content(),
                        message.userId(),
                        message.conversationId()
                );

        messageService.createMessage(request);

        // Perform additional processing (e.g., logging, notifications, etc.)
        log.info("Persisted chat message: {}", message);

        // Forward the message via WebSocket to all subscribers
        messagingTemplate.convertAndSend("/topic/public", message);
    }

}
