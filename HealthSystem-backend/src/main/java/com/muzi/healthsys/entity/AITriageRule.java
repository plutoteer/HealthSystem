package com.muzi.healthsys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ai_triage_rule")
public class AITriageRule implements Serializable {
    @TableId("id")
    private Integer id;

    @TableField("rule_name")
    private String ruleName;

    @TableField("rule_type")
    private String ruleType;

    @TableField("condition_json")
    private String conditionJson;

    @TableField("triage_level")
    private String triageLevel;

    @TableField("action_code")
    private String actionCode;

    @TableField("advice_text")
    private String adviceText;

    @TableField("priority")
    private Integer priority;

    @TableField("status")
    private Integer status;
}
