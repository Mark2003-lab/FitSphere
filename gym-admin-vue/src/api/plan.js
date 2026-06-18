import axios from './axios'

export function createPlan(data) {
  return axios.post('/plan', data)
}

export function updatePlan(id, data) {
  return axios.put(`/plan/${id}`, data)
}

export function deletePlan(id) {
  return axios.delete(`/plan/${id}`)
}

export function getPlanById(id) {
  return axios.get(`/plan/${id}`)
}

export function getPlans(page, size) {
  return axios.get('/plan', { params: { page, size } })
}
