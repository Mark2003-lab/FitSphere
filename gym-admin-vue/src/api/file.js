import axios from './axios'

export function uploadAvatar(formData) {
  return axios.post('/file/upload/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
