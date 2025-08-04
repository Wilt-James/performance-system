import { defineStore } from 'pinia'
import { login, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    realName: (state) => state.userInfo?.realName || '',
    avatar: (state) => state.userInfo?.avatar || '',
    deptId: (state) => state.userInfo?.deptId || null
  },

  actions: {
    // 登录
    async login(loginForm) {
      try {
        const response = await login(loginForm)
        if (response.code === 200) {
          this.token = response.data.token
          this.userInfo = response.data.user
          localStorage.setItem('token', this.token)
          return { success: true }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        return { success: false, message: error.message || '登录失败' }
      }
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        const response = await getCurrentUser()
        if (response.code === 200) {
          this.userInfo = response.data
          return { success: true }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        return { success: false, message: error.message || '获取用户信息失败' }
      }
    },

    // 登出
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    },

    // 清除用户信息
    clearUserInfo() {
      this.userInfo = null
    }
  }
})