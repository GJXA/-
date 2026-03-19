package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 反应类型分布DTO
 */
@Data
@Schema(description = "反应类型分布信息")
public class ReactionTypeDistributionDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

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

    @Schema(description = "反应类型详细分布")
    private Map<String, Long> detailedDistribution;

    @Schema(description = "点赞百分比")
    private Double likePercentage;

    @Schema(description = "爱心百分比")
    private Double heartPercentage;

    @Schema(description = "大笑百分比")
    private Double laughPercentage;

    @Schema(description = "难过百分比")
    private Double sadPercentage;

    @Schema(description = "生气百分比")
    private Double angryPercentage;

    @Schema(description = "其他反应百分比")
    private Double otherPercentage;

    @Schema(description = "主导反应类型")
    private String dominantReactionType;

    @Schema(description = "主导反应类型百分比")
    private Double dominantReactionPercentage;

    @Schema(description = "反应多样性指数")
    private Double diversityIndex;

    @Schema(description = "反应集中度指数")
    private Double concentrationIndex;

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算总反应数量
        this.totalReactions = (likeCount != null ? likeCount : 0) +
                             (heartCount != null ? heartCount : 0) +
                             (laughCount != null ? laughCount : 0) +
                             (sadCount != null ? sadCount : 0) +
                             (angryCount != null ? angryCount : 0) +
                             (otherCount != null ? otherCount : 0);

        if (totalReactions > 0) {
            // 计算百分比
            this.likePercentage = likeCount != null ? likeCount * 100.0 / totalReactions : 0.0;
            this.heartPercentage = heartCount != null ? heartCount * 100.0 / totalReactions : 0.0;
            this.laughPercentage = laughCount != null ? laughCount * 100.0 / totalReactions : 0.0;
            this.sadPercentage = sadCount != null ? sadCount * 100.0 / totalReactions : 0.0;
            this.angryPercentage = angryCount != null ? angryCount * 100.0 / totalReactions : 0.0;
            this.otherPercentage = otherCount != null ? otherCount * 100.0 / totalReactions : 0.0;

            // 找到主导反应类型
            findDominantReactionType();

            // 计算反应多样性指数
            calculateDiversityIndex();

            // 计算反应集中度指数
            calculateConcentrationIndex();
        }
    }

    private void findDominantReactionType() {
        long maxCount = 0;
        String dominant = null;

        if (likeCount != null && likeCount > maxCount) {
            maxCount = likeCount;
            dominant = "LIKE";
        }
        if (heartCount != null && heartCount > maxCount) {
            maxCount = heartCount;
            dominant = "HEART";
        }
        if (laughCount != null && laughCount > maxCount) {
            maxCount = laughCount;
            dominant = "LAUGH";
        }
        if (sadCount != null && sadCount > maxCount) {
            maxCount = sadCount;
            dominant = "SAD";
        }
        if (angryCount != null && angryCount > maxCount) {
            maxCount = angryCount;
            dominant = "ANGRY";
        }
        if (otherCount != null && otherCount > maxCount) {
            maxCount = otherCount;
            dominant = "OTHER";
        }

        if (dominant != null) {
            this.dominantReactionType = dominant;
            this.dominantReactionPercentage = maxCount * 100.0 / totalReactions;
        }
    }

    private void calculateDiversityIndex() {
        if (totalReactions == null || totalReactions == 0) {
            this.diversityIndex = 0.0;
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
            this.diversityIndex = 0.0;
            return;
        }

        // 计算熵值
        double entropy = 0.0;
        if (likeCount != null && likeCount > 0) {
            double p = likeCount.doubleValue() / totalReactions;
            entropy -= p * Math.log(p);
        }
        if (heartCount != null && heartCount > 0) {
            double p = heartCount.doubleValue() / totalReactions;
            entropy -= p * Math.log(p);
        }
        if (laughCount != null && laughCount > 0) {
            double p = laughCount.doubleValue() / totalReactions;
            entropy -= p * Math.log(p);
        }
        if (sadCount != null && sadCount > 0) {
            double p = sadCount.doubleValue() / totalReactions;
            entropy -= p * Math.log(p);
        }
        if (angryCount != null && angryCount > 0) {
            double p = angryCount.doubleValue() / totalReactions;
            entropy -= p * Math.log(p);
        }
        if (otherCount != null && otherCount > 0) {
            double p = otherCount.doubleValue() / totalReactions;
            entropy -= p * Math.log(p);
        }

        double maxEntropy = Math.log(typeCount);
        this.diversityIndex = maxEntropy > 0 ? entropy / maxEntropy : 0.0;
    }

    private void calculateConcentrationIndex() {
        if (dominantReactionPercentage == null) {
            this.concentrationIndex = 0.0;
            return;
        }

        // 集中度指数：主导反应类型百分比
        this.concentrationIndex = dominantReactionPercentage / 100.0;
    }

    /**
     * 获取分布类型描述
     */
    public String getDistributionTypeDescription() {
        if (dominantReactionPercentage == null) {
            return "未知";
        }

        if (dominantReactionPercentage >= 80) {
            return "高度集中分布";
        } else if (dominantReactionPercentage >= 60) {
            return "集中分布";
        } else if (dominantReactionPercentage >= 40) {
            return "相对均衡分布";
        } else {
            return "分散分布";
        }
    }

    /**
     * 获取情感氛围描述
     */
    public String getEmotionalAtmosphereDescription() {
        if (dominantReactionType == null) {
            return "未知";
        }

        switch (dominantReactionType) {
            case "LIKE":
                return "积极向上氛围";
            case "HEART":
                return "温暖友好氛围";
            case "LAUGH":
                return "欢乐幽默氛围";
            case "SAD":
                return "同情关怀氛围";
            case "ANGRY":
                return "严肃紧张氛围";
            case "OTHER":
                return "多样化情感氛围";
            default:
                return "中性氛围";
        }
    }

    /**
     * 获取分布健康度评估
     */
    public String getDistributionHealthAssessment() {
        if (angryPercentage != null && angryPercentage > 30) {
            return "需关注（生气反应比例较高）";
        }

        if (diversityIndex != null && diversityIndex < 0.2) {
            return "单一化（反应类型多样性低）";
        }

        if (dominantReactionPercentage != null && dominantReactionPercentage > 80) {
            return "过度集中（单一反应类型主导）";
        }

        return "健康（分布合理）";
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalReactions != null || likeCount != null || heartCount != null;
    }

    /**
     * 获取最需要鼓励的反应类型
     */
    public String getReactionTypeToEncourage() {
        if (totalReactions == null || totalReactions == 0) {
            return "无数据";
        }

        // 找到数量最少的反应类型
        long minCount = Long.MAX_VALUE;
        String typeToEncourage = null;

        if (likeCount != null && likeCount < minCount) {
            minCount = likeCount;
            typeToEncourage = "点赞";
        }
        if (heartCount != null && heartCount < minCount) {
            minCount = heartCount;
            typeToEncourage = "爱心";
        }
        if (laughCount != null && laughCount < minCount) {
            minCount = laughCount;
            typeToEncourage = "大笑";
        }
        if (sadCount != null && sadCount < minCount) {
            minCount = sadCount;
            typeToEncourage = "难过";
        }
        if (angryCount != null && angryCount < minCount) {
            minCount = angryCount;
            typeToEncourage = "生气";
        }

        if (typeToEncourage != null && minCount < totalReactions * 0.1) {
            return typeToEncourage + "（比例较低，建议鼓励使用）";
        }

        return "无需特别鼓励";
    }

    /**
     * 获取建议的改进措施
     */
    public String getImprovementSuggestions() {
        StringBuilder suggestions = new StringBuilder();

        if (angryPercentage != null && angryPercentage > 20) {
            suggestions.append("生气反应比例较高，建议关注会话内容是否引起用户负面情绪。");
        }

        if (diversityIndex != null && diversityIndex < 0.3) {
            suggestions.append("反应类型多样性较低，建议引导用户尝试使用不同反应类型。");
        }

        if (likePercentage != null && likePercentage < 10) {
            suggestions.append("点赞比例较低，积极反馈不足，建议鼓励用户使用点赞表达认可。");
        }

        if (suggestions.length() == 0) {
            suggestions.append("反应类型分布良好，继续保持。");
        }

        return suggestions.toString();
    }

    /**
     * 获取可视化数据摘要
     */
    public String getVisualizationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("总反应数: ").append(totalReactions != null ? totalReactions : 0).append("\\n");

        if (likePercentage != null) {
            summary.append("点赞: ").append(String.format("%.1f", likePercentage)).append("%\\n");
        }
        if (heartPercentage != null) {
            summary.append("爱心: ").append(String.format("%.1f", heartPercentage)).append("%\\n");
        }
        if (laughPercentage != null) {
            summary.append("大笑: ").append(String.format("%.1f", laughPercentage)).append("%\\n");
        }
        if (sadPercentage != null) {
            summary.append("难过: ").append(String.format("%.1f", sadPercentage)).append("%\\n");
        }
        if (angryPercentage != null) {
            summary.append("生气: ").append(String.format("%.1f", angryPercentage)).append("%");
        }

        return summary.toString();
    }
}