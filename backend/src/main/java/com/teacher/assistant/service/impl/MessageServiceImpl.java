package com.teacher.assistant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teacher.assistant.entity.Message;
import com.teacher.assistant.mapper.MessageMapper;
import com.teacher.assistant.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * 消息服务实现
 *
 * @author teacher
 * @since 2025-10-28
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public Page<Message> getChatHistory(Long userId1, Long userId2, Long current, Long size) {
        Page<Message> page = new Page<>(current, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();

        // 查询两人之间的所有消息
        wrapper.and(w -> w
                .eq(Message::getSenderId, userId1).eq(Message::getReceiverId, userId2)
                .or()
                .eq(Message::getSenderId, userId2).eq(Message::getReceiverId, userId1)
        );

        wrapper.orderByAsc(Message::getCreateTime);
        return page(page, wrapper);
    }
}
