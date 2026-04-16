<template>
  <div class="ai-consultation-container">
    <div class="page-header">
      <div class="header-icon">
        <i class="el-icon-service" />
      </div>
      <h2 class="page-title">AI健康咨询</h2>
      <p class="page-subtitle">支持结构化问诊分诊与医保政策导航</p>
    </div>

    <div class="chat-container">
      <div ref="messagesList" class="messages-list">
        <div v-if="messages.length === 0" class="empty-tip">
          <div class="empty-icon">
            <i class="el-icon-chat-line-round" />
          </div>
          <p class="empty-text">您好！我是AI健康顾问</p>
          <p class="empty-desc">可咨询症状分诊、参保报销政策等问题</p>
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

            <div v-if="msg.type === 'ai' && msg.meta && msg.meta.intent" class="meta-row">
              <el-tag size="mini" type="info">{{ msg.meta.intent }}</el-tag>
              <el-tag v-if="msg.meta.triageLevel" size="mini" :type="triageTagType(msg.meta.triageLevel)">
                {{ msg.meta.triageLevel }}
              </el-tag>
              <el-tag v-if="msg.meta.action" size="mini">{{ msg.meta.action }}</el-tag>
              <el-tag v-if="msg.meta.provider" size="mini" type="success">{{ msg.meta.provider }}</el-tag>
            </div>

            <div v-if="msg.type === 'ai' && msg.meta && msg.meta.policyCitations && msg.meta.policyCitations.length" class="citations">
              <div class="citations-title">政策依据：</div>
              <div v-for="(item, i) in msg.meta.policyCitations" :key="i" class="citation-item">
                <div class="citation-name">{{ item.title }}</div>
                <div class="citation-summary">{{ item.summary }}</div>
                <div class="citation-url">{{ item.url }}</div>
              </div>
            </div>

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

      <div class="quick-questions">
        <span class="quick-label">常见问题：</span>
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

      <div class="input-area">
        <el-input
          v-model="inputQuestion"
          type="textarea"
          :rows="3"
          placeholder="请输入问题，Ctrl+Enter 发送"
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
      sessionId: '',
      messages: [],
      inputQuestion: '',
      loading: false,
      quickQuestions: [
        '胸口痛，有点呼吸困难',
        '头痛两天了，6分疼',
        '门诊可以报销多少？',
        '住院报销需要什么材料？'
      ]
    }
  },
  created() {
    this.initSession()
  },
  methods: {
    async initSession() {
      try {
        const res = await aiConsultationApi.startChat()
        if (res && res.data) {
          this.sessionId = res.data.sessionId
          if (res.data.welcome) {
            this.addMessage('ai', res.data.welcome, { intent: 'WELCOME', action: 'start' })
          }
        }
      } catch (e) {
        this.addMessage('ai', '会话初始化失败，您仍可继续提问。', { intent: 'SYSTEM', action: 'error' })
      }
    },

    async sendMessage() {
      if (!this.inputQuestion || this.inputQuestion.trim() === '' || this.loading) {
        return
      }

      const question = this.inputQuestion.trim()
      this.inputQuestion = ''
      this.addMessage('user', question)
      this.loading = true

      try {
        if (!this.sessionId) {
          await this.initSession()
        }

        const response = await aiConsultationApi.sendChatMessage({
          sessionId: this.sessionId,
          question
        })

        if (response && response.data) {
          this.sessionId = response.data.sessionId || this.sessionId
          this.addMessage('ai', response.data.reply || '已收到您的问题。', {
            intent: response.data.intent,
            triageLevel: response.data.triageLevel,
            action: response.data.action,
            provider: response.data.provider,
            policyCitations: response.data.policyCitations || []
          })
        } else {
          this.addMessage('ai', '抱歉，我暂时无法回答您的问题，请稍后再试。', { intent: 'SYSTEM', action: 'empty' })
        }
      } catch (error) {
        console.error('AI咨询错误:', error)
        this.addMessage('ai', '抱歉，服务暂时不可用，请稍后再试。', { intent: 'SYSTEM', action: 'error' })
      } finally {
        this.loading = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    selectQuickQuestion(question) {
      this.inputQuestion = question
      this.sendMessage()
    },

    addMessage(type, content, meta = null) {
      const now = new Date()
      const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`

      this.messages.push({
        type,
        content,
        time,
        meta
      })

      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    triageTagType(level) {
      if (level === 'L3') return 'danger'
      if (level === 'L2') return 'warning'
      return 'success'
    },

    formatMessage(content) {
      return String(content || '').replace(/\n/g, '<br>')
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
  margin-bottom: 20px;
  padding: 10px 0;
}

.header-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 14px;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon i {
  font-size: 36px;
  color: #fff;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #303133;
}

.page-subtitle {
  font-size: 14px;
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
  height: calc(100vh - 260px);
  min-height: 560px;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #fafbfc;
}

.empty-tip {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-icon {
  width: 90px;
  height: 90px;
  margin: 0 auto 16px;
  background: #eaf3ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon i {
  font-size: 42px;
  color: #409EFF;
}

.empty-text {
  font-size: 20px;
  margin: 0 0 8px;
  color: #303133;
}

.empty-desc {
  font-size: 14px;
  margin: 0;
}

.message-item {
  display: flex;
  margin-bottom: 18px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 20px;
}

.message-item.user .message-avatar {
  background: #409EFF;
  color: #fff;
  margin-left: 10px;
}

.message-item.ai .message-avatar {
  background: #337ecc;
  color: #fff;
  margin-right: 10px;
}

.message-content-wrapper {
  max-width: 78%;
}

.message-item.user .message-content-wrapper {
  text-align: right;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.7;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-item.user .message-content {
  background: #409EFF;
  color: #fff;
}

.message-item.ai .message-content {
  background: #fff;
  color: #303133;
  border: 1px solid #e4e7ed;
}

.meta-row {
  margin-top: 6px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.citations {
  margin-top: 8px;
  border-left: 3px solid #409EFF;
  background: #f5f9ff;
  padding: 8px 10px;
  border-radius: 6px;
  text-align: left;
}

.citations-title {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
  font-weight: 600;
}

.citation-item + .citation-item {
  margin-top: 8px;
}

.citation-name {
  font-size: 13px;
  color: #303133;
  font-weight: 600;
}

.citation-summary {
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
}

.citation-url {
  font-size: 12px;
  color: #409EFF;
  margin-top: 2px;
  word-break: break-all;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.typing-indicator {
  display: flex;
  gap: 6px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
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
    transform: translateY(-8px);
    opacity: 1;
  }
}

.quick-questions {
  padding: 14px 24px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.quick-label {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
}

.input-area {
  padding: 16px 24px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-area .el-textarea {
  flex: 1;
}

.input-area .el-button {
  padding: 12px 24px;
}
</style>
