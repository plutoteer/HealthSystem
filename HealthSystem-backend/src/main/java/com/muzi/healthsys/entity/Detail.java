package com.muzi.healthsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("detail")
public class Detail implements Serializable {


    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Integer id;

    @TableField("sport_info_id")
    private Integer sportInfoId;

    @TableField("sport_type")
    private String sportType;

    private String disease;

    private String method;

    private String notes;

    @TableField("image_url")
    private String imageUrl;

    // 以下字段来自sport_info表，使用@TableField(exist = false)表示不在detail表中
    @TableField(exist = false)
    private String suitableTime;

    @TableField(exist = false)
    private String suitableHeartRate;

    @TableField(exist = false)
    private String suitableFrequency;

    @TableField(exist = false)
    private String recommendedSpeed;
}
