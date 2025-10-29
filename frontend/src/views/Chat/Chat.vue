<template>
  <div class="chat-layout">
    <!-- 左侧对话区域 -->
    <div class="chat-main">
      <!-- 对话区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <div v-if="messages.length === 0 && !loading" class="empty-state">
          <el-icon class="empty-icon"><ChatDotRound /></el-icon>
          <p>开始与 AI 助手对话吧！</p>
        </div>

        <div
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :class="{ 'message-user': message.role === 'user', 'message-assistant': message.role === 'assistant' }"
          :style="{ animation: 'fadeIn 0.3s ease-out' }"
        >
          <div class="message-avatar">
            <el-avatar :size="40" :style="{ background: message.role === 'user' ? '#409EFF' : '#67C23A' }">
              {{ message.role === 'user' ? userStore.userInfo?.username?.[0]?.toUpperCase() || '我' : 'AI' }}
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-actions" v-if="message.role === 'assistant'">
                <el-button link :icon="CopyDocument" size="small" @click="copyMessage(message.content)">复制</el-button>
              </div>
            </div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>

        <div v-if="loading" class="message-item message-assistant">
          <div class="message-avatar">
            <el-avatar :size="40" style="background: #67C23A">AI</el-avatar>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="message-text loading-dots">
                <span></span><span></span><span></span>
                正在思考中...
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-container">
        <div class="input-wrapper">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 4 }"
            placeholder="请输入您的问题... (Enter发送，Shift+Enter换行)"
            @keydown.enter.exact="handleEnter"
            @keydown.enter.shift.exact="handleShiftEnter"
            @input="updateInputHeight"
            ref="inputRef"
            class="chat-textarea"
          />
          <div class="input-footer">
            <div class="input-tips">
              <el-icon><InfoFilled /></el-icon>
              <span v-if="inputMessage.length > 0">{{ inputMessage.length }}/2000</span>
              <span v-else>支持 Markdown 格式</span>
            </div>
            <div class="input-actions">
              <el-button :icon="Delete" @click="clearChat" circle :disabled="messages.length === 0" />
              <el-button
                type="primary"
                :icon="Promotion"
                @click="handleSend"
                :loading="loading"
                :disabled="!inputMessage.trim() || inputMessage.length > 2000"
                circle
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧会话列表（可收起） -->
    <div class="chat-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-toggle" @click="sidebarCollapsed = !sidebarCollapsed">
        <el-icon><ArrowLeft v-if="!sidebarCollapsed" /><ArrowRight v-else /></el-icon>
      </div>

      <div class="sidebar-content">
        <div class="sidebar-header">
          <h3>会话列表</h3>
          <el-button type="primary" :icon="Plus" circle @click="newSession" />
        </div>

        <div v-if="sessions.length === 0" class="empty-sessions">
          <el-icon><ChatDotRound /></el-icon>
          <p>暂无会话</p>
        </div>

        <div class="sessions-list">
          <div
            v-for="session in sessions"
            :key="session.id"
            class="session-item"
            :class="{ active: sessionId === session.id }"
            @click="selectSession(session.id)"
            @contextmenu.prevent="showSessionMenu($event, session)"
          >
            <el-icon class="session-icon"><ChatDotRound /></el-icon>
            <span class="session-name" :title="session.name">{{ session.name }}</span>
            <el-dropdown @command="(command) => handleSessionCommand(command, session)">
              <el-icon class="session-more"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="rename">重命名</el-dropdown-item>
                  <el-dropdown-item command="delete" style="color: #F56C6C">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import {
  Promotion, Delete, Plus, ChatDotRound, Close, Loading,
  CopyDocument, InfoFilled, ArrowLeft, ArrowRight, MoreFilled
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { sendMessage, getChatHistory, getSessions } from '@/api/chat'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const messages = ref([])
const sessions = ref([])
const sessionId = ref('')
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const inputRef = ref(null)
const sidebarCollapsed = ref(false)
const isMobile = ref(false)

const formatTime = (time) => {
  return dayjs(time).format('HH:mm:ss')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const updateInputHeight = () => {
  // 自动调整输入框高度
  if (inputRef.value?.textarea) {
    inputRef.value.textarea.style.height = 'auto'
    inputRef.value.textarea.style.height = inputRef.value.textarea.scrollHeight + 'px'
  }
}

const handleEnter = (e) => {
  if (e.ctrlKey || e.metaKey) {
    // Ctrl/Cmd + Enter 发送
    e.preventDefault()
    handleSend()
  } else if (!e.shiftKey) {
    // Enter 发送
    e.preventDefault()
    handleSend()
  }
}

const handleShiftEnter = (e) => {
  // Shift + Enter 换行（默认行为）
}

const copyMessage = async (content) => {
  try {
    await navigator.clipboard.writeText(content)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const loadSessions = async () => {
  try {
    const result = await getSessions()
    if (result.code === 200) {
      const sessionIds = result.data || []
      sessions.value = sessionIds.map(id => ({
        id,
        name: `会话 ${id.slice(-6)}`
      }))
      if (sessions.value.length > 0 && !sessionId.value) {
        sessionId.value = sessions.value[0].id
        loadHistory()
      }
    }
  } catch (error) {
    ElMessage.error('加载会话列表失败')
  }
}

const loadHistory = async () => {
  if (!sessionId.value) return

  try {
    const result = await getChatHistory(sessionId.value)
    if (result.code === 200) {
      const history = result.data || []
      messages.value = history.map(item => ({
        id: item.id,
        role: item.messageType === 1 ? 'user' : 'assistant',
        content: item.content,
        timestamp: item.createTime
      }))
      await scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('加载聊天记录失败')
  }
}

const handleSend = async () => {
  const message = inputMessage.value.trim()
  if (!message) return

  const userMessage = {
    id: Date.now(),
    role: 'user',
    content: message,
    timestamp: new Date()
  }

  messages.value.push(userMessage)
  inputMessage.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const result = await sendMessage({
      sessionId: sessionId.value || 'default',
      message: message
    })

    if (result.code === 200) {
      const assistantMessage = {
        id: Date.now() + 1,
        role: 'assistant',
        content: result.data.response,
        timestamp: result.data.timestamp
      }
      messages.value.push(assistantMessage)
      await scrollToBottom()

      // 更新会话列表
      if (!sessions.value.find(s => s.id === sessionId.value)) {
        sessions.value.unshift({
          id: sessionId.value,
          name: `会话 ${sessionId.value.slice(-6)}`
        })
      }
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
  } finally {
    loading.value = false
  }
}

const selectSession = (session) => {
  sessionId.value = session
  messages.value = []
  loadHistory()
}

const newSession = () => {
  sessionId.value = `session_${Date.now()}`
  messages.value = []
  if (!sessions.value.find(s => s.id === sessionId.value)) {
    sessions.value.unshift({
      id: sessionId.value,
      name: `会话 ${sessionId.value.slice(-6)}`
    })
  }
}

const deleteSession = async (session) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个会话吗？此操作不可恢复。',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const index = sessions.value.findIndex(s => s.id === session.id)
    if (index > -1) {
      sessions.value.splice(index, 1)
    }

    if (sessionId.value === session.id) {
      sessionId.value = sessions.value[0]?.id || ''
      if (sessionId.value) {
        loadHistory()
      } else {
        messages.value = []
      }
    }

    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const renameSession = async (session) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入新的会话名称', '重命名', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: session.name,
      inputValidator: (value) => {
        if (!value || value.trim() === '') {
          return '会话名称不能为空'
        }
        return true
      }
    })

    const index = sessions.value.findIndex(s => s.id === session.id)
    if (index > -1) {
      sessions.value[index].name = value
    }

    ElMessage.success('重命名成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重命名失败')
    }
  }
}

const handleSessionCommand = (command, session) => {
  if (command === 'rename') {
    renameSession(session)
  } else if (command === 'delete') {
    deleteSession(session)
  }
}

const showSessionMenu = (event, session) => {
  // 右键菜单已通过 dropdown 实现
}

const clearChat = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空当前对话吗？',
      '清空确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    messages.value = []
    ElMessage.success('已清空')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    sidebarCollapsed.value = true
  }
}

onMounted(() => {
  sessionId.value = `session_${Date.now()}`
  loadSessions()
  checkScreenSize()
  window.addEventListener('resize', checkScreenSize)
})
</script>

<style scoped>
/* 全屏布局 */
.chat-layout {
  display: flex;
  height: calc(100vh - 60px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

/* 主对话区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 16px 0 0 16px;
  margin: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 消息显示区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: linear(to bottom, #f8f9fa 0%, #ffffff 100%);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-state .empty-icon {
  font-size: 64px;
  color: #409EFF;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 16px;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  animation: slideIn 0.3s ease-out;
}

.message-item.message-user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  max-width: 75%;
}

.message-item.message-user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

/* 消息气泡 */
.message-bubble {
  position: relative;
  padding: 16px 20px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.message-bubble:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

.message-user .message-bubble {
  background: linear(135deg, #409EFF 0%, #66b1ff 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-assistant .message-bubble {
  background: white;
  color: #303133;
  border-bottom-left-radius: 4px;
}

/* 消息文本 */
.message-text {
  line-height: 1.8;
  font-size: 15px;
  word-wrap: break-word;
  white-space: pre-wrap;
}

/* 消息操作按钮 */
.message-actions {
  margin-top: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.message-bubble:hover .message-actions {
  opacity: 1;
}

/* 加载动画 */
.loading-dots {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.loading-dots span {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #909399;
  animation: loading 1.4s infinite ease-in-out;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

.loading-dots span:nth-child(3) {
  animation-delay: 0;
}

/* 消息时间 */
.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  padding: 0 4px;
}

.message-user .message-time {
  text-align: right;
}

.message-assistant .message-time {
  text-align: left;
}

/* 输入区域容器 */
.chat-input-container {
  padding: 20px 24px;
  background: white;
  border-top: 1px solid #EBEEF5;
}

.input-wrapper {
  max-width: 100%;
}

/* 输入框样式 */
.chat-textarea {
  margin-bottom: 12px;
}

.chat-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #EBEEF5;
  padding: 12px 16px;
  font-size: 15px;
  line-height: 1.6;
  transition: all 0.3s;
  resize: none;
}

.chat-textarea :deep(.el-textarea__inner):focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

/* 输入框底部 */
.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.input-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.input-actions {
  display: flex;
  gap: 8px;
}

/* 侧边栏 */
.chat-sidebar {
  width: 320px;
  background: white;
  margin: 16px 16px 16px 0;
  border-radius: 0 16px 16px 0;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.chat-sidebar.collapsed {
  width: 0;
  margin-left: 0;
}

.sidebar-toggle {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
  transition: all 0.3s;
}

.chat-sidebar:not(.collapsed) .sidebar-toggle {
  left: 304px;
}

.sidebar-toggle:hover {
  background: #409EFF;
  color: white;
  transform: translateY(-50%) scale(1.1);
}

.sidebar-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 20px;
}

/* 侧边栏头部 */
.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #EBEEF5;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 空会话状态 */
.empty-sessions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
}

.empty-sessions .el-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

/* 会话列表 */
.sessions-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.session-item:hover {
  background: #f5f7fa;
  transform: translateX(4px);
}

.session-item.active {
  background: linear(135deg, #409EFF 0%, #66b1ff 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.session-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.session-name {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-more {
  font-size: 16px;
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
}

.session-item:hover .session-more {
  opacity: 1;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes loading {
  0%, 80%, 100% {
    transform: scale(0.6);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar,
.sessions-list::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb,
.sessions-list::-webkit-scrollbar-thumb {
  background: #C0C4CC;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover,
.sessions-list::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-layout {
    height: calc(100vh - 60px);
  }

  .chat-main {
    margin: 8px;
    border-radius: 12px;
  }

  .chat-sidebar {
    position: fixed;
    right: 8px;
    top: 68px;
    bottom: 8px;
    z-index: 1000;
    width: 280px;
    border-radius: 12px;
    box-shadow: 0 16px 48px rgba(0, 0, 0, 0.3);
  }

  .chat-sidebar.collapsed {
    width: 0;
    right: -288px;
  }

  .sidebar-toggle {
    display: none;
  }

  .message-item .message-content {
    max-width: 85%;
  }
}
</style>
