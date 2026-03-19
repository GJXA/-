package com.xushu.campus.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会话统计信息DTO
 */
@Data
@Schema(description = "会话统计信息")
public class ConversationStatisticsDTO {

    @Schema(description = "会话ID")
    private Long conversationId;

    @Schema(description = "会话标题")
    private String conversationTitle;

    @Schema(description = "总消息数量")
    private Long totalMessageCount;

    @Schema(description = "今日消息数量")
    private Long todayMessageCount;

    @Schema(description = "本周消息数量")
    private Long weekMessageCount;

    @Schema(description = "本月消息数量")
    private Long monthMessageCount;

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "活跃成员数量（最近7天发送过消息）")
    private Integer activeMemberCount;

    @Schema(description = "未读消息数量（针对当前用户）")
    private Integer unreadCount;

    @Schema(description = "消息类型统计")
    private MessageTypeStats messageTypeStats;

    @Schema(description = "反应统计")
    private ReactionStats reactionStats;

    @Schema(description = "成员加入/离开统计")
    private MemberActivityStats memberActivityStats;

    /**
     * 消息类型统计
     */
    @Data
    @Schema(description = "消息类型统计")
    public static class MessageTypeStats {
        @Schema(description = "文本消息数量")
        private Long textCount;

        @Schema(description = "图片消息数量")
        private Long imageCount;

        @Schema(description = "文件消息数量")
        private Long fileCount;

        @Schema(description = "语音消息数量")
        private Long voiceCount;

        @Schema(description = "视频消息数量")
        private Long videoCount;

        @Schema(description = "系统消息数量")
        private Long systemCount;

        @Schema(description = "其他消息数量")
        private Long otherCount;
    }

    /**
     * 反应统计
     */
    @Data
    @Schema(description = "反应统计")
    public static class ReactionStats {
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

        @Schema(description = "最常用的反应类型")
        private String mostUsedReactionType;

        @Schema(description = "最常用反应类型描述")
        private String mostUsedReactionDesc;

        @Schema(description = "最常用反应类型数量")
        private Long mostUsedReactionCount;
    }

    /**
     * 成员活动统计
     */
    @Data
    @Schema(description = "成员活动统计")
    public static class MemberActivityStats {
        @Schema(description = "今日加入成员数量")
        private Integer todayJoinedCount;

        @Schema(description = "今日离开成员数量")
        private Integer todayLeftCount;

        @Schema(description = "本周加入成员数量")
        private Integer weekJoinedCount;

        @Schema(description = "本周离开成员数量")
        private Integer weekLeftCount;

        @Schema(description = "本月加入成员数量")
        private Integer monthJoinedCount;

        @Schema(description = "本月离开成员数量")
        private Integer monthLeftCount;

        @Schema(description = "最活跃成员ID")
        private Long mostActiveMemberId;

        @Schema(description = "最活跃成员名称")
        private String mostActiveMemberName;

        @Schema(description = "最活跃成员消息数量")
        private Long mostActiveMemberMessageCount;
    }

    /**
     * 计算各种统计信息
     */
    public void calculate() {
        if (messageTypeStats != null) {
            long total = (messageTypeStats.textCount != null ? messageTypeStats.textCount : 0) +
                        (messageTypeStats.imageCount != null ? messageTypeStats.imageCount : 0) +
                        (messageTypeStats.fileCount != null ? messageTypeStats.fileCount : 0) +
                        (messageTypeStats.voiceCount != null ? messageTypeStats.voiceCount : 0) +
                        (messageTypeStats.videoCount != null ? messageTypeStats.videoCount : 0) +
                        (messageTypeStats.systemCount != null ? messageTypeStats.systemCount : 0) +
                        (messageTypeStats.otherCount != null ? messageTypeStats.otherCount : 0);
            this.totalMessageCount = total > 0 ? total : this.totalMessageCount;
        }

        if (reactionStats != null) {
            // 计算最常用的反应类型
            if (reactionStats.likeCount != null && reactionStats.likeCount > 0) {
                reactionStats.mostUsedReactionType = "LIKE";
                reactionStats.mostUsedReactionDesc = "点赞";
                reactionStats.mostUsedReactionCount = reactionStats.likeCount;
            }
            // 这里可以添加更多逻辑来比较各种反应类型的数量
        }
    }

    /**
     * 检查是否有统计信息
     */
    public boolean hasStatistics() {
        return totalMessageCount != null || memberCount != null || unreadCount != null;
    }

    /**
     * 获取活跃度百分比（活跃成员/总成员）
     */
    public Double getActivityPercentage() {
        if (memberCount == null || memberCount == 0 || activeMemberCount == null) {
            return 0.0;
        }
        return activeMemberCount * 100.0 / memberCount;
    }

    /**
     * 获取今日消息增长率（相对于昨日）
     */
    public Double getTodayMessageGrowthRate() {
        // 简化实现，实际需要昨日数据
        return 0.0;
    }
}