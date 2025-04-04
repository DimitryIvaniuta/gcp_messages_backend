package com.messages.engine.service;

import com.messages.engine.dto.MessageRequest;
import com.messages.engine.dto.MessageResponse;
import com.messages.engine.exception.ResourceNotFoundException;
import com.messages.engine.model.Conversation;
import com.messages.engine.model.Message;
import com.messages.engine.model.User;
import com.messages.engine.repository.ConversationRepository;
import com.messages.engine.repository.MessageRepository;
import com.messages.engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of MessageService for handling message operations.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final String SOURCE_NAME = "Message";

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    /**
     * Creates a new message based on the given request.
     *
     * @param request the MessageRequest record containing content, userId, and conversationId.
     * @return a MessageResponse record with details of the persisted message.
     * @throws ResourceNotFoundException if the user or conversation does not exist.
     */
    @Override
    public MessageResponse createMessage(MessageRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));
        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.conversationId()));

        Message message = new Message();
        message.setContent(request.content());
        message.setUser(user);
        message.setConversation(conversation);

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    /**
     * Retrieves a message by its ID.
     *
     * @param id the message ID.
     * @return a MessageResponse record representing the message.
     * @throws ResourceNotFoundException if the message is not found.
     */
    @Override
    public MessageResponse getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SOURCE_NAME, "id", id));
        return mapToResponse(message);
    }

    /**
     * Retrieves all messages.
     *
     * @return a list of MessageResponse records.
     */
    @Override
    public List<MessageResponse> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Updates an existing message.
     *
     * @param id      the ID of the message to update.
     * @param request the MessageRequest record with updated data.
     * @return a MessageResponse record representing the updated message.
     * @throws ResourceNotFoundException if the message, user, or conversation is not found.
     */
    @Override
    public MessageResponse updateMessage(Long id, MessageRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SOURCE_NAME, "id", id));

        message.setContent(request.content());

        Optional.ofNullable(request.userId()).ifPresent(userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            message.setUser(user);
        });

        Optional.ofNullable(request.conversationId()).ifPresent(conversationId -> {
            Conversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
            message.setConversation(conversation);
        });

        Message updated = messageRepository.save(message);
        return mapToResponse(updated);
    }

    /**
     * Deletes a message by its ID.
     *
     * @param id the ID of the message to delete.
     * @throws ResourceNotFoundException if the message is not found.
     */
    @Override
    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SOURCE_NAME, "id", id));
        messageRepository.delete(message);
    }

    /**
     * Maps a Message entity to a MessageResponse record.
     *
     * @param message the Message entity.
     * @return a MessageResponse record with details from the entity.
     */
    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .userId(message.getUser().getId())
                .conversationId(message.getConversation().getId())
//                .createdAt(message.getCreatedAt())
                .build();
    }

}

