package com.xushu.campus.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.user.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户地址Mapper接口
 */
@Mapper
@Repository
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    /**
     * 根据用户ID查询所有地址（包含已删除的）
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserAddress> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询未删除的地址
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<UserAddress> selectActiveByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的默认地址
     */
    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} AND is_default = 1 AND is_deleted = 0 LIMIT 1")
    UserAddress selectDefaultByUserId(@Param("userId") Long userId);

    /**
     * 取消用户的所有默认地址
     */
    @Update("UPDATE user_addresses SET is_default = 0, update_time = NOW() WHERE user_id = #{userId} AND is_deleted = 0")
    void clearDefaultByUserId(@Param("userId") Long userId);

    /**
     * 设置指定地址为默认地址
     */
    @Update("UPDATE user_addresses SET is_default = 1, update_time = NOW() WHERE id = #{addressId} AND user_id = #{userId} AND is_deleted = 0")
    int setDefaultAddress(@Param("userId") Long userId, @Param("addressId") Long addressId);

}