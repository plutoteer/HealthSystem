package com.muzi.healthsys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ai_symptom_slot_template")
public class AISymptomSlotTemplate implements Serializable {
    @TableId("id")
    private Integer id;

    @TableField("slot_code")
    private String slotCode;

    @TableField("slot_name")
    private String slotName;

    @TableField("required_flag")
    private Integer requiredFlag;

    @TableField("question_prompt")
    private String questionPrompt;

    @TableField("value_hint")
    private String valueHint;

    @TableField("sort_no")
    private Integer sortNo;

    @TableField("status")
    private Integer status;
}
