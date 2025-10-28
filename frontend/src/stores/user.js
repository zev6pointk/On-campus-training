import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getCurrentUser } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  // 登录
  const login = async (username, password) => {
    try {
      const result = await loginApi({ username, password })
      if (result.code === 200) {
        token.value = result.data.token
        userInfo.value = result.data.user
        localStorage.setItem('token', token.value)
        ElMessage.success('登录成功')
        return true
      } else {
        ElMessage.error(result.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error(error.message || '登录失败')
      return false
    }
  }

  // 检查登录状态
  const checkLogin = async () => {
    if (!token.value) return false

    try {
      const result = await getCurrentUser()
      if (result.code === 200) {
        userInfo.value = result.data
        return true
      } else {
        logout()
        return false
      }
    } catch (error) {
      logout()
      return false
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    checkLogin,
    logout
  }
})
