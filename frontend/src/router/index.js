import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录', requiresAuth: false }
    },
    {
      path: '/',
      redirect: '/statistics',
      component: () => import('@/views/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '/statistics',
          name: 'Statistics',
          component: () => import('@/views/Statistics/Statistics.vue'),
          meta: { title: '数据统计', icon: 'DataAnalysis' }
        },
        {
          path: '/users',
          name: 'Users',
          component: () => import('@/views/UserManage/Users.vue'),
          meta: { title: '用户管理', icon: 'User' }
        },
        {
          path: '/chat',
          name: 'Chat',
          component: () => import('@/views/Chat/Chat.vue'),
          meta: { title: '大模型助手', icon: 'ChatDotRound' }
        },
        {
          path: '/message',
          name: 'Message',
          component: () => import('@/views/Message/Message.vue'),
          meta: { title: '即时通讯', icon: 'Message' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 师生助手系统`
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth !== false) {
    if (!userStore.isLoggedIn) {
      next('/login')
      return
    }
  }

  // 如果已登录且访问登录页，跳转到首页
  if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
    return
  }

  // 检查用户管理页面权限（仅管理员可访问）
  if (to.path === '/users') {
    const userInfo = userStore.userInfo
    if (!userInfo || userInfo.userType !== 1) {
      next('/statistics')
      return
    }
  }

  next()
})

export default router
