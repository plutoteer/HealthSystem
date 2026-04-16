package com.muzi.healthsys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muzi.Data_unification.Unification;
import com.muzi.healthsys.entity.PhysicalExamSlot;
import com.muzi.healthsys.service.IPhysicalExamSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/examSlot")
public class PhysicalExamSlotController {

    @Autowired
    private IPhysicalExamSlotService slotService;

    /**
     * 场次分页（管理员发布/管理用）
     */
    @GetMapping("/list")
    public Unification<Map<String, Object>> list(
            @RequestParam(value = "region", required = false) String region,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize
    ) {
        LambdaQueryWrapper<PhysicalExamSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(region), PhysicalExamSlot::getRegion, region);
        wrapper.orderByDesc(PhysicalExamSlot::getExamTime);

        Page<PhysicalExamSlot> page = new Page<>(pageNo, pageSize);
        slotService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());
        return Unification.success(data);
    }

    /**
     * 场次列表（用户预约弹窗下拉框用）
     */
    @GetMapping("/all")
    public Unification<?> all() {
        LambdaQueryWrapper<PhysicalExamSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(PhysicalExamSlot::getExamTime);
        return Unification.success(slotService.list(wrapper));
    }

    @PostMapping("/add")
    public Unification<?> add(@RequestBody PhysicalExamSlot slot) {
        if (!StringUtils.hasLength(slot.getRegion())) {
            return Unification.fail(400, "体检地区不能为空");
        }
        if (slot.getExamTime() == null) {
            return Unification.fail(400, "体检时间不能为空");
        }
        slot.setCreatedAt(new Date());
        boolean ok = slotService.save(slot);
        return ok ? Unification.success("发布成功") : Unification.fail(500, "发布失败");
    }

    @PutMapping("/update")
    public Unification<?> update(@RequestBody PhysicalExamSlot slot) {
        if (slot.getId() == null) {
            return Unification.fail(400, "ID不能为空");
        }
        boolean ok = slotService.updateById(slot);
        return ok ? Unification.success("修改成功") : Unification.fail(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public Unification<?> delete(@PathVariable("id") Integer id) {
        boolean ok = slotService.removeById(id);
        return ok ? Unification.success("删除成功") : Unification.fail(500, "删除失败");
    }
}

