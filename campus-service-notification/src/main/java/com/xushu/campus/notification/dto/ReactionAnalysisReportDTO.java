package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 反应分析报告DTO
 */
@Data
@Schema(description = "反应分析报告")
public class ReactionAnalysisReportDTO {

    @Schema(description = "报告ID")
    private String reportId;

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "报告生成时间")
    private LocalDateTime generatedAt;

    @Schema(description = "分析时间段")
    private String analysisPeriod;

    @Schema(description = "报告概述")
    private ReportOverview overview;

    @Schema(description = "反应总体统计")
    private OverallStatistics overallStatistics;

    @Schema(description = "反应类型分析")
    private ReactionTypeAnalysis reactionTypeAnalysis;

    @Schema(description = "用户行为分析")
    private UserBehaviorAnalysis userBehaviorAnalysis;

    @Schema(description = "趋势分析")
    private TrendAnalysis trendAnalysis;

    @Schema(description = "对比分析")
    private ComparativeAnalysis comparativeAnalysis;

    @Schema(description = "关键发现")
    private List<KeyFinding> keyFindings;

    @Schema(description = "建议措施")
    private List<Recommendation> recommendations;

    @Schema(description = "预测展望")
    private FutureOutlook futureOutlook;

    @Schema(description = "报告评分（0-100）")
    private Double reportScore;

    /**
     * 报告概述
     */
    @Data
    @Schema(description = "报告概述")
    public static class ReportOverview {
        @Schema(description = "报告标题")
        private String title;

        @Schema(description = "执行摘要")
        private String executiveSummary;

        @Schema(description = "分析目的")
        private String analysisPurpose;

        @Schema(description = "数据范围")
        private String dataScope;

        @Schema(description = "分析方法")
        private String analysisMethodology;

        @Schema(description = "主要结论")
        private String mainConclusion;
    }

    /**
     * 总体统计
     */
    @Data
    @Schema(description = "总体统计")
    public static class OverallStatistics {
        @Schema(description = "总反应数量")
        private Long totalReactions;

        @Schema(description = "总消息数量")
        private Long totalMessages;

        @Schema(description = "有反应的消息比例")
        private Double reactedMessagesRatio;

        @Schema(description = "平均每消息反应数量")
        private Double averageReactionsPerMessage;

        @Schema(description = "总参与用户数")
        private Integer totalParticipatingUsers;

        @Schema(description = "活跃用户比例")
        private Double activeUsersRatio;

        @Schema(description = "反应密度（反应/用户）")
        private Double reactionDensityPerUser;

        @Schema(description = "反应效率（反应/时间）")
        private Double reactionEfficiency;
    }

    /**
     * 反应类型分析
     */
    @Data
    @Schema(description = "反应类型分析")
    public static class ReactionTypeAnalysis {
        @Schema(description = "类型分布")
        private ReactionTypeDistributionDTO typeDistribution;

        @Schema(description = "主导反应类型")
        private String dominantReactionType;

        @Schema(description = "反应多样性评估")
        private String diversityAssessment;

        @Schema(description = "情感氛围分析")
        private String emotionalAtmosphere;

        @Schema(description = "反应类型健康度")
        private String typeHealthStatus;

        @Schema(description = "异常反应检测")
        private List<AnomalyDetection> anomalies;
    }

    /**
     * 用户行为分析
     */
    @Data
    @Schema(description = "用户行为分析")
    public static class UserBehaviorAnalysis {
        @Schema(description = "用户参与度分布")
        private UserParticipationDistribution participationDistribution;

        @Schema(description = "核心贡献者")
        private List<CoreContributor> coreContributors;

        @Schema(description = "沉默观察者")
        private List<SilentObserver> silentObservers;

        @Schema(description = "反应模式分析")
        private ReactionPatternAnalysis reactionPatterns;

        @Schema(description = "用户互动网络")
        private UserInteractionNetwork interactionNetwork;
    }

    /**
     * 趋势分析
     */
    @Data
    @Schema(description = "趋势分析")
    public static class TrendAnalysis {
        @Schema(description = "总体趋势")
        private ReactionTrendDTO overallTrend;

        @Schema(description = "趋势周期检测")
        private String cycleDetection;

        @Schema(description = "季节性模式")
        private String seasonalPatterns;

        @Schema(description = "趋势预测")
        private String trendPrediction;

        @Schema(description = "趋势稳定性评估")
        private String trendStabilityAssessment;
    }

    /**
     * 对比分析
     */
    @Data
    @Schema(description = "对比分析")
    public static class ComparativeAnalysis {
        @Schema(description = "历史同期对比")
        private HistoricalComparison historicalComparison;

        @Schema(description = "同类会话对比")
        private SimilarConversationComparison similarConversationComparison;

        @Schema(description = "用户群体对比")
        private UserGroupComparison userGroupComparison;

        @Schema(description = "时间维度对比")
        private TimeDimensionComparison timeDimensionComparison;
    }

    /**
     * 关键发现
     */
    @Data
    @Schema(description = "关键发现")
    public static class KeyFinding {
        @Schema(description = "发现标题")
        private String title;

        @Schema(description = "发现描述")
        private String description;

        @Schema(description = "重要性级别")
        private String importanceLevel; // HIGH, MEDIUM, LOW

        @Schema(description = "证据支持")
        private String evidence;

        @Schema(description = "影响评估")
        private String impactAssessment;
    }

    /**
     * 建议措施
     */
    @Data
    @Schema(description = "建议措施")
    public static class Recommendation {
        @Schema(description = "建议标题")
        private String title;

        @Schema(description = "建议描述")
        private String description;

        @Schema(description = "实施优先级")
        private String priority; // HIGH, MEDIUM, LOW

        @Schema(description = "预期效果")
        private String expectedOutcome;

        @Schema(description = "实施难度")
        private String implementationDifficulty;

        @Schema(description = "建议时间")
        private String suggestedTimeline;
    }

    /**
     * 未来展望
     */
    @Data
    @Schema(description = "未来展望")
    public static class FutureOutlook {
        @Schema(description = "短期预测（1-4周）")
        private String shortTermPrediction;

        @Schema(description = "中期预测（1-3个月）")
        private String mediumTermPrediction;

        @Schema(description = "长期趋势")
        private String longTermTrend;

        @Schema(description = "风险预警")
        private List<RiskWarning> riskWarnings;

        @Schema(description = "机会识别")
        private List<Opportunity> opportunities;
    }

    // 以下是一些辅助类的定义

    @Data
    @Schema(description = "异常检测")
    public static class AnomalyDetection {
        @Schema(description = "异常类型")
        private String anomalyType;

        @Schema(description = "异常描述")
        private String description;

        @Schema(description = "检测时间")
        private String detectionTime;

        @Schema(description = "异常程度")
        private String severity;

        @Schema(description = "可能原因")
        private String possibleCauses;
    }

    @Data
    @Schema(description = "用户参与度分布")
    public static class UserParticipationDistribution {
        @Schema(description = "高度参与用户数")
        private Integer highlyActiveUsers;

        @Schema(description = "中度参与用户数")
        private Integer moderatelyActiveUsers;

        @Schema(description = "低度参与用户数")
        private Integer lowActiveUsers;

        @Schema(description = "沉默用户数")
        private Integer silentUsers;

        @Schema(description = "参与度不均衡指数")
        private Double participationImbalanceIndex;
    }

    @Data
    @Schema(description = "核心贡献者")
    public static class CoreContributor {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名称")
        private String userName;

        @Schema(description = "贡献度评分")
        private Double contributionScore;

        @Schema(description = "反应数量")
        private Long reactionCount;

        @Schema(description = "反应多样性")
        private Double reactionDiversity;

        @Schema(description = "影响力评分")
        private Double influenceScore;
    }

    @Data
    @Schema(description = "沉默观察者")
    public static class SilentObserver {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名称")
        private String userName;

        @Schema(description = "观察时长（天）")
        private Integer observationDays;

        @Schema(description = "阅读消息数")
        private Long readMessageCount;

        @Schema(description = "潜在参与度")
        private Double potentialParticipation;

        @Schema(description = "激活建议")
        private String activationSuggestion;
    }

    @Data
    @Schema(description = "反应模式分析")
    public static class ReactionPatternAnalysis {
        @Schema(description = "常见反应模式")
        private List<CommonPattern> commonPatterns;

        @Schema(description = "反应时间模式")
        private TimePattern timePattern;

        @Schema(description = "反应链模式")
        private ChainPattern chainPattern;

        @Schema(description = "反应聚类分析")
        private ClusterAnalysis clusterAnalysis;
    }

    @Data
    @Schema(description = "用户互动网络")
    public static class UserInteractionNetwork {
        @Schema(description = "网络密度")
        private Double networkDensity;

        @Schema(description = "中心性分析")
        private CentralityAnalysis centralityAnalysis;

        @Schema(description = "社群检测")
        private CommunityDetection communityDetection;

        @Schema(description = "关键连接点")
        private List<KeyConnection> keyConnections;
    }

    @Data
    @Schema(description = "历史对比")
    public static class HistoricalComparison {
        @Schema(description = "同期对比结果")
        private String comparisonResult;

        @Schema(description = "增长率")
        private Double growthRate;

        @Schema(description = "变化显著性")
        private String changeSignificance;

        @Schema(description = "改进领域")
        private List<String> improvementAreas;
    }

    @Data
    @Schema(description = "风险预警")
    public static class RiskWarning {
        @Schema(description = "风险类型")
        private String riskType;

        @Schema(description = "风险描述")
        private String description;

        @Schema(description = "发生概率")
        private String probability;

        @Schema(description = "潜在影响")
        private String potentialImpact;

        @Schema(description = "缓解措施")
        private String mitigationMeasures;
    }

    @Data
    @Schema(description = "机会识别")
    public static class Opportunity {
        @Schema(description = "机会类型")
        private String opportunityType;

        @Schema(description = "机会描述")
        private String description;

        @Schema(description = "潜在价值")
        private String potentialValue;

        @Schema(description = "实现难度")
        private String implementationDifficulty;

        @Schema(description = "建议行动")
        private String suggestedAction;
    }

    // 更多辅助类...

    /**
     * 计算报告评分
     */
    public void calculateReportScore() {
        double score = 0.0;

        // 数据完整性（30%）
        if (overallStatistics != null) {
            score += 30;
        }

        // 分析深度（40%）
        if (reactionTypeAnalysis != null && userBehaviorAnalysis != null && trendAnalysis != null) {
            score += 40;
        }

        // 建议实用性（30%）
        if (recommendations != null && !recommendations.isEmpty()) {
            score += 30;
        }

        this.reportScore = Math.min(score, 100);
    }

    /**
     * 获取报告质量评估
     */
    public String getReportQualityAssessment() {
        if (reportScore == null) {
            return "未评估";
        }

        if (reportScore >= 90) {
            return "优秀报告";
        } else if (reportScore >= 80) {
            return "良好报告";
        } else if (reportScore >= 70) {
            return "合格报告";
        } else if (reportScore >= 60) {
            return "基本合格";
        } else {
            return "需要改进";
        }
    }

    /**
     * 获取报告摘要
     */
    public String getReportSummary() {
        StringBuilder summary = new StringBuilder();

        if (overview != null && overview.getExecutiveSummary() != null) {
            summary.append(overview.getExecutiveSummary());
        } else {
            summary.append("本报告分析了会话中的反应互动情况。");
        }

        if (keyFindings != null && !keyFindings.isEmpty()) {
            summary.append("\\n\\n关键发现：");
            for (int i = 0; i < Math.min(keyFindings.size(), 3); i++) {
                KeyFinding finding = keyFindings.get(i);
                summary.append("\\n").append(i + 1).append(". ").append(finding.getTitle());
            }
        }

        if (recommendations != null && !recommendations.isEmpty()) {
            summary.append("\\n\\n主要建议：");
            for (int i = 0; i < Math.min(recommendations.size(), 3); i++) {
                Recommendation rec = recommendations.get(i);
                summary.append("\\n").append(i + 1).append(". ").append(rec.getTitle());
            }
        }

        return summary.toString();
    }

    /**
     * 检查报告是否完整
     */
    public boolean isReportComplete() {
        return overview != null && overallStatistics != null &&
               reactionTypeAnalysis != null && userBehaviorAnalysis != null &&
               trendAnalysis != null && keyFindings != null &&
               recommendations != null;
    }

    /**
     * 获取报告生成状态
     */
    public String getReportGenerationStatus() {
        if (isReportComplete()) {
            return "已生成完整报告";
        } else {
            int completedSections = 0;
            int totalSections = 7; // overview, overallStatistics, reactionTypeAnalysis, userBehaviorAnalysis, trendAnalysis, keyFindings, recommendations

            if (overview != null) completedSections++;
            if (overallStatistics != null) completedSections++;
            if (reactionTypeAnalysis != null) completedSections++;
            if (userBehaviorAnalysis != null) completedSections++;
            if (trendAnalysis != null) completedSections++;
            if (keyFindings != null) completedSections++;
            if (recommendations != null) completedSections++;

            return String.format("报告生成中（%d/%d部分）", completedSections, totalSections);
        }
    }

    // 缺失的内部类定义 - 临时解决编译问题
    @Data
    @Schema(description = "常见反应模式")
    public static class CommonPattern {
        @Schema(description = "模式名称")
        private String patternName;
        @Schema(description = "模式描述")
        private String description;
        @Schema(description = "出现频率")
        private Double frequency;
    }

    @Data
    @Schema(description = "反应时间模式")
    public static class TimePattern {
        @Schema(description = "模式类型")
        private String patternType;
        @Schema(description = "时间分布")
        private String timeDistribution;
    }

    @Data
    @Schema(description = "反应链模式")
    public static class ChainPattern {
        @Schema(description = "链长度")
        private Integer chainLength;
        @Schema(description = "链类型")
        private String chainType;
    }

    @Data
    @Schema(description = "反应聚类分析")
    public static class ClusterAnalysis {
        @Schema(description = "聚类数量")
        private Integer clusterCount;
        @Schema(description = "聚类结果")
        private String clusterResults;
    }

    @Data
    @Schema(description = "中心性分析")
    public static class CentralityAnalysis {
        @Schema(description = "中心性类型")
        private String centralityType;
        @Schema(description = "分析结果")
        private String analysisResult;
    }

    @Data
    @Schema(description = "社群检测")
    public static class CommunityDetection {
        @Schema(description = "社群数量")
        private Integer communityCount;
        @Schema(description = "检测结果")
        private String detectionResult;
    }

    @Data
    @Schema(description = "关键连接点")
    public static class KeyConnection {
        @Schema(description = "连接点名称")
        private String connectionName;
        @Schema(description = "连接强度")
        private Double connectionStrength;
    }


    @Data
    @Schema(description = "同类会话对比")
    public static class SimilarConversationComparison {
        @Schema(description = "对比会话ID")
        private Long comparisonConversationId;
        @Schema(description = "对比会话标题")
        private String comparisonConversationTitle;
        @Schema(description = "相似度评分")
        private Double similarityScore;
        @Schema(description = "比较结果")
        private String comparisonResult;
        @Schema(description = "差异分析")
        private String differenceAnalysis;
    }

    @Data
    @Schema(description = "用户群体对比")
    public static class UserGroupComparison {
        @Schema(description = "对比群体类型")
        private String groupType;
        @Schema(description = "群体A统计")
        private GroupStatistics groupA;
        @Schema(description = "群体B统计")
        private GroupStatistics groupB;
        @Schema(description = "对比分析结果")
        private String comparisonResult;
        @Schema(description = "显著性差异")
        private String significantDifferences;
    }

    @Data
    @Schema(description = "群体统计")
    public static class GroupStatistics {
        @Schema(description = "群体名称")
        private String groupName;
        @Schema(description = "用户数量")
        private Integer userCount;
        @Schema(description = "平均反应数量")
        private Double averageReactions;
        @Schema(description = "反应类型分布")
        private String reactionTypeDistribution;
        @Schema(description = "活跃度评分")
        private Double activityScore;
    }

    @Data
    @Schema(description = "时间维度对比")
    public static class TimeDimensionComparison {
        @Schema(description = "对比时间段")
        private String comparisonPeriod;
        @Schema(description = "时间维度A统计")
        private TimeStatistics periodA;
        @Schema(description = "时间维度B统计")
        private TimeStatistics periodB;
        @Schema(description = "对比分析结果")
        private String comparisonResult;
        @Schema(description = "趋势变化")
        private String trendChange;
    }

    @Data
    @Schema(description = "时间统计")
    public static class TimeStatistics {
        @Schema(description = "时间段名称")
        private String periodName;
        @Schema(description = "反应总数")
        private Long totalReactions;
        @Schema(description = "活跃用户数")
        private Integer activeUsers;
        @Schema(description = "平均反应强度")
        private Double averageReactionIntensity;
        @Schema(description = "时间模式")
        private String timePattern;
    }
}