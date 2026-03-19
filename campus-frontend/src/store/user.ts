import { defineStore } from 'pinia'
import type { UserInfo } from '../types/user'

interface UserState {
  token: string | null
  userInfo: UserInfo | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token'),
    userInfo: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    getUserInfo: (state) => state.userInfo
  },

  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
    },

    clearToken() {
      this.token = null
      this.userInfo = null
      localStorage.removeItem('token')
    },

    // 模拟登录
    async login(credentials: { username: string; password: string }) {
      // TODO: 调用实际登录接口
      const mockToken = 'mock-jwt-token-123456'
      this.setToken(mockToken)

      const mockUserInfo: UserInfo = {
        id: 1,
        username: credentials.username,
        email: `${credentials.username}@example.com`,
        phone: '13800138000',
        avatar: '',
        role: 'USER'
      }
      this.setUserInfo(mockUserInfo)

      return Promise.resolve()
    },

    // 模拟注册
    async register(_userData: { username: string; password: string; email: string }) {
      // TODO: 调用实际注册接口
      return Promise.resolve()
    },

    // 模拟退出
    logout() {
      this.clearToken()
    }
  }
})