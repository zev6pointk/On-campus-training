import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { data } = response
    return data
  },
  error => {
    const { response } = error

    if (response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    } else if (response?.status === 403) {
      ElMessage.error('没有权限访问该资源')
    } else if (response?.status === 500) {
      ElMessage.error('服务器内部错误')
    } else {
      ElMessage.error(response?.data?.message || error.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

export default request
