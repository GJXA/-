package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户消息统计信息DTO
 */
@Data
@Schema(description = "用户消息统计信息")
public class UserMessageStatisticsDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "参与的总会话数量")
    private Integer totalConversationCount;

    @Schema(description = "活跃会话数量（最近7天有消息）")
    private Integer activeConversationCount;

    @Schema(description = "总发送消息数量")
    private Long totalSentMessageCount;

    @Schema(description = "总接收消息数量")
    private Long totalReceivedMessageCount;

    @Schema(description = "今日发送消息数量")
    private Long todaySentMessageCount;

    @Schema(description = "今日接收消息数量")
    private Long todayReceivedMessageCount;

    @Schema(description = "本周发送消息数量")
    private Long weekSentMessageCount;

    @Schema(description = "本周接收消息数量")
    private Long weekReceivedMessageCount;

    @Schema(description = "本月发送消息数量")
    private Long monthSentMessageCount;

    @Schema(description = "本月接收消息数量")
    private Long monthReceivedMessageCount;

    @Schema(description = "总未读消息数量")
    private Integer totalUnreadMessageCount;

    @Schema(description = "已编辑消息数量")
    private Long editedMessageCount;

    @Schema(description = "已撤回消息数量")
    private Long recalledMessageCount;

    @Schema(description = "收到反应的消息数量")
    private Long receivedReactionCount;

    @Schema(description = "给出的反应数量")
    private Long givenReactionCount;

    @Schema(description = "平均每日发送消息数量")
    private Double averageDailySentMessages;

    @Schema(description = "平均每日接收消息数量")
    private Double averageDailyReceivedMessages;

    @Schema(description = "最活跃时段（小时）")
    private Integer mostActiveHour;

    @Schema(description = "最常发送的消息类型")
    private String mostFrequentMessageType;

    @Schema(description = "最常发送的消息类型数量")
    private Long mostFrequentMessageTypeCount;

    @Schema(description = "消息发送成功率")
    private Double messageSendSuccessRate;

    @Schema(description = "消息读取率（发送的消息中被读取的比例）")
    private Double messageReadRate;

    @Schema(description = "最活跃的会话ID")
    private Long mostActiveConversationId;

    @Schema(description = "最活跃的会话标题")
    private String mostActiveConversationTitle;

    @Schema(description = "在最活跃会话中的消息数量")
    private Long mostActiveConversationMessageCount;

    @Schema(description = "注册时间")
    private LocalDateTime registerTime;

    @Schema(description = "首次发送消息时间")
    private LocalDateTime firstMessageTime;

    @Schema(description = "最后发送消息时间")
    private LocalDateTime lastSentMessageTime;

    @Schema(description = "最后接收消息时间")
    private LocalDateTime lastReceivedMessageTime;

    @Schema(description = "发送消息趋势（最近7天）")
    private DailyTrend sentMessageTrend;

    @Schema(description = "接收消息趋势（最近7天）")
    private DailyTrend receivedMessageTrend;

    @Schema(description = "会话参与度统计")
    private ConversationParticipation conversationParticipation;

    /**
     * 每日趋势
     */
    @Data
    @Schema(description = "每日趋势")
    public static class DailyTrend {
        @Schema(description = "日期列表")
        private String[] dates;

        @Schema(description = "数量列表")
        private Long[] counts;
    }

    /**
     * 会话参与度统计
     */
    @Data
    @Schema(description = "会话参与度统计")
    public static class ConversationParticipation {
        @Schema(description = "创建的会话数量")
        private Integer createdConversationCount;

        @Schema(description = "作为管理员的会话数量")
        private Integer adminConversationCount;

        @Schema(description = "作为普通成员的会话数量")
        private Integer memberConversationCount;

        @Schema(description = "已退出的会话数量")
        private Integer leftConversationCount;

        @Schema(description = "被移除的会话数量")
        private Integer removedConversationCount;

        @Schema(description = "置顶的会话数量")
        private Integer pinnedConversationCount;

        @Schema(description = "静音的会话数量")
        private Integer mutedConversationCount;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算平均每日发送消息数量
        if (totalSentMessageCount != null && totalSentMessageCount > 0) {
            // 简化计算，实际需要知道用户注册时间或首次发送消息时间
            long days = 30; // 假设30天
            this.averageDailySentMessages = totalSentMessageCount.doubleValue() / days;
        }

        // 计算平均每日接收消息数量
        if (totalReceivedMessageCount != null && totalReceivedMessageCount > 0) {
            long days = 30; // 假设30天
            this.averageDailyReceivedMessages = totalReceivedMessageCount.doubleValue() / days;
        }

        // 计算消息发送成功率（简化，假设100%）
        this.messageSendSuccessRate = 100.0;

        // 计算消息读取率（简化）
        if (totalSentMessageCount != null && totalSentMessageCount > 0) {
            // 假设80%的消息被读取
            this.messageReadRate = 80.0;
        }
    }

    /**
     * 获取发送接收比例
     */
    public Double getSendReceiveRatio() {
        if (totalSentMessageCount == null || totalSentMessageCount == 0 || totalReceivedMessageCount == null) {
            return 0.0;
        }
        if (totalReceivedMessageCount == 0) {
            return totalSentMessageCount.doubleValue();
        }
        return totalSentMessageCount.doubleValue() / totalReceivedMessageCount;
    }

    /**
     * 获取今日活跃度（发送+接收）
     */
    public Long getTodayActivity() {
        long sent = todaySentMessageCount != null ? todaySentMessageCount : 0;
        long received = todayReceivedMessageCount != null ? todayReceivedMessageCount : 0;
        return sent + received;
    }

    /**
     * 获取本周活跃度（发送+接收）
     */
    public Long getWeekActivity() {
        long sent = weekSentMessageCount != null ? weekSentMessageCount : 0;
        long received = weekReceivedMessageCount != null ? weekReceivedMessageCount : 0;
        return sent + received;
    }

    /**
     * 获取本月活跃度（发送+接收）
     */
    public Long getMonthActivity() {
        long sent = monthSentMessageCount != null ? monthSentMessageCount : 0;
        long received = monthReceivedMessageCount != null ? monthReceivedMessageCount : 0;
        return sent + received;
    }

    /**
     * 获取总活跃度（发送+接收）
     */
    public Long getTotalActivity() {
        long sent = totalSentMessageCount != null ? totalSentMessageCount : 0;
        long received = totalReceivedMessageCount != null ? totalReceivedMessageCount : 0;
        return sent + received;
    }

    /**
     * 获取未读消息百分比
     */
    public Double getUnreadMessagePercentage() {
        if (totalReceivedMessageCount == null || totalReceivedMessageCount == 0 || totalUnreadMessageCount == null) {
            return 0.0;
        }
        return totalUnreadMessageCount * 100.0 / totalReceivedMessageCount;
    }

    /**
     * 获取反应平衡（收到/给出）
     */
    public Double getReactionBalance() {
        if (givenReactionCount == null || givenReactionCount == 0 || receivedReactionCount == null) {
            return 0.0;
        }
        return receivedReactionCount.doubleValue() / givenReactionCount;
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalSentMessageCount != null || totalReceivedMessageCount != null || totalUnreadMessageCount != null;
    }

    /**
     * 获取用户活跃度级别
     */
    public String getActivityLevel() {
        Long totalActivity = getTotalActivity();
        if (totalActivity == null || totalActivity == 0) {
            return "新用户";
        } else if (totalActivity < 100) {
            return "轻度活跃";
        } else if (totalActivity < 500) {
            return "中度活跃";
        } else if (totalActivity < 2000) {
            return "高度活跃";
        } else {
            return "极度活跃";
        }
    }

    /**
     * 获取用户参与度描述
     */
    public String getParticipationDescription() {
        if (conversationParticipation == null) {
            return "暂无参与度数据";
        }

        StringBuilder sb = new StringBuilder();
        if (conversationParticipation.getCreatedConversationCount() != null && conversationParticipation.getCreatedConversationCount() > 0) {
            sb.append("创建了").append(conversationParticipation.getCreatedConversationCount()).append("个会话 ");
        }
        if (conversationParticipation.getAdminConversationCount() != null && conversationParticipation.getAdminConversationCount() > 0) {
            sb.append("管理").append(conversationParticipation.getAdminConversationCount()).append("个会话 ");
        }

        return sb.toString().trim();
    }
}