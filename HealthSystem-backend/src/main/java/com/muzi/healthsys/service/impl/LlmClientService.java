package com.muzi.healthsys.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dify 调用客户端
 */
@Service
public class LlmClientService {

    @Value("${ai.dify.enabled:false}")
    private boolean enabled;

    @Value("${ai.dify.base-url:}")
    private String baseUrl;

    @Value("${ai.dify.api-key:}")
    private String apiKey;

    @Value("${ai.dify.timeout-ms:45000}")
    private Integer timeoutMs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> enhancePolicyAnswer(String question,
                                                   String fallbackAnswer,
                                                   List<Map<String, String>> citations,
                                                   String conversationId,
                                                   String userId) {
        Map<String, Object> out = new HashMap<>();
        out.put("reply", fallbackAnswer);
        out.put("provider", "rule");
        out.put("conversationId", conversationId == null ? "" : conversationId);

        if (!enabled || isBlank(baseUrl) || isBlank(apiKey)) {
            return out;
        }

        try {
            String endpoint = baseUrl.endsWith("/") ? (baseUrl + "chat-messages") : (baseUrl + "/chat-messages");

            StringBuilder evidence = new StringBuilder();
            if (citations != null) {
                for (Map<String, String> c : citations) {
                    evidence.append("- 标题: ").append(c.getOrDefault("title", "")).append("\n")
                            .append("  摘要: ").append(c.getOrDefault("summary", "")).append("\n")
                            .append("  链接: ").append(c.getOrDefault("url", "")).append("\n");
                }
            }

            String query = "你是校医院医保政策助手。只根据给定证据回答，不得编造。"
                    + "\n用户问题：" + question
                    + "\n\n政策证据：\n" + evidence
                    + "\n\n请按结构输出：结论、适用条件、办理建议。证据不足请明确说明。";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("inputs", new HashMap<>());
            body.put("query", query);
            body.put("response_mode", "blocking");
            body.put("conversation_id", conversationId == null ? "" : conversationId);
            body.put("user", isBlank(userId) ? "health-user" : userId);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String answer = root.path("answer").asText("");
                String cid = root.path("conversation_id").asText(out.get("conversationId").toString());
                if (!isBlank(answer)) {
                    out.put("reply", answer);
                    out.put("provider", "hybrid");
                    out.put("conversationId", cid);
                }
            }
        } catch (Exception ignored) {
            // 保持 rule 降级结果
        }

        return out;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
