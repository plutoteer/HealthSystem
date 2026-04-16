package com.muzi.healthsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员发布的体检场次（地区 + 时间）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("j_physical_exam_slot")
public class PhysicalExamSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("region")
    private String region;

    @TableField("exam_time")
    private Date examTime;

    @TableField("created_at")
    private Date createdAt;
}

