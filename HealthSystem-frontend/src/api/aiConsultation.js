import request from '@/utils/request'

export default {
  // 兼容旧接口：单轮AI咨询
  consultAI(question) {
    return request({
      url: '/ai/consult',
      method: 'post',
      data: { question }
    })
  },

  // 开启多轮会话
  startChat(userId) {
    return request({
      url: '/ai/chat/start',
      method: 'post',
      data: { userId }
    })
  },

  // 发送多轮消息
  sendChatMessage({ sessionId, question, userId }) {
    return request({
      url: '/ai/chat/message',
      method: 'post',
      data: { sessionId, question, userId }
    })
  },

  // 获取所有问答列表（管理用）
  getAllQA() {
    return request({
      url: '/ai/qa/list',
      method: 'get'
    })
  },

  // 分页查询AI咨询问题列表
  getQAList(searchModel) {
    return request({
      url: '/ai/qa/page',
      method: 'get',
      params: {
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        keywords: searchModel.keywords,
        category: searchModel.category,
        question: searchModel.question
      }
    })
  },

  // 新增AI咨询问题
  addQA(aiQA) {
    return request({
      url: '/ai/qa',
      method: 'post',
      data: aiQA
    })
  },

  // 修改AI咨询问题
  updateQA(aiQA) {
    return request({
      url: '/ai/qa',
      method: 'put',
      data: aiQA
    })
  },

  // 保存AI咨询问题（新增或修改）
  saveQA(aiQA) {
    if (aiQA.id === null || aiQA.id === undefined) {
      return this.addQA(aiQA)
    }
    return this.updateQA(aiQA)
  },

  // 根据ID获取AI咨询问题
  getQAById(id) {
    return request({
      url: `/ai/qa/${id}`,
      method: 'get'
    })
  },

  // 根据ID删除AI咨询问题
  deleteQAById(id) {
    return request({
      url: `/ai/qa/${id}`,
      method: 'delete'
    })
  }
}
