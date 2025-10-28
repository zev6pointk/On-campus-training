package com.teacher.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限编码
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限描述
     */
    @TableField("description")
    private String description;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 状态 0-禁用 1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
