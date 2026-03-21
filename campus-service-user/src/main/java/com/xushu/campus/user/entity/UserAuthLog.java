package com.xushu.campus.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户认证记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_auth_logs")
public class UserAuthLog {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 认证类型：LOGIN-登录，REGISTER-注册，LOGOUT-登出，PASSWORD_RESET-密码重置
     */
    private String authType;

    /**
     * 认证状态：0-失败，1-成功
     */
    private Integer authStatus;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 认证时间
     */
    private LocalDateTime authenticatedAt;

}