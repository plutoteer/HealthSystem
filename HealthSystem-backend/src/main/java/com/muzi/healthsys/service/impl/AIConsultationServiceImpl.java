package com.muzi.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muzi.healthsys.entity.AIConsultation;
import com.muzi.healthsys.mapper.AIConsultationMapper;
import com.muzi.healthsys.service.IAIConsultationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI健康咨询Service实现类
 *
 * @author MuZi
 * @since 2025-01-XX
 */
@Service
public class AIConsultationServiceImpl extends ServiceImpl<AIConsultationMapper, AIConsultation> 
        implements IAIConsultationService {
    
    // 同义词映射表（关键词 -> 同义词列表）
    private static final Map<String, List<String>> SYNONYM_MAP = new HashMap<>();
    
    static {
        // 健身相关同义词
        SYNONYM_MAP.put("健身", Arrays.asList("运动", "锻炼", "健身", "训练", "健身运动"));
        SYNONYM_MAP.put("运动", Arrays.asList("健身", "锻炼", "运动", "训练", "健身运动"));
        SYNONYM_MAP.put("锻炼", Arrays.asList("健身", "运动", "锻炼", "训练", "健身运动"));
        
        // 减肥相关同义词
        SYNONYM_MAP.put("减肥", Arrays.asList("减重", "瘦身", "瘦下来", "减肥"));
        SYNONYM_MAP.put("减重", Arrays.asList("减肥", "瘦身", "瘦下来", "减重"));
        SYNONYM_MAP.put("瘦身", Arrays.asList("减肥", "减重", "瘦下来", "瘦身"));
        
        // 饮食相关同义词
        SYNONYM_MAP.put("饮食", Arrays.asList("营养", "健康饮食", "饮食", "膳食"));
        SYNONYM_MAP.put("营养", Arrays.asList("饮食", "健康饮食", "营养", "膳食"));
        
        // 睡眠相关同义词
        SYNONYM_MAP.put("睡眠", Arrays.asList("失眠", "睡不着", "睡眠", "休息"));
        SYNONYM_MAP.put("失眠", Arrays.asList("睡眠", "睡不着", "失眠", "休息"));
    }
    
    @Override
    public String getAnswer(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "您好，请描述您的健康问题，我会尽力为您提供建议。";
        }
        
        // 智能提取关键词：提取所有可能的2-4字词组
        Set<String> keywords = extractKeywords(question);
        
        // 查找匹配的问答
        List<AIConsultation> matchedQA = new ArrayList<>();
        Set<Integer> addedIds = new HashSet<>();
        
        // 使用提取的关键词进行匹配
        for (String keyword : keywords) {
            if (keyword.length() >= 2) { // 至少2个字符
                // 直接匹配
                List<AIConsultation> results = baseMapper.findByKeyword(keyword);
                if (results != null && !results.isEmpty()) {
                    for (AIConsultation qa : results) {
                        if (qa.getId() != null && !addedIds.contains(qa.getId())) {
                            matchedQA.add(qa);
                            addedIds.add(qa.getId());
                        }
                    }
                }
                
                // 同义词匹配
                List<String> synonyms = getSynonyms(keyword);
                for (String synonym : synonyms) {
                    if (!synonym.equals(keyword)) {
                        List<AIConsultation> synonymResults = baseMapper.findByKeyword(synonym);
                        if (synonymResults != null && !synonymResults.isEmpty()) {
                            for (AIConsultation qa : synonymResults) {
                                if (qa.getId() != null && !addedIds.contains(qa.getId())) {
                                    matchedQA.add(qa);
                                    addedIds.add(qa.getId());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // 如果找到匹配的，返回优先级最高的
        if (!matchedQA.isEmpty()) {
            // 按优先级排序
            matchedQA = matchedQA.stream()
                    .sorted((a, b) -> {
                        int priorityA = a.getPriority() != null ? a.getPriority() : 0;
                        int priorityB = b.getPriority() != null ? b.getPriority() : 0;
                        int priorityCompare = Integer.compare(priorityB, priorityA);
                        if (priorityCompare != 0) {
                            return priorityCompare;
                        }
                        int idA = a.getId() != null ? a.getId() : 0;
                        int idB = b.getId() != null ? b.getId() : 0;
                        return Integer.compare(idB, idA);
                    })
                    .collect(Collectors.toList());
            
            return matchedQA.get(0).getAnswer();
        }
        
        // 如果没有匹配的，返回通用建议
        LambdaQueryWrapper<AIConsultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AIConsultation::getCategory, "其他");
        wrapper.orderByDesc(AIConsultation::getPriority);
        wrapper.last("LIMIT 1");
        AIConsultation defaultQA = this.getOne(wrapper);
        
        if (defaultQA != null) {
            return defaultQA.getAnswer();
        }
        
        return "感谢您的提问。由于您的问题比较特殊，建议您咨询专业医生或健康顾问，以获得更准确的建议。同时，您可以尝试描述更具体的问题，我会尽力为您提供帮助。";
    }
    
    /**
     * 智能提取关键词：提取所有可能的2-4字词组
     * 例如："如何健身" -> ["如何", "健身", "如何健身"]
     *      "怎样运动好" -> ["怎样", "运动", "运动好", "怎样运动"]
     */
    private Set<String> extractKeywords(String question) {
        Set<String> keywords = new HashSet<>();
        
        // 去除标点符号
        String cleaned = question.replaceAll("[，。！？、；：,.\"!?;:（）()【】\\[\\]《》<>]", "");
        
        // 提取所有可能的2-4字词组
        for (int len = 2; len <= 4 && len <= cleaned.length(); len++) {
            for (int i = 0; i <= cleaned.length() - len; i++) {
                String keyword = cleaned.substring(i, i + len);
                // 过滤掉只包含常见疑问词的词组（如"如何"、"怎样"、"什么"等）
                if (!isCommonQuestionWord(keyword)) {
                    keywords.add(keyword);
                }
            }
        }
        
        return keywords;
    }
    
    /**
     * 判断是否为常见的疑问词
     */
    private boolean isCommonQuestionWord(String word) {
        String[] questionWords = {"如何", "怎样", "怎么", "什么", "哪些", "哪个", "为什么", "为何", "能否", "可以", "应该", "需要"};
        for (String qw : questionWords) {
            if (qw.equals(word)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取同义词列表
     */
    private List<String> getSynonyms(String keyword) {
        List<String> synonyms = new ArrayList<>();
        synonyms.add(keyword); // 包含自身
        
        // 查找同义词映射
        for (Map.Entry<String, List<String>> entry : SYNONYM_MAP.entrySet()) {
            if (entry.getValue().contains(keyword)) {
                synonyms.addAll(entry.getValue());
            }
        }
        
        // 去重
        return synonyms.stream().distinct().collect(Collectors.toList());
    }
    
    @Override
    public List<AIConsultation> getAllQA() {
        LambdaQueryWrapper<AIConsultation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AIConsultation::getPriority);
        wrapper.orderByDesc(AIConsultation::getId);
        return this.list(wrapper);
    }
}

