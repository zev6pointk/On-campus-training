<template>
  <div class="chat-container">
    <el-card class="chat-card">
      <!-- 对话区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :class="{ 'message-user': message.role === 'user', 'message-assistant': message.role === 'assistant' }"
        >
          <div class="message-avatar">
            <el-avatar :size="36">
              {{ message.role === 'user' ? '我' : 'AI' }}
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>

        <div v-if="loading" class="message-item message-assistant">
          <div class="message-avatar">
            <el-avatar :size="36">AI</el-avatar>
          </div>
          <div class="message-content">
            <div class="message-text">
              <el-icon class="is-loading"><Loading /></el-icon>
              正在思考中...
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          @keydown.enter.exact="handleEnter"
          @keydown.enter.shift.exact="handleShiftEnter"
        />
        <div class="input-actions">
          <el-button
            type="primary"
            :icon="Promotion"
            @click="handleSend"
            :loading="loading"
            :disabled="!inputMessage.trim()"
          >
            发送
          </el-button>
          <el-button :icon="Delete" @click="clearChat">清空对话</el-button>
        </div>
      </div>
    </el-card>

    <!-- 会话列表 -->
    <el-card class="sessions-card">
      <template #header>
        <div class="card-header">
          <span>会话列表</span>
          <el-button type="primary" :icon="Plus" circle @click="newSession" />
        </div>
      </template>

      <div class="sessions-list">
        <div
          v-for="session in sessions"
          :key="session.id"
          class="session-item"
          :class="{ active: sessionId === session }"
          @click="selectSession(session)"
        >
          <el-icon><ChatDotRound /></el-icon>
          <span class="session-name">{{ session }}</span>
          <el-button
            type="danger"
            :icon="Close"
            circle
            size="small"
            @click.stop="deleteSession(session)"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import { Promotion, Delete, Plus, ChatDotRound, Close, Loading } from '@element-plus/icons-vue'
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

const formatTime = (time) => {
  return dayjs(time).format('HH:mm:ss')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const handleEnter = (e) => {
  e.preventDefault()
  handleSend()
}

const handleShiftEnter = (e) => {
  // Shift + Enter 换行
}

const loadSessions = async () => {
  try {
    const result = await getSessions()
    if (result.code === 200) {
      sessions.value = result.data || []
      if (sessions.value.length > 0 && !sessionId.value) {
        sessionId.value = sessions.value[0]
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
      if (!sessions.value.includes(sessionId.value)) {
        sessions.value.unshift(sessionId.value)
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
  loadHistory()
}

const newSession = () => {
  sessionId.value = `session_${Date.now()}`
  messages.value = []
  if (!sessions.value.includes(sessionId.value)) {
    sessions.value.unshift(sessionId.value)
  }
}

const deleteSession = async (session) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个会话吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const index = sessions.value.indexOf(session)
    if (index > -1) {
      sessions.value.splice(index, 1)
    }

    if (sessionId.value === session) {
      sessionId.value = sessions.value[0] || ''
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

onMounted(() => {
  sessionId.value = `session_${Date.now()}`
  loadSessions()
})
</script>

<style scoped>
.chat-container {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 20px;
  height: calc(100vh - 160px);
}

.chat-card {
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
  min-height: 400px;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-user {
  flex-direction: row-reverse;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 8px;
  word-wrap: break-word;
  line-height: 1.6;
}

.message-user .message-text {
  background: #409EFF;
  color: white;
}

.message-assistant .message-text {
  background: white;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  text-align: right;
}

.message-user .message-time {
  text-align: left;
}

.chat-input {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.sessions-card {
  height: fit-content;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sessions-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s;
}

.session-item:hover {
  background: #f5f7fa;
}

.session-item.active {
  background: #ecf5ff;
  color: #409EFF;
}

.session-name {
  flex: 1;
  font-size: 14px;
}
</style>
