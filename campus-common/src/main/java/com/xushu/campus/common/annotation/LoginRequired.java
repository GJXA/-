package com.xushu.campus.common.annotation;

import java.lang.annotation.*;

/**
 * 登录验证注解
 * 标记需要用户登录才能访问的接口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequired {
    /**
     * 是否要求验证token有效性（默认true）
     */
    boolean verifyToken() default true;
}