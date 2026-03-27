package com.muzi.healthsys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muzi.healthsys.entity.PhysicalExamAppointment;
import com.muzi.healthsys.mapper.PhysicalExamAppointmentMapper;
import com.muzi.healthsys.service.IPhysicalExamAppointmentService;
import org.springframework.stereotype.Service;

@Service
public class PhysicalExamAppointmentServiceImpl
        extends ServiceImpl<PhysicalExamAppointmentMapper, PhysicalExamAppointment>
        implements IPhysicalExamAppointmentService {
}

