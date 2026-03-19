package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建邮件模板请求
 */
@Data
@Schema(description = "创建邮件模板请求")
public class CreateEmailTemplateRequest {

    @NotBlank(message = "模板代码不能为空")
    @Schema(description = "模板代码（唯一标识）", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String templateName;

    @NotBlank(message = "邮件主题不能为空")
    @Schema(description = "邮件主题", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 200)
    private String subject;

    @NotBlank(message = "邮件内容不能为空")
    @Schema(description = "邮件内容（HTML格式）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "邮件内容（纯文本格式）")
    private String plainTextContent;

    @Schema(description = "模板变量说明（JSON格式，描述模板中可用的变量）")
    private String variables;

    @NotBlank(message = "模板类型不能为空")
    @Schema(description = "模板类型：VERIFICATION_CODE-验证码邮件，REGISTRATION_SUCCESS-注册成功邮件，PASSWORD_RESET-密码重置邮件，ORDER_CONFIRMATION-订单确认邮件，JOB_APPLICATION-兼职申请邮件，SYSTEM_NOTIFICATION-系统通知邮件，PROMOTIONAL-促销邮件", requiredMode = Schema.RequiredMode.REQUIRED)
    private String templateType;

    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "1")
    private Integer enabled = 1;

    @Schema(description = "备注")
    private String remark;

    /**
     * 验证模板类型是否有效
     */
    public boolean isValidTemplateType() {
        return com.xushu.campus.notification.constant.NotificationConstants.EmailTemplate.isValid(this.templateType);
    }

    /**
     * 验证请求数据是否有效
     */
    public boolean isValid() {
        return this.templateCode != null && !this.templateCode.trim().isEmpty() &&
                this.templateName != null && !this.templateName.trim().isEmpty() &&
                this.subject != null && !this.subject.trim().isEmpty() &&
                this.content != null && !this.content.trim().isEmpty() &&
                this.templateType != null && this.isValidTemplateType() &&
                this.enabled != null && (this.enabled == 0 || this.enabled == 1);
    }
}