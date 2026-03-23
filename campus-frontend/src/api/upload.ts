/**
 * 文件上传API
 */
import request from './request'
import type { FileInfo, UploadResponse } from '@/types/common'

/**
 * 上传配置
 */
export interface UploadConfig {
  maxFileSize?: number // 最大文件大小，单位字节
  allowedTypes?: string[] // 允许的文件类型
  maxFiles?: number // 最大文件数量
  chunkSize?: number // 分片大小，单位字节
  enableCompression?: boolean // 是否启用压缩
}

/**
 * 上传进度回调
 */
export interface UploadProgressEvent {
  loaded: number
  total: number
  percent: number
  file: File
}

/**
 * 分片上传参数
 */
export interface ChunkUploadParams {
  file: File
  chunkIndex: number
  totalChunks: number
  chunkSize: number
  fileHash: string
  fileName: string
}

/**
 * 分片上传响应
 */
export interface ChunkUploadResponse {
  chunkIndex: number
  uploaded: boolean
  url?: string
  message?: string
}

/**
 * 文件上传API
 */
export const uploadApi = {
  /**
   * 上传单个文件
   */
  uploadFile(file: File, onProgress?: (event: UploadProgressEvent) => void) {
    const formData = new FormData()
    formData.append('file', file)

    return request.post<UploadResponse>('/api/upload/file', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          onProgress({
            loaded: progressEvent.loaded,
            total: progressEvent.total,
            percent: Math.round((progressEvent.loaded * 100) / progressEvent.total),
            file
          })
        }
      }
    })
  },

  /**
   * 上传图片
   */
  uploadImage(file: File, onProgress?: (event: UploadProgressEvent) => void) {
    const formData = new FormData()
    formData.append('image', file)

    return request.post<UploadResponse>('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          onProgress({
            loaded: progressEvent.loaded,
            total: progressEvent.total,
            percent: Math.round((progressEvent.loaded * 100) / progressEvent.total),
            file
          })
        }
      }
    })
  },

  /**
   * 批量上传文件
   */
  uploadFiles(files: File[], onProgress?: (event: UploadProgressEvent) => void) {
    const formData = new FormData()
    files.forEach((file) => {
      formData.append(`files`, file)
    })

    return request.post<UploadResponse[]>('/api/upload/files', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total && files.length > 0) {
          // 简化处理，假设所有文件大小相同
          const avgFileSize = progressEvent.total / files.length
          const uploadedFiles = Math.floor(progressEvent.loaded / avgFileSize)
          onProgress({
            loaded: progressEvent.loaded,
            total: progressEvent.total,
            percent: Math.round((progressEvent.loaded * 100) / progressEvent.total),
            file: files[Math.min(uploadedFiles, files.length - 1)]
          })
        }
      }
    })
  },

  /**
   * 批量上传图片
   */
  uploadImages(files: File[], onProgress?: (event: UploadProgressEvent) => void) {
    const formData = new FormData()
    files.forEach((file) => {
      formData.append(`images`, file)
    })

    return request.post<UploadResponse[]>('/api/upload/images', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total && files.length > 0) {
          const avgFileSize = progressEvent.total / files.length
          const uploadedFiles = Math.floor(progressEvent.loaded / avgFileSize)
          onProgress({
            loaded: progressEvent.loaded,
            total: progressEvent.total,
            percent: Math.round((progressEvent.loaded * 100) / progressEvent.total),
            file: files[Math.min(uploadedFiles, files.length - 1)]
          })
        }
      }
    })
  },

  /**
   * 分片上传文件
   */
  uploadChunk(params: ChunkUploadParams, onProgress?: (event: UploadProgressEvent) => void) {
    const formData = new FormData()
    const chunk = params.file.slice(
      params.chunkIndex * params.chunkSize,
      Math.min((params.chunkIndex + 1) * params.chunkSize, params.file.size)
    )

    formData.append('chunk', chunk)
    formData.append('chunkIndex', params.chunkIndex.toString())
    formData.append('totalChunks', params.totalChunks.toString())
    formData.append('fileHash', params.fileHash)
    formData.append('fileName', params.fileName)
    formData.append('fileSize', params.file.size.toString())

    return request.post<ChunkUploadResponse>('/api/upload/chunk', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          onProgress({
            loaded: progressEvent.loaded,
            total: progressEvent.total,
            percent: Math.round((progressEvent.loaded * 100) / progressEvent.total),
            file: params.file
          })
        }
      }
    })
  },

  /**
   * 合并分片文件
   */
  mergeChunks(fileHash: string, fileName: string, totalChunks: number) {
    return request.post<UploadResponse>('/api/upload/merge', {
      fileHash,
      fileName,
      totalChunks
    })
  },

  /**
   * 检查文件是否已上传
   */
  checkFileExists(fileHash: string, fileName: string) {
    return request.get<{ exists: boolean; url?: string }>('/api/upload/check', {
      params: { fileHash, fileName }
    })
  },

  /**
   * 删除文件
   */
  deleteFile(url: string) {
    return request.delete<boolean>('/api/upload/file', {
      params: { url }
    })
  },

  /**
   * 获取上传配置
   */
  getUploadConfig() {
    return request.get<UploadConfig>('/api/upload/config')
  },

  /**
   * 获取文件信息
   */
  getFileInfo(url: string) {
    return request.get<FileInfo>('/api/upload/info', {
      params: { url }
    })
  }
}

export default uploadApi