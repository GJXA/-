package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.notification.dto.MessageReadStatusDTO;
import com.xushu.campus.notification.dto.ReadStatisticsDTO;
import com.xushu.campus.notification.dto.UserReadStatisticsDTO;
import com.xushu.campus.notification.dto.ReadDelayStatisticsDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息阅读状态服务接口
 */
public interface MessageReadStatusService {

    /**
     * 记录消息阅读状态
     */
    MessageReadStatusDTO recordMessageRead(Long messageId, Long userId) throws BusinessException;

    /**
     * 批量记录消息阅读状态
     */
    List<MessageReadStatusDTO> batchRecordMessageRead(List<Long> messageIds, Long userId) throws BusinessException;

    /**
     * 获取消息的阅读状态列表
     */
    List<MessageReadStatusDTO> getMessageReadStatus(Long messageId, Long operatorId) throws BusinessException;

    /**
     * 获取消息的阅读状态列表（分页）
     */
    IPage<MessageReadStatusDTO> getMessageReadStatusPage(Long messageId, Long operatorId, Integer page, Integer size) throws BusinessException;

    /**
     * 获取用户的消息阅读记录
     */
    List<MessageReadStatusDTO> getUserMessageReadRecords(Long userId, Integer limit);

    /**
     * 获取用户的消息阅读记录（分页）
     */
    IPage<MessageReadStatusDTO> getUserMessageReadRecordsPage(Long userId, Integer page, Integer size);

    /**
     * 获取用户指定时间范围内的阅读记录
     */
    List<MessageReadStatusDTO> getUserReadRecordsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取消息的已读用户列表
     */
    List<Long> getMessageReaders(Long messageId, Long operatorId) throws BusinessException;

    /**
     * 获取消息的未读用户列表
     */
    List<Long> getMessageUnreaders(Long messageId, Long operatorId) throws BusinessException;

    /**
     * 检查用户是否已读消息
     */
    boolean hasUserReadMessage(Long messageId, Long userId);

    /**
     * 获取用户已读消息数量
     */
    Long countUserReadMessages(Long userId);

    /**
     * 获取消息已读用户数量
     */
    Long countMessageReaders(Long messageId) throws BusinessException;

    /**
     * 获取消息未读用户数量
     */
    Long countMessageUnreaders(Long messageId) throws BusinessException;

    /**
     * 获取消息阅读率（已读/总成员）
     */
    Double getMessageReadRate(Long messageId) throws BusinessException;

    /**
     * 获取会话的平均阅读率
     */
    Double getConversationAverageReadRate(Long conversationId) throws BusinessException;

    /**
     * 获取用户的平均阅读时间
     */
    Double getUserAverageReadTime(Long userId);

    /**
     * 获取消息的平均阅读时间（从发送到阅读）
     */
    Double getMessageAverageReadTime(Long messageId) throws BusinessException;

    /**
     * 获取最早阅读的用户
     */
    MessageReadStatusDTO getEarliestReader(Long messageId, Long operatorId) throws BusinessException;

    /**
     * 获取最晚阅读的用户
     */
    MessageReadStatusDTO getLatestReader(Long messageId, Long operatorId) throws BusinessException;

    /**
     * 获取阅读统计信息
     */
    ReadStatisticsDTO getReadStatistics(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取用户的阅读统计
     */
    UserReadStatisticsDTO getUserReadStatistics(Long userId);

    /**
     * 清除过期的阅读记录（定时任务）
     */
    void cleanupExpiredReadRecords();

    /**
     * 导出阅读记录
     */
    List<MessageReadStatusDTO> exportReadRecords(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 批量删除阅读记录
     */
    void batchDeleteReadRecords(List<Long> recordIds, Long operatorId) throws BusinessException;

    /**
     * 验证阅读记录访问权限
     */
    void validateReadRecordAccess(Long recordId, Long userId) throws BusinessException;

    /**
     * 获取阅读记录详情
     */
    MessageReadStatusDTO getReadRecordById(Long recordId, Long userId) throws BusinessException;

    /**
     * 获取最近阅读的消息
     */
    List<MessageReadStatusDTO> getRecentlyReadMessages(Long userId, Integer limit);

    /**
     * 获取未阅读的消息提醒
     */
    List<Long> getUnreadMessageReminders(Long userId, Integer hoursThreshold);

    /**
     * 标记消息为已读并返回阅读时间
     */
    LocalDateTime markMessageAsReadAndGetTime(Long messageId, Long userId) throws BusinessException;

    /**
     * 检查消息是否被所有成员阅读
     */
    boolean isMessageReadByAll(Long messageId) throws BusinessException;

    /**
     * 获取阅读延迟统计（消息发送到阅读的时间）
     */
    ReadDelayStatisticsDTO getReadDelayStatistics(Long conversationId, Long operatorId) throws BusinessException;

    /**
     * 获取活跃阅读者（最近24小时阅读消息的用户）
     */
    List<Long> getActiveReaders(Long conversationId, Long operatorId) throws BusinessException;
}