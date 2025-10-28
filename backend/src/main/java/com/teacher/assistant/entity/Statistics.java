package com.teacher.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统计数据实体类
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("statistics")
public class Statistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 统计类型 user_count-用户数量 active_users-活跃用户 chat_count-对话数量
     */
    @TableField("stat_type")
    private String statType;

    /**
     * 统计值
     */
    @TableField("stat_value")
    private Long statValue;

    /**
     * 统计时间
     */
    @TableField("stat_date")
    private LocalDateTime statDate;

    /**
     * 维度信息 (如院系、专业等)
     */
    @TableField("dimension")
    private String dimension;

    /**
     * 维度值
     */
    @TableField("dimension_value")
    private String dimensionValue;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
