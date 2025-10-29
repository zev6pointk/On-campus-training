<template>
  <div class="message-container">
    <el-card class="message-card">
      <div class="message-layout">
        <!-- 联系人列表 -->
        <div class="contacts-panel">
          <div class="panel-header">
            <span>联系人列表</span>
            <el-button type="primary" :icon="Refresh" circle @click="loadContacts" />
          </div>

          <div class="contacts-list">
            <div
              v-for="contact in contacts"
              :key="contact.id"
              class="contact-item"
              :class="{ active: selectedContact?.id === contact.id }"
              @click="selectContact(contact)"
            >
              <el-avatar :size="40">
                {{ contact.realName?.charAt(0) || contact.username?.charAt(0) }}
              </el-avatar>
              <div class="contact-info">
                <div class="contact-name">{{ contact.realName || contact.username }}</div>
                <div class="contact-status">
                  <el-icon class="status-icon online"><CircleCheck /></el-icon>
                  在线
                </div>
              </div>
              <el-badge v-if="contact.unreadCount > 0" :value="contact.unreadCount" />
            </div>
          </div>
        </div>

        <!-- 聊天区域 -->
        <div class="chat-panel">
          <div v-if="!selectedContact" class="empty-state">
            <el-icon :size="80"><ChatDotSquare /></el-icon>
            <p>选择一个联系人开始聊天</p>
          </div>

          <template v-else>
            <div class="chat-header">
              <div class="chat-user-info">
                <el-avatar :size="36">
                  {{ selectedContact.realName?.charAt(0) || selectedContact.username?.charAt(0) }}
                </el-avatar>
                <div class="user-details">
                  <div class="user-name">{{ selectedContact.realName || selectedContact.username }}</div>
                  <div class="user-status">
                    <el-icon class="status-icon online"><CircleCheck /></el-icon>
                    在线
                  </div>
                </div>
              </div>
            </div>

            <div class="chat-messages" ref="messagesContainer">
              <div
                v-for="message in chatMessages"
                :key="message.id"
                class="message-item"
                :class="{ 'message-sent': message.senderId === userStore.userInfo?.id }"
              >
                <div class="message-bubble">
                  {{ message.content }}
                </div>
                <div class="message-time">
                  {{ formatTime(message.createTime) }}
                </div>
              </div>

              <div v-if="typing" class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>

            <div class="chat-input">
              <el-input
                v-model="inputMessage"
                placeholder="输入消息..."
                @keydown.enter="handleSend"
                clearable
              >
                <template #append>
                  <el-button
                    :icon="Promotion"
                    @click="handleSend"
                    :disabled="!inputMessage.trim()"
                  >
                    发送
                  </el-button>
                </template>
              </el-input>
            </div>
          </template>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted, onUnmounted } from 'vue'
import { io } from 'socket.io-client'
import { Promotion, Refresh, ChatDotSquare, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { sendMessage, getChatHistory } from '@/api/message'
import { getUsers } from '@/api/auth'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const contacts = ref([])
const selectedContact = ref(null)
const chatMessages = ref([])
const inputMessage = ref('')
const typing = ref(false)
const messagesContainer = ref(null)
let socket = null

const formatTime = (time) => {
  return dayjs(time).format('HH:mm')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const loadContacts = async () => {
  try {
    const result = await getUsers({ size: 100 })
    if (result.code === 200) {
      contacts.value = result.data.records.filter(
        user => user.id !== userStore.userInfo?.id
      )
    }
  } catch (error) {
    ElMessage.error('加载联系人列表失败')
  }
}

const selectContact = async (contact) => {
  selectedContact.value = contact
  contact.unreadCount = 0
  await loadChatHistory()
}

const loadChatHistory = async () => {
  if (!selectedContact.value) return

  try {
    const userId1 = userStore.userInfo.id
    const userId2 = selectedContact.value.id

    const result = await getChatHistory(userId1, userId2, { size: 50 })
    if (result.code === 200) {
      chatMessages.value = result.data.records || []
      await scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('加载聊天记录失败')
  }
}

const handleSend = async () => {
  const message = inputMessage.value.trim()
  if (!message || !selectedContact.value) return

  const messageData = {
    senderId: userStore.userInfo.id,
    receiverId: selectedContact.value.id,
    content: message,
    messageType: 1,
    status: 1
  }

  try {
    await sendMessage(messageData)
    chatMessages.value.push({
      ...messageData,
      id: Date.now(),
      createTime: new Date()
    })
    inputMessage.value = ''
    await scrollToBottom()

    // 发送WebSocket消息
    if (socket) {
      socket.emit('chat', {
        from: userStore.userInfo.id.toString(),
        to: selectedContact.value.id.toString(),
        content: message,
        timestamp: Date.now()
      })
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
  }
}

const initWebSocket = () => {
  socket = io('http://localhost:8080/ws/chat', {
    transports: ['websocket']
  })

  socket.on('connect', () => {
    console.log('WebSocket连接成功')
  })

  socket.on('chat', (data) => {
    if (data.from === selectedContact.value?.id.toString()) {
      chatMessages.value.push({
        id: Date.now(),
        senderId: data.from,
        content: data.content,
        createTime: new Date(data.timestamp)
      })
      scrollToBottom()
    }
  })

  socket.on('typing', (data) => {
    if (data.from === selectedContact.value?.id.toString()) {
      typing.value = data.typing
      if (data.typing) {
        setTimeout(() => {
          typing.value = false
        }, 3000)
      }
    }
  })

  socket.on('disconnect', () => {
    console.log('WebSocket连接断开')
  })
}

onMounted(() => {
  loadContacts()
  initWebSocket()
})

onUnmounted(() => {
  if (socket) {
    socket.disconnect()
  }
})
</script>

<style scoped>
.message-container {
  padding: 0;
}

.message-card {
  height: calc(100vh - 200px);
}

.message-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  height: 100%;
}

.contacts-panel {
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contacts-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
  margin-bottom: 5px;
}

.contact-item:hover {
  background: #f5f7fa;
}

.contact-item.active {
  background: #ecf5ff;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 2px;
}

.contact-status {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-icon {
  font-size: 12px;
}

.status-icon.online {
  color: #67C23A;
}

.chat-panel {
  display: flex;
  flex-direction: column;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-state p {
  margin-top: 10px;
}

.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e6e6e6;
  background: #fafafa;
}

.chat-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.user-status {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 2px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.message-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

.message-sent {
  align-items: flex-end;
}

.message-bubble {
  max-width: 60%;
  padding: 10px 15px;
  border-radius: 8px;
  background: white;
  color: #303133;
  word-wrap: break-word;
  line-height: 1.5;
}

.message-sent .message-bubble {
  background: #409EFF;
  color: white;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 10px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #909399;
  animation: typing 1.5s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.5s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 1s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.chat-input {
  padding: 15px 20px;
  border-top: 1px solid #e6e6e6;
}
</style>
