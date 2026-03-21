import { defineStore } from 'pinia'
import type { UserInfo } from '../types/user'
import { userApi } from '../api'

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

    // 登录
    async login(credentials: { username: string; password: string }) {
      try {
        const response = await userApi.login(credentials)
        // response 已经是 LoginResponse 对象 (accessToken, user)
        const { accessToken, user } = response
        this.setToken(accessToken)
        this.setUserInfo(user)
        return Promise.resolve()
      } catch (error: any) {
        throw new Error(error.response?.data?.message || '登录失败')
      }
    },

    // 注册
    async register(userData: { username: string; password: string; email: string }) {
      try {
        const response = await userApi.register(userData)
        // 假设注册成功后自动登录或返回成功信息
        return Promise.resolve(response)
      } catch (error: any) {
        throw new Error(error.response?.data?.message || '注册失败')
      }
    },

    // 模拟退出
    logout() {
      this.clearToken()
    }
  }
})