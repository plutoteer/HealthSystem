package com.muzi.healthsys.controller;

import com.muzi.Data_unification.Unification;
import com.muzi.healthsys.dto.HealthReportDTO;
import com.muzi.healthsys.service.IHealthReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 健康报告控制器
 */
@RestController
@RequestMapping("/healthReport")
public class HealthReportController {
    
    @Autowired
    private IHealthReportService healthReportService;
    
    /**
     * 获取健康报告数据（返回JSON，不生成PDF）
     */
    @PostMapping("/data")
    public Unification<HealthReportDTO> getHealthReportData(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = null;
            Object userIdObj = params.get("userId");
            
            if (userIdObj instanceof Integer) {
                userId = (Integer) userIdObj;
            } else if (userIdObj instanceof Map) {
                Map<String, Object> userIdMap = (Map<String, Object>) userIdObj;
                Object idObj = userIdMap.get("id");
                if (idObj instanceof Integer) {
                    userId = (Integer) idObj;
                } else if (idObj != null) {
                    userId = Integer.parseInt(idObj.toString());
                }
            } else if (userIdObj != null) {
                String userIdStr = userIdObj.toString();
                if (userIdStr.contains("id=")) {
                    String idPart = userIdStr.substring(userIdStr.indexOf("id=") + 3);
                    idPart = idPart.replaceAll("[^0-9]", "");
                    if (!idPart.isEmpty()) {
                        userId = Integer.parseInt(idPart);
                    }
                } else {
                    userId = Integer.parseInt(userIdStr);
                }
            }
            
            if (userId == null) {
                return Unification.fail(400, "用户ID不能为空");
            }
            
            List<Integer> selectedNotesIds = null;
            Object selectedNotesIdsObj = params.get("selectedNotesIds");
            if (selectedNotesIdsObj instanceof List) {
                selectedNotesIds = new ArrayList<>();
                for (Object item : (List<?>) selectedNotesIdsObj) {
                    if (item instanceof Integer) {
                        selectedNotesIds.add((Integer) item);
                    } else if (item != null) {
                        try {
                            selectedNotesIds.add(Integer.parseInt(item.toString()));
                        } catch (NumberFormatException e) {
                            // 忽略无效的数字
                        }
                    }
                }
            }
            
            HealthReportDTO reportData = healthReportService.getHealthReportData(userId, selectedNotesIds);
            return Unification.success(reportData, "获取报告数据成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Unification.fail(500, "获取报告数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出健康报告为PDF
     */
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportHealthReport(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = null;
            Object userIdObj = params.get("userId");
            
            if (userIdObj instanceof Integer) {
                userId = (Integer) userIdObj;
            } else if (userIdObj instanceof Map) {
                // 处理对象格式 {id: 1}
                Map<String, Object> userIdMap = (Map<String, Object>) userIdObj;
                Object idObj = userIdMap.get("id");
                if (idObj instanceof Integer) {
                    userId = (Integer) idObj;
                } else if (idObj != null) {
                    userId = Integer.parseInt(idObj.toString());
                }
            } else if (userIdObj != null) {
                // 尝试解析字符串格式的数字
                String userIdStr = userIdObj.toString();
                // 如果包含 {id=1} 这样的格式，提取数字
                if (userIdStr.contains("id=")) {
                    String idPart = userIdStr.substring(userIdStr.indexOf("id=") + 3);
                    idPart = idPart.replaceAll("[^0-9]", "");
                    if (!idPart.isEmpty()) {
                        userId = Integer.parseInt(idPart);
                    }
                } else {
                    userId = Integer.parseInt(userIdStr);
                }
            }
            
            if (userId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // 获取选中的记录ID列表（notesId列表）
            List<Integer> selectedNotesIds = null;
            Object selectedNotesIdsObj = params.get("selectedNotesIds");
            if (selectedNotesIdsObj instanceof List) {
                selectedNotesIds = new ArrayList<>();
                for (Object item : (List<?>) selectedNotesIdsObj) {
                    if (item instanceof Integer) {
                        selectedNotesIds.add((Integer) item);
                    } else if (item != null) {
                        try {
                            selectedNotesIds.add(Integer.parseInt(item.toString()));
                        } catch (NumberFormatException e) {
                            // 忽略无效的数字
                        }
                    }
                }
            }
            
            // 如果selectedNotesIds为空或null，使用所有记录（向后兼容）
            HealthReportDTO reportData = healthReportService.getHealthReportData(userId, selectedNotesIds);
            ByteArrayOutputStream outputStream = healthReportService.exportReportToPDF(reportData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            
            // 根据用户ID生成文件名
            String fileName = "健康报告_" + userId + "_" + System.currentTimeMillis() + ".pdf";
            // 使用RFC 5987标准编码文件名，支持中文字符
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build();
            headers.setContentDisposition(contentDisposition);
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

