package com.muzi.healthsys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muzi.Data_unification.Unification;
import com.muzi.healthsys.entity.AIConsultation;
import com.muzi.healthsys.service.IAIConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI健康咨询Controller
 */
@RestController
@RequestMapping("/ai")
public class AIConsultationController {

    @Autowired
    private IAIConsultationService aiConsultationService;

    /**
     * 兼容旧接口（单轮）
     */
    @PostMapping("/consult")
    public Unification<String> consult(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = aiConsultationService.getAnswer(question);
        return Unification.success(answer, "咨询成功");
    }

    /**
     * 创建会话
     */
    @PostMapping("/chat/start")
    public Unification<Map<String, Object>> startChat(@RequestBody(required = false) Map<String, Object> request) {
        Integer userId = null;
        if (request != null && request.get("userId") != null) {
            try {
                userId = Integer.parseInt(String.valueOf(request.get("userId")));
            } catch (Exception ignored) {
            }
        }
        String sessionId = aiConsultationService.startSession(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", sessionId);
        data.put("welcome", "您好，我是AI健康顾问。您可以咨询症状分诊或医保报销政策。");
        return Unification.success(data, "会话创建成功");
    }

    /**
     * 多轮对话（结构化问诊 + 政策导航）
     */
    @PostMapping("/chat/message")
    public Unification<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String sessionId = request.get("sessionId") == null ? null : String.valueOf(request.get("sessionId"));
        String question = request.get("question") == null ? null : String.valueOf(request.get("question"));

        Integer userId = null;
        if (request.get("userId") != null) {
            try {
                userId = Integer.parseInt(String.valueOf(request.get("userId")));
            } catch (Exception ignored) {
            }
        }

        if (!StringUtils.hasLength(sessionId)) {
            sessionId = aiConsultationService.startSession(userId);
        }

        Map<String, Object> data = aiConsultationService.chat(sessionId, userId, question);
        data.put("sessionId", sessionId);
        return Unification.success(data, "咨询成功");
    }

    @GetMapping("/qa/list")
    public Unification<List<AIConsultation>> getAllQA() {
        List<AIConsultation> qaList = aiConsultationService.getAllQA();
        return Unification.success(qaList, "查询成功");
    }

    @GetMapping("/qa/page")
    public Unification<Map<String, Object>> getQAPage(
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "question", required = false) String question,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize) {

        LambdaQueryWrapper<AIConsultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(keywords), AIConsultation::getKeywords, keywords);
        wrapper.eq(StringUtils.hasLength(category), AIConsultation::getCategory, category);
        wrapper.like(StringUtils.hasLength(question), AIConsultation::getQuestion, question);
        wrapper.orderByDesc(AIConsultation::getPriority);
        wrapper.orderByDesc(AIConsultation::getId);

        Page<AIConsultation> page = new Page<>(pageNo, pageSize);
        aiConsultationService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());
        return Unification.success(data);
    }

    @PostMapping("/qa")
    public Unification<?> addQA(@RequestBody AIConsultation aiQA) {
        boolean result = aiConsultationService.save(aiQA);
        if (result) {
            return Unification.success("新增成功");
        } else {
            return Unification.fail("新增失败");
        }
    }

    @PutMapping("/qa")
    public Unification<?> updateQA(@RequestBody AIConsultation aiQA) {
        boolean result = aiConsultationService.updateById(aiQA);
        if (result) {
            return Unification.success("修改成功");
        } else {
            return Unification.fail("修改失败");
        }
    }

    @GetMapping("/qa/{id}")
    public Unification<AIConsultation> getQAById(@PathVariable("id") Integer id) {
        AIConsultation aiQA = aiConsultationService.getById(id);
        return Unification.success(aiQA);
    }

    @DeleteMapping("/qa/{id}")
    public Unification<?> deleteQAById(@PathVariable("id") Integer id) {
        boolean result = aiConsultationService.removeById(id);
        if (result) {
            return Unification.success("删除成功");
        } else {
            return Unification.fail("删除失败");
        }
    }
}
