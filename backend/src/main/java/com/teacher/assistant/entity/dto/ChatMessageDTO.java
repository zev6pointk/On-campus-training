package com.teacher.assistant.entity.dto;

import lombok.Data;

/**
 * 聊天消息DTO
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
public class ChatMessageDTO {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息内容
     */
    private String message;
}
