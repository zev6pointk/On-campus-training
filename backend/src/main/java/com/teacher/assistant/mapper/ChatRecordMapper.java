package com.teacher.assistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teacher.assistant.entity.ChatRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对话记录Mapper接口
 *
 * @author teacher
 * @since 2025-10-28
 */
@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

}
