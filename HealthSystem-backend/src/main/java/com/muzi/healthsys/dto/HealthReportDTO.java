package com.muzi.healthsys.dto;

import lombok.Data;
import java.util.List;

/**
 * 健康报告数据传输对象
 */
@Data
public class HealthReportDTO {
    // 用户基本信息
    private Integer userId;
    private String username;
    private Integer age;
    private String gender;
    
    // 身体基本信息
    private Double height;
    private Double weight;
    private Double bmi;
    private String bmiStatus; // 偏瘦/正常/偏胖/肥胖
    
    // 健康指标
    private Double bloodSugar;
    private String bloodPressure;
    private String bloodLipid;
    private Double heartRate;
    private Integer vision;
    
    // 健康评分
    private Integer score;
    
    // 历史数据
    private List<BodyNotesDTO> historyData;
    
    // 统计数据
    private Double avgHeartRate;
    private Double avgVision;
    private Double avgBloodSugar;
    private Double avgBloodPressure;
    
    // 生活习惯相关
    private String foodTypes;
    private Double sleepDuration;
    private String sleepQuality;
    private Boolean smoking;
    private Boolean drinking;
    private Boolean exercise;
    private Double waterConsumption;
    
    // 疾病分析
    private String diseaseRisk; // 可能的疾病
    
    // 基础能量消耗状况
    private Double standardHeight; // 到达身体年龄的百分比
    private Double bmr; // 基本能量消耗百分比
    
    // 肥胖分析
    private String healthRisk; // 根据计算的结果
    private String obesityRisk; // 肥胖可能的风险
    private Integer obesityPercentage; // 肥胖百分比
    
    // 生活习惯分析
    private String habits; // 生活习惯描述
    
    // 视力分析
    private String visionType; // 近视等级
    private String visionSuggestion; // 视力建议
    
    // 体型判断
    private String bodyType; // 体型类型
    private String bodyTypeSuggestion; // 体型建议
    
    // 导出时间
    private String exportTime;
    
    @Data
    public static class BodyNotesDTO {
        private String date;
        private Integer vision;
        private Double waterConsumption;
        private Double bloodSugar;
        private String bloodPressure;
        private Double heartRate;
    }
}

