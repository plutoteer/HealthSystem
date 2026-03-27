package com.muzi.healthsys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muzi.healthsys.entity.Detail;

import java.util.List;

public interface DetailMapper extends BaseMapper<Detail> {
    List<Detail> getDetailInfo(String sportName);
    
    Detail getDetailByIdWithSportInfo(Integer id);
    
    Detail getDetailBySportInfoIdWithSportInfo(Integer sportInfoId);
}

