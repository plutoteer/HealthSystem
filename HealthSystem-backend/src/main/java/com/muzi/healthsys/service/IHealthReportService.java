package com.muzi.healthsys.service;

import com.muzi.healthsys.dto.HealthReportDTO;
import java.io.ByteArrayOutputStream;

import java.util.List;

public interface IHealthReportService {
    /**
     * 获取健康报告数据（根据选中的记录ID列表）
     */
    HealthReportDTO getHealthReportData(Integer userId, List<Integer> selectedNotesIds);
    
    /**
     * 生成健康报告HTML预览
     */
    String generateReportHTML(HealthReportDTO reportData);
    
    /**
     * 导出健康报告为PDF
     */
    ByteArrayOutputStream exportReportToPDF(HealthReportDTO reportData);
}

