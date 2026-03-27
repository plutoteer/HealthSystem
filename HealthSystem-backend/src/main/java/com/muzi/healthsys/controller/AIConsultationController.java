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
 *
 * @author MuZi
 * @since 2025-01-XX
 */
@RestController
@RequestMapping("/ai")
public class AIConsultationController {
    
    @Autowired
    private IAIConsultationService aiConsultationService;
    
    /**
     * AI咨询接口
     * @param request 请求参数，包含question字段
     * @return 回答内容
     */
    @PostMapping("/consult")
    public Unification<String> consult(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = aiConsultationService.getAnswer(question);
        return Unification.success(answer, "咨询成功");
    }
    
    /**
     * 获取所有问答列表（用于管理）
     * @return 问答列表
     */
    @GetMapping("/qa/list")
    public Unification<List<AIConsultation>> getAllQA() {
        List<AIConsultation> qaList = aiConsultationService.getAllQA();
        return Unification.success(qaList, "查询成功");
    }
    
    /**
     * 分页查询AI咨询问题列表（用于管理页面）
     * @param keywords 关键词（可选）
     * @param category 分类（可选）
     * @param question 问题（可选）
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/qa/page")
    public Unification<Map<String, Object>> getQAPage(
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "question", required = false) String question,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize) {
        
        // 构造查询条件
        LambdaQueryWrapper<AIConsultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(keywords), AIConsultation::getKeywords, keywords);
        wrapper.eq(StringUtils.hasLength(category), AIConsultation::getCategory, category);
        wrapper.like(StringUtils.hasLength(question), AIConsultation::getQuestion, question);
        wrapper.orderByDesc(AIConsultation::getPriority);
        wrapper.orderByDesc(AIConsultation::getId);
        
        // 分页查询
        Page<AIConsultation> page = new Page<>(pageNo, pageSize);
        aiConsultationService.page(page, wrapper);
        
        // 将查询结果封装到Map中返回
        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());
        return Unification.success(data);
    }
    
    /**
     * 新增AI咨询问题
     * @param aiQA AI咨询问题对象
     * @return 操作结果
     */
    @PostMapping("/qa")
    public Unification<?> addQA(@RequestBody AIConsultation aiQA) {
        boolean result = aiConsultationService.save(aiQA);
        if (result) {
            return Unification.success("新增成功");
        } else {
            return Unification.fail("新增失败");
        }
    }
    
    /**
     * 修改AI咨询问题
     * @param aiQA AI咨询问题对象
     * @return 操作结果
     */
    @PutMapping("/qa")
    public Unification<?> updateQA(@RequestBody AIConsultation aiQA) {
        boolean result = aiConsultationService.updateById(aiQA);
        if (result) {
            return Unification.success("修改成功");
        } else {
            return Unification.fail("修改失败");
        }
    }
    
    /**
     * 根据ID获取AI咨询问题
     * @param id 问题ID
     * @return AI咨询问题对象
     */
    @GetMapping("/qa/{id}")
    public Unification<AIConsultation> getQAById(@PathVariable("id") Integer id) {
        AIConsultation aiQA = aiConsultationService.getById(id);
        return Unification.success(aiQA);
    }
    
    /**
     * 根据ID删除AI咨询问题
     * @param id 问题ID
     * @return 操作结果
     */
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

