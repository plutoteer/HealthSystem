package com.muzi.healthsys.service;

import com.muzi.healthsys.entity.InsuranceEnrollment;
import com.muzi.healthsys.entity.InsuranceInfo;

import java.util.List;
import java.util.Map;

public interface IInsuranceService {
    List<InsuranceInfo> getActiveInsuranceList();

    Map<String, Object> enrollAndPay(Integer userId, Map<String, Object> body);

    List<InsuranceEnrollment> getMyEnrollments(Integer userId);

    Map<String, Object> getInsuranceInfoPage(String title, String insuranceYear, Long pageNo, Long pageSize);

    boolean saveInsuranceInfo(InsuranceInfo info);

    boolean updateInsuranceInfo(InsuranceInfo info);

    boolean deleteInsuranceInfoById(Integer id);

    Map<String, Object> getEnrollmentPage(Integer userId, Integer insuranceInfoId, Long pageNo, Long pageSize);
}
