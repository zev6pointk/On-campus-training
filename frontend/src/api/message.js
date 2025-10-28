import request from '@/utils/request'

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/message/send',
    method: 'post',
    data
  })
}

// 获取聊天记录
export function getChatHistory(userId1, userId2, params) {
  return request({
    url: `/message/history/${userId1}/${userId2}`,
    method: 'get',
    params
  })
}
