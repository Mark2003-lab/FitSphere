import axios from './axios'

export function getEquipments(page, size) {
  return axios.get('/equipment', { params: { page, size } })
}

export function getEquipmentById(id) {
  return axios.get(`/equipment/${id}`)
}

export function addEquipment(data) {
  return axios.post('/equipment', data)
}

export function updateEquipment(id, data) {
  return axios.put(`/equipment/${id}`, data)
}

export function deleteEquipment(id) {
  return axios.delete(`/equipment/${id}`)
}
