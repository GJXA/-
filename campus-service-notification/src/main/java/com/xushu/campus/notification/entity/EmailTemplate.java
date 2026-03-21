package com.xushu.campus.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件模板实体类
 */
@Data
@TableName("email_templates")
public class EmailTemplate {

    /**
     * 模板ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板代码（唯一标识）
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容（HTML格式）
     */
    private String content;

    /**
     * 邮件内容（纯文本格式）
     */
    private String plainTextContent;

    /**
     * 模板变量说明（JSON格式，描述模板中可用的变量）
     */
    private String variables;

    /**
     * 模板类型：VERIFICATION_CODE-验证码邮件，REGISTRATION_SUCCESS-注册成功邮件等
     */
    private String templateType;

    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isEnabled;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;
}