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
  NotificationStats,
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
  getUserNotifications(userId: number, params?: Omit<NotificationQueryParams, 'userId'>) {
    return request.get<PageResult<Notification>>(`/api/notifications/user/${userId}`, { params })
  },

  /**
   * 获取通知详情
   */
  getNotificationDetail(id: number) {
    return request.get<Notification>(`/api/notifications/${id}`)
  },

  /**
   * 创建通知（管理员）
   */
  createNotification(data: NotificationCreateRequest) {
    return request.post<Notification>('/api/notifications', data)
  },

  /**
   * 批量创建通知（管理员）
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
    return request.post<NotificationActionResponse>('/api/notifications/batch-delete', {
      notificationIds: ids
    })
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
    return request.put<NotificationActionResponse>(`/api/notifications/user/${userId}/read-all`)
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
    return request.get<NotificationStats>(`/api/notifications/user/${userId}/statistics`)
  }
}

export default notificationApi
