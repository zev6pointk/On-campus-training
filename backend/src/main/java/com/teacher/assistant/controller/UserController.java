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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    public ResultVO<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证Token
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return ResultVO.error("未提供认证信息");
            }

            String token = authHeader.substring(7);
            if (!jwtConfig.validateToken(token)) {
                return ResultVO.error("Token无效或已过期");
            }

            // 验证文件
            if (file.isEmpty()) {
                return ResultVO.error("请选择要上传的文件");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                return ResultVO.error("仅支持 JPG、PNG 格式的图片");
            }

            // 验证文件大小 (2MB)
            long maxSize = 2 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return ResultVO.error("文件大小不能超过 2MB");
            }

            // 获取用户信息
            Long currentUserId = jwtConfig.getUserIdFromToken(token);
            if (!currentUserId.equals(userId)) {
                return ResultVO.error("只能修改自己的头像");
            }

            // 创建上传目录
            String uploadDir = "/tmp/uploads/avatars";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = userId + "_" + System.currentTimeMillis() + extension;
            Path filePath = uploadPath.resolve(filename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 更新用户头像URL
            User user = userService.getById(userId);
            if (user != null) {
                String avatarUrl = "/api/file/avatar/" + filename;
                user.setAvatar(avatarUrl);
                userService.updateById(user);
                log.info("用户 {} 头像上传成功: {}", user.getUsername(), avatarUrl);
                return ResultVO.success("头像上传成功", avatarUrl);
            }

            return ResultVO.error("用户不存在");
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return ResultVO.error("头像上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("头像上传异常", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ResultVO<String> changePassword(
            @RequestBody Map<String, String> passwordData,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证Token
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return ResultVO.error("未提供认证信息");
            }

            String token = authHeader.substring(7);
            if (!jwtConfig.validateToken(token)) {
                return ResultVO.error("Token无效或已过期");
            }

            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");

            if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
                return ResultVO.error("请提供完整的密码信息");
            }

            if (newPassword.length() < 6 || newPassword.length() > 20) {
                return ResultVO.error("新密码长度应在6-20个字符之间");
            }

            Long userId = jwtConfig.getUserIdFromToken(token);
            User user = userService.getById(userId);

            if (user == null) {
                return ResultVO.error("用户不存在");
            }

            // 验证当前密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResultVO.error("当前密码不正确");
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updateById(user);

            log.info("用户 {} 密码修改成功", user.getUsername());
            return ResultVO.success("密码修改成功");
        } catch (Exception e) {
            log.error("密码修改失败", e);
            return ResultVO.error(e.getMessage());
        }
    }
}
