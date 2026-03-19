package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.notification.dto.AddReactionRequest;
import com.xushu.campus.notification.dto.MessageReactionDTO;
import com.xushu.campus.notification.dto.ReactionStatisticsDTO;
import com.xushu.campus.notification.dto.ConversationReactionStatisticsDTO;
import com.xushu.campus.notification.dto.UserReactionStatisticsDTO;
import com.xushu.campus.notification.dto.ReactionTypeDistributionDTO;
import com.xushu.campus.notification.dto.ReactionTrendDTO;
import com.xushu.campus.notification.dto.ReactionAnalysisReportDTO;
import com.xushu.campus.notification.dto.ReactionNetworkDTO;

import java.util.List;

/**
 * 消息反应服务接口
 */
public interface MessageReactionService {

    /**
     * 添加消息反应
     */
    MessageReactionDTO addReaction(AddReactionRequest request, Long userId) throws BusinessException;

    /**
     * 移除消息反应
     */
    void removeReaction(Long messageId, Long userId) throws BusinessException;

    /**
     * 移除指定反应
     */
    void removeSpecificReaction(Long messageId, Long userId, String reactionType) throws BusinessException;

    /**
     * 切换消息反应（如果已存在则移除，否则添加）
     */
    MessageReactionDTO toggleReaction(Long messageId, String reactionType, Long userId) throws BusinessException;

    /**
     * 获取消息的所有反应
     */
    List<MessageReactionDTO> getMessageReactions(Long messageId, Long operatorId) throws BusinessException;

    /**
     * 获取消息的反应列表（分页）
     */
    IPage<MessageReactionDTO> getMessageReactionsPage(Long messageId, Long operatorId, Integer page, Integer size) throws BusinessException;

    /**
     * 获取用户对消息的反应
     */
    MessageReactionDTO getUserReactionToMessage(Long messageId, Long userId, Long operatorId) throws BusinessException;

    /**
     * 获取用户的所有反应记录
     */
    List<MessageReactionDTO> getUserReactions(Long userId, Integer limit);

    /**
     * 获取用户的所有反应记录（分页）
     */
    IPage<MessageReactionDTO> getUserReactionsPage(Long userId, Integer page, Integer size);

    /**
     * 获取消息的反应统计
     */
    ReactionStatisticsDTO getMessageReactionStatistics(Long messageId, Long operatorId) throws BusinessException;

    /**
     * 获取会话的反应统计
     */
    ConversationReactionStatisticsDTO getConversationReactionStatistics(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取用户反应统计
     */
    UserReactionStatisticsDTO getUserReactionStatistics(Long userId);

    /**
     * 获取热门反应消息（按反应数量排序）
     */
    List<MessageReactionDTO> getTopReactedMessages(Long conversationId, Long operatorId, Integer limit) throws BusinessException;

    /**
     * 获取最常见的反应类型
     */
    String getMostCommonReactionType(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取反应类型分布
     */
    ReactionTypeDistributionDTO getReactionTypeDistribution(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 批量添加反应
     */
    List<MessageReactionDTO> batchAddReactions(List<AddReactionRequest> requests, Long userId) throws BusinessException;

    /**
     * 批量移除反应
     */
    void batchRemoveReactions(List<Long> messageIds, Long userId) throws BusinessException;

    /**
     * 检查用户是否对消息有反应
     */
    boolean hasUserReactedToMessage(Long messageId, Long userId);

    /**
     * 检查用户是否对消息有特定反应
     */
    boolean hasUserReactedWithType(Long messageId, Long userId, String reactionType);

    /**
     * 获取消息的反应数量
     */
    Long countMessageReactions(Long messageId) throws BusinessException;

    /**
     * 获取消息的特定反应数量
     */
    Long countMessageReactionsByType(Long messageId, String reactionType) throws BusinessException;

    /**
     * 获取用户的反应数量
     */
    Long countUserReactions(Long userId);

    /**
     * 获取用户给出的特定反应数量
     */
    Long countUserReactionsByType(Long userId, String reactionType);

    /**
     * 获取用户收到的反应数量
     */
    Long countReactionsReceivedByUser(Long userId);

    /**
     * 获取用户收到的特定反应数量
     */
    Long countReactionsReceivedByUserAndType(Long userId, String reactionType);

    /**
     * 清除过期的反应记录（定时任务）
     */
    void cleanupExpiredReactions();

    /**
     * 导出反应记录
     */
    List<MessageReactionDTO> exportReactions(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 批量删除反应记录
     */
    void batchDeleteReactions(List<Long> reactionIds, Long operatorId) throws BusinessException;

    /**
     * 验证反应记录访问权限
     */
    void validateReactionAccess(Long reactionId, Long userId) throws BusinessException;

    /**
     * 获取反应记录详情
     */
    MessageReactionDTO getReactionById(Long reactionId, Long userId) throws BusinessException;

    /**
     * 获取最近的反应记录
     */
    List<MessageReactionDTO> getRecentReactions(Long userId, Integer limit);

    /**
     * 获取反应趋势（最近7天）
     */
    ReactionTrendDTO getReactionTrend(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取反应最多的用户
     */
    List<Long> getMostReactiveUsers(Long conversationId, Long operatorId, Integer limit) throws BusinessException;

    /**
     * 获取最受欢迎的消息（按反应数量）
     */
    List<Long> getMostPopularMessages(Long conversationId, Long operatorId, Integer limit) throws BusinessException;

    /**
     * 更新反应表情
     */
    MessageReactionDTO updateReactionEmoji(Long reactionId, String newEmoji, Long userId) throws BusinessException;

    /**
     * 获取反应分析报告
     */
    ReactionAnalysisReportDTO getReactionAnalysisReport(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取反应互动网络（用户之间的反应关系）
     */
    ReactionNetworkDTO getReactionNetwork(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 检查反应是否存在
     */
    boolean checkReactionExists(Long reactionId);

    /**
     * 获取反应的影响分数（基于反应类型和数量）
     */
    Double getReactionImpactScore(Long messageId) throws BusinessException;
}