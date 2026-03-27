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

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("j_physical_exam_appointment")
public class PhysicalExamAppointment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    /**
     * 体检时间（精确到分钟/秒均可，建议 DATETIME）
     */
    @TableField("exam_time")
    private Date examTime;

    /**
     * 体检地点
     */
    @TableField("location")
    private String location;

    @TableField("created_at")
    private Date createdAt;
}

