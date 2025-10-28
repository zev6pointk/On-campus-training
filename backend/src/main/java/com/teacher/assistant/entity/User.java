package com.teacher.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 性别 0-男 1-女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 用户类型 1-系统管理员 2-教师 3-学生
     */
    @TableField("user_type")
    private Integer userType;

    /**
     * 院系
     */
    @TableField("department")
    private String department;

    /**
     * 专业
     */
    @TableField("major")
    private String major;

    /**
     * 年级
     */
    @TableField("grade")
    private String grade;

    /**
     * 学号/工号
     */
    @TableField("student_id")
    private String studentId;

    /**
     * 状态 0-禁用 1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

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
