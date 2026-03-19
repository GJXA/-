package com.xushu.campus.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.job.constant.JobConstants;
import com.xushu.campus.job.dto.*;
import com.xushu.campus.job.entity.Job;
import com.xushu.campus.job.entity.JobApplication;
import com.xushu.campus.job.mapper.JobApplicationMapper;
import com.xushu.campus.job.mapper.JobMapper;
import com.xushu.campus.job.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 兼职申请服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final JobMapper jobMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplicationDTO applyJob(ApplyJobRequest request, Long applicantId) throws BusinessException {
        log.info("申请兼职: jobId={}, applicantId={}", request.getJobId(), applicantId);

        // 1. 检查兼职是否存在且可申请
        Job job = jobMapper.selectById(request.getJobId());
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 检查兼职状态是否为招聘中
        if (job.getStatus() != JobConstants.JobStatus.RECRUITING) {
            throw new BusinessException("该兼职当前不可申请");
        }

        // 3. 检查是否还有剩余名额
        Integer remainingCount = job.getRecruitCount() - job.getAcceptedCount();
        if (remainingCount <= 0) {
            throw new BusinessException("该兼职已招满");
        }

        // 4. 检查是否已截止
        if (job.getDeadline() != null && LocalDateTime.now().isAfter(job.getDeadline())) {
            throw new BusinessException("该兼职已截止申请");
        }

        // 5. 检查是否已申请过
        JobApplication existingApplication = jobApplicationMapper.selectByApplicantAndJob(applicantId, request.getJobId());
        if (existingApplication != null && existingApplication.getDeleted() == 0) {
            throw new BusinessException("您已申请过该兼职");
        }

        // 6. 构建申请记录
        JobApplication application = new JobApplication();
        application.setJobId(request.getJobId());
        application.setApplicantId(applicantId);
        application.setApplicantName(request.getApplicantName());
        application.setApplicantPhone(request.getApplicantPhone());
        application.setApplicantEmail(request.getApplicantEmail());
        application.setApplicantGrade(request.getApplicantGrade());
        application.setResume(request.getResume());
        application.setApplyRemark(request.getApplyRemark());
        application.setStatus(JobConstants.ApplicationStatus.PENDING);
        application.setApplyTime(LocalDateTime.now());
        application.setConfirmStatus(JobConstants.ConfirmStatus.UNCONFIRMED);

        // 设置冗余字段
        application.setJobTitle(job.getTitle());
        application.setJobSalary(job.getSalary() + "元/" + JobConstants.SalaryUnit.getDesc(job.getSalaryUnit()));
        application.setJobType(job.getJobType());
        application.setJobLocation(job.getLocation());
        application.setPublisherId(job.getPublisherId());
        application.setPublisherName(job.getPublisherName());

        // 7. 保存申请记录
        int result = jobApplicationMapper.insert(application);
        if (result <= 0) {
            log.error("申请兼职失败: jobId={}, applicantId={}", request.getJobId(), applicantId);
            throw new BusinessException("申请兼职失败");
        }

        // 8. 更新兼职的已申请人数
        jobMapper.increaseAppliedCount(request.getJobId());

        log.info("兼职申请成功: applicationId={}, jobId={}, applicantId={}",
                application.getId(), request.getJobId(), applicantId);
        return convertToDTO(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelApplication(Long applicationId, Long applicantId) throws BusinessException {
        log.info("取消申请: applicationId={}, applicantId={}", applicationId, applicantId);

        // 1. 检查申请记录是否存在
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        // 2. 检查申请人是否为本人
        if (!application.getApplicantId().equals(applicantId)) {
            throw new BusinessException("只能取消自己的申请");
        }

        // 3. 检查申请状态是否可以取消
        if (!JobConstants.ApplicationStatus.canCancel(application.getStatus())) {
            throw new BusinessException("当前状态不能取消申请");
        }

        // 4. 更新申请状态为已取消
        application.setStatus(JobConstants.ApplicationStatus.CANCELLED);
        application.setUpdateTime(LocalDateTime.now());

        int result = jobApplicationMapper.updateById(application);
        if (result <= 0) {
            log.error("取消申请失败: applicationId={}", applicationId);
            throw new BusinessException("取消申请失败");
        }

        // 5. 更新兼职的已申请人数
        jobMapper.decreaseAppliedCount(application.getJobId());

        log.info("申请取消成功: applicationId={}", applicationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplicationDTO processApplication(Long applicationId, boolean approved, Long processorId, String processRemark) throws BusinessException {
        log.info("处理申请: applicationId={}, approved={}, processorId={}", applicationId, approved, processorId);

        // 1. 检查申请记录是否存在
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        // 2. 检查申请状态是否为待处理
        if (application.getStatus() != JobConstants.ApplicationStatus.PENDING) {
            throw new BusinessException("只能处理待处理的申请");
        }

        // 3. 检查兼职是否存在
        Job job = jobMapper.selectById(application.getJobId());
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 4. 检查操作权限（发布者可以处理自己兼职的申请）
        if (!job.getPublisherId().equals(processorId)) {
            // TODO: 检查是否为管理员
            throw new BusinessException("没有权限处理该申请");
        }

        // 5. 设置新状态
        Integer newStatus = approved ? JobConstants.ApplicationStatus.APPROVED : JobConstants.ApplicationStatus.REJECTED;

        // 6. 更新申请状态
        application.setStatus(newStatus);
        application.setProcessTime(LocalDateTime.now());
        application.setProcessorId(processorId);
        application.setProcessorName("系统"); // TODO: 获取处理人姓名
        application.setProcessRemark(processRemark);
        application.setUpdateTime(LocalDateTime.now());

        int result = jobApplicationMapper.updateById(application);
        if (result <= 0) {
            log.error("处理申请失败: applicationId={}", applicationId);
            throw new BusinessException("处理申请失败");
        }

        // 7. 如果通过申请，更新兼职的已录取人数
        if (approved) {
            jobMapper.increaseAcceptedCount(application.getJobId());

            // 检查是否已招满
            Integer remainingCount = job.getRecruitCount() - (job.getAcceptedCount() + 1);
            if (remainingCount <= 0) {
                // 自动结束兼职
                job.setStatus(JobConstants.JobStatus.ENDED);
                jobMapper.updateById(job);
                log.info("兼职已招满，自动结束: jobId={}", application.getJobId());
            }
        }

        log.info("申请处理成功: applicationId={}, 处理结果={}", applicationId, approved ? "通过" : "拒绝");
        return convertToDTO(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplicationDTO scheduleInterview(Long applicationId, String interviewTime, String interviewLocation, String interviewRemark, Long processorId) throws BusinessException {
        log.info("安排面试: applicationId={}, processorId={}", applicationId, processorId);

        // 1. 检查申请记录是否存在
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        // 2. 检查申请状态是否为已通过
        if (application.getStatus() != JobConstants.ApplicationStatus.APPROVED) {
            throw new BusinessException("只能为已通过的申请安排面试");
        }

        // 3. 检查操作权限（发布者可以安排面试）
        Job job = jobMapper.selectById(application.getJobId());
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }
        if (!job.getPublisherId().equals(processorId)) {
            // TODO: 检查是否为管理员
            throw new BusinessException("没有权限安排面试");
        }

        // 4. 安排面试
        application.setInterviewTime(LocalDateTime.parse(interviewTime)); // TODO: 时间格式转换
        application.setInterviewLocation(interviewLocation);
        application.setInterviewRemark(interviewRemark);
        application.setUpdateTime(LocalDateTime.now());

        int result = jobApplicationMapper.updateById(application);
        if (result <= 0) {
            log.error("安排面试失败: applicationId={}", applicationId);
            throw new BusinessException("安排面试失败");
        }

        log.info("面试安排成功: applicationId={}, interviewTime={}", applicationId, interviewTime);
        return convertToDTO(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplicationDTO sendOffer(Long applicationId, String offerContent, Long processorId) throws BusinessException {
        log.info("发送录用通知: applicationId={}, processorId={}", applicationId, processorId);

        // 1. 检查申请记录是否存在
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        // 2. 检查是否已安排面试
        if (application.getInterviewTime() == null) {
            throw new BusinessException("请先安排面试");
        }

        // 3. 检查操作权限（发布者可以发送录用通知）
        Job job = jobMapper.selectById(application.getJobId());
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }
        if (!job.getPublisherId().equals(processorId)) {
            // TODO: 检查是否为管理员
            throw new BusinessException("没有权限发送录用通知");
        }

        // 4. 发送录用通知
        application.setOfferTime(LocalDateTime.now());
        application.setOfferContent(offerContent);
        application.setUpdateTime(LocalDateTime.now());

        int result = jobApplicationMapper.updateById(application);
        if (result <= 0) {
            log.error("发送录用通知失败: applicationId={}", applicationId);
            throw new BusinessException("发送录用通知失败");
        }

        log.info("录用通知发送成功: applicationId={}", applicationId);
        return convertToDTO(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplicationDTO confirmParticipation(Long applicationId, boolean confirmed, String confirmRemark, Long applicantId) throws BusinessException {
        log.info("确认参加: applicationId={}, confirmed={}, applicantId={}", applicationId, confirmed, applicantId);

        // 1. 检查申请记录是否存在
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        // 2. 检查申请人是否为本人
        if (!application.getApplicantId().equals(applicantId)) {
            throw new BusinessException("只能确认自己的申请");
        }

        // 3. 检查是否已发送录用通知
        if (application.getOfferTime() == null) {
            throw new BusinessException("尚未收到录用通知");
        }

        // 4. 检查确认状态是否为待确认
        if (application.getConfirmStatus() != JobConstants.ConfirmStatus.UNCONFIRMED) {
            throw new BusinessException("已确认过参加状态");
        }

        // 5. 更新确认状态
        Integer newConfirmStatus = confirmed ? JobConstants.ConfirmStatus.CONFIRMED : JobConstants.ConfirmStatus.REJECTED;
        application.setConfirmStatus(newConfirmStatus);
        application.setConfirmTime(LocalDateTime.now());
        application.setConfirmRemark(confirmRemark);
        application.setUpdateTime(LocalDateTime.now());

        int result = jobApplicationMapper.updateById(application);
        if (result <= 0) {
            log.error("确认参加失败: applicationId={}", applicationId);
            throw new BusinessException("确认参加失败");
        }

        log.info("参加确认成功: applicationId={}, confirmed={}", applicationId, confirmed);
        return convertToDTO(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplicationDTO recordWorkTime(Long applicationId, String actualStartTime, String actualEndTime, Long publisherId) throws BusinessException {
        log.info("记录实际工作时间: applicationId={}, publisherId={}", applicationId, publisherId);

        // 1. 检查申请记录是否存在
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        // 2. 检查操作权限（发布者可以记录工作时间）
        Job job = jobMapper.selectById(application.getJobId());
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }
        if (!job.getPublisherId().equals(publisherId)) {
            // TODO: 检查是否为管理员
            throw new BusinessException("没有权限记录工作时间");
        }

        // 3. 检查确认状态是否为已确认参加
        if (application.getConfirmStatus() != JobConstants.ConfirmStatus.CONFIRMED) {
            throw new BusinessException("申请人尚未确认参加");
        }

        // 4. 记录工作时间
        application.setActualStartTime(LocalDateTime.parse(actualStartTime)); // TODO: 时间格式转换
        application.setActualEndTime(LocalDateTime.parse(actualEndTime));
        application.setUpdateTime(LocalDateTime.now());

        int result = jobApplicationMapper.updateById(application);
        if (result <= 0) {
            log.error("记录工作时间失败: applicationId={}", applicationId);
            throw new BusinessException("记录工作时间失败");
        }

        log.info("工作时间记录成功: applicationId={}", applicationId);
        return convertToDTO(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobApplicationDTO addWorkEvaluation(Long applicationId, String workEvaluation, Integer evaluationScore, Long publisherId) throws BusinessException {
        log.info("添加工作评价: applicationId={}, publisherId={}", applicationId, publisherId);

        // 1. 检查申请记录是否存在
        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        // 2. 检查操作权限（发布者可以添加工作评价）
        Job job = jobMapper.selectById(application.getJobId());
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }
        if (!job.getPublisherId().equals(publisherId)) {
            // TODO: 检查是否为管理员
            throw new BusinessException("没有权限添加工作评价");
        }

        // 3. 检查是否已记录实际工作时间
        if (application.getActualStartTime() == null || application.getActualEndTime() == null) {
            throw new BusinessException("请先记录实际工作时间");
        }

        // 4. 验证评价分数
        if (evaluationScore < 1 || evaluationScore > 5) {
            throw new BusinessException("评价分数必须在1-5分之间");
        }

        // 5. 添加工作评价
        application.setWorkEvaluation(workEvaluation);
        application.setEvaluationScore(evaluationScore);
        application.setEvaluationTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        int result = jobApplicationMapper.updateById(application);
        if (result <= 0) {
            log.error("添加工作评价失败: applicationId={}", applicationId);
            throw new BusinessException("添加工作评价失败");
        }

        log.info("工作评价添加成功: applicationId={}, evaluationScore={}", applicationId, evaluationScore);
        return convertToDTO(application);
    }

    @Override
    public JobApplicationDTO getApplicationById(Long applicationId) throws BusinessException {
        log.debug("获取申请详情: applicationId={}", applicationId);

        JobApplication application = jobApplicationMapper.selectById(applicationId);
        if (application == null || application.getDeleted() == 1) {
            throw new BusinessException("申请记录不存在或已被删除");
        }

        return convertToDTO(application);
    }

    @Override
    public IPage<JobApplicationDTO> getApplicationsByApplicantId(Long applicantId, Integer page, Integer size) {
        log.debug("获取申请人的申请列表: applicantId={}", applicantId);

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .eq(JobApplication::getApplicantId, applicantId)
                   .orderByDesc(JobApplication::getApplyTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobApplicationDTO> getApplicationsByJobId(Long jobId, Integer page, Integer size) {
        log.debug("获取兼职的申请列表: jobId={}", jobId);

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .eq(JobApplication::getJobId, jobId)
                   .orderByDesc(JobApplication::getApplyTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobApplicationDTO> getApplicationsByPublisherId(Long publisherId, Integer page, Integer size) {
        log.debug("获取发布者的申请列表: publisherId={}", publisherId);

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .eq(JobApplication::getPublisherId, publisherId)
                   .orderByDesc(JobApplication::getApplyTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobApplicationDTO> getApplicationsByStatus(Integer status, Integer page, Integer size) {
        log.debug("获取指定状态的申请列表: status={}", status);

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 验证状态
        if (!JobConstants.ApplicationStatus.isValid(status)) {
            log.warn("无效的申请状态: {}", status);
            Page<JobApplicationDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .eq(JobApplication::getStatus, status)
                   .orderByDesc(JobApplication::getApplyTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobApplicationDTO> getPendingApplications(Integer page, Integer size) {
        log.debug("获取待处理的申请列表");

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .eq(JobApplication::getStatus, JobConstants.ApplicationStatus.PENDING)
                   .orderByAsc(JobApplication::getApplyTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobApplicationDTO> getApplicationsWithInterview(Integer page, Integer size) {
        log.debug("获取已安排面试的申请列表");

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .isNotNull(JobApplication::getInterviewTime)
                   .orderByAsc(JobApplication::getInterviewTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobApplicationDTO> getApplicationsWithOffer(Integer page, Integer size) {
        log.debug("获取已发送录用通知的申请列表");

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .isNotNull(JobApplication::getOfferTime)
                   .orderByDesc(JobApplication::getOfferTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobApplicationDTO> getConfirmedApplications(Integer page, Integer size) {
        log.debug("获取已确认参加的申请列表");

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<JobApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobApplication::getDeleted, 0)
                   .eq(JobApplication::getConfirmStatus, JobConstants.ConfirmStatus.CONFIRMED)
                   .orderByDesc(JobApplication::getConfirmTime);

        // 分页查询
        Page<JobApplication> pageParam = new Page<JobApplication>(page, size);
        IPage<JobApplication> applicationPage = jobApplicationMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobApplicationDTO> applicationDTOs = applicationPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobApplicationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(applicationPage, resultPage, "records");
        resultPage.setRecords(applicationDTOs);
        return resultPage;
    }

    @Override
    public boolean hasApplied(Long jobId, Long applicantId) {
        log.debug("检查是否已申请: jobId={}, applicantId={}", jobId, applicantId);

        JobApplication application = jobApplicationMapper.selectByApplicantAndJob(applicantId, jobId);
        return application != null && application.getDeleted() == 0;
    }

    @Override
    public ApplicationStatisticsDTO getApplicationStatistics(Long publisherId, Long jobId) {
        log.debug("获取申请统计数据: publisherId={}, jobId={}", publisherId, jobId);

        ApplicationStatisticsDTO statistics = new ApplicationStatisticsDTO();

        // 这里简化实现，实际应该从数据库聚合查询
        // TODO: 实现完整的申请统计数据查询

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationRedundantFields(Long jobId) {
        log.info("更新申请冗余字段: jobId={}", jobId);

        // 1. 获取兼职信息
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            log.warn("兼职不存在或已被删除: jobId={}", jobId);
            return;
        }

        // 2. 构建冗余字段值
        String jobSalary = job.getSalary() + "元/" + JobConstants.SalaryUnit.getDesc(job.getSalaryUnit());

        // 3. 更新所有相关申请的冗余字段
        jobApplicationMapper.updateRedundantFields(
                jobId,
                job.getTitle(),
                jobSalary,
                job.getJobType(),
                job.getLocation(),
                job.getPublisherId(),
                job.getPublisherName()
        );

        log.info("申请冗余字段更新完成: jobId={}", jobId);
    }

    // ========== 私有方法 ==========

    /**
     * 将JobApplication实体转换为JobApplicationDTO
     */
    private JobApplicationDTO convertToDTO(JobApplication application) {
        if (application == null) {
            return null;
        }

        JobApplicationDTO dto = new JobApplicationDTO();
        BeanUtils.copyProperties(application, dto);

        // 计算状态描述
        dto.calculateDesc();

        return dto;
    }
}