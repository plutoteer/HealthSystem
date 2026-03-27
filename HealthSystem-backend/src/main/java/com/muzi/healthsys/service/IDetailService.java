package com.muzi.healthsys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.muzi.healthsys.entity.Detail;

import java.util.List;


public interface IDetailService extends IService<Detail> {
    List<Detail> getDetailInfo(String sportName);

    void updateDetail(Detail detail);

    Detail getDetailById(Integer id);

    Detail getDetailBySportInfoId(Integer sportInfoId);

    void deletDetailById(Integer id);

    boolean addDetail(Detail detail);
}



