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
    @Select("SELECT * FROM category WHERE parent_id = 0 AND status = 1 ORDER BY sort ASC")
    List<Category> selectRootCategories();

    /**
     * 根据父分类ID查询子分类
     */
    @Select("SELECT * FROM category WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort ASC")
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据分类名称查询
     */
    @Select("SELECT * FROM category WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 1")
    List<Category> selectByName(@Param("name") String name);

    /**
     * 查询所有启用的分类（树形结构）
     */
    @Select("SELECT * FROM category WHERE status = 1 ORDER BY level ASC, sort ASC")
    List<Category> selectAllEnabled();

}