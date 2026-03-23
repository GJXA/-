/**
 * 公共类型定义
 */

/**
 * API统一响应格式
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: string
}

/**
 * 分页结果
 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 分页查询参数
 */
export interface PageParams {
  page?: number
  size?: number
  keyword?: string
  sortField?: string
  sortDirection?: 'asc' | 'desc'
}

/**
 * 通用键值对
 */
export interface KeyValuePair<K = string, V = any> {
  key: K
  value: V
}

/**
 * 通用选项
 */
export interface Option<T = string | number> {
  label: string
  value: T
  disabled?: boolean
}

/**
 * 通用树节点
 */
export interface TreeNode<T = any> {
  id: number | string
  label: string
  children?: TreeNode<T>[]
  parentId?: number | string
  data?: T
  disabled?: boolean
}

/**
 * 文件信息
 */
export interface FileInfo {
  uid: string
  name: string
  url: string
  size?: number
  type?: string
  status?: 'uploading' | 'done' | 'error' | 'removed'
  percent?: number
  response?: any
}

/**
 * 上传响应
 */
export interface UploadResponse {
  url: string
  name: string
  size: number
  type: string
}

/**
 * 统一错误响应
 */
export interface ErrorResponse {
  code: number
  message: string
  timestamp: string
  path?: string
  errors?: Record<string, string[]>
}