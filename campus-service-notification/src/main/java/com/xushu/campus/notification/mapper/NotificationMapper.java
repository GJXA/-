package com.xushu.campus.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知信息Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 根据用户ID查询通知列表
     */
    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND deleted = 0 ORDER BY send_time DESC")
    List<Notification> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询通知列表
     */
    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND status = #{status} AND deleted = 0 ORDER BY send_time DESC")
    List<Notification> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据用户ID和类型查询通知列表
     */
    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND type = #{type} AND deleted = 0 ORDER BY send_time DESC")
    List<Notification> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 查询用户未读通知数量
     */
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND status = 0 AND deleted = 0")
    Long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 根据优先级查询通知列表
     */
    @Select("SELECT * FROM notifications WHERE priority = #{priority} AND deleted = 0 AND status = 0 ORDER BY send_time DESC")
    List<Notification> selectByPriority(@Param("priority") Integer priority);

    /**
     * 查询过期的通知
     */
    @Select("SELECT * FROM notifications WHERE expire_time < NOW() AND deleted = 0 AND status != 2 ORDER BY expire_time DESC")
    List<Notification> selectExpiredNotifications();

    /**
     * 查询需要发送的通知（状态为未发送且发送时间已到或为空）
     */
    @Select("SELECT * FROM notifications WHERE status = 0 AND deleted = 0 AND (send_time IS NULL OR send_time <= NOW()) ORDER BY priority DESC, send_time ASC")
    List<Notification> selectNotificationsToSend();

    /**
     * 标记通知为已读
     */
    @Select("UPDATE notifications SET status = 1, read_time = NOW(), update_time = NOW() WHERE id = #{id} AND deleted = 0 AND status = 0")
    int markAsRead(@Param("id") Long id);

    /**
     * 批量标记通知为已读
     */
    @Select("UPDATE notifications SET status = 1, read_time = NOW(), update_time = NOW() WHERE user_id = #{userId} AND status = 0 AND deleted = 0")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 更新邮件发送状态
     */
    @Select("UPDATE notifications SET email_status = #{emailStatus}, email_failure_reason = #{emailFailureReason}, email_retry_count = #{emailRetryCount}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateEmailStatus(@Param("id") Long id, @Param("emailStatus") Integer emailStatus,
                         @Param("emailFailureReason") String emailFailureReason,
                         @Param("emailRetryCount") Integer emailRetryCount);

    /**
     * 更新短信发送状态
     */
    @Select("UPDATE notifications SET sms_status = #{smsStatus}, sms_failure_reason = #{smsFailureReason}, sms_retry_count = #{smsRetryCount}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateSmsStatus(@Param("id") Long id, @Param("smsStatus") Integer smsStatus,
                       @Param("smsFailureReason") String smsFailureReason,
                       @Param("smsRetryCount") Integer smsRetryCount);

    /**
     * 更新WebSocket发送状态
     */
    @Select("UPDATE notifications SET websocket_status = #{websocketStatus}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateWebSocketStatus(@Param("id") Long id, @Param("websocketStatus") Integer websocketStatus);

    /**
     * 根据关联业务ID和类型查询通知
     */
    @Select("SELECT * FROM notifications WHERE related_id = #{relatedId} AND related_type = #{relatedType} AND deleted = 0 ORDER BY send_time DESC")
    List<Notification> selectByRelatedInfo(@Param("relatedId") Long relatedId, @Param("relatedType") String relatedType);

    /**
     * 统计各类通知数量
     */
    @Select("SELECT type, COUNT(*) as count FROM notifications WHERE user_id = #{userId} AND deleted = 0 GROUP BY type")
    List<java.util.Map<String, Object>> countByType(@Param("userId") Long userId);

    /**
     * 根据条件搜索通知
     */
    List<Notification> searchNotifications(@Param("userId") Long userId, @Param("type") String type,
                                          @Param("status") Integer status, @Param("priority") Integer priority,
                                          @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                          @Param("keyword") String keyword);
}