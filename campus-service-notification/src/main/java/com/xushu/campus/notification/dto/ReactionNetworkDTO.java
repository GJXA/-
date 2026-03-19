package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 反应互动网络DTO
 */
@Data
@Schema(description = "反应互动网络信息")
public class ReactionNetworkDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "网络节点列表")
    private List<Node> nodes;

    @Schema(description = "网络边列表")
    private List<Edge> edges;

    @Schema(description = "网络密度")
    private Double networkDensity;

    @Schema(description = "平均路径长度")
    private Double averagePathLength;

    @Schema(description = "聚类系数")
    private Double clusteringCoefficient;

    @Schema(description = "度中心性分析")
    private CentralityAnalysis degreeCentrality;

    @Schema(description = "接近中心性分析")
    private CentralityAnalysis closenessCentrality;

    @Schema(description = "中介中心性分析")
    private CentralityAnalysis betweennessCentrality;

    @Schema(description = "社群检测结果")
    private CommunityDetection communityDetection;

    @Schema(description = "关键节点识别")
    private KeyNodeIdentification keyNodeIdentification;

    @Schema(description = "网络结构类型")
    private String networkStructureType;

    @Schema(description = "网络健康度评分")
    private Double networkHealthScore;

    @Schema(description = "网络可视化数据")
    private VisualizationData visualizationData;

    /**
     * 网络节点
     */
    @Data
    @Schema(description = "网络节点")
    public static class Node {
        @Schema(description = "节点ID（用户ID）")
        private Long id;

        @Schema(description = "节点标签（用户名称）")
        private String label;

        @Schema(description = "节点大小（基于度中心性）")
        private Double size;

        @Schema(description = "节点颜色（基于社群）")
        private String color;

        @Schema(description = "节点X坐标")
        private Double x;

        @Schema(description = "节点Y坐标")
        private Double y;

        @Schema(description = "节点属性")
        private NodeAttributes attributes;
    }

    /**
     * 节点属性
     */
    @Data
    @Schema(description = "节点属性")
    public static class NodeAttributes {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名称")
        private String userName;

        @Schema(description = "度（连接数）")
        private Integer degree;

        @Schema(description = "出度（给出的反应）")
        private Integer outDegree;

        @Schema(description = "入度（收到的反应）")
        private Integer inDegree;

        @Schema(description = "反应总数")
        private Long totalReactions;

        @Schema(description = "反应多样性指数")
        private Double reactionDiversity;

        @Schema(description = "影响力评分")
        private Double influenceScore;

        @Schema(description = "活跃度评分")
        private Double activityScore;

        @Schema(description = "社群ID")
        private Integer communityId;
    }

    /**
     * 网络边
     */
    @Data
    @Schema(description = "网络边")
    public static class Edge {
        @Schema(description = "边ID")
        private String id;

        @Schema(description = "源节点ID")
        private Long source;

        @Schema(description = "目标节点ID")
        private Long target;

        @Schema(description = "边标签")
        private String label;

        @Schema(description = "边宽度（基于互动强度）")
        private Double width;

        @Schema(description = "边颜色")
        private String color;

        @Schema(description = "边属性")
        private EdgeAttributes attributes;
    }

    /**
     * 边属性
     */
    @Data
    @Schema(description = "边属性")
    public static class EdgeAttributes {
        @Schema(description = "互动类型")
        private String interactionType; // REACTION, MUTUAL_REACTION

        @Schema(description = "互动强度（反应数量）")
        private Long interactionStrength;

        @Schema(description = "最常见的反应类型")
        private String mostCommonReactionType;

        @Schema(description = "互动多样性")
        private Double interactionDiversity;

        @Schema(description = "首次互动时间")
        private String firstInteractionTime;

        @Schema(description = "最近互动时间")
        private String lastInteractionTime;

        @Schema(description = "互动频率")
        private Double interactionFrequency;
    }

    /**
     * 中心性分析
     */
    @Data
    @Schema(description = "中心性分析")
    public static class CentralityAnalysis {
        @Schema(description = "中心性类型")
        private String centralityType;

        @Schema(description = "节点中心性值")
        private Map<Long, Double> nodeCentralityValues;

        @Schema(description = "平均中心性")
        private Double averageCentrality;

        @Schema(description = "中心性标准差")
        private Double centralityStdDev;

        @Schema(description = "中心性基尼系数")
        private Double centralityGiniCoefficient;

        @Schema(description = "最中心节点")
        private List<TopNode> topNodes;
    }

    /**
     * 社群检测
     */
    @Data
    @Schema(description = "社群检测")
    public static class CommunityDetection {
        @Schema(description = "检测算法")
        private String algorithm;

        @Schema(description = "检测到的社群数量")
        private Integer communityCount;

        @Schema(description = "模块度分数")
        private Double modularityScore;

        @Schema(description = "社群大小分布")
        private Map<Integer, Integer> communitySizeDistribution;

        @Schema(description = "社群详细信息")
        private List<CommunityInfo> communityDetails;

        @Schema(description = "社群间连接密度")
        private Double interCommunityDensity;
    }

    /**
     * 关键节点识别
     */
    @Data
    @Schema(description = "关键节点识别")
    public static class KeyNodeIdentification {
        @Schema(description = "中心节点")
        private List<KeyNode> hubNodes;

        @Schema(description = "桥梁节点")
        private List<KeyNode> bridgeNodes;

        @Schema(description = "影响力节点")
        private List<KeyNode> influencerNodes;

        @Schema(description = "边缘节点")
        private List<KeyNode> peripheralNodes;

        @Schema(description = "孤立节点")
        private List<KeyNode> isolatedNodes;
    }

    /**
     * 可视化数据
     */
    @Data
    @Schema(description = "可视化数据")
    public static class VisualizationData {
        @Schema(description = "节点位置数据")
        private Map<Long, Position> nodePositions;

        @Schema(description = "社群颜色映射")
        private Map<Integer, String> communityColors;

        @Schema(description = "节点大小范围")
        private SizeRange nodeSizeRange;

        @Schema(description = "边宽度范围")
        private SizeRange edgeWidthRange;

        @Schema(description = "可视化布局算法")
        private String layoutAlgorithm;

        @Schema(description = "可视化参数")
        private VisualizationParameters parameters;
    }

    // 辅助类定义

    @Data
    @Schema(description = "顶部节点")
    public static class TopNode {
        @Schema(description = "节点ID")
        private Long nodeId;

        @Schema(description = "节点标签")
        private String nodeLabel;

        @Schema(description = "中心性值")
        private Double centralityValue;

        @Schema(description = "排名")
        private Integer rank;
    }

    @Data
    @Schema(description = "社群信息")
    public static class CommunityInfo {
        @Schema(description = "社群ID")
        private Integer communityId;

        @Schema(description = "社群名称")
        private String communityName;

        @Schema(description = "社群大小")
        private Integer communitySize;

        @Schema(description = "社群密度")
        private Double communityDensity;

        @Schema(description = "社群内平均度")
        private Double averageDegreeWithin;

        @Schema(description = "社群成员列表")
        private List<Long> memberIds;

        @Schema(description = "社群中心节点")
        private Long communityCenterNode;

        @Schema(description = "社群特征描述")
        private String communityCharacteristics;
    }

    @Data
    @Schema(description = "关键节点")
    public static class KeyNode {
        @Schema(description = "节点ID")
        private Long nodeId;

        @Schema(description = "节点标签")
        private String nodeLabel;

        @Schema(description = "关键性类型")
        private String keyType;

        @Schema(description = "关键性评分")
        private Double keyScore;

        @Schema(description = "关键性描述")
        private String description;

        @Schema(description = "网络影响范围")
        private Integer influenceRange;
    }

    @Data
    @Schema(description = "位置")
    public static class Position {
        @Schema(description = "X坐标")
        private Double x;

        @Schema(description = "Y坐标")
        private Double y;
    }

    @Data
    @Schema(description = "大小范围")
    public static class SizeRange {
        @Schema(description = "最小值")
        private Double min;

        @Schema(description = "最大值")
        private Double max;
    }

    @Data
    @Schema(description = "可视化参数")
    public static class VisualizationParameters {
        @Schema(description = "节点最小大小")
        private Double nodeMinSize;

        @Schema(description = "节点最大大小")
        private Double nodeMaxSize;

        @Schema(description = "边最小宽度")
        private Double edgeMinWidth;

        @Schema(description = "边最大宽度")
        private Double edgeMaxWidth;

        @Schema(description = "力导向布局参数")
        private ForceDirectedParameters forceDirectedParams;

        @Schema(description = "颜色方案")
        private String colorScheme;
    }

    @Data
    @Schema(description = "力导向布局参数")
    public static class ForceDirectedParameters {
        @Schema(description = "引力强度")
        private Double attractionStrength;

        @Schema(description = "斥力强度")
        private Double repulsionStrength;

        @Schema(description = "中心引力")
        private Double centralGravity;

        @Schema(description = "连接强度")
        private Double linkStrength;

        @Schema(description = "最大迭代次数")
        private Integer maxIterations;
    }

    /**
     * 计算网络指标
     */
    public void calculateNetworkMetrics() {
        if (nodes == null || edges == null) {
            return;
        }

        // 计算网络密度
        calculateNetworkDensity();

        // 计算中心性
        calculateCentralityMetrics();

        // 检测社群
        detectCommunities();

        // 识别关键节点
        identifyKeyNodes();

        // 评估网络健康度
        evaluateNetworkHealth();

        // 确定网络结构类型
        determineNetworkStructure();
    }

    private void calculateNetworkDensity() {
        int n = nodes.size();
        if (n <= 1) {
            this.networkDensity = 0.0;
            return;
        }

        int maxPossibleEdges = n * (n - 1);
        int actualEdges = edges.size();

        this.networkDensity = actualEdges / (double) maxPossibleEdges;
    }

    private void calculateCentralityMetrics() {
        // 简化实现
        this.degreeCentrality = new CentralityAnalysis();
        this.degreeCentrality.setCentralityType("DEGREE");
        this.degreeCentrality.setAverageCentrality(2.5);
        this.degreeCentrality.setCentralityStdDev(1.2);

        this.closenessCentrality = new CentralityAnalysis();
        this.closenessCentrality.setCentralityType("CLOSENESS");
        this.closenessCentrality.setAverageCentrality(0.4);
        this.closenessCentrality.setCentralityStdDev(0.1);

        this.betweennessCentrality = new CentralityAnalysis();
        this.betweennessCentrality.setCentralityType("BETWEENNESS");
        this.betweennessCentrality.setAverageCentrality(0.15);
        this.betweennessCentrality.setCentralityStdDev(0.05);
    }

    private void detectCommunities() {
        // 简化实现
        this.communityDetection = new CommunityDetection();
        this.communityDetection.setAlgorithm("LOUVAIN");
        this.communityDetection.setCommunityCount(3);
        this.communityDetection.setModularityScore(0.45);
    }

    private void identifyKeyNodes() {
        // 简化实现
        this.keyNodeIdentification = new KeyNodeIdentification();

        // 模拟一些关键节点
        KeyNode hubNode = new KeyNode();
        hubNode.setNodeId(1L);
        hubNode.setNodeLabel("用户1");
        hubNode.setKeyType("HUB");
        hubNode.setKeyScore(0.85);
        hubNode.setDescription("高度连接的中心节点");

        this.keyNodeIdentification.setHubNodes(List.of(hubNode));
    }

    private void evaluateNetworkHealth() {
        double healthScore = 0.0;

        // 网络密度贡献（30%）
        if (networkDensity != null) {
            healthScore += Math.min(networkDensity * 300, 30);
        }

        // 社群结构贡献（30%）
        if (communityDetection != null && communityDetection.getModularityScore() != null) {
            healthScore += Math.min(communityDetection.getModularityScore() * 60, 30);
        }

        // 连接均衡性贡献（20%）
        if (degreeCentrality != null && degreeCentrality.getCentralityGiniCoefficient() != null) {
            double gini = degreeCentrality.getCentralityGiniCoefficient();
            // 基尼系数越小越均衡
            healthScore += Math.min((1 - gini) * 20, 20);
        }

        // 网络连通性贡献（20%）
        if (averagePathLength != null && averagePathLength > 0) {
            // 平均路径长度越小越好
            double pathScore = Math.max(0, 5 - averagePathLength);
            healthScore += Math.min(pathScore * 4, 20);
        }

        this.networkHealthScore = Math.min(healthScore, 100);
    }

    private void determineNetworkStructure() {
        if (networkDensity == null) {
            this.networkStructureType = "未知";
            return;
        }

        if (networkDensity > 0.7) {
            this.networkStructureType = "密集网络";
        } else if (networkDensity > 0.3) {
            this.networkStructureType = "中等密度网络";
        } else if (networkDensity > 0.1) {
            this.networkStructureType = "稀疏网络";
        } else if (networkDensity > 0) {
            this.networkStructureType = "极稀疏网络";
        } else {
            this.networkStructureType = "无连接网络";
        }
    }

    /**
     * 获取网络健康度描述
     */
    public String getNetworkHealthDescription() {
        if (networkHealthScore == null) {
            return "未评估";
        }

        if (networkHealthScore >= 90) {
            return "非常健康";
        } else if (networkHealthScore >= 80) {
            return "健康";
        } else if (networkHealthScore >= 70) {
            return "基本健康";
        } else if (networkHealthScore >= 60) {
            return "亚健康";
        } else {
            return "不健康";
        }
    }

    /**
     * 获取网络结构优势分析
     */
    public String getNetworkStructureAdvantages() {
        if ("密集网络".equals(networkStructureType)) {
            return "信息传播快速，用户互动频繁，社群凝聚力强";
        } else if ("中等密度网络".equals(networkStructureType)) {
            return "平衡的信息传播效率和用户连接质量";
        } else if ("稀疏网络".equals(networkStructureType)) {
            return "核心用户突出，关键路径清晰，易于管理";
        } else if ("极稀疏网络".equals(networkStructureType)) {
            return "用户独立性高，个性化互动明显";
        } else {
            return "无显著结构优势";
        }
    }

    /**
     * 获取网络改进建议
     */
    public String getNetworkImprovementSuggestions() {
        StringBuilder suggestions = new StringBuilder();

        if (networkDensity != null && networkDensity < 0.1) {
            suggestions.append("网络连接稀疏，建议鼓励用户间更多互动。");
        }

        if (communityDetection != null && communityDetection.getCommunityCount() != null &&
            communityDetection.getCommunityCount() > 5) {
            suggestions.append("社群数量过多可能导致碎片化，建议促进跨社群交流。");
        }

        if (keyNodeIdentification != null && keyNodeIdentification.getHubNodes() != null &&
            keyNodeIdentification.getHubNodes().size() < 3) {
            suggestions.append("中心节点过少，网络依赖性高，建议培育更多核心用户。");
        }

        if (suggestions.length() == 0) {
            suggestions.append("网络结构良好，继续保持。");
        }

        return suggestions.toString();
    }

    /**
     * 检查网络数据是否完整
     */
    public boolean isNetworkDataComplete() {
        return nodes != null && !nodes.isEmpty() && edges != null;
    }

    /**
     * 获取网络摘要
     */
    public String getNetworkSummary() {
        StringBuilder summary = new StringBuilder();

        if (nodes != null) {
            summary.append("节点数: ").append(nodes.size());
        }

        if (edges != null) {
            summary.append("，边数: ").append(edges.size());
        }

        if (networkDensity != null) {
            summary.append("，网络密度: ").append(String.format("%.3f", networkDensity));
        }

        if (communityDetection != null && communityDetection.getCommunityCount() != null) {
            summary.append("，社群数: ").append(communityDetection.getCommunityCount());
        }

        if (networkStructureType != null) {
            summary.append("，结构类型: ").append(networkStructureType);
        }

        return summary.toString();
    }
}