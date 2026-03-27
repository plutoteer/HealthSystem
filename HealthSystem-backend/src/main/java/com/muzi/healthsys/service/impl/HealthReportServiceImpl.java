package com.muzi.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.muzi.healthsys.dto.HealthReportDTO;
import com.muzi.healthsys.entity.Body;
import com.muzi.healthsys.entity.BodyNotes;
import com.muzi.healthsys.entity.User;
import com.muzi.healthsys.mapper.BodyMapper;
import com.muzi.healthsys.mapper.BodyNotesMapper;
import com.muzi.healthsys.mapper.UserMapper;
import com.muzi.healthsys.service.IHealthReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.html2pdf.HtmlConverter;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HealthReportServiceImpl implements IHealthReportService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BodyMapper bodyMapper;
    
    @Autowired
    private BodyNotesMapper bodyNotesMapper;
    
    @Override
    public HealthReportDTO getHealthReportData(Integer userId, List<Integer> selectedNotesIds) {
        HealthReportDTO dto = new HealthReportDTO();
        
        // 获取用户基本信息
        User user = userMapper.selectById(userId);
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
        }
        
        // 获取身体基本信息（最新的一条）
        LambdaQueryWrapper<Body> bodyWrapper = new LambdaQueryWrapper<>();
        bodyWrapper.eq(Body::getId, userId);
        bodyWrapper.orderByDesc(Body::getId);
        bodyWrapper.last("LIMIT 1");
        Body body = bodyMapper.selectOne(bodyWrapper);
        
        if (body != null) {
            dto.setAge(body.getAge());
            dto.setGender(body.getGender());
            dto.setHeight(body.getHeight());
            dto.setWeight(body.getWeight());
            dto.setBloodSugar(body.getBloodSugar());
            dto.setBloodPressure(body.getBloodPressure());
            dto.setBloodLipid(body.getBloodLipid());
            dto.setHeartRate(body.getHeartRate());
            dto.setVision(body.getVision());
            
            // 计算BMI
            if (body.getHeight() != null && body.getWeight() != null) {
                double bmi = body.getWeight() / (body.getHeight() * body.getHeight() / 10000.0); // 身高从cm转换为m
                dto.setBmi(Math.round(bmi * 100.0) / 100.0);
                dto.setBmiStatus(getBMIStatus(bmi));
            }
        }
        
        // 获取历史数据（根据userId查询，BodyNotes的id字段对应userId）
        // 如果selectedNotesIds不为空，只查询选中的记录；否则查询所有记录
        LambdaQueryWrapper<BodyNotes> notesWrapper = new LambdaQueryWrapper<>();
        notesWrapper.eq(BodyNotes::getId, userId);
        if (selectedNotesIds != null && !selectedNotesIds.isEmpty()) {
            // 只查询选中的记录（根据notes_id）
            notesWrapper.in(BodyNotes::getNotesid, selectedNotesIds);
        }
        notesWrapper.orderByAsc(BodyNotes::getDate);
        
        List<BodyNotes> notesList = bodyNotesMapper.selectList(notesWrapper);
        
        // 如果没有选中记录，使用最新的一条BodyNotes数据作为基础数据
        BodyNotes latestNote = null;
        if (notesList != null && !notesList.isEmpty()) {
            latestNote = notesList.get(notesList.size() - 1); // 获取最新的一条
        } else {
            // 如果没有历史数据，尝试获取最新的一条
            LambdaQueryWrapper<BodyNotes> latestWrapper = new LambdaQueryWrapper<>();
            latestWrapper.eq(BodyNotes::getId, userId);
            latestWrapper.orderByDesc(BodyNotes::getDate);
            latestWrapper.last("LIMIT 1");
            latestNote = bodyNotesMapper.selectOne(latestWrapper);
        }
        
        // 如果latestNote不为空，使用它来填充生活习惯等字段
        if (latestNote != null) {
            dto.setFoodTypes(latestNote.getFoodTypes());
            dto.setSleepDuration(latestNote.getSleepDuration());
            dto.setSleepQuality(latestNote.getSleepQuality());
            dto.setSmoking(latestNote.isSmoking());
            dto.setDrinking(latestNote.isDrinking());
            dto.setExercise(latestNote.isExercise());
            dto.setWaterConsumption(latestNote.getWaterConsumption());
            
            // 如果body为空，使用latestNote的数据
            if (body == null) {
                dto.setAge(latestNote.getAge());
                dto.setGender(latestNote.getGender());
                dto.setHeight(latestNote.getHeight());
                dto.setWeight(latestNote.getWeight());
                dto.setBloodSugar(latestNote.getBloodSugar());
                dto.setBloodPressure(latestNote.getBloodPressure());
                dto.setBloodLipid(latestNote.getBloodLipid());
                dto.setHeartRate(latestNote.getHeartRate());
                dto.setVision(latestNote.getVision());
                
                // 计算BMI
                if (latestNote.getHeight() != null && latestNote.getWeight() != null) {
                    double bmi = latestNote.getWeight() / (latestNote.getHeight() * latestNote.getHeight() / 10000.0);
                    dto.setBmi(Math.round(bmi * 100.0) / 100.0);
                    dto.setBmiStatus(getBMIStatus(bmi));
                }
            }
        }
        
        List<HealthReportDTO.BodyNotesDTO> historyData = new ArrayList<>();
        List<Double> heartRates = new ArrayList<>();
        List<Integer> visions = new ArrayList<>();
        List<Double> bloodSugars = new ArrayList<>();
        List<Double> bloodPressures = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (BodyNotes note : notesList) {
            HealthReportDTO.BodyNotesDTO noteDTO = new HealthReportDTO.BodyNotesDTO();
            noteDTO.setDate(note.getDate() != null ? sdf.format(note.getDate()) : "");
            noteDTO.setVision(note.getVision());
            noteDTO.setWaterConsumption(note.getWaterConsumption());
            noteDTO.setBloodSugar(note.getBloodSugar());
            noteDTO.setBloodPressure(note.getBloodPressure());
            noteDTO.setHeartRate(note.getHeartRate());
            historyData.add(noteDTO);
            
            if (note.getHeartRate() > 0) heartRates.add(note.getHeartRate());
            if (note.getVision() != null) visions.add(note.getVision());
            if (note.getBloodSugar() != null) bloodSugars.add(note.getBloodSugar());
            // 血压是String类型，需要解析
            if (note.getBloodPressure() != null && !note.getBloodPressure().isEmpty()) {
                try {
                    // 尝试解析血压字符串，格式可能是 "120/80" 或 "120"
                    String[] parts = note.getBloodPressure().split("/");
                    if (parts.length > 0) {
                        bloodPressures.add(Double.parseDouble(parts[0]));
                    }
                } catch (Exception e) {
                    // 解析失败，忽略
                }
            }
        }
        
        dto.setHistoryData(historyData);
        
        // 计算平均值
        dto.setAvgHeartRate(calculateAverage(heartRates));
        dto.setAvgVision(calculateAverageInt(visions));
        dto.setAvgBloodSugar(calculateAverage(bloodSugars));
        dto.setAvgBloodPressure(calculateAverage(bloodPressures));
        
        // 计算健康评分
        dto.setScore(calculateHealthScore(dto));
        
        // 计算所有分析内容
        calculateAllAnalysis(dto, latestNote != null ? latestNote : body);
        
        // 设置导出时间
        dto.setExportTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        return dto;
    }
    
    @Override
    public String generateReportHTML(HealthReportDTO reportData) {
        // 生成HTML内容
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<title>健康报告</title>");
        html.append("<style>");
        html.append("body { font-family: 'Microsoft YaHei', Arial, sans-serif; padding: 20px; background: #f5f5f5; }");
        html.append(".container { max-width: 1200px; margin: 0 auto; background: #fff; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { text-align: center; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 3px solid #409EFF; }");
        html.append(".header h1 { color: #303133; margin: 0 0 10px 0; font-size: 28px; }");
        html.append(".header p { color: #909399; margin: 0; font-size: 14px; }");
        html.append(".section { margin-bottom: 30px; }");
        html.append(".section-title { font-size: 20px; font-weight: bold; margin-bottom: 15px; padding: 10px 0; border-bottom: 2px solid #409EFF; color: #303133; }");
        html.append(".info-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; margin-top: 15px; }");
        html.append(".info-item { padding: 15px; background: #f5f7fa; border-radius: 8px; border-left: 4px solid #409EFF; display: flex; flex-direction: column; }");
        html.append(".info-label { color: #606266; font-size: 14px; font-weight: 600; margin-bottom: 8px; display: block; }");
        html.append(".info-value { color: #303133; font-size: 18px; font-weight: bold; display: block; }");
        html.append(".info-unit { color: #909399; font-size: 14px; font-weight: normal; margin-left: 5px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 14px; }");
        html.append("th, td { border: 1px solid #dcdfe6; padding: 12px; text-align: center; }");
        html.append("th { background: #f5f7fa; font-weight: bold; color: #303133; }");
        html.append("tr:nth-child(even) { background: #fafafa; }");
        html.append("tr:hover { background: #ecf5ff; }");
        html.append(".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #dcdfe6; text-align: center; color: #909399; font-size: 12px; }");
        html.append("</style></head><body>");
        
        // 报告标题
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1 style='color: #303133; margin: 0 0 10px 0; font-size: 32px; font-weight: bold;'>个人健康报告</h1>");
        html.append("<p style='color: #909399; margin: 0; font-size: 16px;'>导出时间：").append(reportData.getExportTime() != null ? reportData.getExportTime() : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("</p>");
        html.append("</div>");
        
        // 基本信息
        html.append("<div class='section'><div class='section-title'>基本信息</div>");
        html.append("<div class='info-grid'>");
        html.append("<div class='info-item'><div class='info-label'>姓名</div><div class='info-value'>").append(reportData.getUsername() != null ? reportData.getUsername() : "--").append("</div></div>");
        html.append("<div class='info-item'><div class='info-label'>年龄</div><div class='info-value'>").append(reportData.getAge() != null ? reportData.getAge() : "--").append("<span class='info-unit'>岁</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>性别</div><div class='info-value'>").append(reportData.getGender() != null ? reportData.getGender() : "--").append("</div></div>");
        html.append("<div class='info-item'><div class='info-label'>身高</div><div class='info-value'>").append(reportData.getHeight() != null ? reportData.getHeight() : "--").append("<span class='info-unit'>cm</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>体重</div><div class='info-value'>").append(reportData.getWeight() != null ? reportData.getWeight() : "--").append("<span class='info-unit'>kg</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>BMI</div><div class='info-value'>").append(reportData.getBmi() != null ? reportData.getBmi() : "--").append(" (").append(reportData.getBmiStatus() != null ? reportData.getBmiStatus() : "--").append(")</div></div>");
        html.append("</div></div>");
        
        // 身体信息
        html.append("<div class='section'><div class='section-title'>身体信息</div>");
        html.append("<div class='info-grid'>");
        html.append("<div class='info-item'><div class='info-label'>体重</div><div class='info-value'>").append(reportData.getWeight() != null ? reportData.getWeight() : "--").append("<span class='info-unit'>kg</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>身高</div><div class='info-value'>").append(reportData.getHeight() != null ? reportData.getHeight() : "--").append("<span class='info-unit'>cm</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>BMI</div><div class='info-value'>").append(reportData.getBmi() != null ? reportData.getBmi() : "--").append(" (").append(reportData.getBmiStatus() != null ? reportData.getBmiStatus() : "--").append(")</div></div>");
        html.append("<div class='info-item'><div class='info-label'>血糖</div><div class='info-value'>").append(reportData.getBloodSugar() != null ? reportData.getBloodSugar() : "--").append("<span class='info-unit'>mmol/L</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>血压</div><div class='info-value'>").append(reportData.getBloodPressure() != null ? reportData.getBloodPressure() : "--").append("<span class='info-unit'>mmHg</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>胆固醇</div><div class='info-value'>").append(reportData.getBloodLipid() != null ? reportData.getBloodLipid() : "--").append("<span class='info-unit'>mmol/l</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>心率</div><div class='info-value'>").append(reportData.getHeartRate() != null ? reportData.getHeartRate() : "--").append("<span class='info-unit'>次/分钟</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>视力</div><div class='info-value'>").append(reportData.getVision() != null ? reportData.getVision() : "--").append("<span class='info-unit'>度</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>健康评分</div><div class='info-value'>").append(reportData.getScore() != null ? reportData.getScore() : "--").append("<span class='info-unit'>分</span></div></div>");
        html.append("</div></div>");
        
        // 疾病分析
        html.append("<div class='section'><div class='section-title'>疾病分析</div>");
        html.append("<div class='info-item' style='margin-bottom: 15px;'><div class='info-label'>可能的疾病：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getDiseaseRisk() != null ? reportData.getDiseaseRisk() : "--").append("</div></div>");
        html.append("<div class='info-item' style='margin-top: 15px; padding: 15px; background: #fff3cd; border-left: 4px solid #ffc107; border-radius: 8px;'><div class='info-label' style='color: #856404; font-weight: bold; margin-bottom: 8px;'><strong>注意：</strong></div><div class='info-value' style='color: #856404; font-size: 14px; font-weight: normal;'>以上风险只是根据您上传的身体数据进行最基本的分析，并不能作为真正的结果，不管有没有风险，都需要保持运动，如有不舒服的地方请马上就医。</div></div>");
        html.append("</div>");
        
        // 基础能量消耗状况
        html.append("<div class='section'><div class='section-title'>基础能量消耗状况</div>");
        html.append("<div class='info-grid'>");
        html.append("<div class='info-item'><div class='info-label'>到达身体年龄的百分比</div><div class='info-value'>").append(reportData.getStandardHeight() != null ? String.format("%.2f", reportData.getStandardHeight()) : "--").append("<span class='info-unit'>%</span></div></div>");
        html.append("<div class='info-item'><div class='info-label'>基本能量消耗</div><div class='info-value'>").append(reportData.getBmr() != null ? String.format("%.2f", reportData.getBmr()) : "--").append("<span class='info-unit'>%</span></div></div>");
        html.append("</div></div>");
        
        // 肥胖分析
        html.append("<div class='section'><div class='section-title'>肥胖分析</div>");
        html.append("<div class='info-item' style='margin-bottom: 15px;'><div class='info-label'>肥胖百分比</div><div class='info-value'>").append(reportData.getObesityPercentage() != null ? reportData.getObesityPercentage() : "--").append("<span class='info-unit'>%</span></div></div>");
        html.append("<div class='info-item' style='margin-bottom: 15px;'><div class='info-label'>根据计算：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getHealthRisk() != null ? reportData.getHealthRisk() : "--").append("</div></div>");
        html.append("<div class='info-item'><div class='info-label'>肥胖可能的风险：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getObesityRisk() != null ? reportData.getObesityRisk() : "--").append("</div></div>");
        html.append("</div>");
        
        // 生活习惯分析
        html.append("<div class='section'><div class='section-title'>生活习惯分析</div>");
        html.append("<div class='info-item' style='margin-bottom: 15px;'><div class='info-label'>您的习惯如下：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getHabits() != null ? reportData.getHabits() : "--").append("</div></div>");
        html.append("<div class='info-item'><div class='info-label'>建议：</div><div class='info-value' style='font-size: 14px; font-weight: normal;'>阅读运动知识，更好地了解运动的正确姿势和方法，通过了解运动的原理和科学知识，我们可以更好地制定运动计划，减少运动中的风险和不适，避免受伤和疾病的发生。</div></div>");
        html.append("</div>");
        
        // 视力分析
        html.append("<div class='section'><div class='section-title'>视力分析</div>");
        html.append("<div class='info-item' style='margin-bottom: 15px;'><div class='info-label'>您的视力为：</div><div class='info-value'>").append(reportData.getVision() != null ? reportData.getVision() : "--").append("<span class='info-unit'>度</span></div></div>");
        html.append("<div class='info-item' style='margin-bottom: 15px;'><div class='info-label'>近视等级：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getVisionType() != null ? reportData.getVisionType() : "--").append("</div></div>");
        html.append("<div class='info-item'><div class='info-label'>建议：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getVisionSuggestion() != null ? reportData.getVisionSuggestion() : "--").append("</div></div>");
        html.append("</div>");
        
        // 体型判断
        html.append("<div class='section'><div class='section-title'>体型判断</div>");
        html.append("<div class='info-item' style='margin-bottom: 15px;'><div class='info-label'>您的体型属于：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getBodyType() != null ? reportData.getBodyType() : "--").append("</div></div>");
        html.append("<div class='info-item'><div class='info-label'>建议：</div><div class='info-value' style='font-size: 16px;'>").append(reportData.getBodyTypeSuggestion() != null ? reportData.getBodyTypeSuggestion() : "--").append("</div></div>");
        html.append("</div>");
        
        // 历史数据
        if (reportData.getHistoryData() != null && !reportData.getHistoryData().isEmpty()) {
            html.append("<div class='section'><div class='section-title'>历史数据明细</div>");
            html.append("<table><tr><th>日期</th><th>视力(度)</th><th>饮水量(ml)</th><th>血糖(mmol/L)</th><th>血压(mmHg)</th><th>心率(次/分钟)</th></tr>");
            for (HealthReportDTO.BodyNotesDTO note : reportData.getHistoryData()) {
                html.append("<tr>");
                html.append("<td>").append(note.getDate() != null ? note.getDate() : "--").append("</td>");
                html.append("<td>").append(note.getVision() != null ? note.getVision() : "--").append("</td>");
                html.append("<td>").append(note.getWaterConsumption() != null ? note.getWaterConsumption() : "--").append("</td>");
                html.append("<td>").append(note.getBloodSugar() != null ? note.getBloodSugar() : "--").append("</td>");
                html.append("<td>").append(note.getBloodPressure() != null ? note.getBloodPressure() : "--").append("</td>");
                html.append("<td>").append(note.getHeartRate() != null ? note.getHeartRate() : "--").append("</td>");
                html.append("</tr>");
            }
            html.append("</table></div>");
            
            // 统计数据
            html.append("<div class='section'><div class='section-title'>统计数据</div>");
            html.append("<div class='info-grid'>");
            html.append("<div class='info-item'><div class='info-label'>平均心率</div><div class='info-value'>").append(reportData.getAvgHeartRate() != null ? String.format("%.2f", reportData.getAvgHeartRate()) : "--").append("<span class='info-unit'>次/分钟</span></div></div>");
            html.append("<div class='info-item'><div class='info-label'>平均视力</div><div class='info-value'>").append(reportData.getAvgVision() != null ? String.format("%.2f", reportData.getAvgVision()) : "--").append("<span class='info-unit'>度</span></div></div>");
            html.append("<div class='info-item'><div class='info-label'>平均血糖</div><div class='info-value'>").append(reportData.getAvgBloodSugar() != null ? String.format("%.2f", reportData.getAvgBloodSugar()) : "--").append("<span class='info-unit'>mmol/L</span></div></div>");
            html.append("<div class='info-item'><div class='info-label'>平均血压</div><div class='info-value'>").append(reportData.getAvgBloodPressure() != null ? String.format("%.2f", reportData.getAvgBloodPressure()) : "--").append("<span class='info-unit'>mmHg</span></div></div>");
            html.append("</div></div>");
        }
        
        html.append("<div class='footer'>");
        html.append("<p>本报告由健康管理系统自动生成，仅供参考</p>");
        html.append("</div>");
        
        html.append("</div></body></html>");
        return html.toString();
    }
    
    @Override
    public ByteArrayOutputStream exportReportToPDF(HealthReportDTO reportData) {
        // 生成HTML
        String html = generateReportHTML(reportData);
        
        // 使用iText将HTML转换为PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            // 使用iText 7的HtmlConverter将HTML转换为PDF
            HtmlConverter.convertToPdf(html, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果PDF生成失败，返回空流
            try {
                outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return outputStream;
    }
    
    private String getBMIStatus(double bmi) {
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi < 24) {
            return "正常";
        } else if (bmi < 28) {
            return "偏胖";
        } else {
            return "肥胖";
        }
    }
    
    private Double calculateAverage(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    private Double calculateAverageInt(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    
    private Integer calculateHealthScore(HealthReportDTO dto) {
        // 健康评分计算（参考前端test2.vue的scoreCom方法）
        int score = 100;
        
        if (dto.getVision() != null && dto.getVision() > 30) {
            score -= 25;
        }
        
        // 计算疾病风险数量
        String diseaseRisk = dto.getDiseaseRisk();
        if (diseaseRisk != null && !diseaseRisk.isEmpty()) {
            int riskCount = diseaseRisk.split("，").length;
            if (riskCount > 6) {
                score -= 25;
            }
        }
        
        if (dto.getBmi() != null && dto.getBmi() > 24) {
            score -= 25;
        }
        
        if (dto.getStandardHeight() != null && dto.getStandardHeight() < 80) {
            score -= 25;
        }
        
        return Math.max(0, score);
    }
    
    /**
     * 计算所有分析内容
     */
    private void calculateAllAnalysis(HealthReportDTO dto, Object bodyData) {
        if (bodyData == null) {
            return;
        }
        
        Body body = null;
        BodyNotes note = null;
        if (bodyData instanceof Body) {
            body = (Body) bodyData;
        } else if (bodyData instanceof BodyNotes) {
            note = (BodyNotes) bodyData;
        }
        
        // 疾病分析
        dto.setDiseaseRisk(calculateDiseaseRisk(body, note));
        
        // 基础能量消耗状况
        calculateEnergyConsumption(dto, body, note);
        
        // 肥胖分析
        calculateObesityAnalysis(dto);
        
        // 生活习惯分析
        dto.setHabits(calculateHabits(body, note));
        
        // 视力分析
        calculateVisionAnalysis(dto, body, note);
        
        // 体型判断
        calculateBodyType(dto);
    }
    
    /**
     * 计算疾病风险
     */
    private String calculateDiseaseRisk(Body body, BodyNotes note) {
        StringBuilder risk = new StringBuilder();
        
        double bloodPressure = 0;
        double bloodLipid = 0;
        double bloodSugar = 0;
        double heartRate = 0;
        double sleepDuration = 0;
        int sleepQuality = 0;
        boolean smoking = false;
        int vision = 0;
        double waterConsumption = 0;
        boolean drinking = false;
        boolean exercise = false;
        
        if (body != null) {
            try {
                if (body.getBloodPressure() != null && !body.getBloodPressure().isEmpty()) {
                    String[] parts = body.getBloodPressure().split("/");
                    if (parts.length > 0) {
                        bloodPressure = Double.parseDouble(parts[0]);
                    }
                }
            } catch (Exception e) {}
            try {
                if (body.getBloodLipid() != null && !body.getBloodLipid().isEmpty()) {
                    bloodLipid = Double.parseDouble(body.getBloodLipid());
                }
            } catch (Exception e) {}
            bloodSugar = body.getBloodSugar() != null ? body.getBloodSugar() : 0;
            heartRate = body.getHeartRate();
            sleepDuration = body.getSleepDuration();
            if ("好".equals(body.getSleepQuality())) sleepQuality = 3;
            else if ("一般".equals(body.getSleepQuality())) sleepQuality = 2;
            else if ("差".equals(body.getSleepQuality())) sleepQuality = 1;
            smoking = body.isSmoking();
            vision = body.getVision() != null ? body.getVision() : 0;
            waterConsumption = body.getWaterConsumption();
            drinking = body.isDrinking();
            exercise = body.isExercise();
        } else if (note != null) {
            try {
                if (note.getBloodPressure() != null && !note.getBloodPressure().isEmpty()) {
                    String[] parts = note.getBloodPressure().split("/");
                    if (parts.length > 0) {
                        bloodPressure = Double.parseDouble(parts[0]);
                    }
                }
            } catch (Exception e) {}
            try {
                if (note.getBloodLipid() != null && !note.getBloodLipid().isEmpty()) {
                    bloodLipid = Double.parseDouble(note.getBloodLipid());
                }
            } catch (Exception e) {}
            bloodSugar = note.getBloodSugar() != null ? note.getBloodSugar() : 0;
            heartRate = note.getHeartRate();
            sleepDuration = note.getSleepDuration();
            if ("好".equals(note.getSleepQuality())) sleepQuality = 3;
            else if ("一般".equals(note.getSleepQuality())) sleepQuality = 2;
            else if ("差".equals(note.getSleepQuality())) sleepQuality = 1;
            smoking = note.isSmoking();
            vision = note.getVision() != null ? note.getVision() : 0;
            waterConsumption = note.getWaterConsumption();
            drinking = note.isDrinking();
            exercise = note.isExercise();
        }
        
        if (bloodPressure >= 90) {
            risk.append("高血压，");
        }
        if (bloodLipid > 3) {
            risk.append("高血脂，");
        }
        if (bloodSugar > 6.1) {
            risk.append("糖尿病，");
        }
        if (drinking && bloodSugar > 3) { // 简化判断
            risk.append("酗酒，");
        }
        if (!exercise) {
            risk.append("缺乏运动，");
        }
        if (heartRate > 100) {
            risk.append("心率过快，");
        }
        if (sleepDuration < 7 || sleepQuality < 3) {
            risk.append("睡眠不足，");
        }
        if (smoking) {
            risk.append("肺炎，");
        }
        if (vision <= 300 && vision > 0) {
            risk.append("近视，");
        }
        if (waterConsumption < 1500) {
            risk.append("结石");
        }
        
        String result = risk.toString();
        if (result.endsWith("，")) {
            result = result.substring(0, result.length() - 1);
        }
        return result.isEmpty() ? "暂无" : result;
    }
    
    /**
     * 计算基础能量消耗状况
     */
    private void calculateEnergyConsumption(HealthReportDTO dto, Body body, BodyNotes note) {
        Double height = dto.getHeight();
        String gender = dto.getGender();
        Double weight = dto.getWeight();
        Integer age = dto.getAge();
        
        if (height == null || gender == null || weight == null || age == null) {
            return;
        }
        
        // 计算到达身体年龄的百分比
        double standardHeight = 0;
        if ("男".equals(gender)) {
            standardHeight = (height - 80) * 0.7 + 160;
        } else {
            standardHeight = (height - 70) * 0.6 + 160;
        }
        double standardHeightPercent = (standardHeight / height) * 100;
        dto.setStandardHeight(Math.round(standardHeightPercent * 100.0) / 100.0);
        
        // 计算基本能量消耗（BMR）
        double bmr = 0;
        if ("男".equals(gender)) {
            bmr = 88.36 + 13.4 * weight + 4.8 * height - 5.7 * age;
        } else {
            bmr = 447.6 + 9.2 * weight + 3.1 * height - 4.3 * age;
        }
        double reference = 1200; // 参考值
        double bmrPercent = (bmr / reference) * 100;
        dto.setBmr(Math.round(bmrPercent * 100.0) / 100.0);
    }
    
    /**
     * 计算肥胖分析
     */
    private void calculateObesityAnalysis(HealthReportDTO dto) {
        if (dto.getBmi() == null) {
            return;
        }
        
        double bmi = dto.getBmi();
        
        // 计算肥胖百分比
        int percentage = (int) Math.round((bmi / 35) * 100);
        dto.setObesityPercentage(percentage);
        
        // 根据计算的结果
        if (bmi >= 28) {
            dto.setHealthRisk("您的体重太大了，请马上减肥");
            dto.setObesityRisk("心脏病、中风、高血压和高胆固醇，增加心脏病，还有糖尿病、呼吸系统疾病、关节炎等风险");
        } else if (bmi > 24 && bmi <= 28) {
            dto.setHealthRisk("您的体重过大，请及时减肥");
            dto.setObesityRisk("容易导致高血压、高胆固醇、心脏病、中风、患糖尿病的风险，胰岛素分泌异常以及呼吸系统疾病");
        } else if (bmi >= 0 && bmi <= 24) {
            dto.setHealthRisk("您的体重正常，请保持健康生活");
            dto.setObesityRisk("风险不大，但是要保证摄入足够的蛋白质、碳水化合物和脂肪");
        }
    }
    
    /**
     * 计算生活习惯
     */
    private String calculateHabits(Body body, BodyNotes note) {
        List<String> habits = new ArrayList<>();
        
        String foodTypes = null;
        Double bloodSugar = null;
        Double bloodPressure = null;
        Double heartRate = null;
        Integer vision = null;
        Double sleepDuration = null;
        String sleepQuality = null;
        Boolean smoking = null;
        Boolean drinking = null;
        Boolean exercise = null;
        Double waterConsumption = null;
        
        if (body != null) {
            foodTypes = body.getFoodTypes();
            bloodSugar = body.getBloodSugar();
            try {
                if (body.getBloodPressure() != null && !body.getBloodPressure().isEmpty()) {
                    String[] parts = body.getBloodPressure().split("/");
                    if (parts.length > 0) {
                        bloodPressure = Double.parseDouble(parts[0]);
                    }
                }
            } catch (Exception e) {}
            heartRate = body.getHeartRate();
            vision = body.getVision();
            sleepDuration = body.getSleepDuration();
            sleepQuality = body.getSleepQuality();
            smoking = body.isSmoking();
            drinking = body.isDrinking();
            exercise = body.isExercise();
            waterConsumption = body.getWaterConsumption();
        } else if (note != null) {
            foodTypes = note.getFoodTypes();
            bloodSugar = note.getBloodSugar();
            try {
                if (note.getBloodPressure() != null && !note.getBloodPressure().isEmpty()) {
                    String[] parts = note.getBloodPressure().split("/");
                    if (parts.length > 0) {
                        bloodPressure = Double.parseDouble(parts[0]);
                    }
                }
            } catch (Exception e) {}
            heartRate = note.getHeartRate();
            vision = note.getVision();
            sleepDuration = note.getSleepDuration();
            sleepQuality = note.getSleepQuality();
            smoking = note.isSmoking();
            drinking = note.isDrinking();
            exercise = note.isExercise();
            waterConsumption = note.getWaterConsumption();
        }
        
        // 判断膳食习惯
        if (foodTypes != null) {
            if ("蔬菜".equals(foodTypes)) habits.add("爱吃蔬菜");
            else if ("水果".equals(foodTypes)) habits.add("爱吃水果");
            else if ("肉类".equals(foodTypes)) habits.add("爱吃肉");
            else if ("鱼类".equals(foodTypes)) habits.add("爱吃鱼");
            else if ("豆类".equals(foodTypes)) habits.add("爱吃豆类");
            else if ("谷物".equals(foodTypes)) habits.add("爱吃五谷");
        }
        
        if (bloodSugar != null) {
            if (bloodSugar > 7) {
                habits.add("摄入的糖分和生活习惯不好");
            } else {
                habits.add("血糖水平正常");
            }
        }
        
        if (bloodPressure != null) {
            if (bloodPressure > 5.2) {
                habits.add("高胆固醇饮食");
            } else {
                habits.add("低胆固醇饮食");
            }
        }
        
        if (heartRate != null) {
            if (heartRate > 100) {
                habits.add("经常紧张焦虑和压力");
            } else {
                habits.add("心情还不错");
            }
        }
        
        if (vision != null) {
            if (vision > 50) {
                habits.add("熬夜过多过度劳累");
            } else {
                habits.add("准时睡觉");
            }
        }
        
        if (sleepDuration != null) {
            if (sleepDuration < 8) {
                habits.add("睡眠不足");
            } else {
                habits.add("睡眠充足");
            }
        }
        
        if (sleepQuality != null) {
            if ("好".equals(sleepQuality)) {
                habits.add("熬夜过多过度劳累");
            } else if ("一般".equals(sleepQuality)) {
                habits.add("需要注意睡眠质量");
            } else if ("差".equals(sleepQuality)) {
                habits.add("需要改善睡眠质量");
            }
        }
        
        if (smoking != null) {
            if (smoking) {
                habits.add("吸烟");
            } else {
                habits.add("不吸烟");
            }
        }
        
        if (drinking != null) {
            if (drinking) {
                habits.add("饮酒");
            } else {
                habits.add("未饮酒");
            }
        }
        
        if (exercise != null) {
            if (exercise) {
                habits.add("积极锻炼");
            } else {
                habits.add("缺乏运动");
            }
        }
        
        if (waterConsumption != null) {
            if (waterConsumption < 1000) {
                habits.add("饮水不足");
            } else {
                habits.add("饮水充足");
            }
        }
        
        return habits.isEmpty() ? "--" : String.join("，", habits);
    }
    
    /**
     * 计算视力分析
     */
    private void calculateVisionAnalysis(HealthReportDTO dto, Body body, BodyNotes note) {
        Integer vision = dto.getVision();
        
        if (vision == null) {
            dto.setVisionType("--");
            dto.setVisionSuggestion("--");
            return;
        }
        
        if (vision >= 600) {
            dto.setVisionType("高度近视");
            dto.setVisionSuggestion("积极治疗，建议就医");
        } else if (vision >= 300 && vision <= 600) {
            dto.setVisionType("中度近视");
            dto.setVisionSuggestion("注意保护眼睛，建议定期检查视力");
        } else if (vision > 0 && vision <= 300) {
            dto.setVisionType("轻度近视");
            dto.setVisionSuggestion("加强锻炼，注意用眼卫生");
        } else if (vision == 0) {
            dto.setVisionType("没有近视");
            dto.setVisionSuggestion("很好，保持生活习惯，注意保护眼睛");
        } else {
            dto.setVisionType("--");
            dto.setVisionSuggestion("--");
        }
    }
    
    /**
     * 计算体型判断
     */
    private void calculateBodyType(HealthReportDTO dto) {
        if (dto.getBmi() == null) {
            dto.setBodyType("--");
            dto.setBodyTypeSuggestion("--");
            return;
        }
        
        double bmi = dto.getBmi();
        
        if (bmi >= 28) {
            dto.setBodyType("肥胖型");
            dto.setBodyTypeSuggestion("控制饮食，增加运动量，并寻求专业医师的指导。");
        } else if (bmi > 24 && bmi <= 28) {
            dto.setBodyType("超重体型");
            dto.setBodyTypeSuggestion("注意饮食健康，控制摄入量，并加强有氧运动，提高身体代谢率。");
        } else if (bmi >= 0 && bmi <= 24) {
            dto.setBodyType("正常体型");
            dto.setBodyTypeSuggestion("保持良好的生活习惯，适当参加运动，均衡饮食，保持身体健康。");
        } else {
            dto.setBodyType("--");
            dto.setBodyTypeSuggestion("--");
        }
    }
}

