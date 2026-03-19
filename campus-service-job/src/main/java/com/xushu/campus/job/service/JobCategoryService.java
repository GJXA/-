package com.xushu.campus.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.job.dto.CreateJobCategoryRequest;
import com.xushu.campus.job.dto.JobCategoryDTO;
import com.xushu.campus.job.dto.JobCategorySearchRequest;
import com.xushu.campus.job.dto.UpdateJobCategoryRequest;
import com.xushu.campus.job.entity.JobCategory;

import java.util.List;

/**
 * 兼职分类服务接口
 */
public interface JobCategoryService {

    /**
     * 创建兼职分类
     */
    JobCategoryDTO createJobCategory(CreateJobCategoryRequest request) throws BusinessException;

    /**
     * 更新兼职分类
     */
    JobCategoryDTO updateJobCategory(Long id, UpdateJobCategoryRequest request) throws BusinessException;

    /**
     * 删除兼职分类（逻辑删除）
     */
    void deleteJobCategory(Long id) throws BusinessException;

    /**
     * 根据ID获取兼职分类详情
     */
    JobCategoryDTO getJobCategoryById(Long id) throws BusinessException;

    /**
     * 根据编码获取兼职分类
     */
    JobCategoryDTO getJobCategoryByCode(String code) throws BusinessException;

    /**
     * 获取所有兼职分类（分页）
     */
    IPage<JobCategoryDTO> getAllJobCategories(Integer page, Integer size);

    /**
     * 获取所有启用的兼职分类
     */
    List<JobCategoryDTO> getEnabledJobCategories();

    /**
     * 搜索兼职分类
     */
    IPage<JobCategoryDTO> searchJobCategories(JobCategorySearchRequest request);

    /**
     * 启用/禁用兼职分类
     */
    void toggleJobCategoryStatus(Long id, boolean enabled) throws BusinessException;

    /**
     * 检查分类编码是否已存在
     */
    boolean checkCodeExists(String code, Long excludeId);

    /**
     * 批量获取分类详情
     */
    List<JobCategoryDTO> getJobCategoriesByIds(List<Long> ids);
}