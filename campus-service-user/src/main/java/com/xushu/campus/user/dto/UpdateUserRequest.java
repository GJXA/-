package com.xushu.campus.user.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * 更新用户信息请求DTO
 */
@Data
public class UpdateUserRequest {

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
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
}