package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.notification.dto.MessageDTO;
import com.xushu.campus.notification.dto.SendMessageRequest;
import com.xushu.campus.notification.dto.MessageStatisticsDTO;
import com.xushu.campus.notification.dto.UserMessageStatisticsDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 发送消息
     */
    MessageDTO sendMessage(SendMessageRequest request, Long senderId) throws BusinessException;

    /**
     * 根据ID获取消息详情
     */
    MessageDTO getMessageById(Long id, Long userId) throws BusinessException;

    /**
     * 根据会话ID获取消息列表（分页）
     */
    IPage<MessageDTO> getMessagesByConversationId(Long conversationId, Long userId, Integer page, Integer size) throws BusinessException;

    /**
     * 根据会话ID获取消息列表（时间范围）
     */
    List<MessageDTO> getMessagesByTimeRange(Long conversationId, Long userId, LocalDateTime startTime, LocalDateTime endTime) throws BusinessException;

    /**
     * 根据发送者ID获取消息列表
     */
    IPage<MessageDTO> getMessagesBySenderId(Long senderId, Long userId, Integer page, Integer size);

    /**
     * 根据消息类型获取消息列表
     */
    IPage<MessageDTO> getMessagesByType(String messageType, Long userId, Integer page, Integer size);

    /**
     * 获取会话中的新消息（指定时间之后）
     */
    List<MessageDTO> getNewMessages(Long conversationId, Long userId, LocalDateTime afterTime) throws BusinessException;

    /**
     * 获取父消息的回复列表
     */
    List<MessageDTO> getMessageReplies(Long parentMessageId, Long userId) throws BusinessException;

    /**
     * 编辑消息
     */
    MessageDTO editMessage(Long messageId, String newContent, Long userId) throws BusinessException;

    /**
     * 撤回消息
     */
    MessageDTO recallMessage(Long messageId, Long userId) throws BusinessException;

    /**
     * 删除消息（逻辑删除）
     */
    void deleteMessage(Long messageId, Long userId) throws BusinessException;

    /**
     * 批量删除消息
     */
    void batchDeleteMessages(List<Long> messageIds, Long userId) throws BusinessException;

    /**
     * 标记消息为已读
     */
    void markMessageAsRead(Long messageId, Long userId) throws BusinessException;

    /**
     * 批量标记消息为已读
     */
    void batchMarkMessagesAsRead(List<Long> messageIds, Long userId) throws BusinessException;

    /**
     * 标记会话中所有消息为已读
     */
    void markAllMessagesAsRead(Long conversationId, Long userId) throws BusinessException;

    /**
     * 转发消息
     */
    MessageDTO forwardMessage(Long messageId, Long targetConversationId, Long userId) throws BusinessException;

    /**
     * 复制消息到其他会话
     */
    MessageDTO copyMessage(Long messageId, Long targetConversationId, Long userId) throws BusinessException;

    /**
     * 引用消息
     */
    MessageDTO quoteMessage(Long messageId, Long targetConversationId, String additionalContent, Long userId) throws BusinessException;

    /**
     * 搜索消息
     */
    IPage<MessageDTO> searchMessages(String keyword, Long userId, Long conversationId, Integer page, Integer size);

    /**
     * 获取消息统计信息
     */
    MessageStatisticsDTO getMessageStatistics(Long conversationId, Long userId) throws BusinessException;

    /**
     * 获取用户的消息统计
     */
    UserMessageStatisticsDTO getUserMessageStatistics(Long userId);

    /**
     * 清理过期的临时消息（定时任务）
     */
    void cleanupExpiredMessages();

    /**
     * 重新发送失败的消息
     */
    MessageDTO resendMessage(Long messageId, Long userId) throws BusinessException;

    /**
     * 获取消息的已读用户列表
     */
    List<Long> getMessageReaders(Long messageId, Long userId) throws BusinessException;

    /**
     * 获取消息的未读用户列表
     */
    List<Long> getMessageUnreaders(Long messageId, Long userId) throws BusinessException;

    /**
     * 检查用户是否已读消息
     */
    boolean hasUserReadMessage(Long messageId, Long userId);

    /**
     * 更新消息的反应数量
     */
    void updateMessageReactionCount(Long messageId, int count);

    /**
     * 增加消息的已读人数
     */
    void increaseMessageReadCount(Long messageId);

    /**
     * 验证用户是否有权限访问消息
     */
    void validateMessageAccess(Long messageId, Long userId) throws BusinessException;

    /**
     * 检查消息是否存在且有效
     */
    boolean checkMessageExists(Long messageId);

    /**
     * 获取消息的会话ID
     */
    Long getMessageConversationId(Long messageId) throws BusinessException;

    /**
     * 保存消息草稿
     */
    MessageDTO saveMessageDraft(SendMessageRequest request, Long userId) throws BusinessException;

    /**
     * 获取用户的草稿消息列表
     */
    List<MessageDTO> getMessageDrafts(Long userId, Long conversationId);

    /**
     * 删除消息草稿
     */
    void deleteMessageDraft(Long draftId, Long userId) throws BusinessException;

    /**
     * 发送草稿消息
     */
    MessageDTO sendMessageDraft(Long draftId, Long userId) throws BusinessException;
}