package com.teacher.assistant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacher.assistant.config.JwtConfig;
import com.teacher.assistant.config.ResultVO;
import com.teacher.assistant.entity.User;
import com.teacher.assistant.entity.dto.LoginDTO;
import com.teacher.assistant.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResultVO<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            String token = jwtConfig.generateToken(user.getUsername(), user.getId(), user.getUserType());

            Map<String, Object> result = Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "realName", user.getRealName(),
                            "userType", user.getUserType()
                    )
            );

            log.info("用户登录成功: {}", user.getUsername());
            return ResultVO.success("登录成功", result);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResultVO<User> register(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            User existUser = userService.getByUsername(user.getUsername());
            if (existUser != null) {
                return ResultVO.error("用户名已存在");
            }

            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setStatus(1); // 默认启用

            boolean result = userService.save(user);
            if (result) {
                user.setPassword(null); // 不返回密码
                return ResultVO.success("注册成功", user);
            }
            return ResultVO.error("注册失败");
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResultVO<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return ResultVO.error("未提供认证信息");
            }

            String token = authHeader.substring(7);
            if (!jwtConfig.validateToken(token)) {
                return ResultVO.error("Token无效或已过期");
            }

            Long userId = jwtConfig.getUserIdFromToken(token);
            User user = userService.getById(userId);
            if (user != null) {
                user.setPassword(null);
                return ResultVO.success(user);
            }
            return ResultVO.error("用户不存在");
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 用户列表（分页）
     */
    @GetMapping("/users")
    public ResultVO<Page<User>> getUsers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer userType) {

        try {
            Page<User> page = new Page<>(current, size);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

            if (StringUtils.hasText(username)) {
                wrapper.like(User::getUsername, username);
            }
            if (userType != null) {
                wrapper.eq(User::getUserType, userType);
            }

            wrapper.orderByDesc(User::getCreateTime);
            Page<User> result = userService.page(page, wrapper);

            // 隐藏密码
            result.getRecords().forEach(u -> u.setPassword(null));

            return ResultVO.success(result);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users/{id}")
    public ResultVO<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            boolean result = userService.updateById(user);
            if (result) {
                User updated = userService.getById(id);
                updated.setPassword(null);
                return ResultVO.success("更新成功", updated);
            }
            return ResultVO.error("更新失败");
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ResultVO<String> deleteUser(@PathVariable Long id) {
        try {
            boolean result = userService.removeById(id);
            if (result) {
                return ResultVO.success("删除成功");
            }
            return ResultVO.error("删除失败");
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResultVO.error(e.getMessage());
        }
    }
}
