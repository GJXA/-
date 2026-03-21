package com.xushu.campus.user.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

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
     * 角色列表
     */
    private List<String> roles;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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
}