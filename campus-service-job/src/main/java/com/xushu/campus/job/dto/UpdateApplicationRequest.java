package com.xushu.campus.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新兼职申请请求
 */
@Data
@Schema(description = "更新兼职申请请求")
public class UpdateApplicationRequest {

    @Schema(description = "申请状态：0-待处理，1-已通过，2-已拒绝，3-已取消")
    private Integer status;

    @Schema(description = "处理备注", maxLength = 500)
    private String processRemark;

    @Schema(description = "面试时间")
    private LocalDateTime interviewTime;

    @Schema(description = "面试地点", maxLength = 200)
    private String interviewLocation;

    @Schema(description = "面试备注", maxLength = 500)
    private String interviewRemark;

    @Schema(description = "录用通知内容", maxLength = 1000)
    private String offerContent;

    @Schema(description = "是否确认参加：0-未确认，1-确认参加，2-拒绝参加")
    private Integer confirmStatus;

    @Schema(description = "确认备注", maxLength = 500)
    private String confirmRemark;

    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @Schema(description = "实际结束时间")
    private LocalDateTime actualEndTime;

    @Schema(description = "工作评价", maxLength = 1000)
    private String workEvaluation;

    @Schema(description = "评价分数（1-5分）")
    private Integer evaluationScore;

    /**
     * 检查是否有更新字段
     */
    public boolean hasUpdates() {
        return this.status != null ||
                this.processRemark != null ||
                this.interviewTime != null ||
                this.interviewLocation != null ||
                this.interviewRemark != null ||
                this.offerContent != null ||
                this.confirmStatus != null ||
                this.confirmRemark != null ||
                this.actualStartTime != null ||
                this.actualEndTime != null ||
                this.workEvaluation != null ||
                this.evaluationScore != null;
    }
}