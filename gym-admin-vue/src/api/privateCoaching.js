import axios from './axios'

// ========== 私教预约 ==========

/** 会员提交私教预约 */
export function createPrivateCoaching(data) {
  return axios.post('/private-coaching', data)
}

/** 会员使用兑换券提交私教预约 */
export function createBookingWithCoupon(data, couponId) {
  return axios.post('/private-coaching/coupon', data, { params: { couponId } })
}

/** 会员查看自己的私教预约记录 */
export function getMyPrivateCoaching(page, size) {
  return axios.get('/private-coaching/my', { params: { page, size } })
}

/** 管理员查看所有私教预约 */
export function getAllPrivateCoaching(page, size, status) {
  return axios.get('/private-coaching/admin/list', { params: { page, size, status } })
}

/** 教练查看自己的私教课程 */
export function getCoachPrivateCoaching(page, size) {
  return axios.get('/private-coaching/coach', { params: { page, size } })
}

/** 管理员审核通过 */
export function approvePrivateCoaching(id) {
  return axios.put(`/private-coaching/${id}/approve`)
}

/** 管理员审核拒绝 */
export function rejectPrivateCoaching(id) {
  return axios.put(`/private-coaching/${id}/reject`)
}

/** 教练确认接受 */
export function confirmPrivateCoaching(id) {
  return axios.put(`/private-coaching/${id}/confirm`)
}

/** 教练标记完成 */
export function completePrivateCoaching(id) {
  return axios.put(`/private-coaching/${id}/complete`)
}

/** 会员取消预约 */
export function cancelPrivateCoaching(id) {
  return axios.put(`/private-coaching/${id}/cancel`)
}

/** 会员支付 */
export function payPrivateCoaching(id) {
  return axios.put(`/private-coaching/${id}/pay`)
}

/** 会员评价教练 */
export function reviewPrivateCoaching(id, rating, review) {
  return axios.put(`/private-coaching/${id}/review`, null, { params: { rating, review } })
}

/** 管理员查看统计数据 */
export function getPrivateCoachingStatistics() {
  return axios.get('/private-coaching/statistics')
}

// ========== 教练时间表 ==========

/** 教练创建可用时间段 */
export function createCoachSchedule(data) {
  return axios.post('/coach-schedule', data)
}

/** 教练批量创建可用时间段 */
export function batchCreateCoachSchedule(data, endDate) {
  return axios.post('/coach-schedule/batch', data, { params: { endDate } })
}

/** 删除时间段 */
export function deleteCoachSchedule(id) {
  return axios.delete(`/coach-schedule/${id}`)
}

/** 查看教练指定日期的可用时间 */
export function getCoachScheduleByDate(coachId, date) {
  return axios.get(`/coach-schedule/coach/${coachId}`, { params: { date } })
}

/** 查看教练日期范围内的可用时间 */
export function getCoachScheduleByRange(coachId, startDate, endDate) {
  return axios.get(`/coach-schedule/coach/${coachId}/range`, { params: { startDate, endDate } })
}

/** 教练查看自己所有可用时间 */
export function getMyCoachSchedule() {
  return axios.get('/coach-schedule/my')
}

/** 设置时间段是否可用 */
export function setCoachScheduleAvailability(id, available) {
  return axios.put(`/coach-schedule/${id}/availability`, null, { params: { available } })
}

// ========== 教练列表（会员浏览用） ==========

/** 获取所有教练列表 */
export function getAllCoaches() {
  return axios.get('/coach/all')
}
