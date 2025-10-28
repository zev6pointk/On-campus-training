package com.teacher.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话记录实体类
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("chat_record")
public class ChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对话ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 消息类型 1-用户消息 2-AI回复
     */
    @TableField("message_type")
    private Integer messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 对话上下文ID
     */
    @TableField("context_id")
    private Long contextId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
