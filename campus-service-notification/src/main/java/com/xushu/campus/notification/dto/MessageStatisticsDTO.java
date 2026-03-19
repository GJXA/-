package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息统计信息DTO
 */
@Data
@Schema(description = "消息统计信息")
public class MessageStatisticsDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "总消息数量")
    private Long totalMessageCount;

    @Schema(description = "今日消息数量")
    private Long todayMessageCount;

    @Schema(description = "本周消息数量")
    private Long weekMessageCount;

    @Schema(description = "本月消息数量")
    private Long monthMessageCount;

    @Schema(description = "未读消息数量（针对当前用户）")
    private Integer unreadMessageCount;

    @Schema(description = "已读消息数量（针对当前用户）")
    private Integer readMessageCount;

    @Schema(description = "发送的消息数量（针对当前用户）")
    private Integer sentMessageCount;

    @Schema(description = "接收的消息数量（针对当前用户）")
    private Integer receivedMessageCount;

    @Schema(description = "文本消息数量")
    private Long textMessageCount;

    @Schema(description = "图片消息数量")
    private Long imageMessageCount;

    @Schema(description = "文件消息数量")
    private Long fileMessageCount;

    @Schema(description = "语音消息数量")
    private Long voiceMessageCount;

    @Schema(description = "视频消息数量")
    private Long videoMessageCount;

    @Schema(description = "系统消息数量")
    private Long systemMessageCount;

    @Schema(description = "已编辑消息数量")
    private Long editedMessageCount;

    @Schema(description = "已撤回消息数量")
    private Long recalledMessageCount;

    @Schema(description = "有反应的消息数量")
    private Long reactedMessageCount;

    @Schema(description = "总反应数量")
    private Long totalReactionCount;

    @Schema(description = "平均每日消息数量")
    private Double averageDailyMessages;

    @Schema(description = "消息高峰期（小时）")
    private Integer peakHour;

    @Schema(description = "最活跃日期")
    private LocalDateTime mostActiveDate;

    @Schema(description = "最活跃日期消息数量")
    private Long mostActiveDateCount;

    @Schema(description = "消息数量趋势（最近7天）")
    private DailyTrend dailyTrend;

    @Schema(description = "发送者统计")
    private SenderStats senderStats;

    /**
     * 每日趋势
     */
    @Data
    @Schema(description = "每日趋势")
    public static class DailyTrend {
        @Schema(description = "日期列表")
        private String[] dates;

        @Schema(description = "消息数量列表")
        private Long[] messageCounts;
    }

    /**
     * 发送者统计
     */
    @Data
    @Schema(description = "发送者统计")
    public static class SenderStats {
        @Schema(description = "发送者ID")
        private Long senderId;

        @Schema(description = "发送者名称")
        private String senderName;

        @Schema(description = "发送消息数量")
        private Long messageCount;

        @Schema(description = "发送消息百分比")
        private Double percentage;

        @Schema(description = "最后发送时间")
        private LocalDateTime lastSentTime;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算总消息数量
        long total = (textMessageCount != null ? textMessageCount : 0) +
                    (imageMessageCount != null ? imageMessageCount : 0) +
                    (fileMessageCount != null ? fileMessageCount : 0) +
                    (voiceMessageCount != null ? voiceMessageCount : 0) +
                    (videoMessageCount != null ? videoMessageCount : 0) +
                    (systemMessageCount != null ? systemMessageCount : 0);
        this.totalMessageCount = total;

        // 计算已读和未读消息
        if (readMessageCount != null && unreadMessageCount != null) {
            long totalForUser = readMessageCount + unreadMessageCount;
            if (totalForUser > 0) {
                // 可以计算百分比等
            }
        }

        // 计算平均每日消息数量
        if (totalMessageCount != null && totalMessageCount > 0) {
            // 简化计算，实际需要知道会话创建时间
            this.averageDailyMessages = totalMessageCount / 30.0; // 假设30天
        }
    }

    /**
     * 获取文本消息百分比
     */
    public Double getTextMessagePercentage() {
        if (totalMessageCount == null || totalMessageCount == 0 || textMessageCount == null) {
            return 0.0;
        }
        return textMessageCount * 100.0 / totalMessageCount;
    }

    /**
     * 获取图片消息百分比
     */
    public Double getImageMessagePercentage() {
        if (totalMessageCount == null || totalMessageCount == 0 || imageMessageCount == null) {
            return 0.0;
        }
        return imageMessageCount * 100.0 / totalMessageCount;
    }

    /**
     * 获取文件消息百分比
     */
    public Double getFileMessagePercentage() {
        if (totalMessageCount == null || totalMessageCount == 0 || fileMessageCount == null) {
            return 0.0;
        }
        return fileMessageCount * 100.0 / totalMessageCount;
    }

    /**
     * 获取已读消息百分比（针对当前用户）
     */
    public Double getReadMessagePercentage() {
        if (readMessageCount == null || unreadMessageCount == null) {
            return 0.0;
        }
        long total = readMessageCount + unreadMessageCount;
        if (total == 0) {
            return 0.0;
        }
        return readMessageCount * 100.0 / total;
    }

    /**
     * 获取反应率（有反应的消息/总消息）
     */
    public Double getReactionRate() {
        if (totalMessageCount == null || totalMessageCount == 0 || reactedMessageCount == null) {
            return 0.0;
        }
        return reactedMessageCount * 100.0 / totalMessageCount;
    }

    /**
     * 获取平均每个消息的反应数
     */
    public Double getAverageReactionsPerMessage() {
        if (totalMessageCount == null || totalMessageCount == 0 || totalReactionCount == null) {
            return 0.0;
        }
        return totalReactionCount.doubleValue() / totalMessageCount;
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalMessageCount != null || todayMessageCount != null || unreadMessageCount != null;
    }

    /**
     * 获取消息类型分布描述
     */
    public String getMessageTypeDistribution() {
        StringBuilder sb = new StringBuilder();
        if (textMessageCount != null && textMessageCount > 0) {
            sb.append("文本:").append(textMessageCount).append(" ");
        }
        if (imageMessageCount != null && imageMessageCount > 0) {
            sb.append("图片:").append(imageMessageCount).append(" ");
        }
        if (fileMessageCount != null && fileMessageCount > 0) {
            sb.append("文件:").append(fileMessageCount).append(" ");
        }
        return sb.toString().trim();
    }
}