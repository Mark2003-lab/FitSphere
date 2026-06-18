import axios from './axios'

// ========== 会员自助签到 ==========
export function memberCheckin(data = {}) {
  return axios.post('/checkin/me', data)
}

export function memberCheckout() {
  return axios.put('/checkin/me/checkout')
}

export function getMyCheckins(status) {
  return axios.get('/checkin/my', { params: { status } })
}

export function memberCourseCheckin(courseId) {
  return axios.post(`/checkin/course/${courseId}`)
}

// ========== 教练课堂签到 ==========
export function getCourseCheckins(courseId) {
  return axios.get('/checkin/coach/courses', { params: { courseId } })
}

export function coachCheckinForMember(data) {
  return axios.post('/checkin/course', data)
}

export function cancelCourseCheckin(courseId, memberId) {
  return axios.delete(`/checkin/course/${courseId}/member/${memberId}`)
}

// ========== 管理员全局管理 ==========
export function getAllCheckins(params) {
  return axios.get('/checkin', { params })
}

export function adminCheckin(data) {
  return axios.post('/checkin', data)
}

export function updateCheckin(id, data) {
  return axios.put(`/checkin/${id}`, data)
}

export function deleteCheckin(id) {
  return axios.delete(`/checkin/${id}`)
}

export function getCheckinStats() {
  return axios.get('/checkin/stats')
}

// ========== 签到排行榜 ==========
export function getCheckinRanking() {
  return axios.get('/checkin/ranking')
}
