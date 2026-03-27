package com.muzi.healthsys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muzi.healthsys.entity.AIConsultation;

import java.util.List;

/**
 * AI健康咨询Service接口
 *
 * @author MuZi
 * @since 2025-01-XX
 */
public interface IAIConsultationService extends IService<AIConsultation> {
    
    /**
     * 根据用户问题匹配最佳答案
     * @param question 用户问题
     * @return 最佳答案
     */
    String getAnswer(String question);
    
    /**
     * 获取所有问答列表（用于管理）
     * @return 问答列表
     */
    List<AIConsultation> getAllQA();
}

