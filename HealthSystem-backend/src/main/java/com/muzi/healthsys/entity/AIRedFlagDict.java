package com.muzi.healthsys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ai_red_flag_dict")
public class AIRedFlagDict implements Serializable {
    @TableId("id")
    private Integer id;

    @TableField("flag_name")
    private String flagName;

    @TableField("symptom_pattern")
    private String symptomPattern;

    @TableField("logic_type")
    private String logicType;

    @TableField("emergency_level")
    private String emergencyLevel;

    @TableField("advice_text")
    private String adviceText;

    @TableField("priority")
    private Integer priority;

    @TableField("status")
    private Integer status;
}
