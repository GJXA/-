import request from './request'

// 用户相关 API
export const userApi = {
  login(data: { username: string; password: string }) {
    return request.post('/user/login', data)
  },

  register(data: { username: string; password: string; email: string }) {
    return request.post('/user/register', data)
  },

  getUserInfo() {
    return request.get('/user/info')
  },

  updateUserInfo(data: any) {
    return request.put('/user/info', data)
  }
}

// 商品相关 API
export const productApi = {
  getProductList(params: { page: number; size: number; keyword?: string; categoryId?: number }) {
    return request.get('/product/list', { params })
  },

  getProductDetail(id: number) {
    return request.get(`/product/${id}`)
  },

  createProduct(data: any) {
    return request.post('/product', data)
  },

  updateProduct(id: number, data: any) {
    return request.put(`/product/${id}`, data)
  },

  deleteProduct(id: number) {
    return request.delete(`/product/${id}`)
  },

  getCategories() {
    return request.get('/product/categories')
  }
}

// 订单相关 API
export const orderApi = {
  getOrderList(params: { page: number; size: number; status?: number }) {
    return request.get('/order/list', { params })
  },

  getOrderDetail(id: number) {
    return request.get(`/order/${id}`)
  },

  createOrder(data: any) {
    return request.post('/order', data)
  },

  cancelOrder(id: number) {
    return request.put(`/order/${id}/cancel`)
  },

  confirmReceipt(id: number) {
    return request.put(`/order/${id}/confirm`)
  }
}

// 兼职相关 API
export const jobApi = {
  getJobList(params: { page: number; size: number; keyword?: string }) {
    return request.get('/job/list', { params })
  },

  getJobDetail(id: number) {
    return request.get(`/job/${id}`)
  },

  createJob(data: any) {
    return request.post('/job', data)
  },

  applyJob(jobId: number, data: any) {
    return request.post(`/job/${jobId}/apply`, data)
  },

  getMyApplications() {
    return request.get('/job/my-applications')
  }
}

export default {
  userApi,
  productApi,
  orderApi,
  jobApi
}