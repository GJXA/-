package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 失败邮件DTO
 */
@Data
@Schema(description = "失败邮件信息")
public class FailedEmailDTO {

    @Schema(description = "消息ID")
    private String messageId;

    @Schema(description = "收件人")
    private String toAddress;

    @Schema(description = "邮件主题")
    private String subject;

    @Schema(description = "失败原因")
    private String failureReason;

    @Schema(description = "失败时间")
    private LocalDateTime failureTime;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "最后重试时间")
    private LocalDateTime lastRetryTime;

    @Schema(description = "邮件内容摘要")
    private String contentSummary;

    @Schema(description = "关联的通知ID")
    private Long notificationId;

    @Schema(description = "关联的模板代码")
    private String templateCode;

    @Schema(description = "是否可重试")
    private Boolean canRetry;

    @Schema(description = "错误详情")
    private String errorDetails;
}