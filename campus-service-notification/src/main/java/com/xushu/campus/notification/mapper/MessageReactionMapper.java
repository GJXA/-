package com.xushu.campus.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.notification.entity.MessageReaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消息反应Mapper接口
 */
@Mapper
public interface MessageReactionMapper extends BaseMapper<MessageReaction> {

    /**
     * 根据消息ID查询反应列表
     */
    @Select("SELECT * FROM message_reactions WHERE message_id = #{messageId} ORDER BY create_time DESC")
    List<MessageReaction> selectByMessageId(@Param("messageId") Long messageId);

    /**
     * 根据用户ID查询反应列表
     */
    @Select("SELECT * FROM message_reactions WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<MessageReaction> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据消息ID和反应类型查询
     */
    @Select("SELECT * FROM message_reactions WHERE message_id = #{messageId} AND reaction_type = #{reactionType} " +
            "ORDER BY create_time DESC")
    List<MessageReaction> selectByMessageAndType(@Param("messageId") Long messageId,
                                                 @Param("reactionType") String reactionType);

    /**
     * 查询消息的反应统计
     */
    @Select("SELECT reaction_type, COUNT(*) as count FROM message_reactions " +
            "WHERE message_id = #{messageId} GROUP BY reaction_type")
    List<ReactionStat> selectReactionStats(@Param("messageId") Long messageId);

    /**
     * 检查用户是否对消息有特定反应
     */
    @Select("SELECT COUNT(*) FROM message_reactions " +
            "WHERE message_id = #{messageId} AND user_id = #{userId} AND reaction_type = #{reactionType}")
    Integer checkUserReaction(@Param("messageId") Long messageId,
                              @Param("userId") Long userId,
                              @Param("reactionType") String reactionType);

    /**
     * 批量查询消息的反应
     */
    @Select("<script>" +
            "SELECT * FROM message_reactions WHERE message_id IN " +
            "<foreach collection='messageIds' item='messageId' open='(' separator=',' close=')'>" +
            "#{messageId}" +
            "</foreach>" +
            " ORDER BY message_id, create_time DESC" +
            "</script>")
    List<MessageReaction> selectByMessageIds(@Param("messageIds") List<Long> messageIds);

    /**
     * 反应统计DTO
     */
    class ReactionStat {
        private String reactionType;
        private Long count;

        // getters and setters
        public String getReactionType() {
            return reactionType;
        }

        public void setReactionType(String reactionType) {
            this.reactionType = reactionType;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}