package com.xushu.campus.user.constant;

/**
 * 用户相关常量
 */
public class UserConstants {

    /**
     * 用户状态
     */
    public static class Status {
        /**
         * 禁用
         */
        public static final int DISABLED = 0;

        /**
         * 正常
         */
        public static final int NORMAL = 1;
    }

    /**
     * 性别
     */
    public static class Gender {
        /**
         * 未知
         */
        public static final int UNKNOWN = 0;

        /**
         * 男
         */
        public static final int MALE = 1;

        /**
         * 女
         */
        public static final int FEMALE = 2;
    }

    /**
     * 用户角色
     */
    public static class Role {
        /**
         * 学生
         */
        public static final String STUDENT = "STUDENT";

        /**
         * 教师
         */
        public static final String TEACHER = "TEACHER";

        /**
         * 管理员
         */
        public static final String ADMIN = "ADMIN";

        /**
         * 商家
         */
        public static final String MERCHANT = "MERCHANT";
    }

    /**
     * 默认值
     */
    public static class Default {
        /**
         * 默认头像
         */
        public static final String AVATAR_URL = "/avatars/default.png";

        /**
         * 默认角色
         */
        public static final String DEFAULT_ROLE = Role.STUDENT;
    }

    /**
     * 缓存键
     */
    public static class CacheKey {
        /**
         * 用户信息缓存前缀
         */
        public static final String USER_INFO = "user:info:";

        /**
         * 用户角色缓存前缀
         */
        public static final String USER_ROLES = "user:roles:";

        /**
         * 登录尝试次数缓存前缀
         */
        public static final String LOGIN_ATTEMPTS = "user:login:attempts:";

        /**
         * 验证码缓存前缀
         */
        public static final String CAPTCHA = "user:captcha:";
    }

    /**
     * 正则表达式
     */
    public static class Regex {
        /**
         * 用户名正则：4-20位字母、数字、下划线
         */
        public static final String USERNAME = "^[a-zA-Z0-9_]{4,20}$";

        /**
         * 密码正则：必须包含大小写字母和数字，长度8-20位
         */
        public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$";

        /**
         * 手机号正则
         */
        public static final String PHONE = "^1[3-9]\\d{9}$";

        /**
         * 邮箱正则
         */
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        /**
         * 学号正则：数字和字母组合，6-20位
         */
        public static final String STUDENT_ID = "^[A-Za-z0-9]{6,20}$";
    }

}