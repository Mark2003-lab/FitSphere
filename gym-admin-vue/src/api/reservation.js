import axios from './axios'

export function getReservations(page, size) {
  return axios.get('/reservation', { params: { page, size } })
}

export function getMyReservations(page, size) {
  return axios.get('/reservation/my', { params: { page, size } })
}

export function getCoachReservations(page, size) {
  return axios.get('/reservation/coach', { params: { page, size } })
}

export function createReservation(courseId) {
  return axios.post('/reservation', null, { params: { courseId } })
}

export function cancelReservation(id) {
  return axios.put(`/reservation/${id}/cancel`)
}

export function deleteReservation(id) {
  return axios.delete(`/reservation/${id}`)
}

export function approveReservation(id) {
  return axios.put(`/reservation/${id}/approve`)
}

export function rejectReservation(id) {
  return axios.put(`/reservation/${id}/reject`)
}

export function payReservation(id) {
  return axios.put(`/reservation/${id}/pay`)
}

export function updateReservation(id, status) {
  return axios.put(`/reservation/${id}`, { status })
}
