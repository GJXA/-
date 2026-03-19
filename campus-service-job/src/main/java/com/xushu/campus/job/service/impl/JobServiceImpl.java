package com.xushu.campus.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.job.constant.JobConstants;
import com.xushu.campus.job.dto.*;
import com.xushu.campus.job.entity.Job;
import com.xushu.campus.job.mapper.JobMapper;
import com.xushu.campus.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 兼职服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobDTO createJob(CreateJobRequest request, Long publisherId, String publisherType, String publisherName) throws BusinessException {
        log.info("创建兼职: title={}, publisherId={}", request.getTitle(), publisherId);

        // 1. 验证请求数据
        validateCreateJobRequest(request);

        // 2. 构建兼职实体
        Job job = new Job();
        BeanUtils.copyProperties(request, job);

        // 设置发布者信息
        job.setPublisherId(publisherId);
        job.setPublisherType(publisherType);
        job.setPublisherName(publisherName);

        // 设置默认值
        job.setStatus(JobConstants.JobStatus.PENDING); // 待审核
        job.setViewCount(0);
        job.setFavoriteCount(0);
        job.setAppliedCount(0);
        job.setAcceptedCount(0);
        job.setIsTop(request.getIsTop() != null ? request.getIsTop() : false);
        job.setIsRecommended(request.getIsRecommended() != null ? request.getIsRecommended() : false);
        job.setTopWeight(0);
        job.setPublishTime(LocalDateTime.now());

        // 设置截止时间（如果未提供，默认为开始日期前7天）
        if (request.getDeadlineDate() != null) {
            job.setDeadline(request.getDeadlineDate().atStartOfDay());
        } else {
            job.setDeadline(request.getStartDate().minusDays(7).atStartOfDay());
        }

        // 3. 保存到数据库
        int result = jobMapper.insert(job);
        if (result <= 0) {
            log.error("创建兼职失败: title={}", request.getTitle());
            throw new BusinessException("创建兼职失败");
        }

        log.info("兼职创建成功: id={}, title={}", job.getId(), job.getTitle());
        return convertToDTO(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobDTO updateJob(Long jobId, UpdateJobRequest request, Long operatorId) throws BusinessException {
        log.info("更新兼职: jobId={}, operatorId={}", jobId, operatorId);

        // 1. 检查兼职是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 检查操作权限（发布者或管理员可以更新）
        checkUpdatePermission(job, operatorId);

        // 3. 验证更新数据
        validateUpdateJobRequest(request);

        // 4. 更新字段
        boolean hasChanges = false;
        if (request.getTitle() != null && !request.getTitle().equals(job.getTitle())) {
            job.setTitle(request.getTitle());
            hasChanges = true;
        }
        if (request.getDescription() != null && !request.getDescription().equals(job.getDescription())) {
            job.setDescription(request.getDescription());
            hasChanges = true;
        }
        if (request.getSalary() != null && !request.getSalary().equals(job.getSalary())) {
            job.setSalary(request.getSalary());
            hasChanges = true;
        }
        if (request.getSalaryUnit() != null && !request.getSalaryUnit().equals(job.getSalaryUnit())) {
            job.setSalaryUnit(request.getSalaryUnit());
            hasChanges = true;
        }
        if (request.getJobType() != null && !request.getJobType().equals(job.getJobType())) {
            job.setJobType(request.getJobType());
            hasChanges = true;
        }
        if (request.getCategory() != null && !request.getCategory().equals(job.getCategory())) {
            job.setCategory(request.getCategory());
            hasChanges = true;
        }
        if (request.getLocation() != null && !request.getLocation().equals(job.getLocation())) {
            job.setLocation(request.getLocation());
            hasChanges = true;
        }
        if (request.getAddress() != null && !request.getAddress().equals(job.getAddress())) {
            job.setAddress(request.getAddress());
            hasChanges = true;
        }
        if (request.getStartDate() != null && !request.getStartDate().equals(job.getStartDate())) {
            job.setStartDate(request.getStartDate());
            hasChanges = true;
        }
        if (request.getEndDate() != null && !request.getEndDate().equals(job.getEndDate())) {
            job.setEndDate(request.getEndDate());
            hasChanges = true;
        }
        if (request.getWorkTime() != null && !request.getWorkTime().equals(job.getWorkTime())) {
            job.setWorkTime(request.getWorkTime());
            hasChanges = true;
        }
        if (request.getRecruitCount() != null && !request.getRecruitCount().equals(job.getRecruitCount())) {
            // 检查新招聘人数是否小于已录取人数
            if (request.getRecruitCount() < job.getAcceptedCount()) {
                throw new BusinessException("招聘人数不能小于已录取人数");
            }
            job.setRecruitCount(request.getRecruitCount());
            hasChanges = true;
        }
        if (request.getContactName() != null && !request.getContactName().equals(job.getContactName())) {
            job.setContactName(request.getContactName());
            hasChanges = true;
        }
        if (request.getContactPhone() != null && !request.getContactPhone().equals(job.getContactPhone())) {
            job.setContactPhone(request.getContactPhone());
            hasChanges = true;
        }
        if (request.getContactEmail() != null && !request.getContactEmail().equals(job.getContactEmail())) {
            job.setContactEmail(request.getContactEmail());
            hasChanges = true;
        }
        if (request.getCompanyName() != null && !request.getCompanyName().equals(job.getCompanyName())) {
            job.setCompanyName(request.getCompanyName());
            hasChanges = true;
        }
        if (request.getCompanyDescription() != null && !request.getCompanyDescription().equals(job.getCompanyDescription())) {
            job.setCompanyDescription(request.getCompanyDescription());
            hasChanges = true;
        }
        if (request.getCompanyLogo() != null && !request.getCompanyLogo().equals(job.getCompanyLogo())) {
            job.setCompanyLogo(request.getCompanyLogo());
            hasChanges = true;
        }
        if (request.getRequirements() != null && !request.getRequirements().equals(job.getRequirements())) {
            job.setRequirements(request.getRequirements());
            hasChanges = true;
        }
        if (request.getBenefits() != null && !request.getBenefits().equals(job.getBenefits())) {
            job.setBenefits(request.getBenefits());
            hasChanges = true;
        }
        if (request.getStatus() != null && !request.getStatus().equals(job.getStatus())) {
            // 状态变更需要特殊处理
            updateJobStatus(jobId, request.getStatus(), operatorId, request.getAuditRemark());
            hasChanges = true;
        }
        if (request.getIsTop() != null && !request.getIsTop().equals(job.getIsTop())) {
            job.setIsTop(request.getIsTop());
            hasChanges = true;
        }
        if (request.getTopWeight() != null && !request.getTopWeight().equals(job.getTopWeight())) {
            job.setTopWeight(request.getTopWeight());
            hasChanges = true;
        }
        if (request.getIsRecommended() != null && !request.getIsRecommended().equals(job.getIsRecommended())) {
            job.setIsRecommended(request.getIsRecommended());
            hasChanges = true;
        }

        // 5. 如果没有实际更新，直接返回
        if (!hasChanges) {
            log.info("兼职没有变化，无需更新: jobId={}", jobId);
            return convertToDTO(job);
        }

        // 6. 更新数据库
        int result = jobMapper.updateById(job);
        if (result <= 0) {
            log.error("更新兼职失败: jobId={}", jobId);
            throw new BusinessException("更新兼职失败");
        }

        log.info("兼职更新成功: jobId={}", jobId);
        return convertToDTO(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long jobId, Long operatorId) throws BusinessException {
        log.info("删除兼职: jobId={}, operatorId={}", jobId, operatorId);

        // 1. 检查兼职是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 检查操作权限（发布者或管理员可以删除）
        checkDeletePermission(job, operatorId);

        // 3. 逻辑删除
        job.setDeleted(1);
        int result = jobMapper.updateById(job);
        if (result <= 0) {
            log.error("删除兼职失败: jobId={}", jobId);
            throw new BusinessException("删除兼职失败");
        }

        log.info("兼职删除成功: jobId={}", jobId);
    }

    @Override
    public JobDTO getJobById(Long jobId) throws BusinessException {
        log.debug("获取兼职详情: jobId={}", jobId);

        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        return convertToDTO(job);
    }

    @Override
    public JobDTO getJobByIdWithView(Long jobId) throws BusinessException {
        log.debug("获取兼职详情（增加浏览次数）: jobId={}", jobId);

        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 增加浏览次数
        jobMapper.increaseViewCount(jobId);

        return convertToDTO(job);
    }

    @Override
    public IPage<JobDTO> searchJobs(JobSearchRequest request) {
        log.debug("搜索兼职: keyword={}, jobType={}, category={}",
                request.getKeyword(), request.getJobType(), request.getCategory());

        // 验证搜索参数
        if (!request.isValid()) {
            log.warn("搜索参数无效: {}", request);
            Page<JobDTO> resultPage = new Page<>(request.getPage(), request.getSize(), 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 如果没有搜索条件，返回空结果
        if (!request.hasSearchCriteria()) {
            log.debug("没有搜索条件，返回空结果");
            Page<JobDTO> resultPage = new Page<>(request.getPage(), request.getSize(), 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 构建分页
        Page<Job> page = new Page<Job>(request.getPage(), request.getSize());

        // 使用自定义搜索方法
        List<Job> jobs = jobMapper.searchJobs(request);

        // 计算总数（这里简化处理，实际应该由mapper返回总数）
        LambdaQueryWrapper<Job> queryWrapper = buildSearchQueryWrapper(request);
        long total = jobMapper.selectCount(queryWrapper);

        // 转换为DTO
        List<JobDTO> jobDTOs = jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobDTO> resultPage = new Page<>(request.getPage(), request.getSize(), total);
        resultPage.setRecords(jobDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobDTO> getJobList(Integer page, Integer size, String sortField, String sortDirection) {
        log.debug("获取兼职列表: page={}, size={}, sortField={}, sortDirection={}",
                page, size, sortField, sortDirection);

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;
        if (!StringUtils.hasText(sortField)) sortField = "publish_time";
        if (!StringUtils.hasText(sortDirection)) sortDirection = "desc";

        // 构建查询条件：只查询招聘中且未删除的兼职
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getDeleted, 0)
                   .eq(Job::getStatus, JobConstants.JobStatus.RECRUITING);

        // 设置排序
        if ("publish_time".equals(sortField)) {
            queryWrapper.orderByAsc("desc".equals(sortDirection), Job::getPublishTime);
        } else if ("salary".equals(sortField)) {
            queryWrapper.orderByAsc("desc".equals(sortDirection), Job::getSalary);
        } else if ("deadline".equals(sortField)) {
            queryWrapper.orderByAsc("desc".equals(sortDirection), Job::getDeadline);
        } else {
            queryWrapper.orderByDesc(Job::getPublishTime);
        }

        // 分页查询
        Page<Job> pageParam = new Page<Job>(page, size);
        IPage<Job> jobPage = jobMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobDTO> jobDTOs = jobPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(jobPage, resultPage, "records");
        resultPage.setRecords(jobDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobDTO> getJobsByPublisherId(Long publisherId, Integer page, Integer size) {
        log.debug("获取发布者的兼职列表: publisherId={}", publisherId);

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getDeleted, 0)
                   .eq(Job::getPublisherId, publisherId)
                   .orderByDesc(Job::getPublishTime);

        // 分页查询
        Page<Job> pageParam = new Page<Job>(page, size);
        IPage<Job> jobPage = jobMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobDTO> jobDTOs = jobPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(jobPage, resultPage, "records");
        resultPage.setRecords(jobDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobDTO> getJobsByJobType(String jobType, Integer page, Integer size) {
        log.debug("根据工作类型获取兼职列表: jobType={}", jobType);

        // 验证工作类型
        if (!JobConstants.JobType.isValid(jobType)) {
            log.warn("无效的工作类型: {}", jobType);
            Page<JobDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件：只查询招聘中且未删除的兼职
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getDeleted, 0)
                   .eq(Job::getStatus, JobConstants.JobStatus.RECRUITING)
                   .eq(Job::getJobType, jobType)
                   .orderByDesc(Job::getPublishTime);

        // 分页查询
        Page<Job> pageParam = new Page<Job>(page, size);
        IPage<Job> jobPage = jobMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobDTO> jobDTOs = jobPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(jobPage, resultPage, "records");
        resultPage.setRecords(jobDTOs);
        return resultPage;
    }

    @Override
    public IPage<JobDTO> getJobsByCategory(String category, Integer page, Integer size) {
        log.debug("根据工作类别获取兼职列表: category={}", category);

        // 验证工作类别
        if (!JobConstants.JobCategory.isValid(category)) {
            log.warn("无效的工作类别: {}", category);
            Page<JobDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 20;

        // 构建查询条件：只查询招聘中且未删除的兼职
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getDeleted, 0)
                   .eq(Job::getStatus, JobConstants.JobStatus.RECRUITING)
                   .eq(Job::getCategory, category)
                   .orderByDesc(Job::getPublishTime);

        // 分页查询
        Page<Job> pageParam = new Page<Job>(page, size);
        IPage<Job> jobPage = jobMapper.selectPage(pageParam, queryWrapper);

        // 转换为DTO
        List<JobDTO> jobDTOs = jobPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(jobPage, resultPage, "records");
        resultPage.setRecords(jobDTOs);
        return resultPage;
    }

    @Override
    public List<JobDTO> getTopJobs() {
        log.debug("获取置顶兼职列表");

        List<Job> jobs = jobMapper.selectTopJobs();
        return jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> getRecommendedJobs() {
        log.debug("获取推荐兼职列表");

        List<Job> jobs = jobMapper.selectRecommendedJobs();
        return jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> getAboutToExpireJobs() {
        log.debug("获取即将过期兼职列表");

        List<Job> jobs = jobMapper.selectAboutToExpireJobs();
        return jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> getJobsWithRemaining() {
        log.debug("获取有剩余名额的兼职列表");

        List<Job> jobs = jobMapper.selectJobsWithRemaining();
        return jobs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobStatus(Long jobId, Integer status, Long auditorId, String auditRemark) throws BusinessException {
        log.info("更新兼职状态: jobId={}, status={}, auditorId={}", jobId, status, auditorId);

        // 1. 检查兼职是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 验证状态是否有效
        if (!JobConstants.JobStatus.isValid(status)) {
            throw new BusinessException("无效的工作状态");
        }

        // 3. 检查状态转换是否合法
        if (!isValidStatusTransition(job.getStatus(), status)) {
            throw new BusinessException("状态转换不合法");
        }

        // 4. 更新状态
        job.setStatus(status);
        job.setAuditorId(auditorId);
        job.setAuditTime(LocalDateTime.now());
        job.setAuditRemark(auditRemark);

        int result = jobMapper.updateById(job);
        if (result <= 0) {
            log.error("更新兼职状态失败: jobId={}, status={}", jobId, status);
            throw new BusinessException("更新兼职状态失败");
        }

        log.info("兼职状态更新成功: jobId={}, 旧状态={}, 新状态={}", jobId, job.getStatus(), status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditJob(Long jobId, boolean approved, Long auditorId, String auditRemark) throws BusinessException {
        log.info("审核兼职: jobId={}, approved={}, auditorId={}", jobId, approved, auditorId);

        // 1. 检查兼职是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 检查当前状态是否为待审核
        if (job.getStatus() != JobConstants.JobStatus.PENDING) {
            throw new BusinessException("只有待审核的兼职可以进行审核");
        }

        // 3. 设置新状态
        Integer newStatus = approved ? JobConstants.JobStatus.RECRUITING : JobConstants.JobStatus.CLOSED;

        // 4. 更新状态
        job.setStatus(newStatus);
        job.setAuditorId(auditorId);
        job.setAuditTime(LocalDateTime.now());
        job.setAuditRemark(auditRemark);

        int result = jobMapper.updateById(job);
        if (result <= 0) {
            log.error("审核兼职失败: jobId={}, approved={}", jobId, approved);
            throw new BusinessException("审核兼职失败");
        }

        log.info("兼职审核成功: jobId={}, 审核结果={}", jobId, approved ? "通过" : "拒绝");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTopStatus(Long jobId, boolean isTop, Integer topWeight, Long operatorId) throws BusinessException {
        log.info("设置兼职置顶状态: jobId={}, isTop={}, topWeight={}, operatorId={}",
                jobId, isTop, topWeight, operatorId);

        // 1. 检查兼职是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 检查操作权限（管理员可以设置置顶）
        // 这里简化处理，实际应该检查用户角色
        // 假设只有管理员可以操作

        // 3. 更新置顶状态
        jobMapper.updateTopStatus(jobId, isTop, topWeight != null ? topWeight : 0);

        log.info("兼职置顶状态设置成功: jobId={}, isTop={}", jobId, isTop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setRecommendedStatus(Long jobId, boolean isRecommended, Long operatorId) throws BusinessException {
        log.info("设置兼职推荐状态: jobId={}, isRecommended={}, operatorId={}",
                jobId, isRecommended, operatorId);

        // 1. 检查兼职是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 检查操作权限（管理员可以设置推荐）
        // 这里简化处理，实际应该检查用户角色
        // 假设只有管理员可以操作

        // 3. 更新推荐状态
        jobMapper.updateRecommendedStatus(jobId, isRecommended);

        log.info("兼职推荐状态设置成功: jobId={}, isRecommended={}", jobId, isRecommended);
    }

    @Override
    public void increaseFavoriteCount(Long jobId) {
        log.debug("增加收藏次数: jobId={}", jobId);
        jobMapper.increaseFavoriteCount(jobId);
    }

    @Override
    public void decreaseFavoriteCount(Long jobId) {
        log.debug("减少收藏次数: jobId={}", jobId);
        jobMapper.decreaseFavoriteCount(jobId);
    }

    @Override
    public boolean canApplyJob(Long jobId, Long applicantId) throws BusinessException {
        log.debug("检查是否可以申请兼职: jobId={}, applicantId={}", jobId, applicantId);

        // 1. 检查兼职是否存在且可申请
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("兼职不存在或已被删除");
        }

        // 2. 检查工作状态是否为招聘中
        if (job.getStatus() != JobConstants.JobStatus.RECRUITING) {
            return false;
        }

        // 3. 检查是否还有剩余名额
        Integer remainingCount = job.getRecruitCount() - job.getAcceptedCount();
        if (remainingCount <= 0) {
            return false;
        }

        // 4. 检查是否已截止
        if (job.getDeadline() != null && LocalDateTime.now().isAfter(job.getDeadline())) {
            return false;
        }

        // 5. 检查是否已申请过（需要JobApplicationMapper，这里暂时省略）
        // TODO: 检查是否已申请过

        return true;
    }

    @Override
    public JobStatisticsDTO getJobStatistics(Long publisherId) {
        log.debug("获取兼职统计数据: publisherId={}", publisherId);

        JobStatisticsDTO statistics = new JobStatisticsDTO();

        // 这里简化实现，实际应该从数据库聚合查询
        // TODO: 实现完整的统计数据查询

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Long> jobIds, Integer status) {
        log.info("批量更新兼职状态: jobIds={}, status={}", jobIds.size(), status);

        if (jobIds == null || jobIds.isEmpty()) {
            return;
        }

        for (Long jobId : jobIds) {
            try {
                jobMapper.updateStatus(jobId, status);
            } catch (Exception e) {
                log.error("批量更新兼职状态失败: jobId={}, error={}", jobId, e.getMessage());
                // 继续处理其他兼职
            }
        }

        log.info("批量更新兼职状态完成: 成功处理 {} 个兼职", jobIds.size());
    }

    @Override
    public boolean checkJobAvailable(Long jobId) {
        if (jobId == null) {
            return false;
        }

        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            return false;
        }

        return job.getStatus() == JobConstants.JobStatus.RECRUITING;
    }

    @Override
    public List<Long> getExpiredJobIds() {
        log.debug("获取过期兼职ID列表");
        List<Job> expiredJobs = jobMapper.selectExpiredJobs();
        return expiredJobs.stream()
                .map(Job::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getFullJobIds() {
        log.debug("获取已招满兼职ID列表");
        List<Job> fullJobs = jobMapper.selectFullJobs();
        return fullJobs.stream()
                .map(Job::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanupDeletedJobs() {
        log.info("开始清理已删除的兼职数据");
        List<Job> jobsToDelete = jobMapper.selectJobsForPhysicalDeletion();

        if (jobsToDelete.isEmpty()) {
            log.info("没有需要物理删除的兼职数据");
            return;
        }

        int deletedCount = 0;
        for (Job job : jobsToDelete) {
            try {
                jobMapper.physicalDelete(job.getId());
                deletedCount++;
                log.debug("物理删除兼职: id={}, title={}", job.getId(), job.getTitle());
            } catch (Exception e) {
                log.error("物理删除兼职失败: id={}, error={}", job.getId(), e.getMessage());
            }
        }

        log.info("完成清理已删除的兼职数据，共删除 {} 条记录", deletedCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobStatistics() {
        log.info("开始更新兼职统计数据");
        // TODO: 实现统计数据更新逻辑
        // 可以更新job_statistics表，或者缓存统计数据到Redis
        log.info("更新兼职统计数据完成");
    }

    // ========== 私有方法 ==========

    /**
     * 验证创建兼职请求
     */
    private void validateCreateJobRequest(CreateJobRequest request) throws BusinessException {
        if (request == null) {
            throw new BusinessException("请求不能为空");
        }

        // 验证薪资单位
        if (!request.isValidSalaryUnit()) {
            throw new BusinessException("无效的薪资单位");
        }

        // 验证工作类型
        if (!request.isValidJobType()) {
            throw new BusinessException("无效的工作类型");
        }

        // 验证工作类别
        if (!request.isValidCategory()) {
            throw new BusinessException("无效的工作类别");
        }

        // 验证日期范围
        if (!request.isValidDateRange()) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        // 验证薪资
        if (request.getSalary() == null || request.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("薪资必须大于等于0");
        }

        // 验证招聘人数
        if (request.getRecruitCount() == null || request.getRecruitCount() < 1) {
            throw new BusinessException("招聘人数必须大于0");
        }
    }

    /**
     * 验证更新兼职请求
     */
    private void validateUpdateJobRequest(UpdateJobRequest request) throws BusinessException {
        if (request == null) {
            throw new BusinessException("请求不能为空");
        }

        // 验证是否有更新字段
        if (!request.hasUpdates()) {
            throw new BusinessException("没有需要更新的字段");
        }

        // 验证薪资单位（如果提供了的话）
        if (request.getSalaryUnit() != null && !request.isValidSalaryUnit()) {
            throw new BusinessException("无效的薪资单位");
        }

        // 验证工作类型（如果提供了的话）
        if (request.getJobType() != null && !request.isValidJobType()) {
            throw new BusinessException("无效的工作类型");
        }

        // 验证工作类别（如果提供了的话）
        if (request.getCategory() != null && !request.isValidCategory()) {
            throw new BusinessException("无效的工作类别");
        }

        // 验证日期范围（如果提供了的话）
        if (!request.isValidDateRange()) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        // 验证薪资（如果提供了的话）
        if (request.getSalary() != null && request.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("薪资必须大于等于0");
        }

        // 验证招聘人数（如果提供了的话）
        if (request.getRecruitCount() != null && request.getRecruitCount() < 1) {
            throw new BusinessException("招聘人数必须大于0");
        }
    }

    /**
     * 检查更新权限
     */
    private void checkUpdatePermission(Job job, Long operatorId) throws BusinessException {
        // 发布者可以更新自己的兼职
        if (job.getPublisherId().equals(operatorId)) {
            return;
        }

        // TODO: 检查是否为管理员
        // 这里简化处理，实际应该检查用户角色
        throw new BusinessException("没有权限更新该兼职");
    }

    /**
     * 检查删除权限
     */
    private void checkDeletePermission(Job job, Long operatorId) throws BusinessException {
        // 发布者可以删除自己的兼职
        if (job.getPublisherId().equals(operatorId)) {
            return;
        }

        // TODO: 检查是否为管理员
        // 这里简化处理，实际应该检查用户角色
        throw new BusinessException("没有权限删除该兼职");
    }

    /**
     * 构建搜索查询条件
     */
    private LambdaQueryWrapper<Job> buildSearchQueryWrapper(JobSearchRequest request) {
        LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Job::getDeleted, 0);

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Job::getTitle, request.getKeyword())
                    .or()
                    .like(Job::getDescription, request.getKeyword())
                    .or()
                    .like(Job::getLocation, request.getKeyword())
                    .or()
                    .like(Job::getCompanyName, request.getKeyword())
                    .or()
                    .like(Job::getRequirements, request.getKeyword())
                    .or()
                    .like(Job::getBenefits, request.getKeyword()));
        }

        if (request.getJobType() != null && !request.getJobType().isEmpty()) {
            queryWrapper.eq(Job::getJobType, request.getJobType());
        }

        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            queryWrapper.eq(Job::getCategory, request.getCategory());
        }

        if (request.getSalaryUnit() != null && !request.getSalaryUnit().isEmpty()) {
            queryWrapper.eq(Job::getSalaryUnit, request.getSalaryUnit());
        }

        if (request.getMinSalary() != null) {
            queryWrapper.ge(Job::getSalary, request.getMinSalary());
        }

        if (request.getMaxSalary() != null) {
            queryWrapper.le(Job::getSalary, request.getMaxSalary());
        }

        if (request.getLocation() != null && !request.getLocation().isEmpty()) {
            queryWrapper.like(Job::getLocation, request.getLocation());
        }

        if (request.getStatus() != null) {
            queryWrapper.eq(Job::getStatus, request.getStatus());
        }

        if (request.getPublisherId() != null) {
            queryWrapper.eq(Job::getPublisherId, request.getPublisherId());
        }

        if (request.getPublisherType() != null && !request.getPublisherType().isEmpty()) {
            queryWrapper.eq(Job::getPublisherType, request.getPublisherType());
        }

        if (request.getStartDateAfter() != null) {
            queryWrapper.ge(Job::getStartDate, request.getStartDateAfter());
        }

        if (request.getEndDateBefore() != null) {
            queryWrapper.le(Job::getEndDate, request.getEndDateBefore());
        }

        if (request.getPublishTimeAfter() != null) {
            queryWrapper.ge(Job::getPublishTime, request.getPublishTimeAfter());
        }

        if (request.getPublishTimeBefore() != null) {
            queryWrapper.le(Job::getPublishTime, request.getPublishTimeBefore());
        }

        if (request.getDeadlineAfter() != null) {
            queryWrapper.ge(Job::getDeadline, request.getDeadlineAfter());
        }

        if (request.getDeadlineBefore() != null) {
            queryWrapper.le(Job::getDeadline, request.getDeadlineBefore());
        }

        if (request.getIsTop() != null) {
            queryWrapper.eq(Job::getIsTop, request.getIsTop());
        }

        if (request.getIsRecommended() != null) {
            queryWrapper.eq(Job::getIsRecommended, request.getIsRecommended());
        }

        if (request.getHasRemaining() != null && request.getHasRemaining()) {
            queryWrapper.apply("recruit_count > accepted_count");
        }

        if (request.getAboutToExpire() != null && request.getAboutToExpire()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threeDaysLater = now.plusDays(3);
            queryWrapper.ge(Job::getDeadline, now)
                       .le(Job::getDeadline, threeDaysLater);
        }

        return queryWrapper;
    }

    /**
     * 检查状态转换是否合法
     */
    private boolean isValidStatusTransition(Integer oldStatus, Integer newStatus) {
        // 待审核 -> 招聘中、已关闭
        if (oldStatus == JobConstants.JobStatus.PENDING) {
            return newStatus == JobConstants.JobStatus.RECRUITING ||
                   newStatus == JobConstants.JobStatus.CLOSED;
        }

        // 招聘中 -> 已结束、已关闭
        if (oldStatus == JobConstants.JobStatus.RECRUITING) {
            return newStatus == JobConstants.JobStatus.ENDED ||
                   newStatus == JobConstants.JobStatus.CLOSED;
        }

        // 已结束 -> 已关闭
        if (oldStatus == JobConstants.JobStatus.ENDED) {
            return newStatus == JobConstants.JobStatus.CLOSED;
        }

        // 已关闭 -> 不能再改变
        if (oldStatus == JobConstants.JobStatus.CLOSED) {
            return false;
        }

        return false;
    }

    /**
     * 将Job实体转换为JobDTO
     */
    private JobDTO convertToDTO(Job job) {
        if (job == null) {
            return null;
        }

        JobDTO dto = new JobDTO();
        BeanUtils.copyProperties(job, dto);

        // 计算描述信息和剩余名额
        dto.calculateDesc();

        return dto;
    }
}