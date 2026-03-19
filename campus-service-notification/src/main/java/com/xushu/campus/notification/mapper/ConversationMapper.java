package com.xushu.campus.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.notification.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天会话Mapper接口
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    /**
     * 根据用户ID查询会话列表（通过会话成员关联）
     */
    @Select("SELECT c.* FROM conversations c " +
            "JOIN conversation_members cm ON c.id = cm.conversation_id " +
            "WHERE cm.user_id = #{userId} AND c.deleted = 0 AND cm.deleted = 0 " +
            "ORDER BY c.last_message_time DESC, c.update_time DESC")
    List<Conversation> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据会话类型查询
     */
    @Select("SELECT * FROM conversations WHERE conversation_type = #{conversationType} AND deleted = 0 " +
            "ORDER BY last_message_time DESC")
    List<Conversation> selectByType(@Param("conversationType") String conversationType);

    /**
     * 查询创建者的会话列表
     */
    @Select("SELECT * FROM conversations WHERE creator_id = #{creatorId} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<Conversation> selectByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 更新会话的最后一条消息信息
     */
    @Select("UPDATE conversations SET " +
            "last_message_id = #{lastMessageId}, " +
            "last_message_content = #{lastMessageContent}, " +
            "last_message_time = #{lastMessageTime}, " +
            "unread_count = unread_count + 1, " +
            "update_time = NOW() " +
            "WHERE id = #{conversationId} AND deleted = 0")
    void updateLastMessageInfo(@Param("conversationId") Long conversationId,
                               @Param("lastMessageId") Long lastMessageId,
                               @Param("lastMessageContent") String lastMessageContent,
                               @Param("lastMessageTime") String lastMessageTime);

    /**
     * 减少会话的未读消息数
     */
    @Select("UPDATE conversations SET unread_count = GREATEST(0, unread_count - #{count}), update_time = NOW() " +
            "WHERE id = #{conversationId} AND deleted = 0")
    void decreaseUnreadCount(@Param("conversationId") Long conversationId, @Param("count") Integer count);

    /**
     * 增加会话的成员数量
     */
    @Select("UPDATE conversations SET member_count = member_count + #{count}, update_time = NOW() " +
            "WHERE id = #{conversationId} AND deleted = 0")
    void increaseMemberCount(@Param("conversationId") Long conversationId, @Param("count") Integer count);

    /**
     * 减少会话的成员数量
     */
    @Select("UPDATE conversations SET member_count = GREATEST(0, member_count - #{count}), update_time = NOW() " +
            "WHERE id = #{conversationId} AND deleted = 0")
    void decreaseMemberCount(@Param("conversationId") Long conversationId, @Param("count") Integer count);
}