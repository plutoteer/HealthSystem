package com.muzi.healthsys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muzi.healthsys.entity.PhysicalExamSlot;
import com.muzi.healthsys.mapper.PhysicalExamSlotMapper;
import com.muzi.healthsys.service.IPhysicalExamSlotService;
import org.springframework.stereotype.Service;

@Service
public class PhysicalExamSlotServiceImpl
        extends ServiceImpl<PhysicalExamSlotMapper, PhysicalExamSlot>
        implements IPhysicalExamSlotService {
}

