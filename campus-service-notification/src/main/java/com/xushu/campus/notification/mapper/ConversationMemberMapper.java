package com.xushu.campus.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.notification.entity.ConversationMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会话成员Mapper接口
 */
@Mapper
public interface ConversationMemberMapper extends BaseMapper<ConversationMember> {

    /**
     * 根据会话ID查询成员列表
     */
    @Select("SELECT * FROM conversation_members WHERE conversation_id = #{conversationId} AND deleted = 0 " +
            "ORDER BY join_time ASC")
    List<ConversationMember> selectByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 根据用户ID查询成员列表
     */
    @Select("SELECT * FROM conversation_members WHERE user_id = #{userId} AND deleted = 0 " +
            "ORDER BY join_time DESC")
    List<ConversationMember> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID和用户ID查询成员
     */
    @Select("SELECT * FROM conversation_members WHERE conversation_id = #{conversationId} AND user_id = #{userId} AND deleted = 0")
    ConversationMember selectByConversationAndUser(@Param("conversationId") Long conversationId,
                                                   @Param("userId") Long userId);

    /**
     * 查询会话中的成员数量
     */
    @Select("SELECT COUNT(*) FROM conversation_members WHERE conversation_id = #{conversationId} AND deleted = 0")
    Integer countByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 更新成员的未读消息数
     */
    @Select("UPDATE conversation_members SET unread_count = unread_count + #{count}, update_time = NOW() " +
            "WHERE id = #{memberId} AND deleted = 0")
    void increaseUnreadCount(@Param("memberId") Long memberId, @Param("count") Integer count);

    /**
     * 重置成员的未读消息数
     */
    @Select("UPDATE conversation_members SET unread_count = 0, last_read_message_id = #{lastReadMessageId}, update_time = NOW() " +
            "WHERE id = #{memberId} AND deleted = 0")
    void resetUnreadCount(@Param("memberId") Long memberId, @Param("lastReadMessageId") Long lastReadMessageId);

    /**
     * 批量查询会话中的成员
     */
    @Select("<script>" +
            "SELECT * FROM conversation_members WHERE conversation_id IN " +
            "<foreach collection='conversationIds' item='conversationId' open='(' separator=',' close=')'>" +
            "#{conversationId}" +
            "</foreach>" +
            " AND deleted = 0" +
            "</script>")
    List<ConversationMember> selectByConversationIds(@Param("conversationIds") List<Long> conversationIds);
}