<template>
  <div class="ai-consultation-container">
    <!-- 标题区域 -->
    <div class="page-header">
      <div class="header-icon">
        <i class="el-icon-service" />
      </div>
      <h2 class="page-title">AI健康咨询</h2>
      <p class="page-subtitle">向AI咨询您的健康问题，获取专业建议</p>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-container">
      <!-- 消息列表 -->
      <div ref="messagesList" class="messages-list">
        <div v-if="messages.length === 0" class="empty-tip">
          <div class="empty-icon">
            <i class="el-icon-chat-line-round" />
          </div>
          <p class="empty-text">👋 您好！我是AI健康顾问</p>
          <p class="empty-desc">有什么健康问题可以问我哦~</p>
        </div>
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-item', msg.type]"
        >
          <div class="message-avatar">
            <i :class="msg.type === 'user' ? 'el-icon-user' : 'el-icon-service'" />
          </div>
          <div class="message-content-wrapper">
            <div class="message-content" v-html="formatMessage(msg.content)" />
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
        <div v-if="loading" class="message-item ai typing">
          <div class="message-avatar">
            <i class="el-icon-service" />
          </div>
          <div class="message-content-wrapper">
            <div class="message-content typing-indicator">
              <span /><span /><span />
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷问题（固定在输入区域上方） -->
      <div class="quick-questions">
        <span class="quick-label">💡 常见问题：</span>
        <el-button
          v-for="(q, index) in quickQuestions"
          :key="index"
          size="small"
          type="info"
          plain
          round
          :disabled="loading"
          @click="selectQuickQuestion(q)"
        >{{ q }}</el-button>
      </div>

      <!-- 关键词类型（固定在输入区域上方） -->
      <div class="keyword-types">
        <span class="quick-label">🏷️ 关键词类型：</span>
        <el-button
          v-for="(keyword, index) in keywordTypes"
          :key="index"
          size="small"
          type="info"
          plain
          round
          :disabled="loading"
          @click="selectKeyword(keyword)"
        >{{ keyword }}</el-button>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputQuestion"
          type="textarea"
          :rows="3"
          placeholder="请输入您的健康问题，按 Ctrl+Enter 发送..."
          :disabled="loading"
          resize="none"
          @keyup.ctrl.enter="sendMessage"
        />
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!inputQuestion || inputQuestion.trim() === ''"
          icon="el-icon-position"
          @click="sendMessage"
        >发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import aiConsultationApi from '@/api/aiConsultation'

export default {
  name: 'AIConsultation',
  data() {
    return {
      messages: [],
      inputQuestion: '',
      loading: false,
      quickQuestions: [
        '如何减肥？',
        '每天应该运动多久？',
        '如何健康饮食？',
        '如何改善睡眠质量？',
        '高血压如何调理？'
      ],
      keywordTypes: [
        '健身',
        '运动',
        '减肥',
        '饮食',
        '睡眠'
      ]
    }
  },
  methods: {
    async sendMessage() {
      if (!this.inputQuestion || this.inputQuestion.trim() === '' || this.loading) {
        return
      }

      const question = this.inputQuestion.trim()
      this.inputQuestion = ''

      // 添加用户消息
      this.addMessage('user', question)

      // 显示加载状态
      this.loading = true

      try {
        // 调用API
        const response = await aiConsultationApi.consultAI(question)

        // 添加AI回复
        if (response && response.data) {
          this.addMessage('ai', response.data)
        } else {
          this.addMessage('ai', '抱歉，我暂时无法回答您的问题，请稍后再试。')
        }
      } catch (error) {
        console.error('AI咨询错误:', error)
        this.addMessage('ai', '抱歉，服务暂时不可用，请稍后再试。')
      } finally {
        this.loading = false
        // 滚动到底部
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    selectQuickQuestion(question) {
      this.inputQuestion = question
      this.sendMessage()
    },

    selectKeyword(keyword) {
      // 点击关键词类型时，直接发送关键词进行咨询
      this.inputQuestion = keyword
      this.sendMessage()
    },

    addMessage(type, content) {
      const now = new Date()
      const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`

      this.messages.push({
        type,
        content,
        time
      })

      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    formatMessage(content) {
      // 将换行符转换为<br>
      return content.replace(/\n/g, '<br>')
    },

    scrollToBottom() {
      const messagesList = this.$refs.messagesList
      if (messagesList) {
        messagesList.scrollTop = messagesList.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
.ai-consultation-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 50%, #f0f4f8 100%);
  min-height: calc(100vh - 84px);
  box-sizing: border-box;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px 0;
}

.header-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

.header-icon i {
  font-size: 40px;
  color: #fff;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 12px 30px rgba(64, 158, 255, 0.5);
  }
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 10px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.chat-container {
  max-width: 1000px;
  margin: 0 auto;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 300px);
  min-height: 600px;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  padding: 30px;
  background: #fafbfc;
  scroll-behavior: smooth;
}

.messages-list::-webkit-scrollbar {
  width: 6px;
}

.messages-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.messages-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.messages-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.empty-tip {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-icon {
  width: 100px;
  height: 100px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #d9ecff 0%, #b3d8ff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon i {
  font-size: 50px;
  color: #409EFF;
}

.empty-text {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px 0;
}

.empty-desc {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.message-item {
  display: flex;
  margin-bottom: 24px;
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 22px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-item.user .message-avatar {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  color: #fff;
  margin-left: 12px;
}

.message-item.ai .message-avatar {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  color: #fff;
  margin-right: 12px;
}

.message-content-wrapper {
  max-width: 70%;
  min-width: 120px;
}

.message-item.user .message-content-wrapper {
  text-align: right;
}

.message-content {
  padding: 14px 18px;
  border-radius: 16px;
  word-wrap: break-word;
  line-height: 1.8;
  font-size: 15px;
  white-space: pre-wrap;
}

.message-item.user .message-content {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.message-item.ai .message-content {
  background: #fff;
  color: #303133;
  border: 1px solid #e4e7ed;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  padding: 0 4px;
}

.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 14px 18px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 16px;
  border-bottom-left-radius: 4px;
}

.typing-indicator span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #909399;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.7;
  }
  30% {
    transform: translateY(-12px);
    opacity: 1;
  }
}

.quick-questions {
  padding: 20px 30px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-top: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.keyword-types {
  padding: 20px 30px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-top: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-label {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-right: 5px;
}

.quick-questions .el-button {
  transition: all 0.3s;
}

.quick-questions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.keyword-types .el-button {
  transition: all 0.3s;
}

.keyword-types .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.input-area {
  padding: 20px 30px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-area .el-textarea {
  flex: 1;
}

.input-area .el-textarea >>> .el-textarea__inner {
  border-radius: 12px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
  font-size: 14px;
  line-height: 1.6;
}

.input-area .el-textarea >>> .el-textarea__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.input-area .el-button {
  height: auto;
  padding: 14px 32px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.input-area .el-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

.input-area .el-button:active:not(:disabled) {
  transform: translateY(0);
}

.input-area .el-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-consultation-container {
    padding: 10px;
  }

  .page-header {
    margin-bottom: 20px;
  }

  .header-icon {
    width: 60px;
    height: 60px;
  }

  .header-icon i {
    font-size: 30px;
  }

  .page-title {
    font-size: 28px;
  }

  .chat-container {
    height: calc(100vh - 200px);
    min-height: 500px;
  }

  .messages-list {
    padding: 20px;
  }

  .message-content-wrapper {
    max-width: 85%;
  }

  .quick-questions {
    padding: 15px 20px;
  }

  .keyword-types {
    padding: 15px 20px;
  }

  .input-area {
    padding: 15px 20px;
  }
}
</style>

