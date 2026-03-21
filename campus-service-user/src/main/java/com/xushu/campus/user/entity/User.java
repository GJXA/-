package com.xushu.campus.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 学号/工号
     */
    private String studentId;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 学校
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 年级
     */
    private String grade;

    /**
     * 地址
     */
    private String address;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;
}