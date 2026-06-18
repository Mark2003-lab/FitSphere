import axios from './axios'

export function getMembers(page, size) {
  return axios.get('/member', { params: { page, size } })
}

export function getMemberById(id) {
  return axios.get(`/member/${id}`)
}

export function addMember(data) {
  return axios.post('/member', data)
}

export function updateMember(id, data) {
  return axios.put(`/member/${id}`, data)
}

export function deleteMember(id) {
  return axios.delete(`/member/${id}`)
}

export function getCoachMembers(page, size) {
  return axios.get('/member/coach', { params: { page, size } })
}
