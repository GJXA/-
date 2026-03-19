package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 通知统计信息DTO
 */
@Data
@Schema(description = "通知统计信息")
public class NotificationStatisticsDTO {

    @Schema(description = "总通知数量")
    private Long totalCount;

    @Schema(description = "未读通知数量")
    private Long unreadCount;

    @Schema(description = "已读通知数量")
    private Long readCount;

    @Schema(description = "已删除通知数量")
    private Long deletedCount;

    @Schema(description = "按类型统计的通知数量")
    private Map<String, Long> countByType;

    @Schema(description = "按优先级统计的通知数量")
    private Map<Integer, Long> countByPriority;

    @Schema(description = "按发送方式统计的通知数量")
    private Map<String, Long> countByChannel;

    @Schema(description = "今日通知数量")
    private Long todayCount;

    @Schema(description = "本周通知数量")
    private Long weekCount;

    @Schema(description = "本月通知数量")
    private Long monthCount;

    @Schema(description = "邮件发送成功数量")
    private Long emailSuccessCount;

    @Schema(description = "邮件发送失败数量")
    private Long emailFailedCount;

    @Schema(description = "短信发送成功数量")
    private Long smsSuccessCount;

    @Schema(description = "短信发送失败数量")
    private Long smsFailedCount;

    @Schema(description = "WebSocket发送成功数量")
    private Long websocketSuccessCount;

    @Schema(description = "WebSocket发送失败数量")
    private Long websocketFailedCount;

    @Schema(description = "平均发送延迟（秒）")
    private Double avgSendDelay;

    @Schema(description = "发送成功率（百分比）")
    private Double successRate;
}