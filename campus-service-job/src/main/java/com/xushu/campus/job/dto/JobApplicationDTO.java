package com.xushu.campus.job.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 兼职申请DTO
 */
@Data
@Schema(description = "兼职申请信息")
public class JobApplicationDTO {

    @Schema(description = "申请记录ID")
    private Long id;

    @Schema(description = "兼职ID")
    private Long jobId;

    @Schema(description = "兼职标题")
    private String jobTitle;

    @Schema(description = "兼职薪资")
    private String jobSalary;

    @Schema(description = "兼职工作类型")
    private String jobType;

    @Schema(description = "兼职工作地点")
    private String jobLocation;

    @Schema(description = "申请人ID")
    private Long applicantId;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "申请人电话")
    private String applicantPhone;

    @Schema(description = "申请人邮箱")
    private String applicantEmail;

    @Schema(description = "申请人年级/专业")
    private String applicantGrade;

    @Schema(description = "简历")
    private String resume;

    @Schema(description = "申请状态")
    private Integer status;

    @Schema(description = "申请状态描述")
    private String statusDesc;

    @Schema(description = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;

    @Schema(description = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processTime;

    @Schema(description = "处理人ID")
    private Long processorId;

    @Schema(description = "处理人姓名")
    private String processorName;

    @Schema(description = "处理备注")
    private String processRemark;

    @Schema(description = "申请备注")
    private String applyRemark;

    @Schema(description = "面试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime interviewTime;

    @Schema(description = "面试地点")
    private String interviewLocation;

    @Schema(description = "面试备注")
    private String interviewRemark;

    @Schema(description = "录用通知发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime offerTime;

    @Schema(description = "录用通知内容")
    private String offerContent;

    @Schema(description = "确认状态")
    private Integer confirmStatus;

    @Schema(description = "确认状态描述")
    private String confirmStatusDesc;

    @Schema(description = "确认时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;

    @Schema(description = "确认备注")
    private String confirmRemark;

    @Schema(description = "实际开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualStartTime;

    @Schema(description = "实际结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualEndTime;

    @Schema(description = "工作评价")
    private String workEvaluation;

    @Schema(description = "评价分数")
    private Integer evaluationScore;

    @Schema(description = "评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluationTime;

    @Schema(description = "发布者ID")
    private Long publisherId;

    @Schema(description = "发布者名称")
    private String publisherName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 计算状态描述
     */
    public void calculateDesc() {
        if (this.status != null) {
            this.statusDesc = com.xushu.campus.job.constant.JobConstants.ApplicationStatus.getDesc(this.status);
        }
        if (this.confirmStatus != null) {
            this.confirmStatusDesc = com.xushu.campus.job.constant.JobConstants.ConfirmStatus.getDesc(this.confirmStatus);
        }
    }

    /**
     * 检查是否可以处理（只有待处理的申请可以处理）
     */
    public boolean canProcess() {
        return com.xushu.campus.job.constant.JobConstants.ApplicationStatus.canProcess(this.status);
    }

    /**
     * 检查是否可以取消（待处理的申请可以取消）
     */
    public boolean canCancel() {
        return com.xushu.campus.job.constant.JobConstants.ApplicationStatus.canCancel(this.status);
    }

    /**
     * 检查是否已通过
     */
    public boolean isApproved() {
        return this.status != null && this.status == com.xushu.campus.job.constant.JobConstants.ApplicationStatus.APPROVED;
    }

    /**
     * 检查是否已安排面试
     */
    public boolean hasInterviewScheduled() {
        return this.interviewTime != null;
    }

    /**
     * 检查是否已发送录用通知
     */
    public boolean hasOfferSent() {
        return this.offerTime != null;
    }

    /**
     * 检查是否已确认参加
     */
    public boolean hasConfirmedParticipation() {
        return this.confirmStatus != null && this.confirmStatus == com.xushu.campus.job.constant.JobConstants.ConfirmStatus.CONFIRMED;
    }

    /**
     * 检查是否已完成工作评价
     */
    public boolean hasWorkEvaluation() {
        return this.evaluationTime != null && this.workEvaluation != null;
    }
}