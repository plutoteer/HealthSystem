package com.muzi.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muzi.healthsys.entity.InsuranceEnrollment;
import com.muzi.healthsys.entity.InsuranceInfo;
import com.muzi.healthsys.mapper.InsuranceEnrollmentMapper;
import com.muzi.healthsys.mapper.InsuranceInfoMapper;
import com.muzi.healthsys.service.IInsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InsuranceServiceImpl implements IInsuranceService {

    @Autowired
    private InsuranceInfoMapper insuranceInfoMapper;

    @Autowired
    private InsuranceEnrollmentMapper insuranceEnrollmentMapper;

    @Override
    public List<InsuranceInfo> getActiveInsuranceList() {
        LambdaQueryWrapper<InsuranceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InsuranceInfo::getStatus, 1);
        wrapper.orderByDesc(InsuranceInfo::getId);
        return insuranceInfoMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> enrollAndPay(Integer userId, Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();

        Integer insuranceInfoId;
        try {
            insuranceInfoId = Integer.parseInt(String.valueOf(body.get("insuranceInfoId")));
        } catch (Exception e) {
            result.put("ok", false);
            result.put("message", "医保项目ID不能为空或格式错误");
            return result;
        }

        String realName = body.get("realName") == null ? "" : String.valueOf(body.get("realName")).trim();
        String studentNo = body.get("studentNo") == null ? "" : String.valueOf(body.get("studentNo")).trim();
        String idCard = body.get("idCard") == null ? "" : String.valueOf(body.get("idCard")).trim();
        String phone = body.get("phone") == null ? "" : String.valueOf(body.get("phone")).trim();
        String major = body.get("major") == null ? "" : String.valueOf(body.get("major")).trim();
        String grade = body.get("grade") == null ? "" : String.valueOf(body.get("grade")).trim();

        if (!StringUtils.hasLength(realName) || !StringUtils.hasLength(studentNo)
                || !StringUtils.hasLength(idCard) || !StringUtils.hasLength(phone)) {
            result.put("ok", false);
            result.put("message", "姓名、学号、身份证号、手机号不能为空");
            return result;
        }

        InsuranceInfo info = insuranceInfoMapper.selectById(insuranceInfoId);
        if (info == null || info.getStatus() == null || info.getStatus() != 1) {
            result.put("ok", false);
            result.put("message", "医保项目不存在或未开放");
            return result;
        }

        Date now = new Date();
        if (info.getStartTime() != null && now.before(info.getStartTime())) {
            result.put("ok", false);
            result.put("message", "未到参保开始时间");
            return result;
        }
        if (info.getEndTime() != null && now.after(info.getEndTime())) {
            result.put("ok", false);
            result.put("message", "参保时间已截止");
            return result;
        }

        LambdaQueryWrapper<InsuranceEnrollment> existedWrapper = new LambdaQueryWrapper<>();
        existedWrapper.eq(InsuranceEnrollment::getUserId, userId)
                .eq(InsuranceEnrollment::getInsuranceInfoId, insuranceInfoId)
                .last("LIMIT 1");
        InsuranceEnrollment existed = insuranceEnrollmentMapper.selectOne(existedWrapper);

        if (existed != null && existed.getPayStatus() != null && existed.getPayStatus() == 1) {
            result.put("ok", false);
            result.put("message", "您已完成该医保项目参保缴费，无需重复提交");
            return result;
        }

        InsuranceEnrollment enrollment = existed == null ? new InsuranceEnrollment() : existed;
        enrollment.setUserId(userId);
        enrollment.setInsuranceInfoId(insuranceInfoId);
        enrollment.setRealName(realName);
        enrollment.setStudentNo(studentNo);
        enrollment.setIdCard(idCard);
        enrollment.setPhone(phone);
        enrollment.setMajor(major);
        enrollment.setGrade(grade);

        BigDecimal amount = info.getAmount() == null ? BigDecimal.ZERO : info.getAmount();
        enrollment.setPayAmount(amount);

        enrollment.setPayStatus(1);
        enrollment.setPayTime(now);

        if (existed == null) {
            enrollment.setCreatedAt(now);
            insuranceEnrollmentMapper.insert(enrollment);
        } else {
            insuranceEnrollmentMapper.updateById(enrollment);
        }

        result.put("ok", true);
        result.put("message", "参保报名并缴费成功");
        result.put("enrollmentId", enrollment.getId());
        result.put("payAmount", amount);
        return result;
    }

    @Override
    public List<InsuranceEnrollment> getMyEnrollments(Integer userId) {
        LambdaQueryWrapper<InsuranceEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InsuranceEnrollment::getUserId, userId);
        wrapper.orderByDesc(InsuranceEnrollment::getCreatedAt);
        return insuranceEnrollmentMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getInsuranceInfoPage(String title, String insuranceYear, Long pageNo, Long pageSize) {
        LambdaQueryWrapper<InsuranceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(title), InsuranceInfo::getTitle, title);
        wrapper.eq(StringUtils.hasLength(insuranceYear), InsuranceInfo::getInsuranceYear, insuranceYear);
        wrapper.orderByDesc(InsuranceInfo::getId);

        Page<InsuranceInfo> page = new Page<>(pageNo, pageSize);
        insuranceInfoMapper.selectPage(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());
        return data;
    }

    @Override
    public boolean saveInsuranceInfo(InsuranceInfo info) {
        if (info.getCreatedAt() == null) {
            info.setCreatedAt(new Date());
        }
        if (info.getStatus() == null) {
            info.setStatus(1);
        }
        return insuranceInfoMapper.insert(info) > 0;
    }

    @Override
    public boolean updateInsuranceInfo(InsuranceInfo info) {
        return insuranceInfoMapper.updateById(info) > 0;
    }

    @Override
    public boolean deleteInsuranceInfoById(Integer id) {
        return insuranceInfoMapper.deleteById(id) > 0;
    }

    @Override
    public Map<String, Object> getEnrollmentPage(Integer userId, Integer insuranceInfoId, Long pageNo, Long pageSize) {
        LambdaQueryWrapper<InsuranceEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, InsuranceEnrollment::getUserId, userId);
        wrapper.eq(insuranceInfoId != null, InsuranceEnrollment::getInsuranceInfoId, insuranceInfoId);
        wrapper.orderByDesc(InsuranceEnrollment::getCreatedAt);

        Page<InsuranceEnrollment> page = new Page<>(pageNo, pageSize);
        insuranceEnrollmentMapper.selectPage(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());
        return data;
    }
}
