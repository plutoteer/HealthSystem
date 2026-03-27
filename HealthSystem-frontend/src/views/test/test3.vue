<template>
  <div v-loading="loading" class="page-container" element-loading-text="加载中...">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <h1 class="page-title">运动知识</h1>
      <p class="page-subtitle">探索适合您的运动方式，科学制定运动计划</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-wrapper">
      <div class="search-box">
        <el-input
          v-model="searchText"
          placeholder="请输入运动类型进行搜索"
          class="search-input"
          clearable
          @keyup.enter.native="handleSearch"
        >
          <i slot="prefix" class="el-input__icon el-icon-search" />
        </el-input>
        <el-button type="primary" icon="el-icon-search" class="search-btn" @click="handleSearch">
          搜索
        </el-button>
        <el-button icon="el-icon-refresh" class="reset-btn" @click="handleReset">
          重置
        </el-button>
      </div>
    </div>

    <!-- 空状态提示 -->
    <div v-if="!loading && sportInfos.length === 0" class="empty-state">
      <i class="el-icon-warning-outline" />
      <p>暂无数据，请稍后重试</p>
      <el-button type="primary" @click="loadAllData">重新加载</el-button>
    </div>

    <!-- 卡片网格区域 -->
    <div v-else class="content-wrapper">
      <div class="grid-container">
        <div
          v-for="(sportInfo, index) in paginatedSportInfos"
          :key="sportInfo.id || index"
          class="grid-item"
          :class="`card-${((currentPage - 1) * pageSize + index) % 4 + 1}`"
          @click="goToDetail(sportInfo.sportType, sportInfo)"
        >
          <div class="card-header">
            <h2 class="card-title">{{ sportInfo.sportType }}</h2>
          </div>
          <div class="card-content">
            <div class="info-item">
              <span class="info-label">运动种类</span>
              <span class="info-value">{{ sportInfo.sportType }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">适宜时间</span>
              <span class="info-value">{{ sportInfo.suitableTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">适宜心率</span>
              <span class="info-value">{{ sportInfo.suitableHeartRate }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">适宜频率</span>
              <span class="info-value">{{ sportInfo.suitableFrequency }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">推荐速度</span>
              <span class="info-value">{{ sportInfo.recommendedSpeed }}</span>
            </div>
          </div>
          <div class="card-footer">
            <el-button type="primary" class="detail-btn" @click.stop="goToDetail(sportInfo.sportType, sportInfo)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>
      <!-- 分页组件 -->
      <div v-if="sportInfos.length > 0" class="pagination-wrapper">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="sportInfos.length"
          layout="total, prev, pager, next, jumper"
          background
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import sportApi from '@/api/Function_Menu'

export default {
  data() {
    return {
      searchText: '',
      sportInfos: [],
      allSportInfos: [], // 保存所有数据用于重置
      DetailInfo: [],
      loading: false, // 添加loading状态
      currentPage: 1, // 当前页码
      pageSize: 6 // 每页显示数量：2行 * 3列 = 6个卡片
    }
  },

  computed: {
    // 计算当前页显示的数据
    paginatedSportInfos() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.sportInfos.slice(start, end)
    }
  },

  async created() {
    await this.loadAllData()
  },

  methods: {
    goToDetail(sportName, sportInfo) {
      const loadingInstance = this.$loading({
        text: '加载详情中...',
        target: document.querySelector('.page-container')
      })
      // 优先使用sportInfoId获取详情，如果没有sportInfoId则使用sportName
      if (sportInfo && sportInfo.id) {
        // 使用sportInfoId获取详情
        sportApi
          .getDetailBySportInfoId(sportInfo.id)
          .then((response) => {
            const detailInfo = response.data
            if (detailInfo && detailInfo.id) {
              // 只传递detail的id，简化URL
              this.$router.push({ path: '/detail', query: { id: detailInfo.id }})
            } else {
              // 如果没有找到详情，使用sportName作为后备
              this.fallbackToSportName(sportName, loadingInstance)
            }
          })
          .catch((error) => {
            console.error('根据sportInfoId加载详情失败:', error)
            // 如果根据sportInfoId获取失败，使用sportName作为后备
            this.fallbackToSportName(sportName, loadingInstance)
          })
      } else {
        // 如果没有sportInfoId，使用sportName作为后备
        this.fallbackToSportName(sportName, loadingInstance)
      }
    },

    fallbackToSportName(sportName, loadingInstance) {
      sportApi
        .DetailInfo(sportName)
        .then((response) => {
          const detailInfo = response.data
          if (detailInfo && detailInfo.id) {
            // 只传递detail的id，简化URL
            this.$router.push({ path: '/detail', query: { id: detailInfo.id }})
          } else {
            this.$message({
              message: '未找到对应的运动详情',
              type: 'error',
              duration: 3000
            })
            loadingInstance.close()
          }
        })
        .catch((error) => {
          console.error('加载详情失败:', error)
          this.$message({
            message: error.message || '加载详情失败，请稍后重试',
            type: 'error',
            duration: 3000
          })
          loadingInstance.close()
        })
    },

    // 加载所有数据
    async loadAllData() {
      this.loading = true // 开始加载
      try {
        const response = await sportApi.getAllSportInfo()
        // 取得运动信息数组
        const sportInfoData = response.data.sportInfos || []
        // 重构每条运动信息的数据格式
        const sportInfos = sportInfoData.map((info) => ({
          id: info.id,
          sportType: info.sportType,
          suitableTime: info.suitableTime,
          suitableHeartRate: info.suitableHeartRate,
          suitableFrequency: info.suitableFrequency,
          recommendedSpeed: info.recommendedSpeed
        }))
        this.allSportInfos = sportInfos
        this.sportInfos = sportInfos
        if (sportInfos.length > 0) {
          this.$message({
            message: '查询成功',
            type: 'success'
          })
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        this.sportInfos = [] // 确保失败时清空数据，显示空状态
        this.allSportInfos = []
        this.$message({
          message: error.message || '查询失败，请稍后重试',
          type: 'error',
          duration: 3000
        })
      } finally {
        this.loading = false // 结束加载
      }
    },

    // 搜索处理
    handleSearch() {
      if (!this.searchText.trim()) {
        this.sportInfos = this.allSportInfos
        this.currentPage = 1 // 重置到第一页
        this.$message({
          message: '请输入搜索内容',
          type: 'warning'
        })
        return
      }
      // 根据输入的搜索内容进行过滤
      const filteredSportInfoData = this.allSportInfos.filter((info) => {
        return info.sportType.includes(this.searchText.trim())
      })
      // 更新运动信息列表
      this.sportInfos = filteredSportInfoData
      this.currentPage = 1 // 重置到第一页
      if (filteredSportInfoData.length === 0) {
        this.$message({
          message: '未找到相关结果',
          type: 'warning'
        })
      } else {
        this.$message({
          message: `找到 ${filteredSportInfoData.length} 条结果`,
          type: 'success'
        })
      }
    },

    // 重置处理
    handleReset() {
      this.searchText = ''
      this.sportInfos = this.allSportInfos
      this.currentPage = 1 // 重置到第一页
      this.$message({
        message: '已重置',
        type: 'success'
      })
    },

    // 分页变化处理
    handlePageChange(page) {
      this.currentPage = page
      // 滚动到顶部
      this.$nextTick(() => {
        const container = document.querySelector('.page-container')
        if (container) {
          container.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      })
    }
  }
}
</script>

<style scoped>
.page-container {
  /* 抵消 App.vue 中 .app-main 的 padding: 10px */
  margin: -10px;
  /* 调整 min-height，考虑 App.vue 的 padding 和 AppMain 的高度计算 */
  min-height: calc(100vh - 84px + 20px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 24px;
  box-sizing: border-box;
}

/* 页面标题区域 */
.page-header {
  margin-bottom: 32px;
  padding: 24px 0;
  text-align: center;
}

.page-title {
  margin: 0 0 12px 0;
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  margin: 0;
  font-size: 16px;
  color: #606266;
  font-weight: 400;
  line-height: 1.6;
}

/* 搜索区域样式 */
.search-wrapper {
  margin-bottom: 32px;
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.search-box {
  display: flex;
  align-items: center;
  gap: 16px;
  max-width: 800px;
  margin: 0 auto;
}

.search-input {
  flex: 1;
}

.search-input >>> .el-input__inner {
  height: 42px;
  border-radius: 8px;
  font-size: 14px;
}

.search-btn,
.reset-btn {
  height: 42px;
  padding: 0 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.search-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #653a8f 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.reset-btn {
  background: #f5f7fa;
  border-color: #e4e7ed;
  color: #606266;
}

.reset-btn:hover {
  background: #ecf0f3;
  border-color: #c0c4cc;
}

/* 内容包装器 */
.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 卡片网格容器 */
.grid-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-gap: 20px;
  margin: 0;
  /* 移除固定最小高度，让容器根据内容自适应 */
}

/* 卡片基础样式 */
.grid-item {
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  padding: 0;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  min-height: 280px;
}

.grid-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

/* 卡片渐变背景样式 */
.card-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

/* 卡片头部 */
.card-header {
  padding: 16px 16px 12px;
  text-align: center;
  position: relative;
}

.card-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 0.5px;
}

/* 卡片内容区域 */
.card-content {
  flex: 1;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.98);
  min-height: 140px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
  flex-shrink: 0;
}

.info-value {
  font-size: 13px;
  color: #303133;
  font-weight: 600;
  text-align: right;
  flex: 1;
  margin-left: 12px;
  word-break: break-all;
}

/* 卡片底部 */
.card-footer {
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.98);
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: center;
  align-items: center;
}

.detail-btn {
  width: auto;
  min-width: 100px;
  padding: 0 24px;
  height: 36px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
  color: #ffffff;
}

.detail-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #653a8f 100%);
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.empty-state i {
  font-size: 64px;
  color: #c0c4cc;
  margin-bottom: 16px;
  display: block;
}

.empty-state p {
  font-size: 16px;
  color: #909399;
  margin-bottom: 24px;
}

.empty-state .el-button {
  padding: 12px 32px;
}

/* 分页组件样式 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px 0;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .grid-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1024px) {
  .grid-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .search-box {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .search-btn,
  .reset-btn {
    width: 100%;
  }

  .grid-container {
    grid-template-columns: 1fr;
    grid-gap: 16px;
  }
}
</style>
