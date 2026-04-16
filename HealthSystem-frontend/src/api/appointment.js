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
  },

  // 管理员端：预约分页列表
  getAppointmentList(searchModel) {
    return request({
      url: '/appointment/list',
      method: 'get',
      params: {
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        userId: searchModel.userId
      }
    })
  }
}

