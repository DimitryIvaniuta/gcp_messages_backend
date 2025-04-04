package com.messages.engine.service;

import com.messages.engine.dto.MessageRequest;
import com.messages.engine.dto.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse createMessage(MessageRequest request);

    MessageResponse getMessageById(Long id);

    List<MessageResponse> getAllMessages();

    MessageResponse updateMessage(Long id, MessageRequest request);

    void deleteMessage(Long id);

}
