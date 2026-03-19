package com.xushu.campus.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.job.dto.*;
import com.xushu.campus.job.entity.JobCategory;
import com.xushu.campus.job.mapper.JobCategoryMapper;
import com.xushu.campus.job.service.JobCategoryService;
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
 * 兼职分类服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements JobCategoryService {

    private final JobCategoryMapper jobCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobCategoryDTO createJobCategory(CreateJobCategoryRequest request) throws BusinessException {
        log.info("创建兼职分类: code={}, name={}", request.getCode(), request.getName());

        // 1. 验证请求数据
        validateCreateRequest(request);

        // 2. 检查分类编码是否已存在
        if (checkCodeExists(request.getCode(), null)) {
            throw BusinessException.paramError( "分类编码已存在: " + request.getCode());
        }

        // 3. 构建分类实体
        JobCategory jobCategory = new JobCategory();
        BeanUtils.copyProperties(request, jobCategory);

        // 4. 保存到数据库
        jobCategoryMapper.insert(jobCategory);
        log.info("兼职分类创建成功: id={}, code={}", jobCategory.getId(), jobCategory.getCode());

        // 5. 返回DTO
        return convertToDTO(jobCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobCategoryDTO updateJobCategory(Long id, UpdateJobCategoryRequest request) throws BusinessException {
        log.info("更新兼职分类: id={}", id);

        // 1. 验证请求数据
        if (!request.hasUpdates()) {
            throw BusinessException.paramError( "没有提供要更新的字段");
        }
        validateUpdateRequest(request);

        // 2. 查询分类是否存在
        JobCategory jobCategory = jobCategoryMapper.selectById(id);
        if (jobCategory == null || jobCategory.getDeleted() == 1) {
            throw BusinessException.notFound( "兼职分类不存在: id=" + id);
        }

        // 3. 如果更新了编码，检查新编码是否已存在（排除当前分类）
        if (StringUtils.hasText(request.getCode()) && !request.getCode().equals(jobCategory.getCode())) {
            if (checkCodeExists(request.getCode(), id)) {
                throw BusinessException.paramError( "分类编码已存在: " + request.getCode());
            }
        }

        // 4. 更新字段
        boolean updated = false;
        if (StringUtils.hasText(request.getCode())) {
            jobCategory.setCode(request.getCode());
            updated = true;
        }
        if (StringUtils.hasText(request.getName())) {
            jobCategory.setName(request.getName());
            updated = true;
        }
        if (StringUtils.hasText(request.getDescription())) {
            jobCategory.setDescription(request.getDescription());
            updated = true;
        }
        if (StringUtils.hasText(request.getIcon())) {
            jobCategory.setIcon(request.getIcon());
            updated = true;
        }
        if (request.getSortOrder() != null) {
            jobCategory.setSortOrder(request.getSortOrder());
            updated = true;
        }
        if (request.getEnabled() != null) {
            jobCategory.setEnabled(request.getEnabled());
            updated = true;
        }

        if (updated) {
            jobCategoryMapper.updateById(jobCategory);
            log.info("兼职分类更新成功: id={}", id);
        }

        return convertToDTO(jobCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobCategory(Long id) throws BusinessException {
        log.info("删除兼职分类: id={}", id);

        // 1. 查询分类是否存在
        JobCategory jobCategory = jobCategoryMapper.selectById(id);
        if (jobCategory == null || jobCategory.getDeleted() == 1) {
            throw BusinessException.notFound( "兼职分类不存在: id=" + id);
        }

        // 2. 逻辑删除
        jobCategory.setDeleted(1);
        jobCategoryMapper.updateById(jobCategory);
        log.info("兼职分类删除成功: id={}", id);
    }

    @Override
    public JobCategoryDTO getJobCategoryById(Long id) throws BusinessException {
        log.debug("查询兼职分类详情: id={}", id);

        JobCategory jobCategory = jobCategoryMapper.selectById(id);
        if (jobCategory == null || jobCategory.getDeleted() == 1) {
            throw BusinessException.notFound( "兼职分类不存在: id=" + id);
        }

        return convertToDTO(jobCategory);
    }

    @Override
    public JobCategoryDTO getJobCategoryByCode(String code) throws BusinessException {
        log.debug("查询兼职分类详情: code={}", code);

        JobCategory jobCategory = jobCategoryMapper.selectByCode(code);
        if (jobCategory == null || jobCategory.getDeleted() == 1) {
            throw BusinessException.notFound( "兼职分类不存在: code=" + code);
        }

        return convertToDTO(jobCategory);
    }

    @Override
    public IPage<JobCategoryDTO> getAllJobCategories(Integer page, Integer size) {
        log.debug("查询所有兼职分类: page={}, size={}", page, size);

        Page<JobCategory> pageParam = new Page<JobCategory>(page, size);
        LambdaQueryWrapper<JobCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobCategory::getDeleted, 0)
                .orderByAsc(JobCategory::getSortOrder)
                .orderByDesc(JobCategory::getCreateTime);

        IPage<JobCategory> result = jobCategoryMapper.selectPage(pageParam, wrapper);

        List<JobCategoryDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobCategoryDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtoList);
        return resultPage;
    }

    @Override
    public List<JobCategoryDTO> getEnabledJobCategories() {
        log.debug("查询所有启用的兼职分类");

        List<JobCategory> categories = jobCategoryMapper.selectEnabledCategories();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<JobCategoryDTO> searchJobCategories(JobCategorySearchRequest request) {
        log.debug("搜索兼职分类: keyword={}, enabled={}", request.getKeyword(), request.getEnabled());

        // 验证排序参数
        if (!request.isValidSortField()) {
            request.setSortField("sort_order");
        }
        if (!request.isValidSortDirection()) {
            request.setSortDirection("asc");
        }

        Page<JobCategory> pageParam = new Page<JobCategory>(request.getPage(), request.getSize());
        LambdaQueryWrapper<JobCategory> wrapper = buildSearchWrapper(request);

        // 设置排序
        if ("code".equals(request.getSortField())) {
            wrapper.orderBy(true, "asc".equals(request.getSortDirection()), JobCategory::getCode);
        } else if ("name".equals(request.getSortField())) {
            wrapper.orderBy(true, "asc".equals(request.getSortDirection()), JobCategory::getName);
        } else if ("sort_order".equals(request.getSortField())) {
            wrapper.orderBy(true, "asc".equals(request.getSortDirection()), JobCategory::getSortOrder);
        } else if ("create_time".equals(request.getSortField())) {
            wrapper.orderBy(true, "asc".equals(request.getSortDirection()), JobCategory::getCreateTime);
        }

        IPage<JobCategory> result = jobCategoryMapper.selectPage(pageParam, wrapper);

        List<JobCategoryDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<JobCategoryDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtoList);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleJobCategoryStatus(Long id, boolean enabled) throws BusinessException {
        log.info("切换兼职分类状态: id={}, enabled={}", id, enabled);

        JobCategory jobCategory = jobCategoryMapper.selectById(id);
        if (jobCategory == null || jobCategory.getDeleted() == 1) {
            throw BusinessException.notFound( "兼职分类不存在: id=" + id);
        }

        jobCategory.setEnabled(enabled ? 1 : 0);
        jobCategoryMapper.updateById(jobCategory);
        log.info("兼职分类状态切换成功: id={}, enabled={}", id, enabled);
    }

    @Override
    public boolean checkCodeExists(String code, Long excludeId) {
        LambdaQueryWrapper<JobCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobCategory::getCode, code)
                .eq(JobCategory::getDeleted, 0);

        if (excludeId != null) {
            wrapper.ne(JobCategory::getId, excludeId);
        }

        return jobCategoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<JobCategoryDTO> getJobCategoriesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<JobCategory> categories = jobCategoryMapper.selectByIds(ids);
        return categories.stream()
                .filter(category -> category.getDeleted() == 0)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 验证创建请求
     */
    private void validateCreateRequest(CreateJobCategoryRequest request) throws BusinessException {
        if (!request.isValidCode()) {
            throw BusinessException.paramError( "分类编码格式不正确");
        }
        if (!request.isValidEnabled()) {
            throw BusinessException.paramError( "启用状态值不正确");
        }
    }

    /**
     * 验证更新请求
     */
    private void validateUpdateRequest(UpdateJobCategoryRequest request) throws BusinessException {
        if (!request.isValidCode()) {
            throw BusinessException.paramError( "分类编码格式不正确");
        }
        if (!request.isValidEnabled()) {
            throw BusinessException.paramError( "启用状态值不正确");
        }
    }

    /**
     * 构建搜索条件包装器
     */
    private LambdaQueryWrapper<JobCategory> buildSearchWrapper(JobCategorySearchRequest request) {
        LambdaQueryWrapper<JobCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobCategory::getDeleted, 0);

        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = "%" + request.getKeyword() + "%";
            wrapper.and(w -> w.like(JobCategory::getCode, keyword)
                    .or().like(JobCategory::getName, keyword)
                    .or().like(JobCategory::getDescription, keyword));
        }

        if (request.getEnabled() != null) {
            wrapper.eq(JobCategory::getEnabled, request.getEnabled());
        }

        return wrapper;
    }

    /**
     * 将实体转换为DTO
     */
    private JobCategoryDTO convertToDTO(JobCategory jobCategory) {
        JobCategoryDTO dto = new JobCategoryDTO();
        BeanUtils.copyProperties(jobCategory, dto);

        // 设置描述字段
        dto.setEnabledDesc(jobCategory.getEnabled() == 1 ? "启用" : "禁用");

        return dto;
    }
}