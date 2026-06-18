import axios from './axios'

export function getUserById(id) {
  return axios.get(`/user/${id}`)
}

export function updateUser(id, data) {
  return axios.put(`/user/${id}`, data)
}

export function changePassword(id, oldPassword, newPassword) {
  return axios.post(`/user/${id}/change-password`, {
    oldPassword,
    newPassword
  })
}

// 解锁账号（管理员使用）
export function unlockAccount(username) {
  return axios.post(`/user/unlock/${username}`)
}

// 获取账号登录安全信息
export function getLoginSecurityInfo(username) {
  return axios.get(`/user/security/${username}`)
}
