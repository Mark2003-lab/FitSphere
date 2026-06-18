import axios from './axios'

export function getCourses(page, size) {
  return axios.get('/course', { params: { page, size } })
}

export function getCourseById(id) {
  return axios.get(`/course/${id}`)
}

export function addCourse(data) {
  return axios.post('/course', data)
}

export function updateCourse(id, data) {
  return axios.put(`/course/${id}`, data)
}

export function deleteCourse(id) {
  return axios.delete(`/course/${id}`)
}

export function getCoachCourses(page, size) {
  return axios.get('/course/coach', { params: { page, size } })
}

export function publishCourseCheckin(courseId, data) {
  return axios.post(`/course/${courseId}/checkin/publish`, data)
}

export function stopCourseCheckin(courseId) {
  return axios.post(`/course/${courseId}/checkin/stop`)
}

export function getMemberCourses() {
  return axios.get('/course/member')
}

// 教练创建课程
export function coachCreateCourse(data) {
  return axios.post('/course/coach/create', data)
}

// 获取待审核课程列表
export function getPendingCourses(page, size) {
  return axios.get('/course/pending', { params: { page, size } })
}

// 审核课程
export function auditCourse(id, auditStatus) {
  return axios.put(`/course/${id}/audit`, { auditStatus })
}
