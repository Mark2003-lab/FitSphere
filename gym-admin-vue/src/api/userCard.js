import axios from './axios'

export function purchaseCard(userId, cardId) {
  return axios.post('/user-card/purchase', null, {
    params: { userId, cardId }
  })
}

export function getCurrentCard(userId) {
  return axios.get(`/user-card/current/${userId}`)
}

export function getUserCards(userId) {
  return axios.get(`/user-card/user/${userId}`)
}

export function getCardById(id) {
  return axios.get(`/user-card/${id}`)
}

export function changeCard(userId, newCardId) {
  return axios.post('/user-card/change', null, {
    params: { userId, newCardId }
  })
}

export function renewCard(userId, cardId) {
  return axios.post('/user-card/renew', null, {
    params: { userId, cardId }
  })
}
