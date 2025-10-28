package com.teacher.assistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teacher.assistant.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息Mapper接口
 *
 * @author teacher
 * @since 2025-10-28
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}
