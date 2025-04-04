package com.messages.engine.service;

import com.messages.engine.dto.ChatMessage;
import com.messages.engine.dto.MessageRequest;
import com.messages.engine.exception.ResourceNotFoundException;
import com.messages.engine.model.Conversation;
import com.messages.engine.model.User;
import com.messages.engine.repository.ConversationRepository;
import com.messages.engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service class for processing different types of chat messages.
 */
@Service
@RequiredArgsConstructor
public class MessageTypeProcessor {

    private final MessageService messageService;

    private final UserRepository userRepository;

    private final ConversationRepository conversationRepository;

    /**
     * Asynchronously processes the given chat message based on its type.
     *
     * @param chatMessage the chat message to process
     * @return a string result describing the outcome
     */
    public CompletableFuture<String> processMessageType(ChatMessage chatMessage) {
        String result = switch (chatMessage.type()) {
            case CHAT  -> processChat(chatMessage);
            case JOIN -> processJoin(chatMessage);
            case LEAVE -> processLeave(chatMessage);
        };
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Processes a chat message by persisting it using the MessageService.
     *
     * @param chatMessage the chat message with type "CHAT"
     * @return a string result indicating successful persistence
     */
    private String processChat(ChatMessage chatMessage) {
        var request = new MessageRequest(
                chatMessage.content(),
                chatMessage.userId(),
                chatMessage.conversationId()
        );
        messageService.createMessage(request);
        return "Chat message processed and persisted.";
    }

    /**
     * Processes a join event by adding the user to the conversation.
     *
     * @param chatMessage the chat message with type "JOIN"
     * @return a string result indicating the join outcome
     * @throws ResourceNotFoundException if the user or conversation is not found
     */
    private String processJoin(ChatMessage chatMessage) {
        var userOpt = userRepository.findById(chatMessage.userId());
        var convOpt = conversationRepository.findById(chatMessage.conversationId());
        if (userOpt.isPresent() && convOpt.isPresent()) {
            User user = userOpt.get();
            Conversation conversation = convOpt.get();
            if (conversation.getParticipants().add(user)) {
                conversationRepository.save(conversation);
                return "User " + user.getUserName() + " joined conversation " + conversation.getId() + ".";
            } else {
                return "User " + user.getUserName() + " is already part of conversation " + conversation.getId() + ".";
            }
        }
        throw new ResourceNotFoundException("JOIN event", "userId/conversationId",
                chatMessage.userId() + "/" + chatMessage.conversationId());
    }

    /**
     * Processes a leave event by removing the user from the conversation.
     *
     * @param chatMessage the chat message with type "LEAVE"
     * @return a string result indicating the leave outcome
     * @throws ResourceNotFoundException if the user or conversation is not found
     */
    private String processLeave(ChatMessage chatMessage) {
        var userOpt = userRepository.findById(chatMessage.userId());
        var convOpt = conversationRepository.findById(chatMessage.conversationId());
        if (userOpt.isPresent() && convOpt.isPresent()) {
            User user = userOpt.get();
            Conversation conversation = convOpt.get();
            boolean removed = conversation.getParticipants().removeIf(u -> u.getId().equals(user.getId()));
            if (removed) {
                conversationRepository.save(conversation);
                return "User " + user.getUserName() + " left conversation " + conversation.getId() + ".";
            } else {
                return "User " + user.getUserName() + " was not part of conversation " + conversation.getId() + ".";
            }
        }
        throw new ResourceNotFoundException("LEAVE event", "userId/conversationId",
                chatMessage.userId() + "/" + chatMessage.conversationId());
    }

}