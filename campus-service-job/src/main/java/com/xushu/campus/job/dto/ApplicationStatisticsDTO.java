package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 兼职申请统计数据DTO
 */
@Data
@Schema(description = "兼职申请统计数据")
public class ApplicationStatisticsDTO {

    @Schema(description = "总申请数量")
    private Integer totalApplications;

    @Schema(description = "待处理申请数量")
    private Integer pendingApplications;

    @Schema(description = "已通过申请数量")
    private Integer approvedApplications;

    @Schema(description = "已拒绝申请数量")
    private Integer rejectedApplications;

    @Schema(description = "已取消申请数量")
    private Integer canceledApplications;

    @Schema(description = "已安排面试数量")
    private Integer scheduledInterviews;

    @Schema(description = "已发送录用通知数量")
    private Integer sentOffers;

    @Schema(description = "已确认参加数量")
    private Integer confirmedParticipation;

    @Schema(description = "已完成工作数量")
    private Integer completedWorks;

    @Schema(description = "平均评价分数")
    private Double averageEvaluationScore;

    @Schema(description = "最高评价分数")
    private Integer maxEvaluationScore;

    @Schema(description = "最低评价分数")
    private Integer minEvaluationScore;

    @Schema(description = "申请转化率（通过申请/总申请）")
    private Double conversionRate;

    @Schema(description = "面试通过率（发送录用通知/已安排面试）")
    private Double interviewPassRate;

    @Schema(description = "录用确认率（确认参加/发送录用通知）")
    private Double offerAcceptanceRate;
}