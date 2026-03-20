package com.xushu.campus.notification.controller;

import com.xushu.campus.common.annotation.LoginRequired;
import com.xushu.campus.common.annotation.RoleRequired;
import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.common.model.Result;
import com.xushu.campus.notification.dto.*;
import com.xushu.campus.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 通知管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@Tag(name = "通知管理", description = "通知信息的创建、查询、管理等接口")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "创建并发送通知", description = "创建通知并立即发送")
    @RoleRequired("ADMIN")
    public Result<NotificationDTO> createAndSendNotification(@Valid @RequestBody CreateNotificationRequest request) {
        try {
            NotificationDTO result = notificationService.createAndSendNotification(request);
            return Result.success("通知发送成功", result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建通知", description = "创建通知但不立即发送")
    @RoleRequired("ADMIN")
    public Result<NotificationDTO> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        try {
            NotificationDTO result = notificationService.createNotification(request);
            return Result.success("通知创建成功", result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知详情", description = "根据通知ID获取通知详情")
    @LoginRequired
    public Result<NotificationDTO> getNotificationById(@PathVariable Long id) {
        try {
            NotificationDTO result = notificationService.getNotificationById(id);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户通知列表", description = "根据用户ID获取通知列表（分页）")
    @LoginRequired
    public Result<IPage<NotificationDTO>> getNotificationsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<NotificationDTO> result = notificationService.getNotificationsByUserId(userId, page, size);
        return Result.success(result);
    }

    @GetMapping("/user/{userId}/status/{status}")
    @Operation(summary = "根据状态获取用户通知", description = "根据用户ID和状态获取通知列表（分页）")
    @LoginRequired
    public Result<IPage<NotificationDTO>> getNotificationsByUserIdAndStatus(
            @PathVariable Long userId,
            @PathVariable Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<NotificationDTO> result = notificationService.getNotificationsByUserIdAndStatus(userId, status, page, size);
        return Result.success(result);
    }

    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "根据类型获取用户通知", description = "根据用户ID和类型获取通知列表（分页）")
    @LoginRequired
    public Result<IPage<NotificationDTO>> getNotificationsByUserIdAndType(
            @PathVariable Long userId,
            @PathVariable String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<NotificationDTO> result = notificationService.getNotificationsByUserIdAndType(userId, type, page, size);
        return Result.success(result);
    }

    @PostMapping("/search")
    @Operation(summary = "搜索通知", description = "根据条件搜索通知（分页）")
    @LoginRequired
    public Result<IPage<NotificationDTO>> searchNotifications(@Valid @RequestBody NotificationSearchRequest request) {
        IPage<NotificationDTO> result = notificationService.searchNotifications(request);
        return Result.success(result);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记通知为已读", description = "将指定通知标记为已读")
    @LoginRequired
    public Result<Void> markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        try {
            notificationService.markAsRead(id, userId);
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/read-all")
    @Operation(summary = "标记所有通知为已读", description = "将用户的所有通知标记为已读")
    @LoginRequired
    public Result<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.<Void>success("全部标记为已读成功", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知", description = "删除指定通知（逻辑删除）")
    @LoginRequired
    public Result<Void> deleteNotification(@PathVariable Long id, @RequestParam Long userId) {
        try {
            notificationService.deleteNotification(id, userId);
            return Result.<Void>success("删除成功", null);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除通知", description = "批量删除通知（逻辑删除）")
    @LoginRequired
    public Result<Void> batchDeleteNotifications(@RequestBody List<Long> ids, @RequestParam Long userId) {
        try {
            notificationService.batchDeleteNotifications(ids, userId);
            return Result.<Void>success("批量删除成功", null);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "获取未读通知数量", description = "获取用户的未读通知数量")
    @LoginRequired
    public Result<Long> countUnreadNotifications(@PathVariable Long userId) {
        Long count = notificationService.countUnreadNotifications(userId);
        return Result.success(count);
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "发送通知", description = "发送指定的通知")
    @RoleRequired("ADMIN")
    public Result<Void> sendNotification(@PathVariable Long id) {
        try {
            notificationService.sendNotification(id);
            return Result.<Void>success("通知发送成功", null);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "重试发送失败的通知", description = "重新发送失败的通知")
    @RoleRequired("ADMIN")
    public Result<Void> retryFailedNotification(@PathVariable Long id) {
        try {
            notificationService.retryFailedNotification(id);
            return Result.<Void>success("重试发送成功", null);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/related/{relatedId}/{relatedType}")
    @Operation(summary = "根据关联信息获取通知", description = "根据关联的业务ID和类型获取通知列表")
    @LoginRequired
    public Result<List<NotificationDTO>> getNotificationsByRelatedInfo(
            @PathVariable Long relatedId,
            @PathVariable String relatedType) {
        List<NotificationDTO> result = notificationService.getNotificationsByRelatedInfo(relatedId, relatedType);
        return Result.success(result);
    }

    @GetMapping("/user/{userId}/statistics")
    @Operation(summary = "获取通知统计数据", description = "获取用户的通知统计数据")
    @LoginRequired
    public Result<NotificationStatisticsDTO> getNotificationStatistics(@PathVariable Long userId) {
        NotificationStatisticsDTO result = notificationService.getNotificationStatistics(userId);
        return Result.success(result);
    }

    @PostMapping("/instant")
    @Operation(summary = "发送即时通知", description = "发送即时通知的简化接口")
    @RoleRequired("ADMIN")
    public Result<NotificationDTO> sendInstantNotification(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) Long relatedId,
            @RequestParam(required = false) String relatedType,
            @RequestParam(required = false) String businessData) {
        try {
            NotificationDTO result = notificationService.sendInstantNotification(
                    userId, type, title, content, priority, channel, relatedId, relatedType, businessData);
            return Result.success("即时通知发送成功", result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/admin/send-pending")
    @Operation(summary = "发送待处理通知", description = "发送所有待处理的通知（管理员调用）")
    @RoleRequired("ADMIN")
    public Result<Void> sendPendingNotifications() {
        notificationService.sendPendingNotifications();
        return Result.<Void>success("开始发送待处理通知", null);
    }

    @PostMapping("/admin/process-expired")
    @Operation(summary = "处理过期通知", description = "处理所有已过期的通知（管理员调用）")
    @RoleRequired("ADMIN")
    public Result<Void> processExpiredNotifications() {
        notificationService.processExpiredNotifications();
        return Result.<Void>success("开始处理过期通知", null);
    }

    @GetMapping("/check/{id}/belongs-to/{userId}")
    @Operation(summary = "检查通知是否属于用户", description = "检查指定通知是否属于指定用户")
    @LoginRequired
    public Result<Boolean> checkNotificationBelongsToUser(@PathVariable Long id, @PathVariable Long userId) {
        boolean result = notificationService.checkNotificationBelongsToUser(id, userId);
        return Result.success(result);
    }
}