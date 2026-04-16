package com.muzi.healthsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("j_insurance_enrollment")
public class InsuranceEnrollment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("insurance_info_id")
    private Integer insuranceInfoId;

    @TableField("real_name")
    private String realName;

    @TableField("student_no")
    private String studentNo;

    @TableField("id_card")
    private String idCard;

    @TableField("phone")
    private String phone;

    @TableField("major")
    private String major;

    @TableField("grade")
    private String grade;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("pay_status")
    private Integer payStatus;

    @TableField("pay_time")
    private Date payTime;

    @TableField("created_at")
    private Date createdAt;
}
