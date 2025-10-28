package com.teacher.assistant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacher.assistant.config.JwtConfig;
import com.teacher.assistant.config.ResultVO;
import com.teacher.assistant.entity.ChatRecord;
import com.teacher.assistant.entity.dto.ChatMessageDTO;
import com.teacher.assistant.service.ChatRecordService;
import com.teacher.assistant.service.DeepSeekService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private ChatRecordService chatRecordService;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ResultVO<Map<String, Object>> sendMessage(
            @RequestBody ChatMessageDTO request,
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

            // 获取会话历史
            List<Map<String, String>> history = getChatHistory(userId, request.getSessionId());

            // 调用DeepSeek API
            String response = deepSeekService.sendMessage(request.getMessage(), history);

            // 保存对话记录
            saveChatRecord(userId, request.getSessionId(), "user", request.getMessage());
            saveChatRecord(userId, request.getSessionId(), "assistant", response);

            Map<String, Object> result = Map.of(
                    "sessionId", request.getSessionId(),
                    "message", request.getMessage(),
                    "response", response,
                    "timestamp", LocalDateTime.now()
            );

            return ResultVO.success(result);

        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取聊天历史
     */
    @GetMapping("/history/{sessionId}")
    public ResultVO<List<ChatRecord>> getHistory(
            @PathVariable String sessionId,
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

            LambdaQueryWrapper<ChatRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ChatRecord::getUserId, userId)
                   .eq(ChatRecord::getSessionId, sessionId)
                   .orderByAsc(ChatRecord::getCreateTime);

            List<ChatRecord> history = chatRecordService.list(wrapper);

            return ResultVO.success(history);

        } catch (Exception e) {
            log.error("获取聊天历史失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取用户的会话列表
     */
    @GetMapping("/sessions")
    public ResultVO<List<String>> getSessions(@RequestHeader("Authorization") String authHeader) {
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

            LambdaQueryWrapper<ChatRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ChatRecord::getUserId, userId)
                   .select(ChatRecord::getSessionId)
                   .groupBy(ChatRecord::getSessionId);

            List<ChatRecord> records = chatRecordService.list(wrapper);
            List<String> sessions = records.stream()
                    .map(ChatRecord::getSessionId)
                    .distinct()
                    .toList();

            return ResultVO.success(sessions);

        } catch (Exception e) {
            log.error("获取会话列表失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取聊天历史
     */
    private List<Map<String, String>> getChatHistory(Long userId, String sessionId) {
        LambdaQueryWrapper<ChatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatRecord::getUserId, userId)
               .eq(ChatRecord::getSessionId, sessionId)
               .orderByAsc(ChatRecord::getCreateTime)
               .last("LIMIT 20"); // 限制历史记录数量

        List<ChatRecord> records = chatRecordService.list(wrapper);
        List<Map<String, String>> history = new ArrayList<>();

        for (ChatRecord record : records) {
            Map<String, String> message = new HashMap<>();
            message.put("role", "user".equals(record.getMessageType()) ? "user" : "assistant");
            message.put("content", record.getContent());
            history.add(message);
        }

        return history;
    }

    /**
     * 保存聊天记录
     */
    private void saveChatRecord(Long userId, String sessionId, String type, String content) {
        ChatRecord record = new ChatRecord();
        record.setUserId(userId);
        record.setSessionId(sessionId);
        record.setMessageType("user".equals(type) ? 1 : 2);
        record.setContent(content);
        chatRecordService.save(record);
    }
}
