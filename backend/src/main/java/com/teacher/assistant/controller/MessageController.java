package com.teacher.assistant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacher.assistant.config.JwtConfig;
import com.teacher.assistant.config.ResultVO;
import com.teacher.assistant.entity.Message;
import com.teacher.assistant.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 消息控制器
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@RestController
@RequestMapping("/message")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ResultVO<Message> sendMessage(@RequestBody Message message) {
        try {
            message.setCreateTime(LocalDateTime.now());
            message.setStatus(1); // 已发送
            boolean result = messageService.save(message);
            if (result) {
                return ResultVO.success("发送成功", message);
            }
            return ResultVO.error("发送失败");
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/history/{userId1}/{userId2}")
    public ResultVO<Page<Message>> getHistory(
            @PathVariable Long userId1,
            @PathVariable Long userId2,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "20") Long size,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // 验证用户身份
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                return ResultVO.error("未提供认证信息");
            }

            String token = authHeader.substring(7);
            if (!jwtConfig.validateToken(token)) {
                return ResultVO.error("Token无效或已过期");
            }

            Long userId = jwtConfig.getUserIdFromToken(token);

            // 检查权限（只能查看自己的聊天记录）
            if (!userId.equals(userId1) && !userId.equals(userId2)) {
                return ResultVO.error("无权限查看该聊天记录");
            }

            Page<Message> history = messageService.getChatHistory(userId1, userId2, current, size);
            return ResultVO.success(history);

        } catch (Exception e) {
            log.error("获取聊天记录失败", e);
            return ResultVO.error(e.getMessage());
        }
    }
}
