import request from './request'
import type { LoginResponse, UserInfo, RegisterRequest } from '../types/user'
import type { PageResult } from '../types/common'
import type { Product, ProductQueryParams, ProductCreateRequest, ProductCategory } from '../types/product'
import type { Order, OrderQueryParams, OrderCreateRequest, OrderStatus, PaymentMethod } from '../types/order'
import type { Job, JobQueryParams, JobCreateRequest, JobApplication, JobApplicationRequest } from '../types/job'
import { notificationApi } from './notification'
import { uploadApi } from './upload'

// 用户相关 API
export const userApi = {
  /**
   * 用户登录
   */
  login(data: { username: string; password: string }): Promise<LoginResponse> {
    return request.post('/api/users/login', data)
  },

  /**
   * 用户注册
   */
  register(data: RegisterRequest): Promise<UserInfo> {
    return request.post('/api/users/register', data)
  },

  /**
   * 获取用户信息
   */
  getUserInfo(): Promise<UserInfo> {
    return request.get('/api/users/profile')
  },

  /**
   * 更新用户信息
   */
  updateUserInfo(data: Partial<UserInfo>): Promise<UserInfo> {
    return request.put('/api/users/profile', data)
  },

  /**
   * 修改密码
   */
  changePassword(userId: number, oldPassword: string, newPassword: string): Promise<boolean> {
    return request.put(`/api/users/${userId}/password`, null, {
      params: { oldPassword, newPassword }
    })
  },

  /**
   * 上传头像
   */
  uploadAvatar(file: File): Promise<{ avatarUrl: string }> {
    const formData = new FormData()
    formData.append('avatar', file)
    return request.post('/api/users/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 获取用户统计
   */
  getUserStats(userId: number): Promise<any> {
    return request.get(`/api/users/${userId}/stats`)
  }
}

// 商品相关 API
export const productApi = {
  /**
   * 获取商品列表（分页）
   */
  getProductList(params: ProductQueryParams): Promise<PageResult<Product>> {
    return request.get('/api/products/search', { params })
  },

  /**
   * 获取商品详情
   */
  getProductDetail(id: number): Promise<Product> {
    return request.get(`/api/products/${id}`)
  },

  /**
   * 创建商品
   */
  createProduct(data: ProductCreateRequest): Promise<Product> {
    return request.post('/api/products', data)
  },

  /**
   * 更新商品
   */
  updateProduct(id: number, data: Partial<ProductCreateRequest>): Promise<Product> {
    return request.put(`/api/products/${id}`, data)
  },

  /**
   * 删除商品
   */
  deleteProduct(id: number): Promise<boolean> {
    return request.delete(`/api/products/${id}`)
  },

  /**
   * 获取商品分类
   */
  getCategories(): Promise<ProductCategory[]> {
    return request.get('/api/products/categories')
  },

  /**
   * 获取热门商品
   */
  getFeaturedProducts(): Promise<PageResult<Product>> {
    return request.get('/api/products/search', {
      params: { page: 1, size: 8, sortField: 'view_count', sortDirection: 'desc' }
    })
  },

  /**
   * 收藏商品
   */
  likeProduct(productId: number): Promise<boolean> {
    return request.post(`/api/products/${productId}/like`)
  },

  /**
   * 取消收藏商品
   */
  unlikeProduct(productId: number): Promise<boolean> {
    return request.delete(`/api/products/${productId}/like`)
  },

  /**
   * 获取热门商品（简化版）
   */
  getHotProducts(limit: number = 10): Promise<Product[]> {
    return request.get('/api/products/hot', { params: { limit } })
  },

  /**
   * 获取最新商品
   */
  getLatestProducts(limit: number = 10): Promise<Product[]> {
    return request.get('/api/products/latest', { params: { limit } })
  },

  /**
   * 搜索商品（简化接口）
   */
  searchProducts(keyword: string, page = 1, size = 20): Promise<PageResult<Product>> {
    return request.get('/api/products/search', {
      params: { keyword, page, size }
    })
  },

  /**
   * 获取用户发布的商品
   */
  getUserProducts(userId: number, params: { page?: number; size?: number; status?: string }): Promise<PageResult<Product>> {
    return request.get(`/api/products/user/${userId}`, { params })
  },

  /**
   * 更新商品状态
   */
  updateProductStatus(id: number, status: string): Promise<Product> {
    return request.put(`/api/products/${id}/status`, { status })
  },

  /**
   * 获取商品统计（后端无此接口，返回空对象）
   */
  getProductStats(_productId?: number): Promise<any> {
    return Promise.resolve({})
  }
}

// 订单相关 API
export const orderApi = {
  /**
   * 获取订单列表
   */
  getOrderList(params: OrderQueryParams): Promise<PageResult<Order>> {
    if (params.status !== undefined) {
      return request.get(`/api/orders/my/status/${params.status}`, {
        params: { page: params.page, size: params.size }
      })
    } else {
      return request.get('/api/orders/my', { params })
    }
  },

  /**
   * 获取订单详情
   */
  getOrderDetail(id: number): Promise<Order> {
    return request.get(`/api/orders/${id}`)
  },

  /**
   * 创建订单
   */
  createOrder(data: OrderCreateRequest): Promise<Order> {
    return request.post('/api/orders', data)
  },

  /**
   * 取消订单
   */
  cancelOrder(id: number, cancelReason: string = '用户取消'): Promise<Order> {
    return request.post(`/api/orders/${id}/cancel`, { orderId: id, cancelReason })
  },

  /**
   * 确认收货
   */
  confirmReceipt(id: number, confirmNote: string = ''): Promise<Order> {
    return request.post(`/api/orders/${id}/confirm`, { orderId: id, confirmNote })
  },

  /**
   * 支付订单
   */
  payOrder(id: number, paymentMethod: PaymentMethod = 'ALIPAY', payPassword: string = '', payVoucher: string = ''): Promise<Order> {
    return request.post(`/api/orders/${id}/pay`, {
      orderId: id,
      paymentMethod,
      payPassword,
      payVoucher
    })
  },

  /**
   * 获取订单统计
   */
  getOrderStats(params?: { startTime?: string; endTime?: string; userId?: number }): Promise<any> {
    return request.get('/api/orders/statistics/my', { params })
  },

  /**
   * 获取卖家订单列表
   */
  getSellerOrders(params: OrderQueryParams): Promise<PageResult<Order>> {
    return request.get('/api/orders/seller', { params })
  },

  /**
   * 更新订单状态（卖家）
   */
  updateOrderStatus(id: number, status: OrderStatus, note?: string): Promise<Order> {
    return request.put(`/api/orders/${id}/status`, { status, note })
  },

  /**
   * 申请退款
   */
  applyRefund(id: number, refundReason: string, refundAmount?: number): Promise<Order> {
    return request.post(`/api/orders/${id}/refund`, { refundReason, refundAmount })
  },

  /**
   * 处理退款（卖家）
   */
  processRefund(id: number, approve: boolean, note?: string): Promise<Order> {
    return request.put(`/api/orders/${id}/refund`, { approve, note })
  }
}

// 兼职相关 API
export const jobApi = {
  /**
   * 获取兼职列表
   */
  getJobList(params: JobQueryParams): Promise<PageResult<Job>> {
    return request.get('/api/jobs', { params })
  },

  /**
   * 获取兼职详情
   */
  getJobDetail(id: number): Promise<Job> {
    return request.get(`/api/jobs/${id}`)
  },

  /**
   * 创建兼职
   */
  createJob(data: JobCreateRequest): Promise<Job> {
    return request.post('/api/jobs', data)
  },

  /**
   * 更新兼职
   */
  updateJob(id: number, data: Partial<JobCreateRequest>): Promise<Job> {
    return request.put(`/api/jobs/${id}`, data)
  },

  /**
   * 删除兼职
   */
  deleteJob(id: number): Promise<boolean> {
    return request.delete(`/api/jobs/${id}`)
  },

  /**
   * 申请兼职
   */
  applyJob(data: JobApplicationRequest): Promise<JobApplication> {
    return request.post('/api/job-applications', data)
  },

  /**
   * 获取我的申请
   */
  getMyApplications(params?: { page?: number; size?: number; status?: string }): Promise<PageResult<JobApplication>> {
    return request.get('/api/job-applications/me', { params })
  },

  /**
   * 获取最新兼职
   */
  getRecentJobs(): Promise<PageResult<Job>> {
    return request.get('/api/jobs', {
      params: { page: 1, size: 6, sortField: 'publish_time', sortDirection: 'desc' }
    })
  },

  /**
   * 获取热门兼职（使用推荐接口）
   */
  getHotJobs(limit: number = 10): Promise<Job[]> {
    return request.get('/api/jobs/recommended', { params: { limit } })
  },

  /**
   * 获取兼职分类（后端无此接口，返回空数组）
   */
  getJobCategories(): Promise<Array<{ id: number; name: string; description?: string }>> {
    return Promise.resolve([])
  },

  /**
   * 收藏兼职
   */
  favoriteJob(jobId: number): Promise<boolean> {
    return request.post(`/api/jobs/${jobId}/favorite/increase`)
  },

  /**
   * 取消收藏兼职
   */
  unfavoriteJob(jobId: number): Promise<boolean> {
    return request.post(`/api/jobs/${jobId}/favorite/decrease`)
  },

  /**
   * 获取收藏的兼职（后端无此接口，返回空分页数据）
   */
  getFavoriteJobs(params?: { page?: number; size?: number }): Promise<PageResult<Job>> {
    return Promise.resolve({ records: [], total: 0, size: params?.size || 10, current: params?.page || 1, pages: 0 })
  },

  /**
   * 获取用户发布的兼职
   */
  getUserJobs(userId: number, params?: { page?: number; size?: number; status?: string }): Promise<PageResult<Job>> {
    return request.get(`/api/jobs/publisher/${userId}`, { params })
  },

  /**
   * 更新兼职状态
   */
  updateJobStatus(id: number, status: string): Promise<Job> {
    return request.put(`/api/jobs/${id}/status`, null, { params: { status } })
  },

  /**
   * 获取申请详情
   */
  getApplicationDetail(id: number): Promise<JobApplication> {
    return request.get(`/api/job-applications/${id}`)
  },

  /**
   * 更新申请状态
   */
  updateApplicationStatus(id: number, status: string, note?: string): Promise<JobApplication> {
    return request.put(`/api/job-applications/${id}/status`, { status, note })
  },

  /**
   * 获取兼职统计
   */
  getJobStats(jobId?: number): Promise<any> {
    if (jobId) {
      return request.get(`/api/jobs/${jobId}/statistics`)
    }
    return request.get('/api/jobs/statistics')
  }
}

// 默认导出所有API
export default {
  userApi,
  productApi,
  orderApi,
  jobApi,
  notificationApi,
  uploadApi
}