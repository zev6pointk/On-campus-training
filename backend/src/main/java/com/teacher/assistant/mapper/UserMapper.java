package com.teacher.assistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teacher.assistant.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author teacher
 * @since 2025-10-28
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
