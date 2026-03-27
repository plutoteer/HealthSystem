import request from '@/utils/request'

export default {
  // 获取健康报告数据（返回JSON）
  getHealthReportData(params) {
    return request({
      url: '/healthReport/data',
      method: 'post',
      data: params
    })
  },
  // 导出健康报告（返回PDF文件流）- 保留用于向后兼容
  exportHealthReport(params) {
    return request({
      url: '/healthReport/export',
      method: 'post',
      data: params,
      responseType: 'blob'
    })
  }
}

