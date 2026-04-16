package com.muzi.healthsys.controller;

import com.muzi.Data_unification.Unification;
import com.muzi.healthsys.entity.InsuranceEnrollment;
import com.muzi.healthsys.entity.InsuranceInfo;
import com.muzi.healthsys.service.IInsuranceService;
import com.muzi.healthsys.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insurance")
public class InsuranceController {

    @Autowired
    private IInsuranceService insuranceService;

    @Autowired
    private IUserService userService;

    // ===== 用户端 =====

    @GetMapping("/list")
    public Unification<List<InsuranceInfo>> list() {
        return Unification.success(insuranceService.getActiveInsuranceList());
    }

    @PostMapping("/enroll")
    public Unification<?> enroll(@RequestBody Map<String, Object> body) {
        Map<String, Object> uid = userService.getUserId();
        if (uid == null || uid.get("id") == null) {
            return Unification.fail(20003, "登录信息有误，请重新登录");
        }

        Integer userId = Integer.parseInt(String.valueOf(uid.get("id")));
        Map<String, Object> result = insuranceService.enrollAndPay(userId, body);

        Boolean ok = (Boolean) result.get("ok");
        if (ok != null && ok) {
            return Unification.success(result, String.valueOf(result.get("message")));
        }
        return Unification.fail(400, String.valueOf(result.get("message")));
    }

    @GetMapping("/my")
    public Unification<List<InsuranceEnrollment>> my() {
        Map<String, Object> uid = userService.getUserId();
        if (uid == null || uid.get("id") == null) {
            return Unification.fail(20003, "登录信息有误，请重新登录");
        }

        Integer userId = Integer.parseInt(String.valueOf(uid.get("id")));
        return Unification.success(insuranceService.getMyEnrollments(userId));
    }

    // ===== 管理员端 =====

    @GetMapping("/page")
    public Unification<Map<String, Object>> page(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "insuranceYear", required = false) String insuranceYear,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize
    ) {
        return Unification.success(insuranceService.getInsuranceInfoPage(title, insuranceYear, pageNo, pageSize));
    }

    @PostMapping("/add")
    public Unification<?> add(@RequestBody InsuranceInfo info) {
        if (info.getTitle() == null || info.getTitle().trim().isEmpty()) {
            return Unification.fail(400, "项目名称不能为空");
        }
        boolean ok = insuranceService.saveInsuranceInfo(info);
        return ok ? Unification.success("发布成功") : Unification.fail(500, "发布失败");
    }

    @PutMapping("/update")
    public Unification<?> update(@RequestBody InsuranceInfo info) {
        if (info.getId() == null) {
            return Unification.fail(400, "ID不能为空");
        }
        boolean ok = insuranceService.updateInsuranceInfo(info);
        return ok ? Unification.success("修改成功") : Unification.fail(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public Unification<?> delete(@PathVariable("id") Integer id) {
        boolean ok = insuranceService.deleteInsuranceInfoById(id);
        return ok ? Unification.success("删除成功") : Unification.fail(500, "删除失败");
    }

    @GetMapping("/enrollment/page")
    public Unification<Map<String, Object>> enrollmentPage(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "insuranceInfoId", required = false) Integer insuranceInfoId,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize
    ) {
        return Unification.success(insuranceService.getEnrollmentPage(userId, insuranceInfoId, pageNo, pageSize));
    }
}
