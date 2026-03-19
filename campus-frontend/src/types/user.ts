export interface UserInfo {
  id: number
  username: string
  email: string
  phone: string
  avatar: string
  role: 'USER' | 'ADMIN'
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
  token: string
  userInfo: UserInfo
}