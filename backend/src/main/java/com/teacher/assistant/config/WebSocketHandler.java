package com.teacher.assistant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket处理器
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("WebSocket连接已建立: {}", sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("收到消息: {}", payload);

        try {
            // 解析消息
            MessageDTO msg = objectMapper.readValue(payload, MessageDTO.class);

            // 处理消息类型
            switch (msg.getType()) {
                case "CHAT":
                    handleChatMessage(msg);
                    break;
                case "TYPING":
                    handleTypingMessage(msg);
                    break;
                case "PING":
                    session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
                    break;
                default:
                    log.warn("未知消息类型: {}", msg.getType());
            }
        } catch (Exception e) {
            log.error("处理消息失败", e);
            session.sendMessage(new TextMessage("{\"error\":\"消息格式错误\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        log.info("WebSocket连接已关闭: {}", sessionId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误", exception);
    }

    /**
     * 处理聊天消息
     */
    private void handleChatMessage(MessageDTO msg) throws IOException {
        // 这里可以将消息保存到数据库，然后广播给接收者
        log.info("聊天消息: from={}, to={}, content={}", msg.getFrom(), msg.getTo(), msg.getContent());

        // 发送给接收者
        broadcastToUser(msg.getTo(), msg);
    }

    /**
     * 处理输入状态消息
     */
    private void handleTypingMessage(MessageDTO msg) throws IOException {
        log.info("输入状态: user={}, typing={}", msg.getFrom(), msg.isTyping());
        broadcastToUser(msg.getTo(), msg);
    }

    /**
     * 广播消息给指定用户
     */
    public void broadcastToUser(String userId, MessageDTO message) throws IOException {
        sessions.values().forEach(session -> {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                log.error("发送消息失败", e);
            }
        });
    }

    /**
     * 消息DTO
     */
    public static class MessageDTO {
        private String type;
        private String from;
        private String to;
        private String content;
        private boolean typing;
        private long timestamp;

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
