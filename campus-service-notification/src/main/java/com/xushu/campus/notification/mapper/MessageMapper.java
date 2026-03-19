package com.xushu.campus.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.notification.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据会话ID查询消息列表（分页）
     */
    @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<Message> selectByConversationId(@Param("conversationId") Long conversationId,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    /**
     * 根据会话ID查询消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE conversation_id = #{conversationId} AND deleted = 0")
    Integer countByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 根据发送者ID查询消息列表
     */
    @Select("SELECT * FROM messages WHERE sender_id = #{senderId} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<Message> selectBySenderId(@Param("senderId") Long senderId, @Param("limit") Integer limit);

    /**
     * 根据消息类型查询
     */
    @Select("SELECT * FROM messages WHERE message_type = #{messageType} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<Message> selectByMessageType(@Param("messageType") String messageType, @Param("limit") Integer limit);

    /**
     * 查询会话中指定时间之后的新消息
     */
    @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} " +
            "AND create_time > #{afterTime} AND deleted = 0 " +
            "ORDER BY create_time ASC")
    List<Message> selectNewMessages(@Param("conversationId") Long conversationId,
                                    @Param("afterTime") LocalDateTime afterTime);

    /**
     * 查询父消息的子消息
     */
    @Select("SELECT * FROM messages WHERE parent_message_id = #{parentMessageId} AND deleted = 0 " +
            "ORDER BY create_time ASC")
    List<Message> selectReplies(@Param("parentMessageId") Long parentMessageId);

    /**
     * 更新消息的已读人数
     */
    @Select("UPDATE messages SET read_count = read_count + 1, update_time = NOW() " +
            "WHERE id = #{messageId} AND deleted = 0")
    void increaseReadCount(@Param("messageId") Long messageId);

    /**
     * 更新消息的反应数量
     */
    @Select("UPDATE messages SET reaction_count = reaction_count + #{count}, update_time = NOW() " +
            "WHERE id = #{messageId} AND deleted = 0")
    void updateReactionCount(@Param("messageId") Long messageId, @Param("count") Integer count);

    /**
     * 批量查询消息
     */
    @Select("<script>" +
            "SELECT * FROM messages WHERE id IN " +
            "<foreach collection='messageIds' item='messageId' open='(' separator=',' close=')'>" +
            "#{messageId}" +
            "</foreach>" +
            " AND deleted = 0 ORDER BY create_time DESC" +
            "</script>")
    List<Message> selectByIds(@Param("messageIds") List<Long> messageIds);
}