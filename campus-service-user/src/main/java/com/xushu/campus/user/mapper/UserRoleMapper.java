package com.xushu.campus.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色Mapper接口
 */
@Mapper
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID查询角色列表
     */
    @Select("SELECT * FROM user_roles WHERE user_id = #{userId}")
    List<UserRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和角色代码查询
     */
    @Select("SELECT * FROM user_roles WHERE user_id = #{userId} AND role_code = #{roleCode}")
    UserRole selectByUserIdAndRoleCode(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    /**
     * 插入用户角色（自定义insert方法）
     */
    @Insert("INSERT INTO user_roles (user_id, role_code, created_at) VALUES (#{userRole.userId}, #{userRole.roleCode}, #{userRole.createTime})")
    int insertUserRole(@Param("userRole") UserRole userRole);

    /**
     * 删除用户的所有角色
     */
    @Select("DELETE FROM user_roles WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

}