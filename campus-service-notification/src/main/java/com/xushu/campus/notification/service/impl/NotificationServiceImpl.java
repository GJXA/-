package com.xushu.campus.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.constant.NotificationConstants;
import com.xushu.campus.notification.dto.*;
import com.xushu.campus.notification.entity.Notification;
import com.xushu.campus.notification.mapper.NotificationMapper;
import com.xushu.campus.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationDTO createAndSendNotification(CreateNotificationRequest request) throws BusinessException {
        // 验证请求数据
        if (!request.isValid()) {
            throw new BusinessException("通知请求数据无效");
        }

        // 创建通知实体
        Notification notification = new Notification();
        BeanUtils.copyProperties(request, notification);

        // 设置默认值
        if (notification.getStatus() == null) {
            notification.setStatus(NotificationConstants.NotificationStatus.UNREAD);
        }
        if (notification.getPriority() == null) {
            notification.setPriority(NotificationConstants.NotificationPriority.NORMAL);
        }
        if (notification.getChannel() == null) {
            notification.setChannel(NotificationConstants.NotificationChannel.IN_APP);
        }
        if (notification.getSendTime() == null) {
            notification.setSendTime(LocalDateTime.now());
        }

        // 设置发送状态
        if (notification.needSendEmail()) {
            notification.setEmailStatus(NotificationConstants.SendStatus.NOT_SENT);
        }
        if (notification.needSendSms()) {
            notification.setSmsStatus(NotificationConstants.SendStatus.NOT_SENT);
        }
        if (notification.needSendWebSocket()) {
            notification.setWebsocketStatus(NotificationConstants.SendStatus.NOT_SENT);
        }

        // 保存到数据库
        notificationMapper.insert(notification);

        // 转换为DTO
        NotificationDTO dto = convertToDTO(notification);

        // 发送通知（异步）
        sendNotificationAsync(notification.getId());

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotificationDTO createNotification(CreateNotificationRequest request) throws BusinessException {
        // 验证请求数据
        if (!request.isValid()) {
            throw new BusinessException("通知请求数据无效");
        }

        // 创建通知实体
        Notification notification = new Notification();
        BeanUtils.copyProperties(request, notification);

        // 设置默认值
        if (notification.getStatus() == null) {
            notification.setStatus(NotificationConstants.NotificationStatus.UNREAD);
        }
        if (notification.getPriority() == null) {
            notification.setPriority(NotificationConstants.NotificationPriority.NORMAL);
        }
        if (notification.getChannel() == null) {
            notification.setChannel(NotificationConstants.NotificationChannel.IN_APP);
        }
        if (notification.getSendTime() == null) {
            notification.setSendTime(LocalDateTime.now());
        }

        // 设置发送状态
        if (notification.needSendEmail()) {
            notification.setEmailStatus(NotificationConstants.SendStatus.NOT_SENT);
        }
        if (notification.needSendSms()) {
            notification.setSmsStatus(NotificationConstants.SendStatus.NOT_SENT);
        }
        if (notification.needSendWebSocket()) {
            notification.setWebsocketStatus(NotificationConstants.SendStatus.NOT_SENT);
        }

        // 保存到数据库
        notificationMapper.insert(notification);

        return convertToDTO(notification);
    }

    @Override
    public NotificationDTO getNotificationById(Long id) throws BusinessException {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || notification.getIsDeleted() == 1) {
            throw new BusinessException("通知不存在");
        }
        return convertToDTO(notification);
    }

    @Override
    public IPage<NotificationDTO> getNotificationsByUserId(Long userId, Integer page, Integer size) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getIsDeleted, 0)
               .orderByDesc(Notification::getSendTime);

        IPage<Notification> result = notificationMapper.selectPage(pageParam, wrapper);

        List<NotificationDTO> dtos = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<NotificationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public IPage<NotificationDTO> getNotificationsByUserIdAndStatus(Long userId, Integer status, Integer page, Integer size) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getStatus, status)
               .eq(Notification::getIsDeleted, 0)
               .orderByDesc(Notification::getSendTime);

        IPage<Notification> result = notificationMapper.selectPage(pageParam, wrapper);

        List<NotificationDTO> dtos = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<NotificationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public IPage<NotificationDTO> getNotificationsByUserIdAndType(Long userId, String type, Integer page, Integer size) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getType, type)
               .eq(Notification::getIsDeleted, 0)
               .orderByDesc(Notification::getSendTime);

        IPage<Notification> result = notificationMapper.selectPage(pageParam, wrapper);

        List<NotificationDTO> dtos = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<NotificationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public IPage<NotificationDTO> searchNotifications(NotificationSearchRequest request) {
        if (!request.isValid()) {
            Page<NotificationDTO> resultPage = new Page<>(request.getPage(), request.getSize(), 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        Page<Notification> pageParam = new Page<>(request.getPage(), request.getSize());

        // 构建查询条件
        LambdaQueryWrapper<Notification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Notification::getIsDeleted, 0);

        if (request.getUserId() != null) {
            wrapper.eq(Notification::getUserId, request.getUserId());
        }
        if (request.getType() != null) {
            wrapper.eq(Notification::getType, request.getType());
        }
        if (request.getStatus() != null) {
            wrapper.eq(Notification::getStatus, request.getStatus());
        }
        if (request.getPriority() != null) {
            wrapper.eq(Notification::getPriority, request.getPriority());
        }
        if (request.getChannel() != null) {
            wrapper.eq(Notification::getChannel, request.getChannel());
        }
        if (request.getStartTime() != null) {
            wrapper.ge(Notification::getSendTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(Notification::getSendTime, request.getEndTime());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Notification::getTitle, "%" + request.getKeyword() + "%")
                             .or().like(Notification::getContent, "%" + request.getKeyword() + "%")
                             .or().like(Notification::getSenderName, "%" + request.getKeyword() + "%"));
        }
        if (Boolean.TRUE.equals(request.getUnreadOnly())) {
            wrapper.eq(Notification::getStatus, NotificationConstants.NotificationStatus.UNREAD);
        }
        if (Boolean.TRUE.equals(request.getHighPriorityOnly())) {
            wrapper.ge(Notification::getPriority, NotificationConstants.NotificationPriority.HIGH);
        }
        if (Boolean.FALSE.equals(request.getIncludeExpired())) {
            wrapper.and(w -> w.isNull(Notification::getExpireTime)
                            .or().gt(Notification::getExpireTime, LocalDateTime.now()));
        }

        // 排序
        if ("priority".equals(request.getSortField())) {
            if ("asc".equals(request.getSortDirection())) {
                wrapper.orderByAsc(Notification::getPriority, Notification::getSendTime);
            } else {
                wrapper.orderByDesc(Notification::getPriority, Notification::getSendTime);
            }
        } else if ("create_time".equals(request.getSortField())) {
            if ("asc".equals(request.getSortDirection())) {
                wrapper.orderByAsc(Notification::getCreatedAt);
            } else {
                wrapper.orderByDesc(Notification::getCreatedAt);
            }
        } else {
            if ("asc".equals(request.getSortDirection())) {
                wrapper.orderByAsc(Notification::getSendTime);
            } else {
                wrapper.orderByDesc(Notification::getSendTime);
            }
        }

        IPage<Notification> result = notificationMapper.selectPage(pageParam, wrapper);

        List<NotificationDTO> dtos = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<NotificationDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long id, Long userId) throws BusinessException {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || notification.getIsDeleted() == 1) {
            throw new BusinessException("通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }

        if (notification.getStatus() == NotificationConstants.NotificationStatus.UNREAD) {
            notification.setStatus(NotificationConstants.NotificationStatus.READ);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long id, Long userId) throws BusinessException {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || notification.getIsDeleted() == 1) {
            throw new BusinessException("通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此通知");
        }

        notification.setIsDeleted(1);
        notificationMapper.updateById(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteNotifications(List<Long> ids, Long userId) throws BusinessException {
        for (Long id : ids) {
            deleteNotification(id, userId);
        }
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public void sendPendingNotifications() {
        // 查询需要发送的通知
        List<Notification> notifications = notificationMapper.selectNotificationsToSend();

        for (Notification notification : notifications) {
            try {
                sendNotification(notification.getId());
            } catch (Exception e) {
                log.error("发送通知失败，通知ID: {}", notification.getId(), e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(Long notificationId) throws BusinessException {
        // 实现发送逻辑（邮件、短信、WebSocket）
        // 这里先实现一个简单的占位符
        log.info("发送通知: {}", notificationId);

        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || notification.getIsDeleted() == 1) {
            throw new BusinessException("通知不存在");
        }

        // 更新状态为已发送
        notification.setStatus(NotificationConstants.NotificationStatus.READ);
        notificationMapper.updateById(notification);
    }

    @Override
    public void retryFailedNotification(Long notificationId) throws BusinessException {
        // 实现重试逻辑
        sendNotification(notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotificationStatus(Long id, Integer status) throws BusinessException {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || notification.getIsDeleted() == 1) {
            throw new BusinessException("通知不存在");
        }

        notification.setStatus(status);
        notificationMapper.updateById(notification);
    }

    @Override
    public boolean checkNotificationBelongsToUser(Long id, Long userId) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || notification.getIsDeleted() == 1) {
            return false;
        }
        return notification.getUserId().equals(userId);
    }

    @Override
    public List<NotificationDTO> getNotificationsByRelatedInfo(Long relatedId, String relatedType) {
        List<Notification> notifications = notificationMapper.selectByRelatedInfo(relatedId, relatedType);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationStatisticsDTO getNotificationStatistics(Long userId) {
        // 这里实现统计逻辑，简化返回
        NotificationStatisticsDTO statistics = new NotificationStatisticsDTO();
        statistics.setTotalCount(notificationMapper.selectCount(Wrappers.<Notification>lambdaQuery()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsDeleted, 0)));
        statistics.setUnreadCount(notificationMapper.countUnreadByUserId(userId));
        // 其他统计数据需要实现...
        return statistics;
    }

    @Override
    public void processExpiredNotifications() {
        List<Notification> expiredNotifications = notificationMapper.selectExpiredNotifications();
        for (Notification notification : expiredNotifications) {
            notification.setStatus(NotificationConstants.NotificationStatus.DELETED);
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public NotificationDTO sendInstantNotification(Long userId, String type, String title, String content,
                                                  Integer priority, String channel, Long relatedId,
                                                  String relatedType, String businessData) throws BusinessException {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setUserId(userId);
        request.setType(type);
        request.setTitle(title);
        request.setContent(content);
        request.setPriority(priority != null ? priority : NotificationConstants.NotificationPriority.NORMAL);
        request.setChannel(channel != null ? channel : NotificationConstants.NotificationChannel.IN_APP);
        request.setRelatedId(relatedId);
        request.setRelatedType(relatedType);
        request.setBusinessData(businessData);

        return createAndSendNotification(request);
    }

    /**
     * 异步发送通知
     */
    private void sendNotificationAsync(Long notificationId) {
        // 这里可以使用消息队列或异步任务
        // 简化实现：直接调用发送方法
        new Thread(() -> {
            try {
                sendNotification(notificationId);
            } catch (Exception e) {
                log.error("异步发送通知失败，通知ID: {}", notificationId, e);
            }
        }).start();
    }

    /**
     * 将Notification实体转换为NotificationDTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO dto = new NotificationDTO();
        BeanUtils.copyProperties(notification, dto);
        dto.calculateDesc();
        return dto;
    }
}