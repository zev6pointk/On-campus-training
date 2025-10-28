import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

// 获取用户列表
export function getUsers(params) {
  return request({
    url: '/auth/users',
    method: 'get',
    params
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/auth/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/auth/users/${id}`,
    method: 'delete'
  })
}
