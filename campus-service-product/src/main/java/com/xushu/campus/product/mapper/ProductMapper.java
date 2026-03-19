package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据用户ID查询商品列表
     */
    @Select("SELECT * FROM product WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Product> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据分类ID查询商品列表
     */
    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND deleted = 0 AND status = 1 ORDER BY create_time DESC")
    List<Product> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 搜索商品（标题和描述）
     */
    @Select("SELECT * FROM product WHERE (title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) AND deleted = 0 AND status = 1 ORDER BY create_time DESC")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE product SET view_count = view_count + 1 WHERE id = #{productId}")
    void incrementViewCount(@Param("productId") Long productId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE product SET like_count = like_count + 1 WHERE id = #{productId}")
    void incrementLikeCount(@Param("productId") Long productId);

    /**
     * 减少点赞数
     */
    @Update("UPDATE product SET like_count = like_count - 1 WHERE id = #{productId} AND like_count > 0")
    void decrementLikeCount(@Param("productId") Long productId);

    /**
     * 更新商品状态
     */
    @Update("UPDATE product SET status = #{status} WHERE id = #{productId}")
    void updateStatus(@Param("productId") Long productId, @Param("status") Integer status);

    /**
     * 批量更新商品状态
     */
    @Update("<script>" +
            "UPDATE product SET status = #{status} WHERE id IN " +
            "<foreach collection='productIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void batchUpdateStatus(@Param("productIds") List<Long> productIds, @Param("status") Integer status);

}