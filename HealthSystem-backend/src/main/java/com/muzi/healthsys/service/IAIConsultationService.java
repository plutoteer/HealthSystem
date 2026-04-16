package com.muzi.healthsys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muzi.healthsys.entity.AIConsultation;

import java.util.List;
import java.util.Map;

/**
 * AI健康咨询Service接口
 */
public interface IAIConsultationService extends IService<AIConsultation> {

    /**
     * 兼容旧接口：单轮问答
     */
    String getAnswer(String question);

    /**
     * 创建会话
     */
    String startSession(Integer userId);

    /**
     * 多轮对话（结构化引导 + 政策导航）
     */
    Map<String, Object> chat(String sessionId, Integer userId, String question);

    /**
     * 获取所有问答列表（用于管理）
     */
    List<AIConsultation> getAllQA();
}
