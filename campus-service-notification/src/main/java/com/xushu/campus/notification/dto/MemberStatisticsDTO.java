package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成员统计信息DTO
 */
@Data
@Schema(description = "成员统计信息")
public class MemberStatisticsDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "总成员数量")
    private Integer totalMemberCount;

    @Schema(description = "活跃成员数量（最近7天有活动）")
    private Integer activeMemberCount;

    @Schema(description = "离线成员数量")
    private Integer offlineMemberCount;

    @Schema(description = "静音成员数量")
    private Integer mutedMemberCount;

    @Schema(description = "拥有者数量")
    private Integer ownerCount;

    @Schema(description = "管理员数量")
    private Integer adminCount;

    @Schema(description = "普通成员数量")
    private Integer memberCount;

    @Schema(description = "今日新加入成员数量")
    private Integer todayJoinedCount;

    @Schema(description = "本周新加入成员数量")
    private Integer weekJoinedCount;

    @Schema(description = "本月新加入成员数量")
    private Integer monthJoinedCount;

    @Schema(description = "今日离开成员数量")
    private Integer todayLeftCount;

    @Schema(description = "本周离开成员数量")
    private Integer weekLeftCount;

    @Schema(description = "本月离开成员数量")
    private Integer monthLeftCount;

    @Schema(description = "平均成员在线时长（分钟）")
    private Double averageOnlineTime;

    @Schema(description = "最活跃成员ID")
    private Long mostActiveMemberId;

    @Schema(description = "最活跃成员名称")
    private String mostActiveMemberName;

    @Schema(description = "最活跃成员消息数量")
    private Long mostActiveMemberMessageCount;

    @Schema(description = "最早加入的成员ID")
    private Long earliestMemberId;

    @Schema(description = "最早加入的成员名称")
    private String earliestMemberName;

    @Schema(description = "最早加入时间")
    private LocalDateTime earliestJoinTime;

    @Schema(description = "最近加入的成员ID")
    private Long latestMemberId;

    @Schema(description = "最近加入的成员名称")
    private String latestMemberName;

    @Schema(description = "最近加入时间")
    private LocalDateTime latestJoinTime;

    @Schema(description = "成员地区分布")
    private RegionDistribution regionDistribution;

    @Schema(description = "成员活跃度趋势")
    private ActivityTrend activityTrend;

    /**
     * 地区分布
     */
    @Data
    @Schema(description = "地区分布")
    public static class RegionDistribution {
        @Schema(description = "地区列表")
        private String[] regions;

        @Schema(description = "成员数量列表")
        private Integer[] memberCounts;
    }

    /**
     * 活跃度趋势
     */
    @Data
    @Schema(description = "活跃度趋势")
    public static class ActivityTrend {
        @Schema(description = "日期列表")
        private String[] dates;

        @Schema(description = "活跃成员数量列表")
        private Integer[] activeMemberCounts;
    }

    /**
     * 计算统计信息
     */
    public void calculate() {
        // 计算总成员数量
        if (ownerCount != null && adminCount != null && memberCount != null) {
            this.totalMemberCount = ownerCount + adminCount + memberCount;
        }

        // 计算活跃成员百分比
        if (totalMemberCount != null && totalMemberCount > 0 && activeMemberCount != null) {
            // 可以在需要时计算百分比
        }

        // 计算角色分布百分比
        if (totalMemberCount != null && totalMemberCount > 0) {
            // 可以在需要时计算百分比
        }
    }

    /**
     * 获取活跃成员百分比
     */
    public Double getActiveMemberPercentage() {
        if (totalMemberCount == null || totalMemberCount == 0 || activeMemberCount == null) {
            return 0.0;
        }
        return activeMemberCount * 100.0 / totalMemberCount;
    }

    /**
     * 获取拥有者百分比
     */
    public Double getOwnerPercentage() {
        if (totalMemberCount == null || totalMemberCount == 0 || ownerCount == null) {
            return 0.0;
        }
        return ownerCount * 100.0 / totalMemberCount;
    }

    /**
     * 获取管理员百分比
     */
    public Double getAdminPercentage() {
        if (totalMemberCount == null || totalMemberCount == 0 || adminCount == null) {
            return 0.0;
        }
        return adminCount * 100.0 / totalMemberCount;
    }

    /**
     * 获取普通成员百分比
     */
    public Double getMemberPercentage() {
        if (totalMemberCount == null || totalMemberCount == 0 || memberCount == null) {
            return 0.0;
        }
        return memberCount * 100.0 / totalMemberCount;
    }

    /**
     * 获取成员净增长（加入-离开）
     */
    public Integer getNetGrowthToday() {
        if (todayJoinedCount == null || todayLeftCount == null) {
            return 0;
        }
        return todayJoinedCount - todayLeftCount;
    }

    /**
     * 获取成员净增长本周
     */
    public Integer getNetGrowthWeek() {
        if (weekJoinedCount == null || weekLeftCount == null) {
            return 0;
        }
        return weekJoinedCount - weekLeftCount;
    }

    /**
     * 获取成员净增长本月
     */
    public Integer getNetGrowthMonth() {
        if (monthJoinedCount == null || monthLeftCount == null) {
            return 0;
        }
        return monthJoinedCount - monthLeftCount;
    }

    /**
     * 检查是否有统计数据
     */
    public boolean hasStatistics() {
        return totalMemberCount != null || activeMemberCount != null || ownerCount != null;
    }

    /**
     * 获取成员稳定性（1 - 离开率）
     */
    public Double getMemberStability() {
        if (totalMemberCount == null || totalMemberCount == 0 || monthLeftCount == null) {
            return 100.0;
        }
        double leaveRate = monthLeftCount * 100.0 / totalMemberCount;
        return Math.max(0, 100.0 - leaveRate);
    }

    /**
     * 获取成员角色分布描述
     */
    public String getRoleDistributionDesc() {
        StringBuilder sb = new StringBuilder();
        if (ownerCount != null && ownerCount > 0) {
            sb.append("拥有者:").append(ownerCount).append(" ");
        }
        if (adminCount != null && adminCount > 0) {
            sb.append("管理员:").append(adminCount).append(" ");
        }
        if (memberCount != null && memberCount > 0) {
            sb.append("成员:").append(memberCount);
        }
        return sb.toString().trim();
    }

    /**
     * 获取成员活跃度级别
     */
    public String getActivityLevel() {
        if (activeMemberCount == null || totalMemberCount == null || totalMemberCount == 0) {
            return "低活跃度";
        }
        double percentage = getActiveMemberPercentage();
        if (percentage >= 70) {
            return "高活跃度";
        } else if (percentage >= 40) {
            return "中活跃度";
        } else {
            return "低活跃度";
        }
    }
}