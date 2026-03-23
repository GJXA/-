package com.xushu.campus.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.product.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品分类Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有启用的一级分类
     */
    @Select("SELECT * FROM product_categories WHERE parent_id = 0 AND is_enabled = 1 AND is_deleted = 0 ORDER BY sort_order ASC")
    List<Category> selectRootCategories();

    /**
     * 根据父分类ID查询子分类
     */
    @Select("SELECT * FROM product_categories WHERE parent_id = #{parentId} AND is_enabled = 1 AND is_deleted = 0 ORDER BY sort_order ASC")
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据分类名称查询
     */
    @Select("SELECT * FROM product_categories WHERE name LIKE CONCAT('%', #{name}, '%') AND is_enabled = 1 AND is_deleted = 0")
    List<Category> selectByName(@Param("name") String name);

    /**
     * 查询所有启用的分类（树形结构）
     */
    @Select("SELECT * FROM product_categories WHERE is_enabled = 1 AND is_deleted = 0 ORDER BY parent_id ASC, sort_order ASC")
    List<Category> selectAllEnabled();

}