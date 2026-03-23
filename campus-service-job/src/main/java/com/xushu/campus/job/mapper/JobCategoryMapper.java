package com.xushu.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.job.entity.JobCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 兼职分类Mapper接口
 */
@Mapper
public interface JobCategoryMapper extends BaseMapper<JobCategory> {

    /**
     * 查询所有启用的分类（按排序字段排序）
     */
    @Select("SELECT * FROM job_categories WHERE is_enabled = 1 AND is_deleted = 0 ORDER BY sort_order ASC")
    List<JobCategory> selectEnabledCategories();

    /**
     * 根据分类编码查询
     */
    @Select("SELECT * FROM job_categories WHERE code = #{code} AND is_deleted = 0")
    JobCategory selectByCode(@Param("code") String code);

    /**
     * 查询所有分类（包括禁用，按排序字段排序）
     */
    @Select("SELECT * FROM job_categories WHERE is_deleted = 0 ORDER BY sort_order ASC, created_at DESC")
    List<JobCategory> selectAllCategories();

    /**
     * 根据ID列表查询分类
     */
    @Select("<script>" +
            "SELECT * FROM job_categories WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND is_deleted = 0" +
            "</script>")
    List<JobCategory> selectByIds(@Param("ids") List<Long> ids);
}