import axios from './axios'

export function getAdminDashboard() {
  return axios.get('/dashboard/admin')
}

export function getMemberDashboard() {
  return axios.get('/dashboard/member')
}

export function getCoachDashboard() {
  return axios.get('/dashboard/coach')
}

export function getReservationLedger() {
  return axios.get('/dashboard/admin/reservation-ledger')
}

export function getPrivateCoachingLedger() {
  return axios.get('/dashboard/admin/private-coaching-ledger')
}
