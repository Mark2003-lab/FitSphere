import axios from './axios'

export function getUnusedCoupons(userId) {
  return axios.get(`/coupon/list/${userId}`)
}

export function getUserCoupons() {
  return axios.get('/coupon/my-coupons')
}

export function getCouponCount(userId) {
  return axios.get(`/coupon/count/${userId}`)
}

export function useCoupon(userId, couponId) {
  return axios.post('/coupon/use', null, {
    params: { userId, couponId }
  })
}

export function getMyAvailableCoupons() {
  return axios.get('/coupon/my-available')
}