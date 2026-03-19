package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.dto.ConversationMemberDTO;
import com.xushu.campus.notification.dto.MemberStatisticsDTO;
import com.xushu.campus.notification.dto.RoleDistributionDTO;

import java.util.List;

/**
 * 会话成员服务接口
 */
public interface ConversationMemberService {

    /**
     * 获取会话成员详情
     */
    ConversationMemberDTO getConversationMember(Long conversationId, Long userId, Long operatorId) throws BusinessException;

    /**
     * 获取会话所有成员列表
     */
    List<ConversationMemberDTO> getAllConversationMembers(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取活跃成员列表（未离开的成员）
     */
    List<ConversationMemberDTO> getActiveConversationMembers(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 根据用户ID获取成员参与的会话列表
     */
    List<ConversationMemberDTO> getConversationMembersByUserId(Long userId, Integer limit);

    /**
     * 更新成员在会话中的昵称
     */
    ConversationMemberDTO updateMemberNickname(Long conversationId, Long userId, String nickname, Long operatorId) throws BusinessException;

    /**
     * 更新成员角色
     */
    ConversationMemberDTO updateMemberRole(Long conversationId, Long userId, String newRole, Long operatorId) throws BusinessException;

    /**
     * 静音/取消静音成员
     */
    ConversationMemberDTO toggleMemberMute(Long conversationId, Long userId, boolean mute, Long operatorId) throws BusinessException;

    /**
     * 更新成员的未读消息数
     */
    void updateMemberUnreadCount(Long conversationId, Long userId, Integer delta) throws BusinessException;

    /**
     * 重置成员的未读消息数
     */
    void resetMemberUnreadCount(Long conversationId, Long userId, Long lastReadMessageId) throws BusinessException;

    /**
     * 获取成员的未读消息数
     */
    Integer getMemberUnreadCount(Long conversationId, Long userId) throws BusinessException;

    /**
     * 更新成员的最后读取消息
     */
    void updateMemberLastReadMessage(Long conversationId, Long userId, Long lastReadMessageId) throws BusinessException;

    /**
     * 检查用户是否是会话成员
     */
    boolean isConversationMember(Long conversationId, Long userId);

    /**
     * 检查用户是否有指定角色
     */
    boolean hasMemberRole(Long conversationId, Long userId, String role);

    /**
     * 检查用户是否有管理权限
     */
    boolean hasManagementPermission(Long conversationId, Long userId);

    /**
     * 获取会话成员数量
     */
    Integer getConversationMemberCount(Long conversationId) throws BusinessException;

    /**
     * 获取活跃成员数量
     */
    Integer getActiveMemberCount(Long conversationId) throws BusinessException;

    /**
     * 获取在线成员列表（需要集成在线状态服务）
     */
    List<ConversationMemberDTO> getOnlineMembers(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取离线成员列表
     */
    List<ConversationMemberDTO> getOfflineMembers(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取最近活跃的成员
     */
    List<ConversationMemberDTO> getRecentlyActiveMembers(Long conversationId, Long operatorId, Integer limit) throws BusinessException;

    /**
     * 搜索会话成员
     */
    List<ConversationMemberDTO> searchMembers(Long conversationId, String keyword, Long operatorId) throws BusinessException;

    /**
     * 获取成员统计信息
     */
    MemberStatisticsDTO getMemberStatistics(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 导出成员列表
     */
    List<ConversationMemberDTO> exportMembers(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 批量更新成员信息
     */
    List<ConversationMemberDTO> batchUpdateMembers(Long conversationId, List<Long> userIds, ConversationMemberDTO updateRequest, Long operatorId) throws BusinessException;

    /**
     * 验证成员访问权限
     */
    void validateMemberAccess(Long conversationId, Long userId) throws BusinessException;

    /**
     * 获取成员加入时间
     */
    String getMemberJoinTimeDesc(Long conversationId, Long userId) throws BusinessException;

    /**
     * 获取成员活跃度（基于消息发送频率）
     */
    Double getMemberActivityLevel(Long conversationId, Long userId) throws BusinessException;

    /**
     * 获取成员角色分布
     */
    RoleDistributionDTO getRoleDistribution(Long conversationId, Long operatorId) throws BusinessException;
}