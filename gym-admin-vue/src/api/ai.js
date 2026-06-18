import axios from './axios'

export function aiChat(data) {
  return axios.post('/ai/chat', data)
}

export function getChats(page, size) {
  return axios.get('/ai/chats', { params: { page, size } })
}
