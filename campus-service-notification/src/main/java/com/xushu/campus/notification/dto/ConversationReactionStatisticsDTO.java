package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 会话反应统计信息DTO
 */
@Data
@Schema(description = "会话反应统计信息")
public class ConversationReactionStatisticsDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "总反应数量")
    private Long totalReactionCount;

    @Schema(description = "总消息数量")
    private Long totalMessageCount;

    @Schema(description = "平均每消息反应数量")
    private Double averageReactionsPerMessage;

    @Schema(description = "反应消息比例（有反应的消息/总消息）")
    private Double reactionMessageRatio;

    @Schema(description = "点赞总数量")
    private Long totalLikeCount;

    @Schema(description = "爱心总数量")
    private Long totalHeartCount;

    @Schema(description = "大笑总数量")
    private Long totalLaughCount;

    @Schema(description = "难过总数量")
    private Long totalSadCount;

    @Schema(description = "生气总数量")
    private Long totalAngryCount;

    @Schema(description = "其他反应总数量")
    private Long totalOtherCount;

    @Schema(description = "反应类型分布")
    private Map<String, Long> reactionTypeDistribution;

    @Schema(description = "最常见的反应类型")
    private String mostCommonReactionType;

    @Schema(description = "最常用反应类型数量")
    private Long mostCommonReactionCount;

    @Schema(description = "最常用反应类型百分比")
    private Double mostCommonReactionPercentage;

    @Schema(description = "反应多样性指数")
    private Double reactionDiversityIndex;

    @Schema(description = "最活跃的用户（按反应数量）")
    private List<UserReactionStats> mostActiveUsers;

    @Schema(description = "最受欢迎的消息（按反应数量）")
    private List<MessageReactionStats> mostPopularMessages;

    @Schema(description = "反应密度（反应/阅读）")
    private Double reactionDensity;

    @Schema(description = "反应趋势（最近7天）")
    private ReactionTrend reactionTrend;

    @Schema(description = "反应高峰时段（小时）")
    private Integer peakReactionHour;

    /**
     * 用户反应统计
     */
    @Data
    @Schema(description = "用户反应统计")
    public static class UserReactionStats {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名称")
        private String userName;

        @Schema(description = "反应数量")
        private Long reactionCount;

        @Schema(description = "最常使用的反应类型")
        private String mostUsedReactionType;

        @Schema(description = "反应多样性指数")
        private Double userReactionDiversity;
    }

    /**
     * 消息反应统计
     */
    @Data
    @Schema(description = "消息反应统计")
    public static class MessageReactionStats {
        @Schema(description = "消息ID")
        private Long messageId;

        @Schema(description = "消息内容摘要")
        private String messageContent;

        @Schema(description = "发送者ID")
        private Long senderId;

        @Schema(description = "发送者名称")
        private String senderName;

        @Schema(description = "反应数量")
        private Long reactionCount;

        @Schema(description = "最常见的反应类型")
        private String mostCommonReactionType;

        @Schema(description = "反应多样性指数")
        private Double messageReactionDiversity;
    }

    /**
     * 反应趋势
     */
    @Data
    @Schema(description = "反应趋势")
    public static class ReactionTrend {
        @Schema(description = "趋势方向")
        private String direction; // UP, DOWN, STABLE

        @Schema(description = "变化百分比")
        private Double changePercentage;

        @Schema(description = "最近7天新增反应")
        private Long recentAdditions;

        @Schema(description = "日均反应数量")
        private Double averageDailyReactions;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算平均每消息反应数量
        if (totalReactionCount != null && totalMessageCount != null && totalMessageCount > 0) {
            this.averageReactionsPerMessage = totalReactionCount.doubleValue() / totalMessageCount;
        }

        // 计算反应消息比例（简化）
        if (totalMessageCount != null && totalMessageCount > 0) {
            // 假设50%的消息有反应（简化计算）
            this.reactionMessageRatio = 0.5;
        }

        // 计算最常用反应类型百分比
        if (mostCommonReactionCount != null && totalReactionCount != null && totalReactionCount > 0) {
            this.mostCommonReactionPercentage = mostCommonReactionCount * 100.0 / totalReactionCount;
        }

        // 计算反应密度（简化）
        if (totalReactionCount != null && totalMessageCount != null && totalMessageCount > 0) {
            // 假设每个消息平均被阅读10次（简化）
            this.reactionDensity = totalReactionCount.doubleValue() / (totalMessageCount * 10.0);
        }
    }

    /**
     * 获取反应活跃度级别
     */
    public String getReactionActivityLevel() {
        if (averageReactionsPerMessage == null) {
            return "未知";
        }

        if (averageReactionsPerMessage == 0) {
            return "无反应";
        } else if (averageReactionsPerMessage < 1) {
            return "低活跃度";
        } else if (averageReactionsPerMessage < 3) {
            return "中等活跃度";
        } else if (averageReactionsPerMessage < 5) {
            return "高活跃度";
        } else {
            return "极高活跃度";
        }
    }

    /**
     * 获取反应多样性描述
     */
    public String getDiversityDescription() {
        if (reactionDiversityIndex == null) {
            return "未知";
        }

        if (reactionDiversityIndex < 0.3) {
            return "低多样性";
        } else if (reactionDiversityIndex < 0.6) {
            return "中等多样性";
        } else if (reactionDiversityIndex < 0.8) {
            return "高多样性";
        } else {
            return "极高多样性";
        }
    }

    /**
     * 获取反应氛围描述
     */
    public String getReactionAtmosphereDescription() {
        if (mostCommonReactionType == null) {
            return "未知";
        }

        switch (mostCommonReactionType) {
            case "LIKE":
                return "积极氛围（点赞为主）";
            case "HEART":
                return "温馨氛围（爱心为主）";
            case "LAUGH":
                return "欢乐氛围（大笑为主）";
            case "SAD":
                return "同情氛围（难过为主）";
            case "ANGRY":
                return "愤怒氛围（生气为主）";
            default:
                return "多样化氛围";
        }
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalReactionCount != null || totalMessageCount != null || mostCommonReactionType != null;
    }

    /**
     * 获取建议的改进措施
     */
    public String getImprovementSuggestions() {
        StringBuilder suggestions = new StringBuilder();

        if (averageReactionsPerMessage != null && averageReactionsPerMessage < 0.5) {
            suggestions.append("会话反应率较低，建议鼓励用户使用反应功能。");
        }

        if (reactionDiversityIndex != null && reactionDiversityIndex < 0.3) {
            suggestions.append("反应类型单一，建议引导用户尝试不同反应类型。");
        }

        if (mostCommonReactionType != null && "ANGRY".equals(mostCommonReactionType)) {
            suggestions.append("生气反应较多，建议关注会话内容是否引起用户不满。");
        }

        if (suggestions.length() == 0) {
            suggestions.append("会话反应氛围良好，继续保持。");
        }

        return suggestions.toString();
    }
}