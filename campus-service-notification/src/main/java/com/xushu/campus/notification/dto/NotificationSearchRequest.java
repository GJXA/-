package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 通知搜索请求
 */
@Data
@Schema(description = "通知搜索请求")
public class NotificationSearchRequest {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "通知类型：SYSTEM-系统通知，ORDER-订单通知，JOB-兼职通知，PRODUCT-商品通知，USER-用户通知")
    private String type;

    @Schema(description = "通知状态：0-未读，1-已读，2-已删除")
    private Integer status;

    @Schema(description = "通知优先级：0-低，1-普通，2-高，3-紧急")
    private Integer priority;

    @Schema(description = "发送方式：IN_APP-站内消息，EMAIL-电子邮件，SMS-短信，WEBSOCKET-WebSocket")
    private String channel;

    @Schema(description = "关键词（标题、内容、发送人名称等模糊搜索）")
    private String keyword;

    @Schema(description = "发送时间开始（查询发送时间在此之后的通知）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "发送时间结束（查询发送时间在此之前的通知）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "是否包含过期通知")
    private Boolean includeExpired = false;

    @Schema(description = "是否仅查询未读通知")
    private Boolean unreadOnly = false;

    @Schema(description = "是否仅查询高优先级通知（优先级>=2）")
    private Boolean highPriorityOnly = false;

    @Schema(description = "页码，默认1")
    private Integer page = 1;

    @Schema(description = "每页大小，默认20")
    private Integer size = 20;

    @Schema(description = "排序字段，默认send_time", allowableValues = {"send_time", "priority", "create_time"})
    private String sortField = "send_time";

    @Schema(description = "排序方向，默认desc", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";

    /**
     * 验证搜索参数是否有效
     */
    public boolean isValid() {
        // 验证时间范围
        if (this.startTime != null && this.endTime != null) {
            if (this.endTime.isBefore(this.startTime)) {
                return false;
            }
        }

        // 验证通知类型
        if (this.type != null && !com.xushu.campus.notification.constant.NotificationConstants.NotificationType.isValid(this.type)) {
            return false;
        }

        // 验证发送方式
        if (this.channel != null && !com.xushu.campus.notification.constant.NotificationConstants.NotificationChannel.isValid(this.channel)) {
            return false;
        }

        // 验证页码和大小
        if (this.page != null && this.page < 1) {
            return false;
        }
        if (this.size != null && (this.size < 1 || this.size > 100)) {
            return false;
        }

        // 验证优先级
        if (this.priority != null && !com.xushu.campus.notification.constant.NotificationConstants.NotificationPriority.isValid(this.priority)) {
            return false;
        }

        return true;
    }

    /**
     * 检查是否有搜索条件
     */
    public boolean hasSearchCriteria() {
        return this.userId != null ||
                this.type != null ||
                this.status != null ||
                this.priority != null ||
                this.channel != null ||
                this.keyword != null ||
                this.startTime != null ||
                this.endTime != null ||
                this.includeExpired != null ||
                this.unreadOnly != null ||
                this.highPriorityOnly != null;
    }
}