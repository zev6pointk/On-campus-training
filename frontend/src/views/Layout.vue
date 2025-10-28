<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <h1 v-show="!isCollapse">师生助手</h1>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        class="sidebar-menu"
      >
        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>

        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>大模型助手</template>
        </el-menu-item>

        <el-menu-item index="/message">
          <el-icon><Message /></el-icon>
          <template #title>即时通讯</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            :icon="Fold"
            circle
            @click="toggleCollapse"
          />
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.realName?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.realName }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Fold,
  DataAnalysis,
  User,
  ChatDotRound,
  Message,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人资料功能开发中')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  background: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #243447;
}

.logo h1 {
  color: white;
  font-size: 20px;
  margin: 0;
}

.sidebar-menu {
  border: none;
  background: #304156;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

/* 菜单项默认状态 - 提升可见度 */
:deep(.el-menu-item) {
  background: rgba(255, 255, 255, 0.05) !important;
  color: #E0E6ED !important;
  border-radius: 0 !important;
  margin: 2px 0;
  transition: all 0.3s ease;
}

/* 菜单项图标颜色 */
:deep(.el-menu-item .el-icon) {
  color: #E0E6ED !important;
}

/* 菜单项激活状态 - 明显的视觉区分 */
:deep(.el-menu-item.is-active) {
  background: #409EFF !important;
  color: white !important;
  position: relative;
}

/* 激活状态左侧蓝色边框指示 */
:deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #fff;
}

/* 菜单项悬停状态 - 平滑过渡效果 */
:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
}

/* 悬停时图标同步变化 */
:deep(.el-menu-item:hover .el-icon) {
  color: #E0E6ED !important;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 5px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
}

.content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
