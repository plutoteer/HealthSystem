package com.muzi.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muzi.healthsys.entity.Detail;
import com.muzi.healthsys.entity.SportInfo;
import com.muzi.healthsys.mapper.DetailMapper;
import com.muzi.healthsys.service.IDetailService;
import com.muzi.healthsys.service.ISportInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;


@Service
public class DetailServiceImpl extends ServiceImpl<DetailMapper, Detail> implements IDetailService {


    @Resource
    private DetailMapper detailMapper;

    @Autowired
    private ISportInfoService sportInfoService;


    @Override
    public List<Detail> getDetailInfo(String sportName) {

        return detailMapper.getDetailInfo(sportName);
    }




    @Transactional
    @Override
    public boolean addDetail(Detail detail) {
        // 如果提供了sportInfoId，直接使用
        // 如果没有提供sportInfoId但提供了sportType，则根据sportType查找对应的sportInfoId
        if (detail.getSportInfoId() == null && detail.getSportType() != null) {
            QueryWrapper<SportInfo> sportWrapper = new QueryWrapper<>();
            sportWrapper.eq("sport_type", detail.getSportType());
            List<SportInfo> sportInfoList = sportInfoService.list(sportWrapper);
            if (!sportInfoList.isEmpty()) {
                detail.setSportInfoId(sportInfoList.get(0).getId());
            }
        }

        // 检查是否已存在相同的sportInfoId的记录
        if (detail.getSportInfoId() != null) {
            QueryWrapper<Detail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sport_info_id", detail.getSportInfoId());
            List<Detail> list = this.baseMapper.selectList(queryWrapper);
            System.out.println(list);
            if (list.isEmpty()) {
                this.baseMapper.insert(detail);
                return true;
            } else {
                return false;
            }
        } else {
            // 如果没有找到对应的sportInfoId，则使用原来的逻辑（通过sportType检查）
            QueryWrapper<Detail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sport_type", detail.getSportType());
            List<Detail> list = this.baseMapper.selectList(queryWrapper);
            System.out.println(list);
            if (list.isEmpty()) {
                this.baseMapper.insert(detail);
                return true;
            } else {
                return false;
            }
        }
    }


    @Override
    public void updateDetail(Detail detail) {
        // 更新用户表中的数据
        this.baseMapper.updateById(detail);
    }

    @Override
    public Detail getDetailById(Integer id) {
        System.out.println(id);
        // 使用联表查询，获取包含sport_info表字段的完整数据
        Detail detail = detailMapper.getDetailByIdWithSportInfo(id);
        return detail;
    }

    @Override
    public Detail getDetailBySportInfoId(Integer sportInfoId) {
        // 使用联表查询，获取包含sport_info表字段的完整数据
        Detail detail = detailMapper.getDetailBySportInfoIdWithSportInfo(sportInfoId);
        return detail;
    }

    @Override
    public void deletDetailById(Integer id) {
        this.baseMapper.deleteById(id);
    }
}


