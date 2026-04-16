import request from '@/utils/request'

export default {
  // 用户端：下拉框用
  getAll() {
    return request({
      url: '/examSlot/all',
      method: 'get'
    })
  },

  // 管理员端：分页列表
  getList(searchModel) {
    return request({
      url: '/examSlot/list',
      method: 'get',
      params: {
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        region: searchModel.region
      }
    })
  },

  add(slot) {
    return request({
      url: '/examSlot/add',
      method: 'post',
      data: slot
    })
  },

  update(slot) {
    return request({
      url: '/examSlot/update',
      method: 'put',
      data: slot
    })
  },

  save(slot) {
    if (slot.id === null || slot.id === undefined) return this.add(slot)
    return this.update(slot)
  },

  deleteById(id) {
    return request({
      url: `/examSlot/${id}`,
      method: 'delete'
    })
  }
}

