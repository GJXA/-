package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件发送状态DTO
 */
@Data
@Schema(description = "邮件发送状态")
public class EmailSendStatusDTO {

    @Schema(description = "消息ID")
    private String messageId;

    @Schema(description = "收件人")
    private String toAddress;

    @Schema(description = "邮件主题")
    private String subject;

    @Schema(description = "发送状态: SENT-已发送, DELIVERED-已送达, FAILED-发送失败, BOUNCED-退回")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "送达时间")
    private LocalDateTime deliveredTime;

    @Schema(description = "失败原因")
    private String failureReason;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "是否已重试")
    private Boolean retried;

    @Schema(description = "邮件内容摘要")
    private String contentSummary;
}