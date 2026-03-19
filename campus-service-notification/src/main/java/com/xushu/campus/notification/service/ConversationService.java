package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.notification.dto.ConversationDTO;
import com.xushu.campus.notification.dto.CreateConversationRequest;
import com.xushu.campus.notification.dto.ConversationMemberDTO;
import com.xushu.campus.notification.dto.ConversationStatisticsDTO;

import java.util.List;

/**
 * 会话服务接口
 */
public interface ConversationService {

    /**
     * 创建会话
     */
    ConversationDTO createConversation(CreateConversationRequest request, Long creatorId) throws BusinessException;

    /**
     * 根据ID获取会话详情
     */
    ConversationDTO getConversationById(Long id, Long userId) throws BusinessException;

    /**
     * 根据用户ID获取会话列表（分页）
     */
    IPage<ConversationDTO> getConversationsByUserId(Long userId, Integer page, Integer size);

    /**
     * 根据会话类型获取会话列表
     */
    List<ConversationDTO> getConversationsByType(String conversationType, Integer limit);

    /**
     * 更新会话信息
     */
    ConversationDTO updateConversation(Long id, ConversationDTO updateRequest, Long operatorId) throws BusinessException;

    /**
     * 删除会话（逻辑删除）
     */
    void deleteConversation(Long id, Long operatorId) throws BusinessException;

    /**
     * 添加成员到会话
     */
    ConversationMemberDTO addMemberToConversation(Long conversationId, Long userId, Long operatorId) throws BusinessException;

    /**
     * 批量添加成员到会话
     */
    List<ConversationMemberDTO> addMembersToConversation(Long conversationId, List<Long> userIds, Long operatorId) throws BusinessException;

    /**
     * 从会话中移除成员
     */
    void removeMemberFromConversation(Long conversationId, Long userId, Long operatorId) throws BusinessException;

    /**
     * 批量移除成员
     */
    void removeMembersFromConversation(Long conversationId, List<Long> userIds, Long operatorId) throws BusinessException;

    /**
     * 获取会话成员列表
     */
    List<ConversationMemberDTO> getConversationMembers(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取会话成员详情
     */
    ConversationMemberDTO getConversationMember(Long conversationId, Long userId, Long operatorId) throws BusinessException;

    /**
     * 更新会话成员信息（如昵称、角色等）
     */
    ConversationMemberDTO updateConversationMember(Long conversationId, Long userId, ConversationMemberDTO updateRequest, Long operatorId) throws BusinessException;

    /**
     * 退出会话
     */
    void leaveConversation(Long conversationId, Long userId) throws BusinessException;

    /**
     * 清空会话消息
     */
    void clearConversationMessages(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 将会话标记为已读
     */
    void markConversationAsRead(Long conversationId, Long userId) throws BusinessException;

    /**
     * 更新会话最后一条消息信息
     */
    void updateLastMessageInfo(Long conversationId, Long messageId, String messageContent) throws BusinessException;

    /**
     * 检查用户是否会话成员
     */
    boolean isConversationMember(Long conversationId, Long userId);

    /**
     * 检查用户是否有权限管理会话
     */
    boolean hasPermissionToManage(Long conversationId, Long userId);

    /**
     * 搜索会话
     */
    IPage<ConversationDTO> searchConversations(String keyword, Long userId, Integer page, Integer size);

    /**
     * 获取用户未读会话数量
     */
    Long countUnreadConversations(Long userId);

    /**
     * 获取会话统计信息
     */
    ConversationStatisticsDTO getConversationStatistics(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 归档会话
     */
    void archiveConversation(Long conversationId, Long userId) throws BusinessException;

    /**
     * 恢复已归档的会话
     */
    void restoreConversation(Long conversationId, Long userId) throws BusinessException;

    /**
     * 静音/取消静音会话
     */
    void muteConversation(Long conversationId, Long userId, boolean mute) throws BusinessException;

    /**
     * 置顶/取消置顶会话
     */
    void pinConversation(Long conversationId, Long userId, boolean pin) throws BusinessException;

    /**
     * 获取置顶会话列表
     */
    List<ConversationDTO> getPinnedConversations(Long userId);

    /**
     * 检查会话是否存在且有效
     */
    boolean checkConversationExists(Long conversationId);

    /**
     * 验证用户是否可以访问会话
     */
    void validateUserAccess(Long conversationId, Long userId) throws BusinessException;
}