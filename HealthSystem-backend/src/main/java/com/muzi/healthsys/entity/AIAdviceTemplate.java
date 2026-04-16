package com.muzi.healthsys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ai_advice_template")
public class AIAdviceTemplate implements Serializable {
    @TableId("id")
    private Integer id;

    @TableField("template_code")
    private String templateCode;

    @TableField("triage_level")
    private String triageLevel;

    @TableField("action_code")
    private String actionCode;

    @TableField("content")
    private String content;

    @TableField("status")
    private Integer status;
}
