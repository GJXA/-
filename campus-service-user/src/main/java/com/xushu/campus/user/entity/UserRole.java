package com.xushu.campus.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_role")
public class UserRole {

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
     * 角色代码：STUDENT, TEACHER, ADMIN
     */
    private String roleCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}