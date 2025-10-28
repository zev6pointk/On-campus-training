import request from '@/utils/request'

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/chat/send',
    method: 'post',
    data
  })
}

// 获取聊天历史
export function getChatHistory(sessionId) {
  return request({
    url: `/chat/history/${sessionId}`,
    method: 'get'
  })
}

// 获取会话列表
export function getSessions() {
  return request({
    url: '/chat/sessions',
    method: 'get'
  })
}
