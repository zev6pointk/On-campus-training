package com.teacher.assistant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.teacher.assistant.entity.Message;

/**
 * 消息服务接口
 *
 * @author teacher
 * @since 2025-10-28
 */
public interface MessageService extends IService<Message> {

    /**
     * 获取聊天记录
     *
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @param current 当前页
     * @param size 每页大小
     * @return 聊天记录分页
     */
    Page<Message> getChatHistory(Long userId1, Long userId2, Long current, Long size);
}
