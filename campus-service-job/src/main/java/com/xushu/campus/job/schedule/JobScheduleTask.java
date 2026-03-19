package com.xushu.campus.job.schedule;

import com.xushu.campus.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 兼职定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduleTask {

    private final JobService jobService;

    /**
     * 处理过期兼职（截止日期已过）
     * 每30分钟执行一次
     */
    @Scheduled(cron = "${campus.job.schedule.process-expired-jobs:0 */30 * * * ?}")
    public void processExpiredJobsTask() {
        try {
            log.info("开始执行处理过期兼职定时任务");
            long startTime = System.currentTimeMillis();

            // 获取过期兼职ID列表
            List<Long> expiredJobIds = jobService.getExpiredJobIds();
            if (!expiredJobIds.isEmpty()) {
                log.info("找到 {} 个过期兼职，正在更新状态为已结束", expiredJobIds.size());
                // 批量更新状态为已结束 (JobConstants.JobStatus.ENDED = 2)
                jobService.batchUpdateStatus(expiredJobIds, 2);
                log.info("已成功更新 {} 个过期兼职的状态", expiredJobIds.size());
            } else {
                log.info("没有找到过期兼职");
            }

            long endTime = System.currentTimeMillis();
            log.info("完成处理过期兼职定时任务，耗时{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("处理过期兼职定时任务执行失败", e);
        }
    }

    /**
     * 自动结束已招满的兼职
     * 每15分钟执行一次
     */
    @Scheduled(cron = "${campus.job.schedule.auto-end-full-jobs:0 */15 * * * ?}")
    public void autoEndFullJobsTask() {
        try {
            log.info("开始执行自动结束已招满兼职定时任务");
            long startTime = System.currentTimeMillis();

            // 获取已招满兼职ID列表
            List<Long> fullJobIds = jobService.getFullJobIds();
            if (!fullJobIds.isEmpty()) {
                log.info("找到 {} 个已招满兼职，正在更新状态为已结束", fullJobIds.size());
                // 批量更新状态为已结束 (JobConstants.JobStatus.ENDED = 2)
                jobService.batchUpdateStatus(fullJobIds, 2);
                log.info("已成功更新 {} 个已招满兼职的状态", fullJobIds.size());
            } else {
                log.info("没有找到已招满兼职");
            }

            long endTime = System.currentTimeMillis();
            log.info("完成自动结束已招满兼职定时任务，耗时{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("自动结束已招满兼职定时任务执行失败", e);
        }
    }

    /**
     * 更新兼职统计数据
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "${campus.job.schedule.update-job-statistics:0 0 3 * * ?}")
    public void updateJobStatisticsTask() {
        try {
            log.info("开始执行更新兼职统计数据定时任务");
            long startTime = System.currentTimeMillis();

            // 调用服务方法更新兼职统计数据
            jobService.updateJobStatistics();

            long endTime = System.currentTimeMillis();
            log.info("完成更新兼职统计数据定时任务，耗时{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("更新兼职统计数据定时任务执行失败", e);
        }
    }

    /**
     * 清理已删除的兼职相关数据
     * 每天凌晨4点执行
     */
    @Scheduled(cron = "${campus.job.schedule.cleanup-deleted-jobs:0 0 4 * * ?}")
    public void cleanupDeletedJobsTask() {
        try {
            log.info("开始执行清理已删除兼职数据定时任务");
            long startTime = System.currentTimeMillis();

            // 调用服务方法清理已删除的兼职数据
            jobService.cleanupDeletedJobs();

            long endTime = System.currentTimeMillis();
            log.info("完成清理已删除兼职数据定时任务，耗时{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("清理已删除兼职数据定时任务执行失败", e);
        }
    }

    /**
     * 检查并更新兼职状态
     * 每小时执行一次
     */
    @Scheduled(cron = "${campus.job.schedule.check-job-status:0 0 */1 * * ?}")
    public void checkAndUpdateJobStatusTask() {
        try {
            log.debug("开始执行检查并更新兼职状态定时任务");
            long startTime = System.currentTimeMillis();

            // TODO: 实现检查并更新兼职状态的逻辑
            // 例如：检查开始日期已过的兼职，发送提醒或更新状态
            // 当前暂时记录日志，后续根据业务需求实现
            log.debug("检查并更新兼职状态定时任务执行中...");

            long endTime = System.currentTimeMillis();
            log.debug("完成检查并更新兼职状态定时任务，耗时{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("检查并更新兼职状态定时任务执行失败", e);
        }
    }
}