package com.xushu.campus.product.constant;

/**
 * 商品相关常量
 */
public class ProductConstants {

    /**
     * 商品状态
     */
    public static class Status {
        /**
         * 待审核
         */
        public static final int PENDING = 0;

        /**
         * 上架中
         */
        public static final int ON_SHELF = 1;

        /**
         * 已下架
         */
        public static final int OFF_SHELF = 2;

        /**
         * 已售出
         */
        public static final int SOLD = 3;

        /**
         * 获取状态描述
         */
        public static String getDesc(int status) {
            switch (status) {
                case PENDING:
                    return "待审核";
                case ON_SHELF:
                    return "上架中";
                case OFF_SHELF:
                    return "已下架";
                case SOLD:
                    return "已售出";
                default:
                    return "未知";
            }
        }
    }

    /**
     * 分类状态
     */
    public static class CategoryStatus {
        /**
         * 禁用
         */
        public static final int DISABLED = 0;

        /**
         * 启用
         */
        public static final int ENABLED = 1;
    }

    /**
     * 分类层级
     */
    public static class CategoryLevel {
        /**
         * 一级分类
         */
        public static final int ROOT = 1;

        /**
         * 二级分类
         */
        public static final int CHILD = 2;
    }

    /**
     * 缓存键
     */
    public static class CacheKey {
        /**
         * 商品详情缓存前缀
         */
        public static final String PRODUCT_DETAIL = "product:detail:";

        /**
         * 商品点赞缓存前缀
         */
        public static final String PRODUCT_LIKE = "product:like:";

        /**
         * 热门商品缓存
         */
        public static final String HOT_PRODUCTS = "product:hot";

        /**
         * 最新商品缓存
         */
        public static final String LATEST_PRODUCTS = "product:latest";

        /**
         * 分类树缓存
         */
        public static final String CATEGORY_TREE = "category:tree";
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
         * 热门商品数量
         */
        public static final int HOT_PRODUCT_LIMIT = 10;

        /**
         * 最新商品数量
         */
        public static final int LATEST_PRODUCT_LIMIT = 10;
    }

    /**
     * 排序字段
     */
    public static class SortField {
        /**
         * 创建时间
         */
        public static final String CREATE_TIME = "create_time";

        /**
         * 价格
         */
        public static final String PRICE = "price";

        /**
         * 浏览量
         */
        public static final String VIEW_COUNT = "view_count";

        /**
         * 点赞数
         */
        public static final String LIKE_COUNT = "like_count";
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
     * 图片配置
     */
    public static class Image {
        /**
         * 最大图片数量
         */
        public static final int MAX_COUNT = 5;

        /**
         * 允许的图片扩展名
         */
        public static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp"};

        /**
         * 最大文件大小（字节）
         */
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

        /**
         * 最大请求大小（字节）
         */
        public static final long MAX_REQUEST_SIZE = 50 * 1024 * 1024; // 50MB
    }

}