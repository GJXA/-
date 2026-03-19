package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 角色分布信息DTO
 */
@Data
@Schema(description = "角色分布信息")
public class RoleDistributionDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "总成员数量")
    private Integer totalMemberCount;

    @Schema(description = "角色分布详情")
    private Map<String, RoleStats> roleStats;

    @Schema(description = "最常用的角色")
    private String mostCommonRole;

    @Schema(description = "最常用角色的成员数量")
    private Integer mostCommonRoleCount;

    @Schema(description = "最常用角色的百分比")
    private Double mostCommonRolePercentage;

    @Schema(description = "角色多样性指数")
    private Double roleDiversityIndex;

    /**
     * 角色统计
     */
    @Data
    @Schema(description = "角色统计")
    public static class RoleStats {
        @Schema(description = "角色名称")
        private String roleName;

        @Schema(description = "角色描述")
        private String roleDesc;

        @Schema(description = "成员数量")
        private Integer memberCount;

        @Schema(description = "成员百分比")
        private Double memberPercentage;

        @Schema(description = "平均在线时长（分钟）")
        private Double averageOnlineTime;

        @Schema(description = "平均消息数量")
        private Double averageMessageCount;

        @Schema(description = "平均加入天数")
        private Double averageJoinDays;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        if (roleStats == null || roleStats.isEmpty() || totalMemberCount == null || totalMemberCount == 0) {
            return;
        }

        // 计算每个角色的百分比
        for (RoleStats stats : roleStats.values()) {
            if (stats.getMemberCount() != null) {
                stats.setMemberPercentage(stats.getMemberCount() * 100.0 / totalMemberCount);
            }
        }

        // 找到最常用的角色
        int maxCount = 0;
        String mostCommon = null;
        for (Map.Entry<String, RoleStats> entry : roleStats.entrySet()) {
            RoleStats stats = entry.getValue();
            if (stats.getMemberCount() != null && stats.getMemberCount() > maxCount) {
                maxCount = stats.getMemberCount();
                mostCommon = entry.getKey();
            }
        }

        if (mostCommon != null) {
            this.mostCommonRole = mostCommon;
            this.mostCommonRoleCount = maxCount;
            this.mostCommonRolePercentage = maxCount * 100.0 / totalMemberCount;
        }

        // 计算角色多样性指数（使用香农熵）
        this.roleDiversityIndex = calculateDiversityIndex();
    }

    /**
     * 计算角色多样性指数
     */
    private Double calculateDiversityIndex() {
        if (roleStats == null || roleStats.isEmpty() || totalMemberCount == null || totalMemberCount == 0) {
            return 0.0;
        }

        double entropy = 0.0;
        for (RoleStats stats : roleStats.values()) {
            if (stats.getMemberCount() != null && stats.getMemberCount() > 0) {
                double probability = stats.getMemberCount().doubleValue() / totalMemberCount;
                entropy -= probability * Math.log(probability);
            }
        }

        // 归一化到0-1范围
        double maxEntropy = Math.log(roleStats.size());
        if (maxEntropy > 0) {
            return entropy / maxEntropy;
        }
        return 0.0;
    }

    /**
     * 获取指定角色的统计信息
     */
    public RoleStats getRoleStats(String role) {
        if (roleStats == null) {
            return null;
        }
        return roleStats.get(role);
    }

    /**
     * 获取拥有者统计
     */
    public RoleStats getOwnerStats() {
        return getRoleStats("OWNER");
    }

    /**
     * 获取管理员统计
     */
    public RoleStats getAdminStats() {
        return getRoleStats("ADMIN");
    }

    /**
     * 获取普通成员统计
     */
    public RoleStats getMemberStats() {
        return getRoleStats("MEMBER");
    }

    /**
     * 获取角色数量
     */
    public Integer getRoleCount() {
        if (roleStats == null) {
            return 0;
        }
        return roleStats.size();
    }

    /**
     * 检查是否有角色分布数据
     */
    public boolean hasRoleDistribution() {
        return roleStats != null && !roleStats.isEmpty();
    }

    /**
     * 获取角色分布描述
     */
    public String getDistributionDescription() {
        if (!hasRoleDistribution()) {
            return "无角色分布数据";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, RoleStats> entry : roleStats.entrySet()) {
            RoleStats stats = entry.getValue();
            if (stats.getMemberCount() != null && stats.getMemberCount() > 0) {
                String roleDesc = stats.getRoleDesc() != null ? stats.getRoleDesc() : entry.getKey();
                sb.append(roleDesc).append(":").append(stats.getMemberCount());
                if (stats.getMemberPercentage() != null) {
                    sb.append("(").append(String.format("%.1f", stats.getMemberPercentage())).append("%)");
                }
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * 获取角色多样性描述
     */
    public String getDiversityDescription() {
        if (roleDiversityIndex == null) {
            return "未知";
        }
        if (roleDiversityIndex < 0.3) {
            return "低多样性";
        } else if (roleDiversityIndex < 0.6) {
            return "中等多样性";
        } else if (roleDiversityIndex < 0.8) {
            return "高多样性";
        } else {
            return "极高多样性";
        }
    }

    /**
     * 检查是否角色分布均衡
     */
    public boolean isBalanced() {
        if (roleStats == null || roleStats.size() < 2) {
            return true;
        }

        // 检查是否有角色占比过高（超过70%）
        for (RoleStats stats : roleStats.values()) {
            if (stats.getMemberPercentage() != null && stats.getMemberPercentage() > 70) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取建议的角色调整
     */
    public String getRoleAdjustmentSuggestions() {
        if (!hasRoleDistribution()) {
            return "暂无建议";
        }

        StringBuilder suggestions = new StringBuilder();

        // 检查是否有角色缺失
        if (getOwnerStats() == null || getOwnerStats().getMemberCount() == null || getOwnerStats().getMemberCount() == 0) {
            suggestions.append("建议设置至少一个拥有者。");
        }

        // 检查管理员比例
        RoleStats adminStats = getAdminStats();
        if (adminStats != null && adminStats.getMemberPercentage() != null) {
            if (adminStats.getMemberPercentage() < 10) {
                suggestions.append("管理员比例较低，考虑增加管理员以分担管理任务。");
            } else if (adminStats.getMemberPercentage() > 30) {
                suggestions.append("管理员比例较高，考虑减少管理员数量或明确职责。");
            }
        }

        return suggestions.toString().trim();
    }
}