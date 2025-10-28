package com.teacher.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息实体类
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 接收者ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 群聊ID (私聊为null)
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * 消息类型 1-文本 2-图片 3-文件
     */
    @TableField("message_type")
    private Integer messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 文件URL (图片/文件类型时使用)
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 消息状态 0-发送中 1-已发送 2-已读
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
