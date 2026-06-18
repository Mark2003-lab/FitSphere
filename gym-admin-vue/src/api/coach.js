import axios from './axios'

export function getCoaches(page, size) {
  return axios.get('/coach', { params: { page, size } })
}

export function getCoachById(id) {
  return axios.get(`/coach/${id}`)
}

export function addCoach(data) {
  return axios.post('/coach', data)
}

export function updateCoach(id, data) {
  return axios.put(`/coach/${id}`, data)
}

export function deleteCoach(id) {
  return axios.delete(`/coach/${id}`)
}
