import request from '@/utils/request'

export default {
  // ===== 用户端 =====
  getInsuranceList() {
    return request({
      url: '/insurance/list',
      method: 'get'
    })
  },

  enroll(payload) {
    return request({
      url: '/insurance/enroll',
      method: 'post',
      data: payload
    })
  },

  getMyEnrollments() {
    return request({
      url: '/insurance/my',
      method: 'get'
    })
  },

  // ===== 管理员端 =====
  getInsurancePage(searchModel) {
    return request({
      url: '/insurance/page',
      method: 'get',
      params: {
        title: searchModel.title,
        insuranceYear: searchModel.insuranceYear,
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize
      }
    })
  },

  addInsuranceInfo(data) {
    return request({
      url: '/insurance/add',
      method: 'post',
      data
    })
  },

  updateInsuranceInfo(data) {
    return request({
      url: '/insurance/update',
      method: 'put',
      data
    })
  },

  saveInsuranceInfo(data) {
    if (data.id === null || data.id === undefined) {
      return this.addInsuranceInfo(data)
    }
    return this.updateInsuranceInfo(data)
  },

  deleteInsuranceInfo(id) {
    return request({
      url: `/insurance/${id}`,
      method: 'delete'
    })
  },

  getEnrollmentPage(searchModel) {
    return request({
      url: '/insurance/enrollment/page',
      method: 'get',
      params: {
        userId: searchModel.userId,
        insuranceInfoId: searchModel.insuranceInfoId,
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize
      }
    })
  }
}
