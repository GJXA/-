package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.notification.dto.CreateNotificationRequest;
import com.xushu.campus.notification.dto.NotificationDTO;
import com.xushu.campus.notification.dto.NotificationStatisticsDTO;
import com.xushu.campus.notification.dto.NotificationSearchRequest;
import com.xushu.campus.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 创建并发送通知
     */
    NotificationDTO createAndSendNotification(CreateNotificationRequest request) throws BusinessException;

    /**
     * 创建通知（不立即发送）
     */
    NotificationDTO createNotification(CreateNotificationRequest request) throws BusinessException;

    /**
     * 根据ID获取通知详情
     */
    NotificationDTO getNotificationById(Long id) throws BusinessException;

    /**
     * 根据用户ID获取通知列表（分页）
     */
    IPage<NotificationDTO> getNotificationsByUserId(Long userId, Integer page, Integer size);

    /**
     * 根据用户ID和状态获取通知列表（分页）
     */
    IPage<NotificationDTO> getNotificationsByUserIdAndStatus(Long userId, Integer status, Integer page, Integer size);

    /**
     * 根据用户ID和类型获取通知列表（分页）
     */
    IPage<NotificationDTO> getNotificationsByUserIdAndType(Long userId, String type, Integer page, Integer size);

    /**
     * 搜索通知
     */
    IPage<NotificationDTO> searchNotifications(NotificationSearchRequest request);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long id, Long userId) throws BusinessException;

    /**
     * 批量标记通知为已读
     */
    void markAllAsRead(Long userId) throws BusinessException;

    /**
     * 删除通知（逻辑删除）
     */
    void deleteNotification(Long id, Long userId) throws BusinessException;

    /**
     * 批量删除通知
     */
    void batchDeleteNotifications(List<Long> ids, Long userId) throws BusinessException;

    /**
     * 获取用户未读通知数量
     */
    Long countUnreadNotifications(Long userId);

    /**
     * 发送待处理的通知（定时任务调用）
     */
    void sendPendingNotifications();

    /**
     * 发送单个通知（处理邮件、短信、WebSocket等）
     */
    void sendNotification(Long notificationId) throws BusinessException;

    /**
     * 重新发送失败的通知
     */
    void retryFailedNotification(Long notificationId) throws BusinessException;

    /**
     * 更新通知状态
     */
    void updateNotificationStatus(Long id, Integer status) throws BusinessException;

    /**
     * 检查通知是否存在且属于用户
     */
    boolean checkNotificationBelongsToUser(Long id, Long userId);

    /**
     * 根据关联业务信息获取通知
     */
    List<NotificationDTO> getNotificationsByRelatedInfo(Long relatedId, String relatedType);

    /**
     * 获取通知统计数据
     */
    NotificationStatisticsDTO getNotificationStatistics(Long userId);

    /**
     * 处理过期的通知（定时任务调用）
     */
    void processExpiredNotifications();

    /**
     * 发送即时通知（简化方法）
     */
    NotificationDTO sendInstantNotification(Long userId, String type, String title, String content,
                                          Integer priority, String channel, Long relatedId,
                                          String relatedType, String businessData) throws BusinessException;
}