package com.xushu.campus.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.job.dto.JobApplicationDTO;
import com.xushu.campus.job.dto.ApplyJobRequest;
import com.xushu.campus.job.dto.UpdateApplicationRequest;
import com.xushu.campus.job.dto.ApplicationStatisticsDTO;

import java.util.List;

/**
 * 兼职申请服务接口
 */
public interface JobApplicationService {

    /**
     * 申请兼职
     */
    JobApplicationDTO applyJob(ApplyJobRequest request, Long applicantId) throws BusinessException;

    /**
     * 取消申请
     */
    void cancelApplication(Long applicationId, Long applicantId) throws BusinessException;

    /**
     * 处理申请（通过/拒绝）
     */
    JobApplicationDTO processApplication(Long applicationId, boolean approved, Long processorId, String processRemark) throws BusinessException;

    /**
     * 安排面试
     */
    JobApplicationDTO scheduleInterview(Long applicationId, String interviewTime, String interviewLocation, String interviewRemark, Long processorId) throws BusinessException;

    /**
     * 发送录用通知
     */
    JobApplicationDTO sendOffer(Long applicationId, String offerContent, Long processorId) throws BusinessException;

    /**
     * 确认参加
     */
    JobApplicationDTO confirmParticipation(Long applicationId, boolean confirmed, String confirmRemark, Long applicantId) throws BusinessException;

    /**
     * 记录实际工作时间
     */
    JobApplicationDTO recordWorkTime(Long applicationId, String actualStartTime, String actualEndTime, Long publisherId) throws BusinessException;

    /**
     * 添加工作评价
     */
    JobApplicationDTO addWorkEvaluation(Long applicationId, String workEvaluation, Integer evaluationScore, Long publisherId) throws BusinessException;

    /**
     * 根据ID获取申请详情
     */
    JobApplicationDTO getApplicationById(Long applicationId) throws BusinessException;

    /**
     * 根据申请人ID获取申请列表
     */
    IPage<JobApplicationDTO> getApplicationsByApplicantId(Long applicantId, Integer page, Integer size);

    /**
     * 根据兼职ID获取申请列表
     */
    IPage<JobApplicationDTO> getApplicationsByJobId(Long jobId, Integer page, Integer size);

    /**
     * 根据发布者ID获取申请列表
     */
    IPage<JobApplicationDTO> getApplicationsByPublisherId(Long publisherId, Integer page, Integer size);

    /**
     * 根据状态获取申请列表
     */
    IPage<JobApplicationDTO> getApplicationsByStatus(Integer status, Integer page, Integer size);

    /**
     * 获取待处理的申请列表
     */
    IPage<JobApplicationDTO> getPendingApplications(Integer page, Integer size);

    /**
     * 获取已安排面试的申请列表
     */
    IPage<JobApplicationDTO> getApplicationsWithInterview(Integer page, Integer size);

    /**
     * 获取已发送录用通知的申请列表
     */
    IPage<JobApplicationDTO> getApplicationsWithOffer(Integer page, Integer size);

    /**
     * 获取已确认参加的申请列表
     */
    IPage<JobApplicationDTO> getConfirmedApplications(Integer page, Integer size);

    /**
     * 检查是否已申请
     */
    boolean hasApplied(Long jobId, Long applicantId);

    /**
     * 获取申请统计数据
     */
    ApplicationStatisticsDTO getApplicationStatistics(Long publisherId, Long jobId);

    /**
     * 更新申请冗余字段（当兼职信息更新时调用）
     */
    void updateApplicationRedundantFields(Long jobId);
}