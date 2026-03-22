package com.xushu.campus.job.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.common.model.Result;
import com.xushu.campus.job.dto.*;
import com.xushu.campus.job.service.JobService;
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
 * 兼职管理控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "兼职管理", description = "兼职信息发布、搜索、管理接口")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @Operation(summary = "创建兼职", description = "发布新的兼职信息")
    public Result<JobDTO> createJob(@Valid @RequestBody CreateJobRequest request,
                                    HttpServletRequest httpRequest) {
        log.info("创建兼职请求: title={}", request.getTitle());

        // 从请求中获取用户信息（实际应从JWT令牌中解析）
        Long publisherId = getUserIdFromRequest(httpRequest);
        String publisherType = getUserTypeFromRequest(httpRequest);
        String publisherName = getUsernameFromRequest(httpRequest);

        JobDTO jobDTO = jobService.createJob(request, publisherId, publisherType, publisherName);
        return Result.success("兼职创建成功，等待审核", jobDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新兼职", description = "更新兼职信息")
    public Result<JobDTO> updateJob(@PathVariable Long id,
                                    @Valid @RequestBody UpdateJobRequest request,
                                    HttpServletRequest httpRequest) {
        log.info("更新兼职请求: id={}", id);

        Long operatorId = getUserIdFromRequest(httpRequest);
        JobDTO jobDTO = jobService.updateJob(id, request, operatorId);
        return Result.success("兼职更新成功", jobDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除兼职", description = "逻辑删除兼职")
    public Result<String> deleteJob(@PathVariable Long id,
                                  HttpServletRequest httpRequest) {
        log.info("删除兼职请求: id={}", id);

        Long operatorId = getUserIdFromRequest(httpRequest);
        jobService.deleteJob(id, operatorId);
        return Result.success("兼职删除成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取兼职详情", description = "根据ID获取兼职详细信息")
    public Result<JobDTO> getJobById(@PathVariable Long id,
                                     @RequestParam(required = false, defaultValue = "false") Boolean incrementView) {
        log.debug("获取兼职详情请求: id={}, incrementView={}", id, incrementView);

        JobDTO jobDTO;
        if (Boolean.TRUE.equals(incrementView)) {
            jobDTO = jobService.getJobByIdWithView(id);
        } else {
            jobDTO = jobService.getJobById(id);
        }
        return Result.success("获取成功", jobDTO);
    }

    @PostMapping("/search")
    @Operation(summary = "搜索兼职", description = "根据条件搜索兼职信息")
    public Result<IPage<JobDTO>> searchJobs(@Valid @RequestBody JobSearchRequest request) {
        log.debug("搜索兼职请求: keyword={}, jobType={}, category={}",
                request.getKeyword(), request.getJobType(), request.getCategory());

        IPage<JobDTO> result = jobService.searchJobs(request);
        return Result.success("搜索成功", result);
    }

    @GetMapping
    @Operation(summary = "获取兼职列表", description = "分页获取兼职列表")
    public Result<IPage<JobDTO>> getJobList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "publish_time") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection) {
        log.debug("获取兼职列表请求: page={}, size={}, sortField={}, sortDirection={}",
                page, size, sortField, sortDirection);

        IPage<JobDTO> result = jobService.getJobList(page, size, sortField, sortDirection);
        return Result.success("获取成功", result);
    }

    @GetMapping("/publisher/{publisherId}")
    @Operation(summary = "获取发布者的兼职列表", description = "根据发布者ID获取兼职列表")
    public Result<IPage<JobDTO>> getJobsByPublisherId(
            @PathVariable Long publisherId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("获取发布者的兼职列表请求: publisherId={}, page={}, size={}", publisherId, page, size);

        IPage<JobDTO> result = jobService.getJobsByPublisherId(publisherId, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/type/{jobType}")
    @Operation(summary = "根据工作类型获取兼职列表", description = "根据工作类型筛选兼职")
    public Result<IPage<JobDTO>> getJobsByJobType(
            @PathVariable String jobType,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("根据工作类型获取兼职列表请求: jobType={}, page={}, size={}", jobType, page, size);

        IPage<JobDTO> result = jobService.getJobsByJobType(jobType, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "根据工作类别获取兼职列表", description = "根据工作类别筛选兼职")
    public Result<IPage<JobDTO>> getJobsByCategory(
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.debug("根据工作类别获取兼职列表请求: category={}, page={}, size={}", category, page, size);

        IPage<JobDTO> result = jobService.getJobsByCategory(category, page, size);
        return Result.success("获取成功", result);
    }

    @GetMapping("/top")
    @Operation(summary = "获取置顶兼职列表", description = "获取所有置顶的兼职")
    public Result<List<JobDTO>> getTopJobs() {
        log.debug("获取置顶兼职列表请求");

        List<JobDTO> result = jobService.getTopJobs();
        return Result.success("获取成功", result);
    }

    @GetMapping("/recommended")
    @Operation(summary = "获取推荐兼职列表", description = "获取所有推荐的兼职")
    public Result<List<JobDTO>> getRecommendedJobs() {
        log.debug("获取推荐兼职列表请求");

        List<JobDTO> result = jobService.getRecommendedJobs();
        return Result.success("获取成功", result);
    }

    @GetMapping("/about-to-expire")
    @Operation(summary = "获取即将过期兼职列表", description = "获取3天内即将过期的兼职")
    public Result<List<JobDTO>> getAboutToExpireJobs() {
        log.debug("获取即将过期兼职列表请求");

        List<JobDTO> result = jobService.getAboutToExpireJobs();
        return Result.success("获取成功", result);
    }

    @GetMapping("/with-remaining")
    @Operation(summary = "获取有剩余名额的兼职列表", description = "获取还有剩余名额的兼职")
    public Result<List<JobDTO>> getJobsWithRemaining() {
        log.debug("获取有剩余名额的兼职列表请求");

        List<JobDTO> result = jobService.getJobsWithRemaining();
        return Result.success("获取成功", result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新兼职状态", description = "更新兼职的工作状态")
    public Result<String> updateJobStatus(@PathVariable Long id,
                                        @RequestParam Integer status,
                                        @RequestParam(required = false) String auditRemark,
                                        HttpServletRequest httpRequest) {
        log.info("更新兼职状态请求: id={}, status={}", id, status);

        Long auditorId = getUserIdFromRequest(httpRequest);
        jobService.updateJobStatus(id, status, auditorId, auditRemark);
        return Result.success("状态更新成功");
    }

    @PutMapping("/{id}/audit")
    @Operation(summary = "审核兼职", description = "审核兼职（通过或拒绝）")
    public Result<String> auditJob(@PathVariable Long id,
                                 @RequestParam Boolean approved,
                                 @RequestParam(required = false) String auditRemark,
                                 HttpServletRequest httpRequest) {
        log.info("审核兼职请求: id={}, approved={}", id, approved);

        Long auditorId = getUserIdFromRequest(httpRequest);
        jobService.auditJob(id, approved, auditorId, auditRemark);
        return Result.success("审核完成");
    }

    @PutMapping("/{id}/top")
    @Operation(summary = "设置置顶状态", description = "置顶或取消置顶兼职")
    public Result<String> setTopStatus(@PathVariable Long id,
                                     @RequestParam Boolean isTop,
                                     @RequestParam(required = false, defaultValue = "0") Integer topWeight,
                                     HttpServletRequest httpRequest) {
        log.info("设置置顶状态请求: id={}, isTop={}, topWeight={}", id, isTop, topWeight);

        Long operatorId = getUserIdFromRequest(httpRequest);
        jobService.setTopStatus(id, isTop, topWeight, operatorId);
        return Result.success("置顶状态设置成功");
    }

    @PutMapping("/{id}/recommended")
    @Operation(summary = "设置推荐状态", description = "推荐或取消推荐兼职")
    public Result<String> setRecommendedStatus(@PathVariable Long id,
                                             @RequestParam Boolean isRecommended,
                                             HttpServletRequest httpRequest) {
        log.info("设置推荐状态请求: id={}, isRecommended={}", id, isRecommended);

        Long operatorId = getUserIdFromRequest(httpRequest);
        jobService.setRecommendedStatus(id, isRecommended, operatorId);
        return Result.success("推荐状态设置成功");
    }

    @PostMapping("/{id}/favorite/increase")
    @Operation(summary = "增加收藏次数", description = "增加兼职的收藏次数")
    public Result<String> increaseFavoriteCount(@PathVariable Long id) {
        log.debug("增加收藏次数请求: id={}", id);

        jobService.increaseFavoriteCount(id);
        return Result.success("收藏次数增加成功");
    }

    @PostMapping("/{id}/favorite/decrease")
    @Operation(summary = "减少收藏次数", description = "减少兼职的收藏次数")
    public Result<String> decreaseFavoriteCount(@PathVariable Long id) {
        log.debug("减少收藏次数请求: id={}", id);

        jobService.decreaseFavoriteCount(id);
        return Result.success("收藏次数减少成功");
    }

    @GetMapping("/{id}/can-apply")
    @Operation(summary = "检查是否可以申请", description = "检查当前用户是否可以申请该兼职")
    public Result<Boolean> canApplyJob(@PathVariable Long id,
                                       HttpServletRequest httpRequest) {
        log.debug("检查是否可以申请请求: id={}", id);

        Long applicantId = getUserIdFromRequest(httpRequest);
        boolean canApply = jobService.canApplyJob(id, applicantId);
        return Result.success("检查完成", canApply);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取兼职统计数据", description = "获取兼职相关的统计数据")
    public Result<JobStatisticsDTO> getJobStatistics(
            @RequestParam(required = false) Long publisherId,
            HttpServletRequest httpRequest) {
        log.debug("获取兼职统计数据请求: publisherId={}", publisherId);

        // 如果未提供publisherId，使用当前用户的ID
        if (publisherId == null) {
            publisherId = getUserIdFromRequest(httpRequest);
        }

        JobStatisticsDTO statistics = jobService.getJobStatistics(publisherId);
        return Result.success("获取成功", statistics);
    }

    @GetMapping("/{id}/available")
    @Operation(summary = "检查兼职是否可用", description = "检查兼职是否存在且可申请")
    public Result<Boolean> checkJobAvailable(@PathVariable Long id) {
        log.debug("检查兼职是否可用请求: id={}", id);

        boolean available = jobService.checkJobAvailable(id);
        return Result.success("检查完成", available);
    }

    // ========== 私有方法 ==========

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的用户ID格式");
        }
    }

    /**
     * 从请求中获取用户类型
     */
    private String getUserTypeFromRequest(HttpServletRequest request) {
        String roles = request.getHeader("X-User-Roles");
        if (roles == null || roles.isEmpty()) {
            return "USER";
        }
        // 如果有管理员角色则返回ADMIN，否则返回USER
        return roles.contains("ADMIN") ? "ADMIN" : "USER";
    }

    /**
     * 从请求中获取用户名
     */
    private String getUsernameFromRequest(HttpServletRequest request) {
        String username = request.getHeader("X-Username");
        return username != null ? username : "未知用户";
    }
}