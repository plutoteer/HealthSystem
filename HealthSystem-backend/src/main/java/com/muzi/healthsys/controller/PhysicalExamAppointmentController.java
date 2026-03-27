package com.muzi.healthsys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.muzi.Data_unification.Unification;
import com.muzi.healthsys.entity.PhysicalExamAppointment;
import com.muzi.healthsys.service.IPhysicalExamAppointmentService;
import com.muzi.healthsys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class PhysicalExamAppointmentController {

    @Autowired
    private IPhysicalExamAppointmentService appointmentService;

    @Autowired
    private IUserService userService;

    /**
     * 创建体检预约（当前登录用户）
     * body: { examTime: "2026-03-27 10:30:00" 或时间戳, location: "xxx" }
     */
    @PostMapping("/create")
    public Unification<?> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> uid = userService.getUserId();
        if (uid == null || uid.get("id") == null) {
            return Unification.fail(20003, "登录信息有误，请重新登录");
        }

        Object examTimeObj = body.get("examTime");
        Object locationObj = body.get("location");

        if (examTimeObj == null) {
            return Unification.fail(400, "体检时间不能为空");
        }
        if (locationObj == null || !StringUtils.hasLength(locationObj.toString())) {
            return Unification.fail(400, "体检地点不能为空");
        }

        Date examTime;
        if (examTimeObj instanceof Number) {
            examTime = new Date(((Number) examTimeObj).longValue());
        } else {
            // 兼容前端直接传 Date 的 JSON 序列化字符串（Spring 会尝试解析；失败则抛异常被全局处理）
            // 为了不引入复杂解析，这里优先让 Spring/Jackson 直接映射成时间戳/标准格式
            // 如果传入的是字符串但无法解析，可改为前端传时间戳
            try {
                long ts = Long.parseLong(examTimeObj.toString());
                examTime = new Date(ts);
            } catch (NumberFormatException e) {
                return Unification.fail(400, "体检时间格式不正确，请传时间戳(毫秒)");
            }
        }

        PhysicalExamAppointment appointment = new PhysicalExamAppointment();
        appointment.setUserId(Integer.parseInt(uid.get("id").toString()));
        appointment.setExamTime(examTime);
        appointment.setLocation(locationObj.toString().trim());
        appointment.setCreatedAt(new Date());

        boolean ok = appointmentService.save(appointment);
        if (ok) {
            return Unification.success("预约成功");
        }
        return Unification.fail(500, "预约失败");
    }

    /**
     * 查询我的预约（按创建时间倒序）
     */
    @GetMapping("/my")
    public Unification<List<PhysicalExamAppointment>> my() {
        Map<String, Object> uid = userService.getUserId();
        if (uid == null || uid.get("id") == null) {
            return Unification.fail(20003, "登录信息有误，请重新登录");
        }

        Integer userId = Integer.parseInt(uid.get("id").toString());
        LambdaQueryWrapper<PhysicalExamAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhysicalExamAppointment::getUserId, userId);
        wrapper.orderByDesc(PhysicalExamAppointment::getCreatedAt);
        List<PhysicalExamAppointment> list = appointmentService.list(wrapper);
        return Unification.success(list);
    }
}

