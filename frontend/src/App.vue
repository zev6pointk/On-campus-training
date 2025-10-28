<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  // 检查是否已登录
  const token = localStorage.getItem('token')
  if (token && !userStore.isLoggedIn) {
    userStore.checkLogin()
  }

  // 如果未登录，跳转到登录页
  if (!token && router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  width: 100%;
  height: 100vh;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

body {
  margin: 0;
  padding: 0;
}

a {
  text-decoration: none;
  color: inherit;
}

button {
  cursor: pointer;
}
</style>
