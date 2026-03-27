<template>
  <div class="detail-container">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <el-button
          type="text"
          icon="el-icon-back"
          class="home-btn"
          @click="goBackToList"
        >
          返回列表
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 图片和详情信息区域 -->
      <div class="detail-header">
        <div class="image-wrapper">
          <el-image
            fit="cover"
            :src="getImageUrl(detailInfo.sportType)"
            class="sport-image"
            :lazy="true"
            :preview-src-list="[getImageUrl(detailInfo.sportType)]"
          >
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline" />
            </div>
          </el-image>
        </div>

        <div class="info-section">
          <div class="info-title">
            <i class="el-icon-info" />
            <h1>运动详情</h1>
          </div>
          <div class="info-list">
            <div class="info-row">
              <div class="info-label">
                <i class="el-icon-basketball" />
                <span>运动种类</span>
              </div>
              <div class="info-value">{{ detailInfo.sportType || '--' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <i class="el-icon-time" />
                <span>适宜时间</span>
              </div>
              <div class="info-value">{{ detailInfo.suitableTime || '--' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <i class="el-icon-view" />
                <span>运动心率</span>
              </div>
              <div class="info-value">{{ detailInfo.suitableHeartRate || '--' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <i class="el-icon-refresh" />
                <span>适宜频率</span>
              </div>
              <div class="info-value">{{ detailInfo.suitableFrequency || '--' }}</div>
            </div>
            <div class="info-row">
              <div class="info-label">
                <i class="el-icon-odometer" />
                <span>推荐速度</span>
              </div>
              <div class="info-value">{{ detailInfo.recommendedSpeed || '--' }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab按钮区域 -->
      <div class="tab-section">
        <div class="tab-buttons">
          <el-button
            :type="showForbiddenDiseases ? 'primary' : 'default'"
            :class="['tab-btn', { 'active': showForbiddenDiseases }]"
            icon="el-icon-warning"
            @click="showBox('forbiddenDiseases')"
          >
            禁忌疾病
          </el-button>
          <el-button
            :type="showMethodIntroduction ? 'primary' : 'default'"
            :class="['tab-btn', { 'active': showMethodIntroduction }]"
            icon="el-icon-document"
            @click="showBox('methodIntroduction')"
          >
            方法介绍
          </el-button>
          <el-button
            :type="showAttentionItems ? 'primary' : 'default'"
            :class="['tab-btn', { 'active': showAttentionItems }]"
            icon="el-icon-info"
            @click="showBox('attentionItems')"
          >
            注意事项
          </el-button>
        </div>
      </div>

      <!-- 内容卡片区域 -->
      <div class="content-section">
        <el-card v-if="showForbiddenDiseases" class="content-card" shadow="hover">
          <div slot="header" class="card-header">
            <i class="el-icon-warning" />
            <span>{{ detailInfo.sportType || '运动' }}禁忌疾病</span>
          </div>
          <div class="card-body">{{ detailInfo.disease || '暂无信息' }}</div>
        </el-card>

        <el-card v-if="showMethodIntroduction" class="content-card" shadow="hover">
          <div slot="header" class="card-header">
            <i class="el-icon-document" />
            <span>{{ detailInfo.sportType || '运动' }}方法介绍</span>
          </div>
          <div class="card-body">{{ detailInfo.method || '暂无信息' }}</div>
        </el-card>

        <el-card v-if="showAttentionItems" class="content-card" shadow="hover">
          <div slot="header" class="card-header">
            <i class="el-icon-info" />
            <span>{{ detailInfo.sportType || '运动' }}注意事项</span>
          </div>
          <div class="card-body">{{ detailInfo.notes || '暂无信息' }}</div>
        </el-card>
      </div>
    </div>
  </div>
</template>
<script>
import sportApi from '@/api/Function_Menu'
export default {
  data() {
    return {
      detailInfo: {},
      showForbiddenDiseases: true,
      showMethodIntroduction: false,
      showAttentionItems: false
    }
  },
  mounted() {
    // 优先使用sportInfoId获取数据，如果没有则使用id
    const sportInfoId = this.$route.query.sportInfoId
    const id = this.$route.query.id

    if (sportInfoId) {
      // 如果有sportInfoId，优先使用sportInfoId获取数据
      sportApi.getDetailBySportInfoId(sportInfoId).then((response) => {
        this.detailInfo = response.data
        // 确保侧边栏打开
        this.ensureSidebarOpen()
      }).catch((error) => {
        console.error('根据sportInfoId获取详情失败', error)
        // 如果根据sportInfoId获取失败，尝试使用id作为后备
        if (id) {
          this.loadDetailById(id)
        } else {
          this.$message.error('获取详情失败，请稍后重试')
          this.ensureSidebarOpen()
        }
      })
    } else if (id) {
      // 如果没有sportInfoId但有id，使用id获取数据
      this.loadDetailById(id)
    } else {
      // 如果既没有sportInfoId也没有id，显示错误提示
      this.$message.error('缺少必要的参数，无法加载详情')
      this.ensureSidebarOpen()
    }
  },
  methods: {
    // 根据id加载详情
    loadDetailById(id) {
      sportApi.getDetailById(id).then((response) => {
        this.detailInfo = response.data
        // 确保侧边栏打开
        this.ensureSidebarOpen()
      }).catch((error) => {
        console.error('获取详情失败', error)
        this.$message.error('获取详情失败，请稍后重试')
        this.ensureSidebarOpen()
      })
    },
    // 确保侧边栏打开
    ensureSidebarOpen() {
      // 只在侧边栏关闭时才打开，避免切换状态
      if (this.$store.state.app.sidebar && !this.$store.state.app.sidebar.opened) {
        this.$store.dispatch('app/toggleSideBar')
      }
      // 如果侧边栏已经打开，则不需要任何操作
    },
    // 获取图片URL - 优先使用上传的图片，如果没有则使用默认映射
    getImageUrl(sportType) {
      // 如果detailInfo中有imageUrl，优先使用上传的图片
      if (this.detailInfo && this.detailInfo.imageUrl) {
        // 如果是相对路径，拼接完整URL
        if (!this.detailInfo.imageUrl.startsWith('http') && !this.detailInfo.imageUrl.startsWith('data:')) {
          const baseUrl = process.env.VUE_APP_BASE_API || 'http://localhost:8080'
          return baseUrl.replace('/api', '') + '/' + this.detailInfo.imageUrl
        }
        return this.detailInfo.imageUrl
      }

      // 否则使用默认的图片映射
      const imageMap = {
        '瑜伽': 'https://img2.baidu.com/it/u=2510084494,3174869554&fm=253&fmt=auto&app=120&f=JPEG?w=1422&h=800',
        '跑步': 'https://img0.baidu.com/it/u=1104443773,2462974808&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500',
        '太极拳': 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fss2.meipian.me%2Fusers%2F8622319%2Fc755f97cda1646b7b61cf07e8b8ff530.jpg%3Fmeipian-raw%2Fbucket%2Fivwen%2Fkey%2FdXNlcnMvODYyMjMxOS9jNzU1Zjk3Y2RhMTY0NmI3YjYxY2YwN2U4YjhmZjUzMC5qcGc%3D%2Fsign%2F2f5cc377ac0b52301613d472737f32d6.jpg&refer=http%3A%2F%2Fss2.meipian.me&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1685467561&t=1e67f20bf6624a35af02cdd0af9853d6',
        '越野跑': 'https://img0.baidu.com/it/u=1104443773,2462974808&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500',
        '快跑': 'https://img0.baidu.com/it/u=1104443773,2462974808&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500',
        '慢跑': 'https://img0.baidu.com/it/u=1104443773,2462974808&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500',
        '羽毛球': 'https://dingyue.ws.126.net/KDl0ByjLstLLrx3NJHNFWHWwWAPFmjJ9ZfWPGF6I1=6VH1534509561915compressflag.jpg',
        '爬山': 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fss2.meipian.me%2Fusers%2F2363585%2F4155d213815a40ff853ea65eb2b2bb5b.jpg%3Fmeipian-raw%2Fbucket%2Fivwen%2Fkey%2FdXNlcnMvMjM2MzU4NS80MTU1ZDIxMzgxNWE0MGZmODUzZWE2NWViMmIyYmI1Yi5qcGc%3D%2Fsign%2F8a0187eb2f2c035dfd34168dda72cbf0.jpg&refer=http%3A%2F%2Fss2.meipian.me&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1685467756&t=34c700d3fc2bf0a700ab2809b9878138',
        '游泳': 'https://image.zsbtv.com.cn/images/2021/3/1/2021311614593116132_198.jpg',
        '滑冰': 'https://img2.baidu.com/it/u=969030030,3125151998&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500',
        '跆拳道': 'https://img.wifiwx.com/material/news/img/2018/11/ecdc969d7d62b41b4ef79aa75cbb8b93.jpg',
        '体操': 'https://img2.baidu.com/it/u=3179170246,2455220037&fm=253&fmt=auto&app=120&f=JPEG?w=1422&h=800',
        '拔河': 'https://img0.baidu.com/it/u=327984275,2262431634&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500'
      }
      return imageMap[sportType] || 'https://p5.toutiaoimg.com/origin/pgc-image/09530a0e2471422f9854cc7436c4ad97?from=pc'
    },
    showBox(box) {
      if (box === 'forbiddenDiseases') {
        this.showForbiddenDiseases = true
        this.showMethodIntroduction = false
        this.showAttentionItems = false
      } else if (box === 'methodIntroduction') {
        this.showForbiddenDiseases = false
        this.showMethodIntroduction = true
        this.showAttentionItems = false
      } else if (box === 'attentionItems') {
        this.showForbiddenDiseases = false
        this.showMethodIntroduction = false
        this.showAttentionItems = true
      }
    },
    goBackToList() {
      // 尝试返回上一页，如果上一页不是列表页面，则跳转到列表页面
      if (window.history.length > 1) {
        this.$router.go(-1)
      } else {
        // 如果无法返回，则尝试跳转到列表页面
        // 注意：这里需要根据实际的路由路径调整
        this.$router.push({ path: '/test/test3' }).catch(() => {
          // 如果跳转失败，则返回主页
          this.$router.push({ path: '/dashboard' })
        })
      }
    }
  }
}
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 50%, #f0f4f8 100%);
  padding: 20px;
  box-sizing: border-box;
}

/* 顶部导航栏 */
.top-nav {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  padding: 12px 20px;
}

.nav-content {
  display: flex;
  align-items: center;
}

.home-btn {
  color: #409EFF;
  font-size: 14px;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

.home-btn:hover {
  color: #66b1ff;
  background-color: #ecf5ff;
  border-radius: 6px;
}

.home-btn i {
  margin-right: 6px;
  font-size: 16px;
}

/* 主要内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
}

/* 详情头部区域 */
.detail-header {
  display: flex;
  gap: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
  margin-bottom: 24px;
  transition: all 0.3s ease;
}

.detail-header:hover {
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

/* 图片区域 */
.image-wrapper {
  flex: 1;
  min-width: 400px;
  max-width: 500px;
  height: 400px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.image-wrapper:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.15);
}

.sport-image {
  width: 100%;
  height: 100%;
  border-radius: 16px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 48px;
}

/* 信息区域 */
.info-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 16px;
}

.info-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e4e7ed;
}

.info-title i {
  font-size: 28px;
  color: #409EFF;
}

.info-title h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.info-row:hover {
  background: #f0f2f5;
  border-left-color: #409EFF;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.info-label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #606266;
  min-width: 120px;
}

.info-label i {
  font-size: 18px;
  color: #409EFF;
}

.info-value {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
  text-align: right;
  flex: 1;
}

/* Tab按钮区域 */
.tab-section {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 20px 24px;
  margin-bottom: 24px;
}

.tab-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.tab-btn {
  flex: 1;
  max-width: 200px;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.tab-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.tab-btn.active {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border-color: #409EFF;
  color: #fff;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
}

.tab-btn i {
  margin-right: 8px;
  font-size: 18px;
}

/* 内容卡片区域 */
.content-section {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
  min-height: 300px;
}

.content-card {
  border: none;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.content-card:hover {
  transform: translateY(-4px);
}

.content-card >>> .el-card__header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 2px solid #e4e7ed;
  padding: 16px 20px;
  border-radius: 12px 12px 0 0;
}

.content-card >>> .el-card__body {
  padding: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.card-header i {
  font-size: 24px;
  color: #409EFF;
}

.card-body {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .detail-header {
    flex-direction: column;
  }

  .image-wrapper {
    width: 100%;
    max-width: 100%;
    height: 350px;
  }

  .info-section {
    padding: 0;
  }
}

@media (max-width: 768px) {
  .detail-container {
    padding: 12px;
  }

  .detail-header {
    padding: 16px;
  }

  .image-wrapper {
    height: 280px;
    min-width: 100%;
  }

  .info-title h1 {
    font-size: 24px;
  }

  .tab-buttons {
    flex-direction: column;
  }

  .tab-btn {
    max-width: 100%;
  }

  .info-label {
    min-width: 100px;
    font-size: 14px;
  }

  .info-value {
    font-size: 14px;
  }

  .card-header {
    font-size: 18px;
  }

  .card-body {
    font-size: 14px;
  }
}
</style>
