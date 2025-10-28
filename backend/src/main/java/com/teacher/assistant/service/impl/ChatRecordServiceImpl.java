package com.teacher.assistant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teacher.assistant.entity.ChatRecord;
import com.teacher.assistant.mapper.ChatRecordMapper;
import com.teacher.assistant.service.ChatRecordService;
import org.springframework.stereotype.Service;

/**
 * 对话记录服务实现
 *
 * @author teacher
 * @since 2025-10-28
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord> implements ChatRecordService {

}
