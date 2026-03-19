package com.xushu.campus.common.annotation;

import java.lang.annotation.*;

/**
 * 角色验证注解
 * 标记需要特定角色才能访问的接口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleRequired {
    /**
     * 需要的角色（如 "ADMIN", "USER" 等）
     */
    String value();

    /**
     * 是否要求所有角色都满足（默认false，表示满足任意一个角色即可）
     */
    boolean requireAll() default false;
}