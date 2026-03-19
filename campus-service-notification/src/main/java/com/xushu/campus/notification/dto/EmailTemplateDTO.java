package com.xushu.campus.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件模板DTO
 */
@Data
@Schema(description = "邮件模板信息")
public class EmailTemplateDTO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板代码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "邮件主题")
    private String subject;

    @Schema(description = "邮件内容（HTML格式）")
    private String content;

    @Schema(description = "邮件内容（纯文本格式）")
    private String plainTextContent;

    @Schema(description = "模板变量说明")
    private String variables;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "模板类型描述")
    private String templateTypeDesc;

    @Schema(description = "是否启用")
    private Integer enabled;

    @Schema(description = "是否启用描述")
    private String enabledDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 计算描述信息
     */
    public void calculateDesc() {
        if (this.templateType != null) {
            this.templateTypeDesc = com.xushu.campus.notification.constant.NotificationConstants.EmailTemplate.getDesc(this.templateType);
        }
        if (this.enabled != null) {
            this.enabledDesc = this.enabled == 1 ? "启用" : "禁用";
        }
    }

    /**
     * 检查是否启用
     */
    public boolean isEnabled() {
        return this.enabled != null && this.enabled == 1;
    }

    /**
     * 获取变量说明的简要信息
     */
    public String getVariablesSummary() {
        if (this.variables == null || this.variables.isEmpty()) {
            return "无变量";
        }
        return this.variables.length() > 50 ? this.variables.substring(0, 50) + "..." : this.variables;
    }
}