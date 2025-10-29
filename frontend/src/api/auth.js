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

// 创建用户（管理员新增）
export function createUser(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 上传用户头像
export function uploadUserAvatar(formData) {
  return request({
    url: '/auth/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/auth/password',
    method: 'put',
    data
  })
}
