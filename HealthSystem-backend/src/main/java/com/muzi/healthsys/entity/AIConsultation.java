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
 * AI健康咨询问答实体类
 *
 * @author MuZi
 * @since 2025-01-XX
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("j_ai_qa")
public class AIConsultation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @TableField("keywords")
    private String keywords;
    
    @TableField("category")
    private String category;
    
    @TableField("question")
    private String question;
    
    @TableField("answer")
    private String answer;
    
    @TableField("priority")
    private Integer priority;
    
    @TableField("create_time")
    private Date createTime;
    
    @TableField("update_time")
    private Date updateTime;
}

