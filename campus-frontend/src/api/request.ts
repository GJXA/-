import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 添加 token 到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    // 如果响应没有code字段，可能是直接返回数据（如文件下载等）
    if (res === null || res === undefined || typeof res !== 'object' || !('code' in res)) {
      return res
    }

    // 支持不同的成功标识
    const isSuccess =
      res.code === 200 ||
      res.code === 0 ||
      res.code === '200' ||
      res.success === true ||
      res.status === 'success' ||
      (typeof res.code === 'string' && res.code.toLowerCase() === 'success')

    if (isSuccess) {
      // 处理分页数据标准化
      let data = res.data

      // 情况1: data本身就是分页结构（有records字段）
      if (data && Array.isArray(data.records)) {
        return {
          records: data.records,
          total: data.total || 0,
          size: data.size || 20,
          current: data.current || 1,
          pages: data.pages || 1
        }
      }

      // 情况2: data是数组但没有分页信息（直接返回数组）
      if (Array.isArray(data)) {
        return data
      }

      // 情况3: data.data可能是分页结构（嵌套一层）
      if (data && data.data && Array.isArray(data.data.records)) {
        return {
          records: data.data.records,
          total: data.data.total || 0,
          size: data.data.size || 20,
          current: data.data.current || 1,
          pages: data.data.pages || 1
        }
      }

      // 情况4: data.data是数组
      if (data && data.data && Array.isArray(data.data)) {
        return data.data
      }

      // 其他情况：直接返回data
      return data
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
  },
  (error) => {
    // HTTP 状态码错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      ElMessage.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

export default service