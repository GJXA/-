package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 渲染后的邮件内容DTO
 */
@Data
@Schema(description = "渲染后的邮件内容")
public class RenderedEmailDTO {

    @Schema(description = "邮件主题")
    private String subject;

    @Schema(description = "邮件内容（HTML格式）")
    private String htmlContent;

    @Schema(description = "邮件内容（纯文本格式）")
    private String plainTextContent;

    @Schema(description = "模板代码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "使用的变量")
    private java.util.Map<String, Object> variables;

    @Schema(description = "收件人")
    private String toAddress;

    @Schema(description = "抄送地址")
    private String ccAddress;

    @Schema(description = "密送地址")
    private String bccAddress;

    @Schema(description = "是否渲染成功")
    private boolean success = true;

    @Schema(description = "错误信息（如果渲染失败）")
    private String errorMessage;
}