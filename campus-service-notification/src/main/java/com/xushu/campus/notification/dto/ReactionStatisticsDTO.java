package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 反应统计信息DTO
 */
@Data
@Schema(description = "反应统计信息")
public class ReactionStatisticsDTO {

    @Schema(description = "消息ID")
    private Long messageId;

    @Schema(description = "总反应数量")
    private Long totalReactionCount;

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

    @Schema(description = "反应类型分布")
    private Map<String, Long> reactionTypeDistribution;

    @Schema(description = "最常用的反应类型")
    private String mostCommonReactionType;

    @Schema(description = "最常用反应类型数量")
    private Long mostCommonReactionCount;

    @Schema(description = "最常用反应类型百分比")
    private Double mostCommonReactionPercentage;

    @Schema(description = "反应多样性指数")
    private Double reactionDiversityIndex;

    @Schema(description = "反应密度（反应/阅读）")
    private Double reactionDensity;

    @Schema(description = "反应趋势（最近24小时）")
    private ReactionTrend recentTrend;

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

        @Schema(description = "最近24小时新增反应")
        private Long recentAdditions;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算总反应数量
        this.totalReactionCount = (likeCount != null ? likeCount : 0) +
                                 (heartCount != null ? heartCount : 0) +
                                 (laughCount != null ? laughCount : 0) +
                                 (sadCount != null ? sadCount : 0) +
                                 (angryCount != null ? angryCount : 0) +
                                 (otherCount != null ? otherCount : 0);

        // 找到最常用的反应类型
        findMostCommonReaction();

        // 计算反应多样性指数
        calculateDiversityIndex();
    }

    private void findMostCommonReaction() {
        long maxCount = 0;
        String mostCommon = null;

        if (likeCount != null && likeCount > maxCount) {
            maxCount = likeCount;
            mostCommon = "LIKE";
        }
        if (heartCount != null && heartCount > maxCount) {
            maxCount = heartCount;
            mostCommon = "HEART";
        }
        if (laughCount != null && laughCount > maxCount) {
            maxCount = laughCount;
            mostCommon = "LAUGH";
        }
        if (sadCount != null && sadCount > maxCount) {
            maxCount = sadCount;
            mostCommon = "SAD";
        }
        if (angryCount != null && angryCount > maxCount) {
            maxCount = angryCount;
            mostCommon = "ANGRY";
        }
        if (otherCount != null && otherCount > maxCount) {
            maxCount = otherCount;
            mostCommon = "OTHER";
        }

        if (mostCommon != null && totalReactionCount != null && totalReactionCount > 0) {
            this.mostCommonReactionType = mostCommon;
            this.mostCommonReactionCount = maxCount;
            this.mostCommonReactionPercentage = maxCount * 100.0 / totalReactionCount;
        }
    }

    private void calculateDiversityIndex() {
        if (totalReactionCount == null || totalReactionCount == 0) {
            this.reactionDiversityIndex = 0.0;
            return;
        }

        // 计算不同类型数量
        int typeCount = 0;
        if (likeCount != null && likeCount > 0) typeCount++;
        if (heartCount != null && heartCount > 0) typeCount++;
        if (laughCount != null && laughCount > 0) typeCount++;
        if (sadCount != null && sadCount > 0) typeCount++;
        if (angryCount != null && angryCount > 0) typeCount++;
        if (otherCount != null && otherCount > 0) typeCount++;

        if (typeCount <= 1) {
            this.reactionDiversityIndex = 0.0;
            return;
        }

        // 简化多样性计算
        double entropy = 0.0;
        if (likeCount != null && likeCount > 0) {
            double p = likeCount.doubleValue() / totalReactionCount;
            entropy -= p * Math.log(p);
        }
        if (heartCount != null && heartCount > 0) {
            double p = heartCount.doubleValue() / totalReactionCount;
            entropy -= p * Math.log(p);
        }
        if (laughCount != null && laughCount > 0) {
            double p = laughCount.doubleValue() / totalReactionCount;
            entropy -= p * Math.log(p);
        }
        if (sadCount != null && sadCount > 0) {
            double p = sadCount.doubleValue() / totalReactionCount;
            entropy -= p * Math.log(p);
        }
        if (angryCount != null && angryCount > 0) {
            double p = angryCount.doubleValue() / totalReactionCount;
            entropy -= p * Math.log(p);
        }
        if (otherCount != null && otherCount > 0) {
            double p = otherCount.doubleValue() / totalReactionCount;
            entropy -= p * Math.log(p);
        }

        double maxEntropy = Math.log(typeCount);
        this.reactionDiversityIndex = maxEntropy > 0 ? entropy / maxEntropy : 0.0;
    }

    /**
     * 获取点赞百分比
     */
    public Double getLikePercentage() {
        if (totalReactionCount == null || totalReactionCount == 0 || likeCount == null) {
            return 0.0;
        }
        return likeCount * 100.0 / totalReactionCount;
    }

    /**
     * 获取爱心百分比
     */
    public Double getHeartPercentage() {
        if (totalReactionCount == null || totalReactionCount == 0 || heartCount == null) {
            return 0.0;
        }
        return heartCount * 100.0 / totalReactionCount;
    }

    /**
     * 获取大笑百分比
     */
    public Double getLaughPercentage() {
        if (totalReactionCount == null || totalReactionCount == 0 || laughCount == null) {
            return 0.0;
        }
        return laughCount * 100.0 / totalReactionCount;
    }

    /**
     * 获取难过百分比
     */
    public Double getSadPercentage() {
        if (totalReactionCount == null || totalReactionCount == 0 || sadCount == null) {
            return 0.0;
        }
        return sadCount * 100.0 / totalReactionCount;
    }

    /**
     * 获取生气百分比
     */
    public Double getAngryPercentage() {
        if (totalReactionCount == null || totalReactionCount == 0 || angryCount == null) {
            return 0.0;
        }
        return angryCount * 100.0 / totalReactionCount;
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
     * 获取反应热度描述
     */
    public String getReactionHeatDescription() {
        if (totalReactionCount == null) {
            return "无反应";
        }
        if (totalReactionCount == 0) {
            return "无反应";
        } else if (totalReactionCount < 5) {
            return "冷淡";
        } else if (totalReactionCount < 20) {
            return "温和";
        } else if (totalReactionCount < 50) {
            return "热烈";
        } else if (totalReactionCount < 100) {
            return "火爆";
        } else {
            return "极其火爆";
        }
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalReactionCount != null || likeCount != null || heartCount != null;
    }

    /**
     * 获取反应类型分布描述
     */
    public String getReactionTypeDistributionDesc() {
        StringBuilder sb = new StringBuilder();
        if (likeCount != null && likeCount > 0) {
            sb.append("点赞:").append(likeCount).append(" ");
        }
        if (heartCount != null && heartCount > 0) {
            sb.append("爱心:").append(heartCount).append(" ");
        }
        if (laughCount != null && laughCount > 0) {
            sb.append("大笑:").append(laughCount).append(" ");
        }
        if (sadCount != null && sadCount > 0) {
            sb.append("难过:").append(sadCount).append(" ");
        }
        if (angryCount != null && angryCount > 0) {
            sb.append("生气:").append(angryCount);
        }
        return sb.toString().trim();
    }
}