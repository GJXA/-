package com.xushu.campus.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.job.dto.*;
import com.xushu.campus.job.entity.Job;

import java.util.List;

/**
 * 兼职服务接口
 */
public interface JobService {

    /**
     * 创建兼职
     */
    JobDTO createJob(CreateJobRequest request, Long publisherId, String publisherType, String publisherName) throws BusinessException;

    /**
     * 更新兼职
     */
    JobDTO updateJob(Long jobId, UpdateJobRequest request, Long operatorId) throws BusinessException;

    /**
     * 删除兼职（逻辑删除）
     */
    void deleteJob(Long jobId, Long operatorId) throws BusinessException;

    /**
     * 根据ID获取兼职详情
     */
    JobDTO getJobById(Long jobId) throws BusinessException;

    /**
     * 根据ID获取兼职详情（增加浏览次数）
     */
    JobDTO getJobByIdWithView(Long jobId) throws BusinessException;

    /**
     * 搜索兼职
     */
    IPage<JobDTO> searchJobs(JobSearchRequest request);

    /**
     * 获取兼职列表（分页）
     */
    IPage<JobDTO> getJobList(Integer page, Integer size, String sortField, String sortDirection);

    /**
     * 根据发布者ID获取兼职列表
     */
    IPage<JobDTO> getJobsByPublisherId(Long publisherId, Integer page, Integer size);

    /**
     * 根据工作类型获取兼职列表
     */
    IPage<JobDTO> getJobsByJobType(String jobType, Integer page, Integer size);

    /**
     * 根据工作类别获取兼职列表
     */
    IPage<JobDTO> getJobsByCategory(String category, Integer page, Integer size);

    /**
     * 获取置顶兼职列表
     */
    List<JobDTO> getTopJobs();

    /**
     * 获取推荐兼职列表
     */
    List<JobDTO> getRecommendedJobs();

    /**
     * 获取即将过期兼职列表（3天内过期）
     */
    List<JobDTO> getAboutToExpireJobs();

    /**
     * 获取有剩余名额的兼职列表
     */
    List<JobDTO> getJobsWithRemaining();

    /**
     * 更新兼职状态
     */
    void updateJobStatus(Long jobId, Integer status, Long auditorId, String auditRemark) throws BusinessException;

    /**
     * 审核兼职（通过/拒绝）
     */
    void auditJob(Long jobId, boolean approved, Long auditorId, String auditRemark) throws BusinessException;

    /**
     * 置顶/取消置顶兼职
     */
    void setTopStatus(Long jobId, boolean isTop, Integer topWeight, Long operatorId) throws BusinessException;

    /**
     * 推荐/取消推荐兼职
     */
    void setRecommendedStatus(Long jobId, boolean isRecommended, Long operatorId) throws BusinessException;

    /**
     * 增加收藏次数
     */
    void increaseFavoriteCount(Long jobId);

    /**
     * 减少收藏次数
     */
    void decreaseFavoriteCount(Long jobId);

    /**
     * 检查用户是否可以申请该兼职
     */
    boolean canApplyJob(Long jobId, Long applicantId) throws BusinessException;

    /**
     * 获取兼职统计数据
     */
    JobStatisticsDTO getJobStatistics(Long publisherId);

    /**
     * 批量更新兼职状态（例如：自动结束过期的兼职）
     */
    void batchUpdateStatus(List<Long> jobIds, Integer status);

    /**
     * 检查兼职是否存在且可用
     */
    boolean checkJobAvailable(Long jobId);

    /**
     * 获取过期兼职ID列表（截止日期已过且状态为招聘中）
     */
    List<Long> getExpiredJobIds();

    /**
     * 获取已招满兼职ID列表（招聘人数等于已录取人数且状态为招聘中）
     */
    List<Long> getFullJobIds();

    /**
     * 清理已删除的兼职（逻辑删除时间超过30天）
     */
    void cleanupDeletedJobs();

    /**
     * 更新兼职统计数据
     */
    void updateJobStatistics();
}