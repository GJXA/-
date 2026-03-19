package com.xushu.campus.job.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.common.model.Result;
import com.xushu.campus.job.dto.*;
import com.xushu.campus.job.service.JobApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 兼职申请管理控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
@Tag(name = "兼职申请管理", description = "兼职申请、处理、状态跟踪接口")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    @Operation(summary = "申请兼职", description = "申请一个兼职职位")
    public Result<JobApplicationDTO> applyJob(@Valid @RequestBody ApplyJobRequest request,
                                              HttpServletRequest httpRequest) {
        log.info("申请兼职请求: jobId={}", request.getJobId());

        Long applicantId = getUserIdFromRequest(httpRequest);
        JobApplicationDTO applicationDTO = jobApplicationService.applyJob(request, applicantId);
        return Result.success("申请提交成功，等待处理", applicationDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "取消申请", description = "取消已提交的兼职申请")
    public Result<String> cancelApplication(@PathVariable Long id,
                                          HttpServletRequest httpRequest) {
        log.info("取消申请请求: id={}", id);

        Long applicantId = getUserIdFromRequest(httpRequest);
        jobApplicationService.cancelApplication(id, applicantId);
        return Result.success("申请取消成功");
    }

    @PutMapping("/{id}/process")
    @Operation(summary = "处理申请", description = "处理兼职申请（通过或拒绝）")
    public Result<JobApplicationDTO> processApplication(@PathVariable Long id,
                                                        @RequestParam Boolean approved,
                                                        @RequestParam(required = false) String processRemark,
                                                        HttpServletRequest httpRequest) {
        log.info("处理申请请求: id={}, approved={}", id, approved);

        Long processorId = getUserIdFromRequest(httpRequest);
        JobApplicationDTO applicationDTO = jobApplicationService.processApplication(id, approved, processorId, processRemark);
        return Result.success("申请处理成功", applicationDTO);
    }

    @PutMapping("/{id}/schedule-interview")
    @Operation(summary = "安排面试", description = "为通过申请的候选人安排面试")
    public Result<JobApplicationDTO> scheduleInterview(@PathVariable Long id,
                                                       @RequestParam String interviewTime,
                                                       @RequestParam String interviewLocation,
                                                       @RequestParam(required = false) String interviewRemark,
                                                       HttpServletRequest httpRequest) {
        log.info("安排面试请求: id={}, interviewTime={}", id, interviewTime);

        Long processorId = getUserIdFromRequest(httpRequest);
        JobApplicationDTO applicationDTO = jobApplicationService.scheduleInterview(id, interviewTime, interviewLocation, interviewRemark, processorId);
        return Result.success("面试安排成功", applicationDTO);
    }

    @PutMapping("/{id}/send-offer")
    @Operation(summary = "发送录用通知", description = "向通过面试的候选人发送录用通知")
    public Result<JobApplicationDTO> sendOffer(@PathVariable Long id,
                                               @RequestParam String offerContent,
                                               HttpServletRequest httpRequest) {
        log.info("发送录用通知请求: id={}", id);

        Long processorId = getUserIdFromRequest(httpRequest);
        JobApplicationDTO applicationDTO = jobApplicationService.sendOffer(id, offerContent, processorId);
        return Result.success("录用通知发送成功", applicationDTO);
    }

    @PutMapping("/{id}/confirm-participation")
    @Operation(summary = "确认参加", description = "候选人确认是否参加兼职工作")
    public Result<JobApplicationDTO> confirmParticipation(@PathVariable Long id,
                                                          @RequestParam Boolean confirmed,
                                                          @RequestParam(required = false) String confirmRemark,
                                                          HttpServletRequest httpRequest) {
        log.info("确认参加请求: id={}, confirmed={}", id, confirmed);

        Long applicantId = getUserIdFromRequest(httpRequest);
        JobApplicationDTO applicationDTO = jobApplicationService.confirmParticipation(id, confirmed, confirmRemark, applicantId);
        return Result.success("参加确认成功", applicationDTO);
    }

    @PutMapping("/{id}/record-work-time")
    @Operation(summary = "记录实际工作时间", description = "记录候选人实际工作的开始和结束时间")
    public Result<JobApplicationDTO> recordWorkTime(@PathVariable Long id,
                                                    @RequestParam String actualStartTime,
                                                    @RequestParam String actualEndTime,
                                                    HttpServletRequest httpRequest) {
        log.info("记录工作时间请求: id={}, actualStartTime={}, actualEndTime={}", id, actualStartTime, actualEndTime);

        Long publisherId = getUserIdFromRequest(httpRequest);
        JobApplicationDTO applicationDTO = jobApplicationService.recordWorkTime(id, actualStartTime, actualEndTime, publisherId);
        return Result.success("工作时间记录成功", applicationDTO);
    }

    @PutMapping("/{id}/add-evaluation")
    @Operation(summary = "添加工作评价", description = "为完成工作的候选人添加工作评价")
    public Result<JobApplicationDTO> addWorkEvaluation(@PathVariable Long id,
                                                       @RequestParam String workEvaluation,
                                                       @RequestParam Integer evaluationScore,
                                                       HttpServletRequest httpRequest) {
        log.info("添加工作评价请求: id={}, evaluationScore={}", id, evaluationScore);

        Long publisherId = getUserIdFromRequest(httpRequest);
        JobApplicationDTO applicationDTO = jobApplicationService.addWorkEvaluation(id, workEvaluation, evaluationScore, publisherId);
        return Result.success("工作评价添加成功", applicationDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取申请详情", description = "根据ID获取申请详细信息")
    public Result<JobApplicationDTO> getApplicationById(@PathVariable Long id) {
        log.debug("获取申请详情请求: id={}", id);

        JobApplicationDTO applicationDTO = jobApplicationService.getApplicationById(id);
        return Result.success("获取成功", applicationDTO);
    }

    @GetMapping("/applicant/{applicantId}")
    @Operation(summary = "获取申请人的申请列表", description = "根据申请人ID获取申请列表")
    public Result<IPage<JobApplicationDTO>> getApplicationsByApplicantId(
            @PathVariable Long applicantId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取申请人的申请列表请求: applicantId={}, page={}, size={}", applicantId, page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsByApplicantId(applicantId, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "获取兼职的申请列表", description = "根据兼职ID获取申请列表")
    public Result<IPage<JobApplicationDTO>> getApplicationsByJobId(
            @PathVariable Long jobId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取兼职的申请列表请求: jobId={}, page={}, size={}", jobId, page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsByJobId(jobId, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/publisher/{publisherId}")
    @Operation(summary = "获取发布者的申请列表", description = "根据发布者ID获取申请列表")
    public Result<IPage<JobApplicationDTO>> getApplicationsByPublisherId(
            @PathVariable Long publisherId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取发布者的申请列表请求: publisherId={}, page={}, size={}", publisherId, page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsByPublisherId(publisherId, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "获取指定状态的申请列表", description = "根据申请状态获取申请列表")
    public Result<IPage<JobApplicationDTO>> getApplicationsByStatus(
            @PathVariable Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取指定状态的申请列表请求: status={}, page={}, size={}", status, page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsByStatus(status, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待处理的申请列表", description = "获取所有待处理的申请")
    public Result<IPage<JobApplicationDTO>> getPendingApplications(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取待处理的申请列表请求: page={}, size={}", page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getPendingApplications(page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/with-interview")
    @Operation(summary = "获取已安排面试的申请列表", description = "获取所有已安排面试的申请")
    public Result<IPage<JobApplicationDTO>> getApplicationsWithInterview(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取已安排面试的申请列表请求: page={}, size={}", page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsWithInterview(page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/with-offer")
    @Operation(summary = "获取已发送录用通知的申请列表", description = "获取所有已发送录用通知的申请")
    public Result<IPage<JobApplicationDTO>> getApplicationsWithOffer(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取已发送录用通知的申请列表请求: page={}, size={}", page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsWithOffer(page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/confirmed")
    @Operation(summary = "获取已确认参加的申请列表", description = "获取所有已确认参加的申请")
    public Result<IPage<JobApplicationDTO>> getConfirmedApplications(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取已确认参加的申请列表请求: page={}, size={}", page, size);

        IPage<JobApplicationDTO> result = jobApplicationService.getConfirmedApplications(page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/check-applied")
    @Operation(summary = "检查是否已申请", description = "检查当前用户是否已申请指定兼职")
    public Result<Boolean> hasApplied(@RequestParam Long jobId,
                                      HttpServletRequest httpRequest) {
        log.debug("检查是否已申请请求: jobId={}", jobId);

        Long applicantId = getUserIdFromRequest(httpRequest);
        boolean hasApplied = jobApplicationService.hasApplied(jobId, applicantId);
        return Result.success("检查完成", hasApplied);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取申请统计数据", description = "获取申请相关的统计数据")
    public Result<ApplicationStatisticsDTO> getApplicationStatistics(
            @RequestParam(required = false) Long publisherId,
            @RequestParam(required = false) Long jobId,
            HttpServletRequest httpRequest) {
        log.debug("获取申请统计数据请求: publisherId={}, jobId={}", publisherId, jobId);

        // 如果未提供publisherId，使用当前用户的ID
        if (publisherId == null) {
            publisherId = getUserIdFromRequest(httpRequest);
        }

        ApplicationStatisticsDTO statistics = jobApplicationService.getApplicationStatistics(publisherId, jobId);
        return Result.success("获取成功", statistics);
    }

    @GetMapping("/me")
    @Operation(summary = "获取我的申请列表", description = "获取当前用户的申请列表")
    public Result<IPage<JobApplicationDTO>> getMyApplications(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            HttpServletRequest httpRequest) {
        log.debug("获取我的申请列表请求: page={}, size={}", page, size);

        Long applicantId = getUserIdFromRequest(httpRequest);
        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsByApplicantId(applicantId, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/my-published")
    @Operation(summary = "获取我发布的兼职的申请列表", description = "获取当前用户发布的兼职的所有申请")
    public Result<IPage<JobApplicationDTO>> getApplicationsForMyJobs(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            HttpServletRequest httpRequest) {
        log.debug("获取我发布的兼职的申请列表请求: page={}, size={}", page, size);

        Long publisherId = getUserIdFromRequest(httpRequest);
        IPage<JobApplicationDTO> result = jobApplicationService.getApplicationsByPublisherId(publisherId, page, size);
        return Result.success("获取成功", result);
    }

    // ========== 私有方法 ==========

    /**
     * 从请求中获取用户ID（模拟实现）
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // TODO: 从JWT令牌中解析用户ID
        // 这里返回一个模拟的用户ID
        return 1L;
    }
}