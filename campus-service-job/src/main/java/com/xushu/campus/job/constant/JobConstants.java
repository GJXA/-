package com.xushu.campus.job.constant;

/**
 * 兼职相关常量
 */
public class JobConstants {

    /**
     * 工作状态
     */
    public static class JobStatus {
        /**
         * 待审核
         */
        public static final int PENDING = 0;

        /**
         * 招聘中
         */
        public static final int RECRUITING = 1;

        /**
         * 已结束
         */
        public static final int ENDED = 2;

        /**
         * 已关闭
         */
        public static final int CLOSED = 3;

        /**
         * 获取状态描述
         */
        public static String getDesc(int status) {
            switch (status) {
                case PENDING:
                    return "待审核";
                case RECRUITING:
                    return "招聘中";
                case ENDED:
                    return "已结束";
                case CLOSED:
                    return "已关闭";
                default:
                    return "未知";
            }
        }

        /**
         * 检查是否可以申请（只有招聘中的职位可以申请）
         */
        public static boolean canApply(int status) {
            return status == RECRUITING;
        }

        /**
         * 检查是否可以编辑（待审核和招聘中的职位可以编辑）
         */
        public static boolean canEdit(int status) {
            return status == PENDING || status == RECRUITING;
        }

        /**
         * 检查是否可以结束（招聘中的职位可以结束）
         */
        public static boolean canEnd(int status) {
            return status == RECRUITING;
        }

        /**
         * 检查状态是否有效
         */
        public static boolean isValid(int status) {
            return status == PENDING || status == RECRUITING || status == ENDED || status == CLOSED;
        }
    }

    /**
     * 申请状态
     */
    public static class ApplicationStatus {
        /**
         * 待处理
         */
        public static final int PENDING = 0;

        /**
         * 已通过
         */
        public static final int APPROVED = 1;

        /**
         * 已拒绝
         */
        public static final int REJECTED = 2;

        /**
         * 已取消
         */
        public static final int CANCELLED = 3;

        /**
         * 获取状态描述
         */
        public static String getDesc(int status) {
            switch (status) {
                case PENDING:
                    return "待处理";
                case APPROVED:
                    return "已通过";
                case REJECTED:
                    return "已拒绝";
                case CANCELLED:
                    return "已取消";
                default:
                    return "未知";
            }
        }

        /**
         * 检查是否可以处理（只有待处理的申请可以处理）
         */
        public static boolean canProcess(int status) {
            return status == PENDING;
        }

        /**
         * 检查是否可以取消（待处理的申请可以取消）
         */
        public static boolean canCancel(int status) {
            return status == PENDING;
        }

        /**
         * 检查状态是否有效
         */
        public static boolean isValid(int status) {
            return status == PENDING || status == APPROVED || status == REJECTED || status == CANCELLED;
        }
    }

    /**
     * 工作类型
     */
    public static class JobType {
        /**
         * 全职
         */
        public static final String FULL_TIME = "FULL_TIME";

        /**
         * 兼职
         */
        public static final String PART_TIME = "PART_TIME";

        /**
         * 实习
         */
        public static final String INTERNSHIP = "INTERNSHIP";

        /**
         * 获取类型描述
         */
        public static String getDesc(String jobType) {
            switch (jobType) {
                case FULL_TIME:
                    return "全职";
                case PART_TIME:
                    return "兼职";
                case INTERNSHIP:
                    return "实习";
                default:
                    return "未知";
            }
        }

        /**
         * 检查工作类型是否有效
         */
        public static boolean isValid(String jobType) {
            return FULL_TIME.equals(jobType) || PART_TIME.equals(jobType) || INTERNSHIP.equals(jobType);
        }
    }

    /**
     * 薪资单位
     */
    public static class SalaryUnit {
        /**
         * 时薪
         */
        public static final String HOUR = "HOUR";

        /**
         * 日薪
         */
        public static final String DAY = "DAY";

        /**
         * 月薪
         */
        public static final String MONTH = "MONTH";

        /**
         * 项目制
         */
        public static final String PROJECT = "PROJECT";

        /**
         * 获取单位描述
         */
        public static String getDesc(String unit) {
            switch (unit) {
                case HOUR:
                    return "元/小时";
                case DAY:
                    return "元/天";
                case MONTH:
                    return "元/月";
                case PROJECT:
                    return "项目制";
                default:
                    return "未知";
            }
        }

        /**
         * 检查薪资单位是否有效
         */
        public static boolean isValid(String unit) {
            return HOUR.equals(unit) || DAY.equals(unit) || MONTH.equals(unit) || PROJECT.equals(unit);
        }
    }

    /**
     * 工作类别
     */
    public static class JobCategory {
        /**
         * 销售
         */
        public static final String SALES = "SALES";

        /**
         * 家教
         */
        public static final String TUTOR = "TUTOR";

        /**
         * 服务员
         */
        public static final String WAITER = "WAITER";

        /**
         * 促销
         */
        public static final String PROMOTION = "PROMOTION";

        /**
         * 文员
         */
        public static final String CLERK = "CLERK";

        /**
         * 设计
         */
        public static final String DESIGN = "DESIGN";

        /**
         * 编程/IT
         */
        public static final String PROGRAMMING = "PROGRAMMING";

        /**
         * 翻译
         */
        public static final String TRANSLATION = "TRANSLATION";

        /**
         * 其他
         */
        public static final String OTHER = "OTHER";

        /**
         * 获取类别描述
         */
        public static String getDesc(String category) {
            switch (category) {
                case SALES:
                    return "销售";
                case TUTOR:
                    return "家教";
                case WAITER:
                    return "服务员";
                case PROMOTION:
                    return "促销";
                case CLERK:
                    return "文员";
                case DESIGN:
                    return "设计";
                case PROGRAMMING:
                    return "编程/IT";
                case TRANSLATION:
                    return "翻译";
                case OTHER:
                    return "其他";
                default:
                    return "未知";
            }
        }

        /**
         * 获取所有类别
         */
        public static String[] getAllCategories() {
            return new String[]{
                SALES, TUTOR, WAITER, PROMOTION, CLERK,
                DESIGN, PROGRAMMING, TRANSLATION, OTHER
            };
        }

        /**
         * 检查类别是否有效
         */
        public static boolean isValid(String category) {
            return SALES.equals(category) || TUTOR.equals(category) || WAITER.equals(category) ||
                   PROMOTION.equals(category) || CLERK.equals(category) || DESIGN.equals(category) ||
                   PROGRAMMING.equals(category) || TRANSLATION.equals(category) || OTHER.equals(category);
        }
    }

    /**
     * 发布者类型
     */
    public static class PublisherType {
        /**
         * 个人用户
         */
        public static final String USER = "USER";

        /**
         * 企业用户
         */
        public static final String COMPANY = "COMPANY";

        /**
         * 管理员
         */
        public static final String ADMIN = "ADMIN";

        /**
         * 获取类型描述
         */
        public static String getDesc(String type) {
            switch (type) {
                case USER:
                    return "个人用户";
                case COMPANY:
                    return "企业用户";
                case ADMIN:
                    return "管理员";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 确认状态（是否参加）
     */
    public static class ConfirmStatus {
        /**
         * 未确认
         */
        public static final int UNCONFIRMED = 0;

        /**
         * 确认参加
         */
        public static final int CONFIRMED = 1;

        /**
         * 拒绝参加
         */
        public static final int REJECTED = 2;

        /**
         * 获取状态描述
         */
        public static String getDesc(int status) {
            switch (status) {
                case UNCONFIRMED:
                    return "未确认";
                case CONFIRMED:
                    return "确认参加";
                case REJECTED:
                    return "拒绝参加";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 缓存键
     */
    public static class CacheKey {
        /**
         * 兼职详情缓存前缀
         */
        public static final String JOB_DETAIL = "job:detail:";

        /**
         * 热门兼职缓存
         */
        public static final String HOT_JOBS = "job:hot";

        /**
         * 最新兼职缓存
         */
        public static final String LATEST_JOBS = "job:latest";

        /**
         * 兼职分类缓存前缀
         */
        public static final String JOB_CATEGORY = "job:category:";

        /**
         * 用户申请记录缓存前缀
         */
        public static final String USER_APPLICATIONS = "job:application:user:";

        /**
         * 兼职申请列表缓存前缀
         */
        public static final String JOB_APPLICATIONS = "job:application:job:";
    }

    /**
     * 默认值
     */
    public static class Default {
        /**
         * 默认分页大小
         */
        public static final int PAGE_SIZE = 20;

        /**
         * 热门兼职数量
         */
        public static final int HOT_JOB_LIMIT = 10;

        /**
         * 最新兼职数量
         */
        public static final int LATEST_JOB_LIMIT = 10;

        /**
         * 默认招聘人数
         */
        public static final int DEFAULT_RECRUIT_COUNT = 1;

        /**
         * 兼职默认有效天数
         */
        public static final int DEFAULT_VALID_DAYS = 30;

        /**
         * 默认置顶权重
         */
        public static final int DEFAULT_TOP_WEIGHT = 0;
    }

    /**
     * 排序字段
     */
    public static class SortField {
        /**
         * 发布时间
         */
        public static final String PUBLISH_TIME = "publish_time";

        /**
         * 薪资
         */
        public static final String SALARY = "salary";

        /**
         * 截止时间
         */
        public static final String DEADLINE = "deadline";

        /**
         * 浏览次数
         */
        public static final String VIEW_COUNT = "view_count";

        /**
         * 申请人数
         */
        public static final String APPLIED_COUNT = "applied_count";

        /**
         * 置顶权重
         */
        public static final String TOP_WEIGHT = "top_weight";
    }

    /**
     * 排序方向
     */
    public static class SortDirection {
        /**
         * 升序
         */
        public static final String ASC = "asc";

        /**
         * 降序
         */
        public static final String DESC = "desc";
    }

    /**
     * 操作类型
     */
    public static class Operation {
        /**
         * 发布兼职
         */
        public static final String PUBLISH = "PUBLISH";

        /**
         * 更新兼职
         */
        public static final String UPDATE = "UPDATE";

        /**
         * 审核兼职
         */
        public static final String AUDIT = "AUDIT";

        /**
         * 结束兼职
         */
        public static final String END = "END";

        /**
         * 关闭兼职
         */
        public static final String CLOSE = "CLOSE";

        /**
         * 申请兼职
         */
        public static final String APPLY = "APPLY";

        /**
         * 处理申请
         */
        public static final String PROCESS_APPLICATION = "PROCESS_APPLICATION";

        /**
         * 取消申请
         */
        public static final String CANCEL_APPLICATION = "CANCEL_APPLICATION";

        /**
         * 安排面试
         */
        public static final String SCHEDULE_INTERVIEW = "SCHEDULE_INTERVIEW";

        /**
         * 发送录用通知
         */
        public static final String SEND_OFFER = "SEND_OFFER";

        /**
         * 确认参加
         */
        public static final String CONFIRM_PARTICIPATION = "CONFIRM_PARTICIPATION";

        /**
         * 评价工作
         */
        public static final String EVALUATE_WORK = "EVALUATE_WORK";

        /**
         * 获取操作描述
         */
        public static String getDesc(String operation) {
            switch (operation) {
                case PUBLISH:
                    return "发布兼职";
                case UPDATE:
                    return "更新兼职";
                case AUDIT:
                    return "审核兼职";
                case END:
                    return "结束兼职";
                case CLOSE:
                    return "关闭兼职";
                case APPLY:
                    return "申请兼职";
                case PROCESS_APPLICATION:
                    return "处理申请";
                case CANCEL_APPLICATION:
                    return "取消申请";
                case SCHEDULE_INTERVIEW:
                    return "安排面试";
                case SEND_OFFER:
                    return "发送录用通知";
                case CONFIRM_PARTICIPATION:
                    return "确认参加";
                case EVALUATE_WORK:
                    return "评价工作";
                default:
                    return "未知操作";
            }
        }
    }

}