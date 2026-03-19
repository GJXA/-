package com.xushu.campus.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.notification.entity.MessageReadStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消息阅读状态Mapper接口
 */
@Mapper
public interface MessageReadStatusMapper extends BaseMapper<MessageReadStatus> {

    /**
     * 根据消息ID查询阅读状态
     */
    @Select("SELECT * FROM message_read_status WHERE message_id = #{messageId} ORDER BY read_time DESC")
    List<MessageReadStatus> selectByMessageId(@Param("messageId") Long messageId);

    /**
     * 根据用户ID查询阅读状态
     */
    @Select("SELECT * FROM message_read_status WHERE user_id = #{userId} ORDER BY read_time DESC")
    List<MessageReadStatus> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询消息的已读用户列表
     */
    @Select("SELECT user_id FROM message_read_status WHERE message_id = #{messageId} ORDER BY read_time DESC")
    List<Long> selectReadUserIdsByMessageId(@Param("messageId") Long messageId);

    /**
     * 检查用户是否已读消息
     */
    @Select("SELECT COUNT(*) FROM message_read_status WHERE message_id = #{messageId} AND user_id = #{userId}")
    Integer checkUserHasRead(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 批量查询消息的阅读状态
     */
    @Select("<script>" +
            "SELECT * FROM message_read_status WHERE message_id IN " +
            "<foreach collection='messageIds' item='messageId' open='(' separator=',' close=')'>" +
            "#{messageId}" +
            "</foreach>" +
            " ORDER BY message_id, read_time DESC" +
            "</script>")
    List<MessageReadStatus> selectByMessageIds(@Param("messageIds") List<Long> messageIds);
}