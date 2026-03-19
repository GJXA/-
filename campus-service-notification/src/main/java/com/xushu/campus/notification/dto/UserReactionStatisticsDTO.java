package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户反应统计信息DTO
 */
@Data
@Schema(description = "用户反应统计信息")
public class UserReactionStatisticsDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "总给出的反应数量")
    private Long totalReactionsGiven;

    @Schema(description = "总收到的反应数量")
    private Long totalReactionsReceived;

    @Schema(description = "今日给出的反应数量")
    private Long todayReactionsGiven;

    @Schema(description = "本周给出的反应数量")
    private Long weekReactionsGiven;

    @Schema(description = "本月给出的反应数量")
    private Long monthReactionsGiven;

    @Schema(description = "给出的点赞数量")
    private Long likesGiven;

    @Schema(description = "给出的爱心数量")
    private Long heartsGiven;

    @Schema(description = "给出的大笑数量")
    private Long laughsGiven;

    @Schema(description = "给出的难过数量")
    private Long sadsGiven;

    @Schema(description = "给出的生气数量")
    private Long angersGiven;

    @Schema(description = "给出的其他反应数量")
    private Long othersGiven;

    @Schema(description = "收到的点赞数量")
    private Long likesReceived;

    @Schema(description = "收到的爱心数量")
    private Long heartsReceived;

    @Schema(description = "收到的大笑数量")
    private Long laughsReceived;

    @Schema(description = "收到的难过数量")
    private Long sadsReceived;

    @Schema(description = "收到的生气数量")
    private Long angersReceived;

    @Schema(description = "收到的其他反应数量")
    private Long othersReceived;

    @Schema(description = "给出的反应类型分布")
    private Map<String, Long> givenReactionDistribution;

    @Schema(description = "收到的反应类型分布")
    private Map<String, Long> receivedReactionDistribution;

    @Schema(description = "最常给出的反应类型")
    private String mostGivenReactionType;

    @Schema(description = "最常收到反应的消息类型")
    private String mostReceivedReactionMessageType;

    @Schema(description = "反应频率（每日平均）")
    private Double averageDailyReactions;

    @Schema(description = "反应及时性（反应时间差平均值）")
    private Double averageReactionTime;

    @Schema(description = "反应影响力评分（0-100）")
    private Double reactionImpactScore;

    @Schema(description = "反应活跃度评分（0-100）")
    private Double reactionActivityScore;

    @Schema(description = "反应多样性指数（给出的反应）")
    private Double givenReactionDiversity;

    @Schema(description = "反应趋势（最近7天）")
    private ReactionTrend reactionTrend;

    @Schema(description = "最常互动的用户")
    private List<InteractionUser> mostInteractedUsers;

    @Schema(description = "最受欢迎的消息（按收到的反应）")
    private List<PopularMessage> mostPopularMessages;

    /**
     * 互动用户
     */
    @Data
    @Schema(description = "互动用户")
    public static class InteractionUser {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名称")
        private String userName;

        @Schema(description = "互动次数（给出的反应）")
        private Long interactionsGiven;

        @Schema(description = "互动次数（收到的反应）")
        private Long interactionsReceived;

        @Schema(description = "总互动次数")
        private Long totalInteractions;
    }

    /**
     * 受欢迎消息
     */
    @Data
    @Schema(description = "受欢迎消息")
    public static class PopularMessage {
        @Schema(description = "消息ID")
        private Long messageId;

        @Schema(description = "消息内容摘要")
        private String messageContent;

        @Schema(description = "会话ID")
        private Long conversationId;

        @Schema(description = "会话标题")
        private String conversationTitle;

        @Schema(description = "收到的反应数量")
        private Long reactionCount;

        @Schema(description = "最常见的反应类型")
        private String mostCommonReactionType;
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

        @Schema(description = "最近7天给出的反应")
        private Long recentReactionsGiven;

        @Schema(description = "最近7天收到的反应")
        private Long recentReactionsReceived;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算每日平均反应数量
        if (totalReactionsGiven != null && totalReactionsGiven > 0) {
            // 假设用户注册30天
            this.averageDailyReactions = totalReactionsGiven / 30.0;
        }

        // 计算反应影响力评分（简化）
        if (totalReactionsReceived != null && totalReactionsGiven != null) {
            double impact = 0.0;
            // 收到的反应权重更高
            if (totalReactionsReceived > 0) {
                impact += Math.min(totalReactionsReceived * 0.5, 50);
            }
            // 给出的反应也有贡献
            if (totalReactionsGiven > 0) {
                impact += Math.min(totalReactionsGiven * 0.2, 20);
            }
            // 多样性加分
            if (givenReactionDiversity != null) {
                impact += givenReactionDiversity * 30;
            }
            this.reactionImpactScore = Math.min(impact, 100);
        }

        // 计算反应活跃度评分（简化）
        if (averageDailyReactions != null) {
            double activityScore = Math.min(averageDailyReactions * 10, 100);
            this.reactionActivityScore = activityScore;
        }
    }

    /**
     * 获取反应活跃度级别
     */
    public String getReactionActivityLevel() {
        if (averageDailyReactions == null) {
            return "未知";
        }

        if (averageDailyReactions == 0) {
            return "无反应";
        } else if (averageDailyReactions < 1) {
            return "轻度活跃";
        } else if (averageDailyReactions < 5) {
            return "中度活跃";
        } else if (averageDailyReactions < 10) {
            return "高度活跃";
        } else {
            return "极度活跃";
        }
    }

    /**
     * 获取反应类型偏好描述
     */
    public String getReactionPreferenceDescription() {
        if (mostGivenReactionType == null) {
            return "无偏好数据";
        }

        switch (mostGivenReactionType) {
            case "LIKE":
                return "偏好点赞表达";
            case "HEART":
                return "偏好爱心表达";
            case "LAUGH":
                return "偏好大笑表达";
            case "SAD":
                return "偏好难过表达";
            case "ANGRY":
                return "偏好生气表达";
            default:
                return "多样化表达";
        }
    }

    /**
     * 获取反应影响力描述
     */
    public String getReactionImpactDescription() {
        if (reactionImpactScore == null) {
            return "未知";
        }

        if (reactionImpactScore >= 90) {
            return "极高影响力";
        } else if (reactionImpactScore >= 80) {
            return "高影响力";
        } else if (reactionImpactScore >= 70) {
            return "中等影响力";
        } else if (reactionImpactScore >= 60) {
            return "一般影响力";
        } else {
            return "低影响力";
        }
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalReactionsGiven != null || totalReactionsReceived != null || averageDailyReactions != null;
    }

    /**
     * 获取反应互动描述
     */
    public String getInteractionDescription() {
        if (totalReactionsGiven == null && totalReactionsReceived == null) {
            return "无互动数据";
        }

        long given = totalReactionsGiven != null ? totalReactionsGiven : 0;
        long received = totalReactionsReceived != null ? totalReactionsReceived : 0;

        if (given == 0 && received == 0) {
            return "无互动";
        } else if (given > received * 2) {
            return "主动互动型";
        } else if (received > given * 2) {
            return "被动互动型";
        } else {
            return "平衡互动型";
        }
    }

    /**
     * 获取建议的改进措施
     */
    public String getImprovementSuggestions() {
        StringBuilder suggestions = new StringBuilder();

        if (averageDailyReactions != null && averageDailyReactions < 0.5) {
            suggestions.append("反应频率较低，建议积极参与互动。");
        }

        if (givenReactionDiversity != null && givenReactionDiversity < 0.3) {
            suggestions.append("反应类型单一，建议尝试使用不同反应类型表达情感。");
        }

        if (totalReactionsReceived != null && totalReactionsReceived == 0 && totalReactionsGiven != null && totalReactionsGiven > 10) {
            suggestions.append("给出反应较多但未收到反应，建议优化消息内容以吸引互动。");
        }

        if (suggestions.length() == 0) {
            suggestions.append("反应互动良好，继续保持。");
        }

        return suggestions.toString();
    }

    /**
     * 获取最有效的互动时段
     */
    public String getMostEffectiveInteractionTime() {
        // 简化实现
        if (averageReactionTime != null && averageReactionTime < 30) {
            return "快速反应型（平均" + String.format("%.1f", averageReactionTime) + "分钟）";
        } else if (averageReactionTime != null && averageReactionTime < 120) {
            return "正常反应型（平均" + String.format("%.1f", averageReactionTime) + "分钟）";
        } else if (averageReactionTime != null) {
            return "延迟反应型（平均" + String.format("%.1f", averageReactionTime) + "分钟）";
        } else {
            return "无数据";
        }
    }
}