package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 阅读统计信息DTO
 */
@Data
@Schema(description = "阅读统计信息")
public class ReadStatisticsDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "总消息数量")
    private Long totalMessageCount;

    @Schema(description = "已读消息数量")
    private Long readMessageCount;

    @Schema(description = "未读消息数量")
    private Long unreadMessageCount;

    @Schema(description = "阅读率（已读/总消息）")
    private Double readRate;

    @Schema(description = "平均阅读时间（分钟）")
    private Double averageReadTime;

    @Schema(description = "中位阅读时间（分钟）")
    private Double medianReadTime;

    @Schema(description = "最快阅读时间（分钟）")
    private Double fastestReadTime;

    @Schema(description = "最慢阅读时间（分钟）")
    private Double slowestReadTime;

    @Schema(description = "今日阅读消息数量")
    private Long todayReadCount;

    @Schema(description = "本周阅读消息数量")
    private Long weekReadCount;

    @Schema(description = "本月阅读消息数量")
    private Long monthReadCount;

    @Schema(description = "总阅读次数")
    private Long totalReadCount;

    @Schema(description = "平均每日阅读消息数量")
    private Double averageDailyReadCount;

    @Schema(description = "阅读高峰期（小时）")
    private Integer peakReadHour;

    @Schema(description = "最活跃阅读者ID")
    private Long mostActiveReaderId;

    @Schema(description = "最活跃阅读者名称")
    private String mostActiveReaderName;

    @Schema(description = "最活跃阅读者阅读数量")
    private Long mostActiveReaderCount;

    @Schema(description = "阅读完成率（所有消息都被阅读的比例）")
    private Double completionRate;

    @Schema(description = "阅读趋势（最近7天）")
    private DailyTrend readTrend;

    @Schema(description = "阅读时间分布")
    private TimeDistribution timeDistribution;

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
     * 时间分布
     */
    @Data
    @Schema(description = "时间分布")
    public static class TimeDistribution {
        @Schema(description = "时间区间列表")
        private String[] timeRanges;

        @Schema(description = "阅读数量列表")
        private Long[] readCounts;

        @Schema(description = "阅读百分比列表")
        private Double[] percentages;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算阅读率
        if (totalMessageCount != null && totalMessageCount > 0 && readMessageCount != null) {
            this.readRate = readMessageCount * 100.0 / totalMessageCount;
        }

        // 计算未读消息数量
        if (totalMessageCount != null && readMessageCount != null) {
            this.unreadMessageCount = totalMessageCount - readMessageCount;
        }

        // 计算平均每日阅读消息数量
        if (totalReadCount != null && totalReadCount > 0) {
            // 简化计算，假设30天
            this.averageDailyReadCount = totalReadCount / 30.0;
        }

        // 计算阅读完成率
        if (totalMessageCount != null && totalMessageCount > 0) {
            // 简化计算，假设有阅读记录的消息比例
            if (readMessageCount != null) {
                this.completionRate = readMessageCount * 100.0 / totalMessageCount;
            }
        }
    }

    /**
     * 获取阅读效率评分（0-100）
     */
    public Double getReadEfficiencyScore() {
        double score = 0.0;

        // 阅读率权重：40%
        if (readRate != null) {
            score += Math.min(readRate, 100) * 0.4;
        }

        // 平均阅读时间权重：30%（时间越短分数越高）
        if (averageReadTime != null) {
            // 假设理想平均阅读时间是5分钟
            double timeScore = Math.max(0, 100 - averageReadTime * 2);
            score += Math.min(timeScore, 100) * 0.3;
        }

        // 阅读完成率权重：30%
        if (completionRate != null) {
            score += Math.min(completionRate, 100) * 0.3;
        }

        return Math.min(score, 100);
    }

    /**
     * 获取阅读状态描述
     */
    public String getReadStatusDescription() {
        if (readRate == null) {
            return "暂无阅读数据";
        }

        if (readRate >= 90) {
            return "优秀阅读率";
        } else if (readRate >= 70) {
            return "良好阅读率";
        } else if (readRate >= 50) {
            return "一般阅读率";
        } else if (readRate >= 30) {
            return "较低阅读率";
        } else {
            return "低阅读率";
        }
    }

    /**
     * 获取阅读时间效率描述
     */
    public String getReadTimeEfficiencyDescription() {
        if (averageReadTime == null) {
            return "暂无时间数据";
        }

        if (averageReadTime <= 5) {
            return "高效阅读";
        } else if (averageReadTime <= 15) {
            return "正常阅读";
        } else if (averageReadTime <= 30) {
            return "较慢阅读";
        } else {
            return "缓慢阅读";
        }
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalMessageCount != null || readMessageCount != null || averageReadTime != null;
    }

    /**
     * 获取未阅读消息百分比
     */
    public Double getUnreadMessagePercentage() {
        if (totalMessageCount == null || totalMessageCount == 0 || unreadMessageCount == null) {
            return 0.0;
        }
        return unreadMessageCount * 100.0 / totalMessageCount;
    }

    /**
     * 获取今日阅读活跃度
     */
    public String getTodayReadActivity() {
        if (todayReadCount == null) {
            return "未知";
        }

        if (todayReadCount == 0) {
            return "无阅读";
        } else if (todayReadCount < 10) {
            return "少量阅读";
        } else if (todayReadCount < 50) {
            return "适度阅读";
        } else if (todayReadCount < 100) {
            return "频繁阅读";
        } else {
            return "大量阅读";
        }
    }

    /**
     * 获取阅读趋势方向（上升/下降/平稳）
     */
    public String getReadTrendDirection() {
        if (readTrend == null || readTrend.getReadCounts() == null || readTrend.getReadCounts().length < 2) {
            return "未知";
        }

        Long[] counts = readTrend.getReadCounts();
        int n = counts.length;
        if (n < 2) {
            return "未知";
        }

        Long recent = counts[n - 1];
        Long previous = counts[n - 2];

        if (recent == null || previous == null) {
            return "未知";
        }

        if (recent > previous * 1.1) {
            return "上升";
        } else if (recent < previous * 0.9) {
            return "下降";
        } else {
            return "平稳";
        }
    }

    /**
     * 获取建议的改进措施
     */
    public String getImprovementSuggestions() {
        StringBuilder suggestions = new StringBuilder();

        if (readRate != null && readRate < 50) {
            suggestions.append("阅读率较低，建议发送阅读提醒或优化消息内容。");
        }

        if (averageReadTime != null && averageReadTime > 30) {
            suggestions.append("平均阅读时间较长，建议简化消息内容或提供摘要。");
        }

        if (completionRate != null && completionRate < 80) {
            suggestions.append("阅读完成率不足，建议分批发送重要消息。");
        }

        if (suggestions.length() == 0) {
            suggestions.append("阅读表现良好，继续保持。");
        }

        return suggestions.toString();
    }
}