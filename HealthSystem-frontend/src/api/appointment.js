import request from '@/utils/request'

export default {
  createAppointment(payload) {
    return request({
      url: '/appointment/create',
      method: 'post',
      data: payload
    })
  },

  getMyAppointments() {
    return request({
      url: '/appointment/my',
      method: 'get'
    })
  }
}

