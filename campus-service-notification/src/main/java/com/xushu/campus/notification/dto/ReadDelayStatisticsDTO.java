package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 阅读延迟统计信息DTO
 */
@Data
@Schema(description = "阅读延迟统计信息")
public class ReadDelayStatisticsDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "总阅读记录数量")
    private Long totalReadRecords;

    @Schema(description = "平均阅读延迟（分钟）")
    private Double averageReadDelay;

    @Schema(description = "中位阅读延迟（分钟）")
    private Double medianReadDelay;

    @Schema(description = "最小阅读延迟（分钟）")
    private Double minReadDelay;

    @Schema(description = "最大阅读延迟（分钟）")
    private Double maxReadDelay;

    @Schema(description = "阅读延迟标准差")
    private Double readDelayStdDev;

    @Schema(description = "即时阅读比例（5分钟内）")
    private Double immediateReadRatio;

    @Schema(description = "快速阅读比例（30分钟内）")
    private Double quickReadRatio;

    @Schema(description = "正常阅读比例（2小时内）")
    private Double normalReadRatio;

    @Schema(description = "延迟阅读比例（2-24小时内）")
    private Double delayedReadRatio;

    @Schema(description = "严重延迟阅读比例（24小时以上）")
    private Double severelyDelayedReadRatio;

    @Schema(description = "从未阅读比例")
    private Double neverReadRatio;

    @Schema(description = "阅读延迟分布")
    private DelayDistribution delayDistribution;

    @Schema(description = "按用户统计的阅读延迟")
    private UserDelayStats[] userDelayStats;

    @Schema(description = "按消息类型统计的阅读延迟")
    private MessageTypeDelayStats[] messageTypeDelayStats;

    @Schema(description = "按时间统计的阅读延迟")
    private TimeBasedDelayStats[] timeBasedDelayStats;

    /**
     * 延迟分布
     */
    @Data
    @Schema(description = "延迟分布")
    public static class DelayDistribution {
        @Schema(description = "延迟区间列表")
        private String[] delayRanges;

        @Schema(description = "阅读数量列表")
        private Long[] readCounts;

        @Schema(description = "百分比列表")
        private Double[] percentages;
    }

    /**
     * 用户延迟统计
     */
    @Data
    @Schema(description = "用户延迟统计")
    public static class UserDelayStats {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名称")
        private String userName;

        @Schema(description = "平均阅读延迟")
        private Double averageDelay;

        @Schema(description = "阅读记录数量")
        private Long readCount;

        @Schema(description = "即时阅读比例")
        private Double immediateReadRatio;

        @Schema(description = "延迟排名")
        private Integer delayRank;
    }

    /**
     * 消息类型延迟统计
     */
    @Data
    @Schema(description = "消息类型延迟统计")
    public static class MessageTypeDelayStats {
        @Schema(description = "消息类型")
        private String messageType;

        @Schema(description = "消息类型描述")
        private String messageTypeDesc;

        @Schema(description = "平均阅读延迟")
        private Double averageDelay;

        @Schema(description = "阅读记录数量")
        private Long readCount;
    }

    /**
     * 基于时间的延迟统计
     */
    @Data
    @Schema(description = "基于时间的延迟统计")
    public static class TimeBasedDelayStats {
        @Schema(description = "时间段")
        private String timeSlot;

        @Schema(description = "平均阅读延迟")
        private Double averageDelay;

        @Schema(description = "阅读记录数量")
        private Long readCount;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算各种比例的总和应为100%
        double totalRatio = (immediateReadRatio != null ? immediateReadRatio : 0) +
                          (quickReadRatio != null ? quickReadRatio : 0) +
                          (normalReadRatio != null ? normalReadRatio : 0) +
                          (delayedReadRatio != null ? delayedReadRatio : 0) +
                          (severelyDelayedReadRatio != null ? severelyDelayedReadRatio : 0) +
                          (neverReadRatio != null ? neverReadRatio : 0);

        // 归一化比例
        if (totalRatio > 0) {
            if (immediateReadRatio != null) immediateReadRatio = immediateReadRatio * 100 / totalRatio;
            if (quickReadRatio != null) quickReadRatio = quickReadRatio * 100 / totalRatio;
            if (normalReadRatio != null) normalReadRatio = normalReadRatio * 100 / totalRatio;
            if (delayedReadRatio != null) delayedReadRatio = delayedReadRatio * 100 / totalRatio;
            if (severelyDelayedReadRatio != null) severelyDelayedReadRatio = severelyDelayedReadRatio * 100 / totalRatio;
            if (neverReadRatio != null) neverReadRatio = neverReadRatio * 100 / totalRatio;
        }
    }

    /**
     * 获取阅读延迟级别
     */
    public String getReadDelayLevel() {
        if (averageReadDelay == null) {
            return "未知";
        }

        if (averageReadDelay <= 5) {
            return "极快";
        } else if (averageReadDelay <= 15) {
            return "快速";
        } else if (averageReadDelay <= 60) {
            return "正常";
        } else if (averageReadDelay <= 180) {
            return "较慢";
        } else if (averageReadDelay <= 720) {
            return "缓慢";
        } else {
            return "极慢";
        }
    }

    /**
     * 获取阅读及时性评分（0-100）
     */
    public Double getReadTimelinessScore() {
        double score = 0.0;

        // 即时阅读比例权重：40%
        if (immediateReadRatio != null) {
            score += immediateReadRatio * 0.4;
        }

        // 快速阅读比例权重：30%
        if (quickReadRatio != null) {
            score += quickReadRatio * 0.3;
        }

        // 平均阅读延迟权重：30%（延迟越短分数越高）
        if (averageReadDelay != null) {
            double delayScore = Math.max(0, 100 - averageReadDelay);
            score += Math.min(delayScore, 100) * 0.3;
        }

        return Math.min(score, 100);
    }

    /**
     * 获取阅读及时性描述
     */
    public String getReadTimelinessDescription() {
        Double score = getReadTimelinessScore();
        if (score == null) {
            return "未知";
        }

        if (score >= 90) {
            return "极佳及时性";
        } else if (score >= 80) {
            return "良好及时性";
        } else if (score >= 70) {
            return "一般及时性";
        } else if (score >= 60) {
            return "较低及时性";
        } else {
            return "差及时性";
        }
    }

    /**
     * 获取延迟稳定性描述
     */
    public String getDelayStabilityDescription() {
        if (readDelayStdDev == null || averageReadDelay == null || averageReadDelay == 0) {
            return "未知";
        }

        double cv = readDelayStdDev / averageReadDelay; // 变异系数
        if (cv < 0.3) {
            return "高度稳定";
        } else if (cv < 0.6) {
            return "中等稳定";
        } else if (cv < 1.0) {
            return "较低稳定";
        } else {
            return "不稳定";
        }
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalReadRecords != null || averageReadDelay != null || immediateReadRatio != null;
    }

    /**
     * 获取阅读响应效率
     */
    public String getReadResponseEfficiency() {
        if (immediateReadRatio == null && quickReadRatio == null) {
            return "未知";
        }

        double quickResponseRatio = (immediateReadRatio != null ? immediateReadRatio : 0) +
                                   (quickReadRatio != null ? quickReadRatio : 0);

        if (quickResponseRatio >= 80) {
            return "高效响应";
        } else if (quickResponseRatio >= 60) {
            return "良好响应";
        } else if (quickResponseRatio >= 40) {
            return "一般响应";
        } else if (quickResponseRatio >= 20) {
            return "较低响应";
        } else {
            return "低效响应";
        }
    }

    /**
     * 获取延迟问题分析
     */
    public String getDelayIssueAnalysis() {
        StringBuilder analysis = new StringBuilder();

        if (severelyDelayedReadRatio != null && severelyDelayedReadRatio > 30) {
            analysis.append("严重延迟阅读比例较高，可能存在消息过载或阅读习惯问题。");
        }

        if (neverReadRatio != null && neverReadRatio > 20) {
            analysis.append("未阅读比例较高，建议优化消息推送策略或内容相关性。");
        }

        if (averageReadDelay != null && averageReadDelay > 120) {
            analysis.append("平均阅读延迟较长，考虑设置阅读提醒或简化消息内容。");
        }

        if (analysis.length() == 0) {
            analysis.append("阅读延迟表现良好，无明显问题。");
        }

        return analysis.toString();
    }

    /**
     * 获取建议的改进措施
     */
    public String getImprovementSuggestions() {
        StringBuilder suggestions = new StringBuilder();

        if (immediateReadRatio != null && immediateReadRatio < 20) {
            suggestions.append("即时阅读比例较低，建议发送重要消息时使用即时通知。");
        }

        if (severelyDelayedReadRatio != null && severelyDelayedReadRatio > 20) {
            suggestions.append("严重延迟比例较高，建议定期发送阅读提醒或摘要。");
        }

        if (neverReadRatio != null && neverReadRatio > 10) {
            suggestions.append("未阅读消息较多，建议优化消息优先级或设置过期清理。");
        }

        if (suggestions.length() == 0) {
            suggestions.append("阅读延迟管理良好，继续保持。");
        }

        return suggestions.toString();
    }

    /**
     * 获取最需要关注的用户（延迟最长的用户）
     */
    public String getUsersNeedingAttention() {
        if (userDelayStats == null || userDelayStats.length == 0) {
            return "无用户数据";
        }

        // 找到延迟最长的用户
        UserDelayStats maxDelayUser = null;
        for (UserDelayStats user : userDelayStats) {
            if (user.getAverageDelay() != null) {
                if (maxDelayUser == null || user.getAverageDelay() > maxDelayUser.getAverageDelay()) {
                    maxDelayUser = user;
                }
            }
        }

        if (maxDelayUser != null) {
            return String.format("%s（平均延迟%.1f分钟）",
                maxDelayUser.getUserName() != null ? maxDelayUser.getUserName() : "用户" + maxDelayUser.getUserId(),
                maxDelayUser.getAverageDelay());
        }

        return "无显著延迟用户";
    }

    /**
     * 获取最易阅读的消息类型
     */
    public String getEasiestToReadMessageType() {
        if (messageTypeDelayStats == null || messageTypeDelayStats.length == 0) {
            return "无消息类型数据";
        }

        // 找到延迟最短的消息类型
        MessageTypeDelayStats minDelayType = null;
        for (MessageTypeDelayStats type : messageTypeDelayStats) {
            if (type.getAverageDelay() != null) {
                if (minDelayType == null || type.getAverageDelay() < minDelayType.getAverageDelay()) {
                    minDelayType = type;
                }
            }
        }

        if (minDelayType != null) {
            return String.format("%s（平均延迟%.1f分钟）",
                minDelayType.getMessageTypeDesc() != null ? minDelayType.getMessageTypeDesc() : minDelayType.getMessageType(),
                minDelayType.getAverageDelay());
        }

        return "无显著差异";
    }
}