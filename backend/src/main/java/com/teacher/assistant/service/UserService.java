package com.teacher.assistant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teacher.assistant.entity.User;

/**
 * 用户服务接口
 *
 * @author teacher
 * @since 2025-10-28
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);
}
