package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 反应趋势DTO - 简化版本
 */
@Data
@Schema(description = "反应趋势信息")
public class ReactionTrendDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "分析时间段（天）")
    private Integer analysisPeriodDays;

    @Schema(description = "总反应趋势")
    private TrendData totalReactionsTrend;

    @Schema(description = "点赞趋势")
    private TrendData likeTrend;

    @Schema(description = "爱心趋势")
    private TrendData heartTrend;

    @Schema(description = "大笑趋势")
    private TrendData laughTrend;

    @Schema(description = "难过趋势")
    private TrendData sadTrend;

    @Schema(description = "生气趋势")
    private TrendData angryTrend;

    @Schema(description = "其他反应趋势")
    private TrendData otherTrend;

    @Schema(description = "每日反应数据")
    private List<DailyReactionData> dailyData;

    @Schema(description = "趋势方向")
    private String overallTrendDirection;

    /**
     * 趋势数据
     */
    @Data
    @Schema(description = "趋势数据")
    public static class TrendData {
        @Schema(description = "趋势方向")
        private String direction;

        @Schema(description = "变化百分比")
        private Double changePercentage;

        @Schema(description = "趋势强度")
        private String strength;

        @Schema(description = "趋势描述")
        private String description;

        // 手动添加getter/setter方法
        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }

        public Double getChangePercentage() { return changePercentage; }
        public void setChangePercentage(Double changePercentage) { this.changePercentage = changePercentage; }

        public String getStrength() { return strength; }
        public void setStrength(String strength) { this.strength = strength; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * 每日反应数据
     */
    @Data
    @Schema(description = "每日反应数据")
    public static class DailyReactionData {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "总反应数量")
        private Long totalReactions;

        @Schema(description = "点赞数量")
        private Long likeCount;

        @Schema(description = "爱心数量")
        private Long heartCount;

        @Schema(description = "大笑数量")
        private Long laughCount;

        @Schema(description = "难过数量")
        private Long sadCount;

        @Schema(description = "生气数量")
        private Long angryCount;

        @Schema(description = "其他反应数量")
        private Long otherCount;

        // 手动添加getter/setter方法
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public Long getTotalReactions() { return totalReactions; }
        public void setTotalReactions(Long totalReactions) { this.totalReactions = totalReactions; }

        public Long getLikeCount() { return likeCount; }
        public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }

        public Long getHeartCount() { return heartCount; }
        public void setHeartCount(Long heartCount) { this.heartCount = heartCount; }

        public Long getLaughCount() { return laughCount; }
        public void setLaughCount(Long laughCount) { this.laughCount = laughCount; }

        public Long getSadCount() { return sadCount; }
        public void setSadCount(Long sadCount) { this.sadCount = sadCount; }

        public Long getAngryCount() { return angryCount; }
        public void setAngryCount(Long angryCount) { this.angryCount = angryCount; }

        public Long getOtherCount() { return otherCount; }
        public void setOtherCount(Long otherCount) { this.otherCount = otherCount; }
    }
}