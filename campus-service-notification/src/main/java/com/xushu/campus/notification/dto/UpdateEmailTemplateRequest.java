package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新邮件模板请求
 */
@Data
@Schema(description = "更新邮件模板请求")
public class UpdateEmailTemplateRequest {

    @Schema(description = "模板代码（唯一标识）", maxLength = 50)
    private String templateCode;

    @Schema(description = "模板名称", maxLength = 100)
    private String templateName;

    @Schema(description = "邮件主题", maxLength = 200)
    private String subject;

    @Schema(description = "邮件内容（HTML格式）")
    private String content;

    @Schema(description = "邮件内容（纯文本格式）")
    private String plainTextContent;

    @Schema(description = "模板变量说明（JSON格式，描述模板中可用的变量）")
    private String variables;

    @Schema(description = "模板类型：VERIFICATION_CODE-验证码邮件，REGISTRATION_SUCCESS-注册成功邮件，PASSWORD_RESET-密码重置邮件，ORDER_CONFIRMATION-订单确认邮件，JOB_APPLICATION-兼职申请邮件，SYSTEM_NOTIFICATION-系统通知邮件，PROMOTIONAL-促销邮件")
    private String templateType;

    @Schema(description = "是否启用：0-禁用，1-启用")
    private Integer enabled;

    @Schema(description = "备注")
    private String remark;

    /**
     * 验证模板类型是否有效
     */
    public boolean isValidTemplateType() {
        if (this.templateType == null) {
            return true; // 如果没有提供模板类型，则不验证
        }
        return com.xushu.campus.notification.constant.NotificationConstants.EmailTemplate.isValid(this.templateType);
    }

    /**
     * 验证是否至少有一个更新字段
     */
    public boolean hasUpdateFields() {
        return this.templateCode != null ||
                this.templateName != null ||
                this.subject != null ||
                this.content != null ||
                this.plainTextContent != null ||
                this.variables != null ||
                this.templateType != null ||
                this.enabled != null ||
                this.remark != null;
    }

    /**
     * 验证更新数据是否有效
     */
    public boolean isValid() {
        // 如果提供了模板类型，验证其有效性
        if (this.templateType != null && !this.isValidTemplateType()) {
            return false;
        }

        // 如果提供了启用状态，验证其有效性
        if (this.enabled != null && this.enabled != 0 && this.enabled != 1) {
            return false;
        }

        return true;
    }
}