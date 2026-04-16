package com.muzi.healthsys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muzi.Data_unification.Unification;
import com.muzi.healthsys.entity.PhysicalExamAppointment;
import com.muzi.healthsys.entity.PhysicalExamSlot;
import com.muzi.healthsys.service.IPhysicalExamAppointmentService;
import com.muzi.healthsys.service.IPhysicalExamSlotService;
import com.muzi.healthsys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class PhysicalExamAppointmentController {

    @Autowired
    private IPhysicalExamAppointmentService appointmentService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IPhysicalExamSlotService slotService;

    /**
     * 创建体检预约（当前登录用户）
     * body: { slotId: 123 }
     */
    @PostMapping("/create")
    public Unification<?> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> uid = userService.getUserId();
        if (uid == null || uid.get("id") == null) {
            return Unification.fail(20003, "登录信息有误，请重新登录");
        }

        Object slotIdObj = body.get("slotId");
        if (slotIdObj == null || !StringUtils.hasLength(slotIdObj.toString())) {
            return Unification.fail(400, "体检场次不能为空");
        }

        Integer slotId;
        try {
            slotId = Integer.parseInt(slotIdObj.toString());
        } catch (NumberFormatException e) {
            return Unification.fail(400, "体检场次ID格式不正确");
        }

        PhysicalExamSlot slot = slotService.getById(slotId);
        if (slot == null) {
            return Unification.fail(404, "体检场次不存在");
        }

        PhysicalExamAppointment appointment = new PhysicalExamAppointment();
        appointment.setUserId(Integer.parseInt(uid.get("id").toString()));
        appointment.setSlotId(slotId);
        appointment.setExamTime(slot.getExamTime());
        appointment.setLocation(slot.getRegion());
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

    /**
     * 管理员查看：预约分页列表（不做权限校验，菜单控制在前端）
     */
    @GetMapping("/list")
    public Unification<Map<String, Object>> list(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize
    ) {
        LambdaQueryWrapper<PhysicalExamAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, PhysicalExamAppointment::getUserId, userId);
        wrapper.orderByDesc(PhysicalExamAppointment::getCreatedAt);

        Page<PhysicalExamAppointment> page = new Page<>(pageNo, pageSize);
        appointmentService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());
        return Unification.success(data);
    }
}

