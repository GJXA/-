/**
 * 通知服务API
 */
import request from './request'
import type {
  PageResult
} from '@/types/common'
import type {
  Notification,
  NotificationQueryParams,
  NotificationCreateRequest,
  BatchNotificationCreateRequest,
  NotificationUpdateRequest,
  MarkAsReadRequest,
  NotificationStats,
  NotificationTemplate,
  NotificationSettings,
  UnreadCountResponse,
  NotificationActionResponse
} from '@/types/notification'

/**
 * 通知服务API
 */
export const notificationApi = {
  /**
   * 获取用户通知列表
   */
  getUserNotifications(params: NotificationQueryParams) {
    return request.get<PageResult<Notification>>('/api/notifications/user', { params })
  },

  /**
   * 获取通知详情
   */
  getNotificationDetail(id: number) {
    return request.get<Notification>(`/api/notifications/${id}`)
  },

  /**
   * 创建通知
   */
  createNotification(data: NotificationCreateRequest) {
    return request.post<Notification>('/api/notifications', data)
  },

  /**
   * 批量创建通知
   */
  createNotificationsBatch(data: BatchNotificationCreateRequest) {
    return request.post<NotificationActionResponse>('/api/notifications/batch', data)
  },

  /**
   * 更新通知
   */
  updateNotification(id: number, data: NotificationUpdateRequest) {
    return request.put<Notification>(`/api/notifications/${id}`, data)
  },

  /**
   * 删除通知
   */
  deleteNotification(id: number) {
    return request.delete<boolean>(`/api/notifications/${id}`)
  },

  /**
   * 批量删除通知
   */
  deleteNotifications(ids: number[]) {
    return request.delete<NotificationActionResponse>('/api/notifications/batch', {
      data: { ids }
    })
  },

  /**
   * 标记为已读
   */
  markAsRead(data: MarkAsReadRequest) {
    return request.put<NotificationActionResponse>('/api/notifications/mark-read', data)
  },

  /**
   * 标记单个通知为已读
   */
  markNotificationAsRead(id: number, userId: number) {
    return request.put<Notification>(`/api/notifications/${id}/read`, null, {
      params: { userId }
    })
  },

  /**
   * 标记所有通知为已读
   */
  markAllAsRead(userId: number) {
    return request.put<NotificationActionResponse>(`/api/notifications/user/${userId}/mark-all-read`)
  },

  /**
   * 获取未读数量
   */
  getUnreadCount(userId: number) {
    return request.get<UnreadCountResponse>(`/api/notifications/user/${userId}/unread-count`)
  },

  /**
   * 获取通知统计
   */
  getNotificationStats(userId: number) {
    return request.get<NotificationStats>(`/api/notifications/user/${userId}/stats`)
  },

  /**
   * 获取通知模板列表
   */
  getNotificationTemplates(params: { page?: number; size?: number; keyword?: string }) {
    return request.get<PageResult<NotificationTemplate>>('/api/notifications/templates', { params })
  },

  /**
   * 获取通知模板详情
   */
  getNotificationTemplate(id: number) {
    return request.get<NotificationTemplate>(`/api/notifications/templates/${id}`)
  },

  /**
   * 获取用户通知设置
   */
  getNotificationSettings(userId: number) {
    return request.get<NotificationSettings>(`/api/notifications/settings/${userId}`)
  },

  /**
   * 更新通知设置
   */
  updateNotificationSettings(userId: number, data: Partial<NotificationSettings>) {
    return request.put<NotificationSettings>(`/api/notifications/settings/${userId}`, data)
  },

  /**
   * 发送测试通知
   */
  sendTestNotification(userId: number, templateCode: string, variables?: Record<string, any>) {
    return request.post<boolean>('/api/notifications/send-test', {
      userId,
      templateCode,
      variables
    })
  },

  /**
   * 清理过期通知
   */
  cleanupExpiredNotifications(userId?: number) {
    return request.delete<NotificationActionResponse>('/api/notifications/cleanup-expired', {
      params: { userId }
    })
  }
}

export default notificationApi