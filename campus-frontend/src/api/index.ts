import request from './request'
import type { LoginResponse, UserInfo, RegisterRequest } from '../types/user'

// 用户相关 API
export const userApi = {
  login(data: { username: string; password: string }): Promise<LoginResponse> {
    return request.post('/api/users/login', data)
  },

  register(data: RegisterRequest): Promise<UserInfo> {
    return request.post('/api/users/register', data)
  },

  getUserInfo() {
    return request.get('/api/users/profile') as Promise<UserInfo>
  },

  updateUserInfo(data: any) {
    return request.put('/api/users/profile', data) as Promise<UserInfo>
  },

  changePassword(userId: number, oldPassword: string, newPassword: string) {
    return request.put(`/api/users/${userId}/password`, null, {
      params: { oldPassword, newPassword }
    }) as Promise<any>
  }
}

// 商品相关 API
export const productApi = {
  getProductList(params: { page: number; size: number; keyword?: string; categoryId?: number }) {
    return request.get('/api/products/search', { params }) as Promise<any>
  },

  getProductDetail(id: number) {
    return request.get(`/api/products/${id}`)
  },

  createProduct(data: any) {
    return request.post('/api/products', data)
  },

  updateProduct(id: number, data: any) {
    return request.put(`/api/products/${id}`, data)
  },

  deleteProduct(id: number) {
    return request.delete(`/api/products/${id}`)
  },

  getCategories() {
    return request.get('/api/products/categories') as Promise<any>
  },

  getFeaturedProducts() {
    // 获取热门商品，默认获取前8个
    return request.get('/api/products/search', {
      params: { page: 1, size: 8, sortField: 'view_count', sortDirection: 'desc' }
    }) as Promise<any>
  },

  likeProduct(productId: number) {
    return request.post(`/api/products/${productId}/like`)
  },

  unlikeProduct(productId: number) {
    return request.delete(`/api/products/${productId}/like`)
  },

  getHotProducts(limit: number = 10) {
    return request.get('/api/products/hot', { params: { limit } }) as Promise<any>
  },

  getLatestProducts(limit: number = 10) {
    return request.get('/api/products/latest', { params: { limit } }) as Promise<any>
  }
}

// 订单相关 API
export const orderApi = {
  getOrderList(params: { page: number; size: number; status?: number }) {
    if (params.status !== undefined) {
      return request.get(`/api/orders/my/status/${params.status}`, {
        params: { page: params.page, size: params.size }
      }) as Promise<any>
    } else {
      return request.get('/api/orders/my', { params }) as Promise<any>
    }
  },

  getOrderDetail(id: number) {
    return request.get(`/api/orders/${id}`)
  },

  createOrder(data: any) {
    return request.post('/api/orders', data)
  },

  cancelOrder(id: number, cancelReason: string = '用户取消') {
    return request.post(`/api/orders/${id}/cancel`, { orderId: id, cancelReason })
  },

  confirmReceipt(id: number, confirmNote: string = '') {
    return request.post(`/api/orders/${id}/confirm`, { orderId: id, confirmNote })
  },

  payOrder(id: number, paymentMethod: string = 'ALIPAY', payPassword: string = '', payVoucher: string = '') {
    return request.post(`/api/orders/${id}/pay`, {
      orderId: id,
      paymentMethod,
      payPassword,
      payVoucher
    })
  }
}

// 兼职相关 API
export const jobApi = {
  getJobList(params: { page: number; size: number; keyword?: string }) {
    return request.get('/api/jobs', { params }) as Promise<any>
  },

  getJobDetail(id: number) {
    return request.get(`/api/jobs/${id}`)
  },

  createJob(data: any) {
    return request.post('/api/jobs', data)
  },

  applyJob(jobId: number, data: any) {
    return request.post('/api/job-applications', { jobId, ...data })
  },

  getMyApplications() {
    return request.get('/api/job-applications/me')
  },

  getRecentJobs() {
    // 获取最新兼职，默认获取前6个
    return request.get('/api/jobs', {
      params: { page: 1, size: 6, sortField: 'publish_time', sortDirection: 'desc' }
    }) as Promise<any>
  }
}

export default {
  userApi,
  productApi,
  orderApi,
  jobApi
}