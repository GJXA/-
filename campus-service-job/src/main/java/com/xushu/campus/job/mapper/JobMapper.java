package com.xushu.campus.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.job.dto.JobSearchRequest;
import com.xushu.campus.job.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 兼职信息Mapper接口
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 根据状态查询兼职列表
     */
    @Select("SELECT * FROM jobs WHERE status = #{status} AND deleted = 0 ORDER BY publish_time DESC")
    List<Job> selectByStatus(@Param("status") Integer status);

    /**
     * 根据发布者ID查询兼职列表
     */
    @Select("SELECT * FROM jobs WHERE publisher_id = #{publisherId} AND deleted = 0 ORDER BY publish_time DESC")
    List<Job> selectByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 根据工作类型查询兼职列表
     */
    @Select("SELECT * FROM jobs WHERE job_type = #{jobType} AND deleted = 0 AND status = 1 ORDER BY publish_time DESC")
    List<Job> selectByJobType(@Param("jobType") String jobType);

    /**
     * 根据工作类别查询兼职列表
     */
    @Select("SELECT * FROM jobs WHERE category = #{category} AND deleted = 0 AND status = 1 ORDER BY publish_time DESC")
    List<Job> selectByCategory(@Param("category") String category);

    /**
     * 查询置顶的兼职列表
     */
    @Select("SELECT * FROM jobs WHERE is_top = true AND deleted = 0 AND status = 1 ORDER BY top_weight DESC, publish_time DESC")
    List<Job> selectTopJobs();

    /**
     * 查询推荐的兼职列表
     */
    @Select("SELECT * FROM jobs WHERE is_recommended = true AND deleted = 0 AND status = 1 ORDER BY publish_time DESC")
    List<Job> selectRecommendedJobs();

    /**
     * 查询即将过期的兼职（3天内过期）
     */
    @Select("SELECT * FROM jobs WHERE deadline <= DATE_ADD(NOW(), INTERVAL 3 DAY) AND deadline > NOW() AND deleted = 0 AND status = 1 ORDER BY deadline ASC")
    List<Job> selectAboutToExpireJobs();

    /**
     * 查询有剩余名额的兼职
     */
    @Select("SELECT * FROM jobs WHERE (recruit_count - accepted_count) > 0 AND deleted = 0 AND status = 1 ORDER BY publish_time DESC")
    List<Job> selectJobsWithRemaining();

    /**
     * 根据条件搜索兼职
     */
    List<Job> searchJobs(@Param("request") JobSearchRequest request);

    /**
     * 增加浏览次数
     */
    @Select("UPDATE jobs SET view_count = view_count + 1, update_time = NOW() WHERE id = #{jobId} AND deleted = 0")
    void increaseViewCount(@Param("jobId") Long jobId);

    /**
     * 增加收藏次数
     */
    @Select("UPDATE jobs SET favorite_count = favorite_count + 1, update_time = NOW() WHERE id = #{jobId} AND deleted = 0")
    void increaseFavoriteCount(@Param("jobId") Long jobId);

    /**
     * 减少收藏次数
     */
    @Select("UPDATE jobs SET favorite_count = favorite_count - 1, update_time = NOW() WHERE id = #{jobId} AND deleted = 0 AND favorite_count > 0")
    void decreaseFavoriteCount(@Param("jobId") Long jobId);

    /**
     * 增加已申请人数
     */
    @Select("UPDATE jobs SET applied_count = applied_count + 1, update_time = NOW() WHERE id = #{jobId} AND deleted = 0")
    void increaseAppliedCount(@Param("jobId") Long jobId);

    /**
     * 减少已申请人数
     */
    @Select("UPDATE jobs SET applied_count = applied_count - 1, update_time = NOW() WHERE id = #{jobId} AND deleted = 0 AND applied_count > 0")
    void decreaseAppliedCount(@Param("jobId") Long jobId);

    /**
     * 增加已录取人数
     */
    @Select("UPDATE jobs SET accepted_count = accepted_count + 1, update_time = NOW() WHERE id = #{jobId} AND deleted = 0")
    void increaseAcceptedCount(@Param("jobId") Long jobId);

    /**
     * 减少已录取人数
     */
    @Select("UPDATE jobs SET accepted_count = accepted_count - 1, update_time = NOW() WHERE id = #{jobId} AND deleted = 0 AND accepted_count > 0")
    void decreaseAcceptedCount(@Param("jobId") Long jobId);

    /**
     * 更新工作状态
     */
    @Select("UPDATE jobs SET status = #{status}, update_time = NOW() WHERE id = #{jobId} AND deleted = 0")
    void updateStatus(@Param("jobId") Long jobId, @Param("status") Integer status);

    /**
     * 更新置顶状态
     */
    @Select("UPDATE jobs SET is_top = #{isTop}, top_weight = #{topWeight}, update_time = NOW() WHERE id = #{jobId} AND deleted = 0")
    void updateTopStatus(@Param("jobId") Long jobId, @Param("isTop") Boolean isTop, @Param("topWeight") Integer topWeight);

    /**
     * 更新推荐状态
     */
    @Select("UPDATE jobs SET is_recommended = #{isRecommended}, update_time = NOW() WHERE id = #{jobId} AND deleted = 0")
    void updateRecommendedStatus(@Param("jobId") Long jobId, @Param("isRecommended") Boolean isRecommended);

    /**
     * 查询过期兼职（截止日期已过且状态为招聘中）
     */
    @Select("SELECT * FROM jobs WHERE deadline < NOW() AND status = 1 AND deleted = 0 ORDER BY deadline ASC")
    List<Job> selectExpiredJobs();

    /**
     * 查询已招满兼职（招聘人数等于已录取人数且状态为招聘中）
     */
    @Select("SELECT * FROM jobs WHERE recruit_count = accepted_count AND status = 1 AND deleted = 0 ORDER BY publish_time DESC")
    List<Job> selectFullJobs();

    /**
     * 查询需要物理删除的已删除兼职（逻辑删除时间超过30天）
     */
    @Select("SELECT * FROM jobs WHERE deleted = 1 AND update_time < DATE_SUB(NOW(), INTERVAL 30 DAY) ORDER BY update_time ASC")
    List<Job> selectJobsForPhysicalDeletion();

    /**
     * 物理删除兼职
     */
    @Select("DELETE FROM jobs WHERE id = #{jobId}")
    void physicalDelete(@Param("jobId") Long jobId);
}