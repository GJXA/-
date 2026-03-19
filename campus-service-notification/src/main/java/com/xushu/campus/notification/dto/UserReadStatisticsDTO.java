package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户阅读统计信息DTO
 */
@Data
@Schema(description = "用户阅读统计信息")
public class UserReadStatisticsDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "总阅读消息数量")
    private Long totalReadMessageCount;

    @Schema(description = "今日阅读消息数量")
    private Long todayReadMessageCount;

    @Schema(description = "本周阅读消息数量")
    private Long weekReadMessageCount;

    @Schema(description = "本月阅读消息数量")
    private Long monthReadMessageCount;

    @Schema(description = "总未读消息数量")
    private Long totalUnreadMessageCount;

    @Schema(description = "阅读率（已读/总接收）")
    private Double readRate;

    @Schema(description = "平均阅读时间（分钟）")
    private Double averageReadTime;

    @Schema(description = "中位阅读时间（分钟）")
    private Double medianReadTime;

    @Schema(description = "最快阅读时间（分钟）")
    private Double fastestReadTime;

    @Schema(description = "最慢阅读时间（分钟）")
    private Double slowestReadTime;

    @Schema(description = "阅读完成率（阅读的消息/应阅读的消息）")
    private Double readCompletionRate;

    @Schema(description = "平均每日阅读消息数量")
    private Double averageDailyReadCount;

    @Schema(description = "阅读高峰期（小时）")
    private Integer peakReadHour;

    @Schema(description = "最常阅读的会话ID")
    private Long mostReadConversationId;

    @Schema(description = "最常阅读的会话标题")
    private String mostReadConversationTitle;

    @Schema(description = "在最常阅读会话中的阅读数量")
    private Long mostReadConversationCount;

    @Schema(description = "阅读及时性评分（0-100）")
    private Double readTimelinessScore;

    @Schema(description = "阅读专注度评分（0-100）")
    private Double readFocusScore;

    @Schema(description = "首次阅读时间")
    private LocalDateTime firstReadTime;

    @Schema(description = "最后阅读时间")
    private LocalDateTime lastReadTime;

    @Schema(description = "阅读间隔统计")
    private ReadIntervalStatistics readIntervalStatistics;

    @Schema(description = "阅读时间分布")
    private TimeOfDayDistribution timeOfDayDistribution;

    @Schema(description = "阅读趋势（最近7天）")
    private DailyTrend readTrend;

    /**
     * 阅读间隔统计
     */
    @Data
    @Schema(description = "阅读间隔统计")
    public static class ReadIntervalStatistics {
        @Schema(description = "平均阅读间隔（分钟）")
        private Double averageInterval;

        @Schema(description = "最短阅读间隔（分钟）")
        private Double shortestInterval;

        @Schema(description = "最长阅读间隔（分钟）")
        private Double longestInterval;

        @Schema(description = "阅读间隔稳定性")
        private Double intervalStability;
    }

    /**
     * 时间分布
     */
    @Data
    @Schema(description = "时间分布")
    public static class TimeOfDayDistribution {
        @Schema(description = "时间段列表")
        private String[] timeSlots;

        @Schema(description = "阅读数量列表")
        private Long[] readCounts;

        @Schema(description = "阅读百分比列表")
        private Double[] percentages;
    }

    /**
     * 每日趋势
     */
    @Data
    @Schema(description = "每日趋势")
    public static class DailyTrend {
        @Schema(description = "日期列表")
        private String[] dates;

        @Schema(description = "阅读数量列表")
        private Long[] readCounts;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算平均每日阅读消息数量
        if (totalReadMessageCount != null && totalReadMessageCount > 0) {
            // 简化计算，假设用户注册30天
            this.averageDailyReadCount = totalReadMessageCount / 30.0;
        }

        // 计算阅读率
        if (totalReadMessageCount != null && totalUnreadMessageCount != null) {
            long totalMessages = totalReadMessageCount + totalUnreadMessageCount;
            if (totalMessages > 0) {
                this.readRate = totalReadMessageCount * 100.0 / totalMessages;
            }
        }

        // 计算阅读及时性评分（简化）
        if (averageReadTime != null) {
            // 假设理想平均阅读时间是10分钟
            this.readTimelinessScore = Math.max(0, 100 - averageReadTime);
        }

        // 计算阅读专注度评分（简化）
        if (readCompletionRate != null) {
            this.readFocusScore = Math.min(readCompletionRate, 100);
        }
    }

    /**
     * 获取阅读活跃度级别
     */
    public String getReadActivityLevel() {
        if (averageDailyReadCount == null) {
            return "未知";
        }

        if (averageDailyReadCount == 0) {
            return "无阅读";
        } else if (averageDailyReadCount < 5) {
            return "轻度阅读";
        } else if (averageDailyReadCount < 20) {
            return "中度阅读";
        } else if (averageDailyReadCount < 50) {
            return "重度阅读";
        } else {
            return "极度阅读";
        }
    }

    /**
     * 获取阅读及时性描述
     */
    public String getReadTimelinessDescription() {
        if (averageReadTime == null) {
            return "未知";
        }

        if (averageReadTime <= 5) {
            return "及时阅读";
        } else if (averageReadTime <= 15) {
            return "正常阅读";
        } else if (averageReadTime <= 60) {
            return "延迟阅读";
        } else if (averageReadTime <= 240) {
            return "严重延迟";
        } else {
            return "极少阅读";
        }
    }

    /**
     * 获取阅读专注度描述
     */
    public String getReadFocusDescription() {
        if (readCompletionRate == null) {
            return "未知";
        }

        if (readCompletionRate >= 90) {
            return "高度专注";
        } else if (readCompletionRate >= 70) {
            return "适度专注";
        } else if (readCompletionRate >= 50) {
            return "一般专注";
        } else {
            return "低专注度";
        }
    }

    /**
     * 获取阅读习惯描述
     */
    public String getReadingHabitDescription() {
        StringBuilder sb = new StringBuilder();

        // 基于阅读高峰期
        if (peakReadHour != null) {
            if (peakReadHour >= 6 && peakReadHour <= 10) {
                sb.append("早晨阅读型");
            } else if (peakReadHour >= 11 && peakReadHour <= 14) {
                sb.append("午间阅读型");
            } else if (peakReadHour >= 15 && peakReadHour <= 18) {
                sb.append("下午阅读型");
            } else if (peakReadHour >= 19 && peakReadHour <= 22) {
                sb.append("晚间阅读型");
            } else {
                sb.append("夜间阅读型");
            }
        }

        // 基于阅读频率
        if (averageDailyReadCount != null) {
            if (averageDailyReadCount > 30) {
                sb.append("高频阅读者");
            } else if (averageDailyReadCount > 10) {
                sb.append("中频阅读者");
            } else if (averageDailyReadCount > 0) {
                sb.append("低频阅读者");
            }
        }

        return sb.toString().trim();
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalReadMessageCount != null || todayReadMessageCount != null || averageReadTime != null;
    }

    /**
     * 获取阅读效率评分（综合评分）
     */
    public Double getReadEfficiencyScore() {
        double score = 0.0;

        // 阅读率权重：30%
        if (readRate != null) {
            score += Math.min(readRate, 100) * 0.3;
        }

        // 阅读及时性权重：30%
        if (readTimelinessScore != null) {
            score += readTimelinessScore * 0.3;
        }

        // 阅读专注度权重：20%
        if (readFocusScore != null) {
            score += readFocusScore * 0.2;
        }

        // 阅读频率权重：20%
        if (averageDailyReadCount != null) {
            double frequencyScore = Math.min(averageDailyReadCount * 2, 100);
            score += frequencyScore * 0.2;
        }

        return Math.min(score, 100);
    }

    /**
     * 获取阅读效率级别
     */
    public String getReadEfficiencyLevel() {
        Double score = getReadEfficiencyScore();
        if (score == null) {
            return "未知";
        }

        if (score >= 90) {
            return "优秀";
        } else if (score >= 80) {
            return "良好";
        } else if (score >= 70) {
            return "中等";
        } else if (score >= 60) {
            return "合格";
        } else {
            return "需改进";
        }
    }

    /**
     * 获取建议的改进措施
     */
    public String getImprovementSuggestions() {
        StringBuilder suggestions = new StringBuilder();

        if (readRate != null && readRate < 60) {
            suggestions.append("阅读率较低，建议设置阅读提醒或优化消息通知。");
        }

        if (averageReadTime != null && averageReadTime > 60) {
            suggestions.append("阅读响应时间较长，建议定期查看消息避免堆积。");
        }

        if (readCompletionRate != null && readCompletionRate < 70) {
            suggestions.append("阅读完成率不足，建议优先阅读重要会话消息。");
        }

        if (averageDailyReadCount != null && averageDailyReadCount < 5) {
            suggestions.append("阅读频率较低，建议培养定期查看消息的习惯。");
        }

        if (suggestions.length() == 0) {
            suggestions.append("阅读习惯良好，继续保持。");
        }

        return suggestions.toString();
    }

    /**
     * 获取最近阅读活跃度
     */
    public String getRecentReadActivity() {
        if (todayReadMessageCount == null) {
            return "未知";
        }

        if (todayReadMessageCount == 0) {
            return "今日无阅读";
        } else if (todayReadMessageCount < 5) {
            return "今日少量阅读";
        } else if (todayReadMessageCount < 20) {
            return "今日适度阅读";
        } else if (todayReadMessageCount < 50) {
            return "今日频繁阅读";
        } else {
            return "今日大量阅读";
        }
    }
}