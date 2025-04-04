package com.messages.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MessageResponse {

    private Long id;

    private String content;

    private Long userId;

    private Long conversationId;

}
