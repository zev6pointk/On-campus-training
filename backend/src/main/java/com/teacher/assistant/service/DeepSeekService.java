package com.teacher.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacher.assistant.entity.dto.DeepSeekResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek服务
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@Service
public class DeepSeekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.base-url}")
    private String baseUrl;

    @Value("${deepseek.api.model}")
    private String model;

    @Value("${deepseek.api.max-tokens}")
    private Integer maxTokens;

    @Value("${deepseek.api.temperature}")
    private Double temperature;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DeepSeekService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 发送消息给DeepSeek
     */
    public String sendMessage(String message, List<Map<String, String>> history) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);

            // 构建消息历史
            List<Map<String, String>> messages = new ArrayList<>();
            if (history != null) {
                messages.addAll(history);
            }
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/chat/completions",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                DeepSeekResponseDTO result = objectMapper.readValue(response.getBody(), DeepSeekResponseDTO.class);
                if (result.getChoices() != null && !result.getChoices().isEmpty()) {
                    return result.getChoices().get(0).getMessage().getContent();
                }
            }

            return "抱歉，我现在无法回答这个问题。";

        } catch (Exception e) {
            log.error("调用DeepSeek API失败", e);
            return "服务暂时不可用，请稍后再试。";
        }
    }
}
