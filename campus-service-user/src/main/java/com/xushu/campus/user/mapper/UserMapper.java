package com.xushu.campus.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper接口
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND (is_deleted = 0 OR is_deleted IS NULL)")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND (is_deleted = 0 OR is_deleted IS NULL)")
    User selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND (is_deleted = 0 OR is_deleted IS NULL)")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据学号查询用户
     */
    @Select("SELECT * FROM users WHERE student_id = #{studentId} AND (is_deleted = 0 OR is_deleted IS NULL)")
    User selectByStudentId(@Param("studentId") String studentId);

    /**
     * 更新用户最后登录时间
     */
    @Update("UPDATE users SET last_login_time = NOW(), updated_at = NOW() WHERE id = #{userId}")
    void updateLastLoginTime(@Param("userId") Long userId);


}