import axios from './axios'

export function createCard(data) {
  return axios.post('/card', data)
}

export function updateCard(id, data) {
  return axios.put(`/card/${id}`, data)
}

export function deleteCard(id) {
  return axios.delete(`/card/${id}`)
}

export function getCardById(id) {
  return axios.get(`/card/${id}`)
}

export function getCards(page, size) {
  return axios.get('/card', { params: { page, size } })
}

export function getActiveCards() {
  return axios.get('/card/active')
}

export function toggleCardStatus(id) {
  return axios.put(`/card/${id}/status`)
}
