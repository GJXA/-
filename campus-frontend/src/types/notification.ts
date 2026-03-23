/**
 * 通知相关类型定义
 */
import type { PageResult, PageParams } from './common'

/**
 * 通知类型
 */
export type NotificationType =
  | 'SYSTEM'                // 系统通知
  | 'ORDER'                 // 订单通知
  | 'PRODUCT'               // 商品通知
  | 'JOB'                   // 兼职通知
  | 'MESSAGE'               // 消息通知
  | 'ACTIVITY'              // 活动通知
  | 'REMINDER'              // 提醒通知

/**
 * 通知状态
 */
export type NotificationStatus =
  | 'UNREAD'                // 未读
  | 'READ'                  // 已读
  | 'DELETED'               // 已删除

/**
 * 通知优先级
 */
export type NotificationPriority =
  | 'LOW'                      // 低
  | 'NORMAL'                   // 正常
  | 'HIGH'                     // 高
  | 'URGENT'                   // 紧急

/**
 * 通知基本信息
 */
export interface Notification {
  id: number
  userId: number
  type: NotificationType
  title: string
  content: string
  data?: any                        // 附加数据，JSON格式
  status: NotificationStatus
  priority: NotificationPriority
  readTime?: string
  expireTime?: string
  createTime: string
  updateTime: string
}

/**
 * 通知查询参数
 */
export interface NotificationQueryParams extends PageParams {
  userId: number
  type?: NotificationType
  status?: NotificationStatus
  priority?: NotificationPriority
  startTime?: string
  endTime?: string
  keyword?: string
}

/**
 * 通知创建请求
 */
export interface NotificationCreateRequest {
  userId: number
  type: NotificationType
  title: string
  content: string
  data?: any
  priority?: NotificationPriority
  expireTime?: string
}

/**
 * 批量通知创建请求
 */
export interface BatchNotificationCreateRequest {
  userIds: number[]
  type: NotificationType
  title: string
  content: string
  data?: any
  priority?: NotificationPriority
  expireTime?: string
}

/**
 * 通知更新请求
 */
export interface NotificationUpdateRequest {
  status?: NotificationStatus
  readTime?: string
}

/**
 * 标记为已读请求
 */
export interface MarkAsReadRequest {
  notificationIds: number[]
  userId: number
}

/**
 * 通知统计
 */
export interface NotificationStats {
  totalCount: number
  unreadCount: number
  readCount: number
  typeDistribution: Record<string, number>
  priorityDistribution: Record<string, number>
  dailyCount: Array<{
    date: string
    count: number
  }>
}

/**
 * 通知模板
 */
export interface NotificationTemplate {
  id: number
  code: string
  name: string
  type: NotificationType
  titleTemplate: string
  contentTemplate: string
  description?: string
  variables?: string[]              // 模板变量列表
  status: number
  createTime: string
  updateTime: string
}

/**
 * 消息推送设置
 */
export interface NotificationSettings {
  userId: number
  emailEnabled: boolean
  smsEnabled: boolean
  pushEnabled: boolean
  webEnabled: boolean
  quietHoursStart?: string          // 免打扰开始时间
  quietHoursEnd?: string            // 免打扰结束时间
  notificationTypes: Record<string, boolean>  // 各类通知的启用状态
  createTime: string
  updateTime: string
}

/**
 * 通知搜索响应
 */
export type NotificationSearchResult = PageResult<Notification>

/**
 * 未读数量响应
 */
export interface UnreadCountResponse {
  totalUnread: number
  typeUnread: Record<string, number>
}

/**
 * 通知操作响应
 */
export interface NotificationActionResponse {
  success: boolean
  message?: string
  affectedCount?: number
}