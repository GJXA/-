package com.xushu.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.job.entity.JobApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 兼职申请Mapper接口
 */
@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {

    /**
     * 根据申请人ID查询申请列表
     */
    @Select("SELECT * FROM job_applications WHERE applicant_id = #{applicantId} AND is_deleted = 0 ORDER BY apply_time DESC")
    List<JobApplication> selectByApplicantId(@Param("applicantId") Long applicantId);

    /**
     * 根据兼职ID查询申请列表
     */
    @Select("SELECT * FROM job_applications WHERE job_id = #{jobId} AND is_deleted = 0 ORDER BY apply_time DESC")
    List<JobApplication> selectByJobId(@Param("jobId") Long jobId);

    /**
     * 根据发布者ID查询申请列表（通过冗余的publisher_id字段）
     */
    @Select("SELECT ja.* FROM job_applications ja JOIN jobs j ON ja.job_id = j.id WHERE j.publisher_id = #{publisherId} AND ja.is_deleted = 0 ORDER BY ja.apply_time DESC")
    List<JobApplication> selectByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 根据状态查询申请列表
     */
    @Select("SELECT * FROM job_applications WHERE status = #{status} AND is_deleted = 0 ORDER BY apply_time DESC")
    List<JobApplication> selectByStatus(@Param("status") Integer status);

    /**
     * 根据申请人和兼职ID查询申请记录（检查是否已申请）
     */
    @Select("SELECT * FROM job_applications WHERE applicant_id = #{applicantId} AND job_id = #{jobId} AND is_deleted = 0")
    JobApplication selectByApplicantAndJob(@Param("applicantId") Long applicantId, @Param("jobId") Long jobId);

    /**
     * 根据申请状态和兼职ID查询申请列表
     */
    @Select("SELECT * FROM job_applications WHERE job_id = #{jobId} AND status = #{status} AND is_deleted = 0 ORDER BY apply_time DESC")
    List<JobApplication> selectByJobIdAndStatus(@Param("jobId") Long jobId, @Param("status") Integer status);

    /**
     * 根据确认状态查询申请列表
     */
    @Select("SELECT * FROM job_applications WHERE confirm_status = #{confirmStatus} AND is_deleted = 0 ORDER BY apply_time DESC")
    List<JobApplication> selectByConfirmStatus(@Param("confirmStatus") Integer confirmStatus);

    /**
     * 查询待处理的申请数量
     */
    @Select("SELECT COUNT(*) FROM job_applications WHERE status = 0 AND is_deleted = 0")
    Integer countPendingApplications();

    /**
     * 查询指定兼职的待处理申请数量
     */
    @Select("SELECT COUNT(*) FROM job_applications WHERE job_id = #{jobId} AND status = 0 AND is_deleted = 0")
    Integer countPendingApplicationsByJobId(@Param("jobId") Long jobId);

    /**
     * 查询已安排面试的申请列表
     */
    @Select("SELECT * FROM job_applications WHERE interview_time IS NOT NULL AND is_deleted = 0 ORDER BY interview_time ASC")
    List<JobApplication> selectApplicationsWithInterview();

    /**
     * 查询已发送录用通知的申请列表
     */
    @Select("SELECT * FROM job_applications WHERE offer_time IS NOT NULL AND is_deleted = 0 ORDER BY offer_time DESC")
    List<JobApplication> selectApplicationsWithOffer();

    /**
     * 查询已确认参加的申请列表
     */
    @Select("SELECT * FROM job_applications WHERE confirm_status = 1 AND is_deleted = 0 ORDER BY confirm_time DESC")
    List<JobApplication> selectConfirmedApplications();

    /**
     * 更新申请状态
     */
    @Select("UPDATE job_applications SET status = #{status}, process_time = NOW(), processor_id = #{processorId}, process_remark = #{processRemark}, updated_at = NOW() WHERE id = #{applicationId} AND is_deleted = 0")
    void updateStatus(@Param("applicationId") Long applicationId, @Param("status") Integer status,
                      @Param("processorId") Long processorId, @Param("processorName") String processorName,
                      @Param("processRemark") String processRemark);

    /**
     * 安排面试
     */
    @Select("UPDATE job_applications SET interview_time = #{interviewTime}, interview_location = #{interviewLocation}, interview_remark = #{interviewRemark}, updated_at = NOW() WHERE id = #{applicationId} AND is_deleted = 0")
    void scheduleInterview(@Param("applicationId") Long applicationId, @Param("interviewTime") LocalDateTime interviewTime,
                          @Param("interviewLocation") String interviewLocation, @Param("interviewRemark") String interviewRemark);

    /**
     * 发送录用通知
     */
    @Select("UPDATE job_applications SET offer_time = NOW(), offer_content = #{offerContent}, updated_at = NOW() WHERE id = #{applicationId} AND is_deleted = 0")
    void sendOffer(@Param("applicationId") Long applicationId, @Param("offerContent") String offerContent);

    /**
     * 确认参加状态
     */
    @Select("UPDATE job_applications SET confirm_status = #{confirmStatus}, confirm_time = NOW(), confirm_remark = #{confirmRemark}, updated_at = NOW() WHERE id = #{applicationId} AND is_deleted = 0")
    void updateConfirmStatus(@Param("applicationId") Long applicationId, @Param("confirmStatus") Integer confirmStatus,
                            @Param("confirmRemark") String confirmRemark);

    /**
     * 记录实际工作时间
     */
    @Select("UPDATE job_applications SET actual_start_time = #{actualStartTime}, actual_end_time = #{actualEndTime}, updated_at = NOW() WHERE id = #{applicationId} AND is_deleted = 0")
    void recordWorkTime(@Param("applicationId") Long applicationId, @Param("actualStartTime") LocalDateTime actualStartTime,
                       @Param("actualEndTime") LocalDateTime actualEndTime);

    /**
     * 添加工作评价
     */
    @Select("UPDATE job_applications SET work_evaluation = #{workEvaluation}, evaluation_score = #{evaluationScore}, evaluation_time = NOW(), updated_at = NOW() WHERE id = #{applicationId} AND is_deleted = 0")
    void addWorkEvaluation(@Param("applicationId") Long applicationId, @Param("workEvaluation") String workEvaluation,
                          @Param("evaluationScore") Integer evaluationScore);

    /**
     * 更新冗余字段（当兼职信息更新时调用）
     */
    @Select("UPDATE job_applications SET job_title = #{jobTitle}, job_salary = #{jobSalary}, job_type = #{jobType}, job_location = #{jobLocation}, publisher_id = #{publisherId}, publisher_name = #{publisherName}, updated_at = NOW() WHERE job_id = #{jobId} AND is_deleted = 0")
    void updateRedundantFields(@Param("jobId") Long jobId, @Param("jobTitle") String jobTitle,
                              @Param("jobSalary") String jobSalary, @Param("jobType") String jobType,
                              @Param("jobLocation") String jobLocation, @Param("publisherId") Long publisherId,
                              @Param("publisherName") String publisherName);
}