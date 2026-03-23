/**
 * 订单相关类型定义
 */
import type { PageResult, PageParams } from './common'

/**
 * 订单状态
 */
export type OrderStatus = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8

// 状态说明
export const OrderStatusLabel: Record<OrderStatus, string> = {
  0: '待支付',
  1: '已支付',
  2: '已发货',
  3: '已送达',
  4: '已完成',
  5: '已取消',
  6: '退款中',
  7: '已退款',
  8: '支付失败'
}

/**
 * 支付方式
 */
export type PaymentMethod =
  | 'ALIPAY'
  | 'WECHAT_PAY'
  | 'BANK_TRANSFER'
  | 'CASH'
  | 'PLATFORM_BALANCE'

/**
 * 订单基本信息
 */
export interface Order {
  id: number
  orderNo: string
  productId: number
  productTitle?: string
  productImage?: string
  productPrice: number
  productOriginalPrice?: number
  quantity: number
  totalAmount: number
  discountAmount?: number
  actualAmount: number
  buyerId: number
  buyerName?: string
  buyerAvatar?: string
  sellerId: number
  sellerName?: string
  sellerAvatar?: string
  status: OrderStatus
  paymentMethod?: PaymentMethod
  paymentTime?: string
  paymentTransactionNo?: string
  shippingAddress?: string
  shippingTime?: string
  deliveryTime?: string
  completeTime?: string
  cancelTime?: string
  cancelReason?: string
  refundTime?: string
  refundReason?: string
  refundAmount?: number
  buyerNote?: string
  sellerNote?: string
  createTime: string
  updateTime: string
}

/**
 * 订单详情
 */
export interface OrderDetail extends Order {
  productDetails?: any // 商品详细信息
  shippingInfo?: ShippingInfo
  paymentInfo?: PaymentInfo
  refundInfo?: RefundInfo
  logs?: OrderLog[]
}

/**
 * 订单查询参数
 */
export interface OrderQueryParams extends PageParams {
  status?: OrderStatus
  orderNo?: string
  productTitle?: string
  buyerId?: number
  sellerId?: number
  startTime?: string
  endTime?: string
  sortField?: 'create_time' | 'update_time' | 'total_amount' | 'status'
  sortDirection?: 'asc' | 'desc'
}

/**
 * 订单创建请求
 */
export interface OrderCreateRequest {
  productId: number
  quantity: number
  shippingAddress: string
  buyerNote?: string
  paymentMethod?: PaymentMethod
}

/**
 * 订单支付请求
 */
export interface OrderPaymentRequest {
  orderId: number
  paymentMethod: PaymentMethod
  payPassword?: string
  payVoucher?: string
}

/**
 * 订单取消请求
 */
export interface OrderCancelRequest {
  orderId: number
  cancelReason: string
}

/**
 * 订单确认收货请求
 */
export interface OrderConfirmRequest {
  orderId: number
  confirmNote?: string
}

/**
 * 物流信息
 */
export interface ShippingInfo {
  shippingCompany: string
  trackingNo: string
  shippingAddress: string
  receiverName: string
  receiverPhone: string
  shippingTime: string
  estimatedDeliveryTime?: string
  actualDeliveryTime?: string
  shippingStatus: string
}

/**
 * 支付信息
 */
export interface PaymentInfo {
  paymentMethod: PaymentMethod
  paymentAmount: number
  paymentTime: string
  paymentTransactionNo: string
  paymentStatus: string
  payerId: number
  payerName?: string
}

/**
 * 退款信息
 */
export interface RefundInfo {
  refundAmount: number
  refundReason: string
  refundTime: string
  refundTransactionNo?: string
  refundStatus: string
  approverId?: number
  approveTime?: string
  approveNote?: string
}

/**
 * 订单日志
 */
export interface OrderLog {
  id: number
  orderId: number
  operatorId: number
  operatorName?: string
  action: string
  description: string
  oldStatus?: OrderStatus
  newStatus: OrderStatus
  ip?: string
  createTime: string
}

/**
 * 订单统计
 */
export interface OrderStats {
  totalOrders: number
  pendingOrders: number
  completedOrders: number
  cancelledOrders: number
  totalAmount: number
  avgOrderAmount: number
  statusDistribution: Record<string, number>
  dailyOrders: Array<{
    date: string
    count: number
    amount: number
  }>
}

/**
 * 订单搜索响应
 */
export type OrderSearchResult = PageResult<Order>