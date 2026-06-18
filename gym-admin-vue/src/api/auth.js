import axios from './axios'

export function login(data) {
  return axios.post('/auth/login', data)
}

export function register(data) {
  return axios.post('/auth/register', data)
}

export function sendCode(data) {
  return axios.post('/auth/send-code', data)
}

export function sendResetCode(data) {
  return axios.post('/auth/send-reset-code', data)
}

export function resetPassword(data) {
  return axios.post('/auth/reset-password', data)
}
