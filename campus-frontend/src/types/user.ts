export interface UserInfo {
  id: number
  username: string
  email: string
  phone: string
  avatarUrl: string
  roles: string[]
  realName?: string
  studentId?: string
  gender?: number
  status?: number
  createTime?: string
  updateTime?: string
  school?: string
  major?: string
  grade?: string
  address?: string
  signature?: string
  birthday?: string
  lastLoginTime?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone?: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: UserInfo
}