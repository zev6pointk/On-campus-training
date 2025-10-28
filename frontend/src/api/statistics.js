import request from '@/utils/request'

// 获取概览统计
export function getOverview() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

// 获取用户数量统计
export function getUserCount() {
  return request({
    url: '/statistics/users/count',
    method: 'get'
  })
}

// 获取用户类型分布
export function getUserTypeDistribution() {
  return request({
    url: '/statistics/users/distribution',
    method: 'get'
  })
}

// 获取院系分布
export function getDepartmentDistribution() {
  return request({
    url: '/statistics/department/distribution',
    method: 'get'
  })
}

// 获取活跃用户趋势
export function getActiveUserTrend(params) {
  return request({
    url: '/statistics/users/trend',
    method: 'get',
    params
  })
}

// 获取对话量统计
export function getChatCount(params) {
  return request({
    url: '/statistics/chat/count',
    method: 'get',
    params
  })
}
