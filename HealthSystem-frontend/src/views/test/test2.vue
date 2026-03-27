<template>
  <div v-loading="loading" class="analysis-container" element-loading-text="健康评估数据正在努力生成中，请稍等片刻...">
    <!-- 空状态提示 -->
    <div v-if="!loading && !bodyInfo" class="empty-state">
      <i class="el-icon-warning-outline" />
      <p>暂无身体信息数据</p>
      <el-button type="primary" @click="getBodyInfo">重新加载</el-button>
    </div>

    <!-- 卡片始终渲染，保持布局结构，避免上面区域空白 -->
    <template v-else>
      <div class="analysis-card score-card">
        <div class="score-wrapper">
          <div class="score-icon">
            <i class="el-icon-trophy" />
          </div>
          <div class="score-title">健康评分</div>
          <div class="score-value">{{ score || 0 }}</div>
          <div class="score-unit">分</div>
        </div>
      </div>

      <div class="analysis-card body-info-card">
        <div class="card-header">
          <i class="el-icon-user card-icon" />
          <h2 class="card-title">身体信息</h2>
        </div>
        <div class="card-content">
          <div class="info-item">
            <div class="info-label">体重</div>
            <div class="info-value">
              {{ (bodyInfo && bodyInfo.weight) ? bodyInfo.weight : '--' }}<span class="info-unit">kg</span>
            </div>
          </div>
          <div class="info-item">
            <div class="info-label">身高</div>
            <div class="info-value">
              {{ (bodyInfo && bodyInfo.height) ? bodyInfo.height : '--' }}<span class="info-unit">m</span>
            </div>
          </div>
          <div class="info-item">
            <div class="info-label">BMI</div>
            <div class="info-value bmi-value">{{ bmiM || '--' }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">血糖</div>
            <div class="info-value">
              {{ (bodyInfo && bodyInfo.bloodSugar) ? bodyInfo.bloodSugar : '--' }}<span class="info-unit">mmol/L</span>
            </div>
          </div>
          <div class="info-item">
            <div class="info-label">血压</div>
            <div class="info-value">
              {{ (bodyInfo && bodyInfo.bloodPressure) ? bodyInfo.bloodPressure : '--' }}<span class="info-unit">mmHg</span>
            </div>
          </div>
          <div class="info-item">
            <div class="info-label">胆固醇</div>
            <div class="info-value">
              {{ (bodyInfo && bodyInfo.bloodLipid) ? bodyInfo.bloodLipid : '--' }}<span class="info-unit">mmol/l</span>
            </div>
          </div>
          <div class="info-item">
            <div class="info-label">心率</div>
            <div class="info-value">
              {{ (bodyInfo && bodyInfo.heartRate) ? bodyInfo.heartRate : '--' }}<span class="info-unit">次/分钟</span>
            </div>
          </div>
          <div class="info-item">
            <div class="info-label">视力</div>
            <div class="info-value">
              {{ (bodyInfo && bodyInfo.vision !== undefined && bodyInfo.vision !== null) ? bodyInfo.vision : '--' }}<span class="info-unit">度</span>
            </div>
          </div>
        </div>
      </div>

      <div class="analysis-card disease-card">
        <div class="card-header">
          <i class="el-icon-warning card-icon" />
          <h2 class="card-title">疾病分析</h2>
        </div>
        <div class="card-content">
          <div class="analysis-item">
            <div class="analysis-label">可能的疾病：</div>
            <div class="analysis-value">{{ risk || '暂无' }}</div>
          </div>
          <div class="analysis-item warning-item">
            <div class="analysis-label warning-label">
              <i class="el-icon-info" /> 注意：
            </div>
            <div class="analysis-value warning-text">
              以上风险只是根据您上传的身体数据进行最基本的分析，并不能作为真正的结果，不管有没有风险，都需要保持运动，如有不舒服的地方请马上就医。
            </div>
          </div>
        </div>
      </div>

      <div class="analysis-card energy-card">
        <div class="card-header">
          <i class="el-icon-lightning card-icon" />
          <h2 class="card-title">基础能量消耗状况</h2>
        </div>
        <div class="card-content energy-content">
          <div class="progress-item">
            <el-progress
              type="circle"
              :percentage="Number(Standard_hight) || 0"
              :width="100"
              :stroke-width="8"
              color="#409EFF"
            />
            <div class="progress-label">到达身体年龄的百分比</div>
          </div>
          <div class="progress-item">
            <el-progress
              type="circle"
              :percentage="Number(BMR) || 0"
              :width="100"
              :stroke-width="8"
              color="#67C23A"
            />
            <div class="progress-label">基本能量消耗</div>
          </div>
        </div>
      </div>

      <div class="analysis-card obesity-card">
        <div class="card-header">
          <i class="el-icon-s-data card-icon" />
          <h2 class="card-title">肥胖分析</h2>
        </div>
        <div class="card-content">
          <div class="progress-bar-wrapper">
            <el-progress
              :percentage="percentage"
              :show-text="true"
              :stroke-width="20"
              color="#E6A23C"
            />
          </div>
          <div class="analysis-item">
            <div class="analysis-label">根据计算：</div>
            <div class="analysis-value">{{ determineHealthRisk || '--' }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">肥胖可能的风险：</div>
            <div class="analysis-value">{{ Disease_risk || '--' }}</div>
          </div>
        </div>
      </div>

      <div class="analysis-card habit-card">
        <div class="card-header">
          <i class="el-icon-star-on card-icon" />
          <h2 class="card-title">生活习惯分析</h2>
        </div>
        <div class="card-content">
          <div class="analysis-item">
            <div class="analysis-label">您的习惯如下：</div>
            <div class="analysis-value habits-text">{{ habits || '--' }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">建议：</div>
            <div class="analysis-value">
              阅读运动知识，更好地了解运动的正确姿势和方法，通过了解运动的原理和科学知识，我们可以更好地制定运动计划，减少运动中的风险和不适，避免受伤和疾病的发生。
            </div>
          </div>
        </div>
      </div>

      <div class="analysis-card vision-card">
        <div class="card-header">
          <i class="el-icon-view card-icon" />
          <h2 class="card-title">视力分析</h2>
        </div>
        <div class="card-content">
          <div class="analysis-item">
            <div class="analysis-label">您的视力为：</div>
            <div class="analysis-value">
              {{ (bodyInfo && bodyInfo.vision !== undefined && bodyInfo.vision !== null) ? bodyInfo.vision : '--' }}<span class="info-unit">度</span>
            </div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">近视等级：</div>
            <div class="analysis-value">{{ visionType || '--' }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">建议：</div>
            <div class="analysis-value">{{ visionSuggestion || '--' }}</div>
          </div>
        </div>
      </div>

      <div class="analysis-card bodytype-card">
        <div class="card-header">
          <i class="el-icon-sort card-icon" />
          <h2 class="card-title">体型判断</h2>
        </div>
        <div class="card-content">
          <div class="analysis-item">
            <div class="analysis-label">您的体型属于：</div>
            <div class="analysis-value bodytype-value">{{ bodyType || '--' }}</div>
          </div>
          <div class="analysis-item">
            <div class="analysis-label">建议：</div>
            <div class="analysis-value">{{ bodyTypeSuggestion || '--' }}</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import userApi from '@/api/userManage'
import axios from 'axios'

export default {
  data() {
    return {
      bodyInfo: '',
      bmi: null,
      risk: '',
      Standard_hight: null,
      metabolic_rate: null,
      BMR: null,
      habits: '',
      score: 100,
      habits_count: [],
      loading: false,
      cancelTokenSource: null // 用于取消请求
    }
  },

  computed: {
    bmiM() {
      // 添加空值检查，避免 bodyInfo 为空时报错
      if (!this.bodyInfo || !this.bodyInfo.weight || !this.bodyInfo.height) {
        return '--'
      }
      // 从bodyInfo中获取身高和体重的值，并转换为 Number 类型
      const weight = Number(this.bodyInfo.weight)
      const height = Number(this.bodyInfo.height)
      // 计算BMI值
      if (!weight || !height || height === 0) {
        return '--'
      }
      const bmiValue = weight / (height * height)
      // 返回计算结果并保留两位小数
      return bmiValue.toFixed(2)
    },

    percentage() {
      if (!this.bodyInfo) return 0
      const bmiValue = parseFloat(this.bmiM) || 0
      if (isNaN(bmiValue) || bmiValue === 0) return 0
      return Math.round((bmiValue / 35) * 100)
    },
    visionType() {
      if (!this.bodyInfo || this.bodyInfo.vision === undefined || this.bodyInfo.vision === null) {
        return ''
      }
      const vision = this.bodyInfo.vision
      if (vision >= 600) {
        return '高度近视'
      } else if (vision >= 300 && vision <= 600) {
        return '中度近视'
      } else if (vision > 0 && vision <= 300) {
        return '轻度近视'
      } else if (vision === 0) {
        return '没有近视'
      }
      return ''
    },

    visionSuggestion() {
      const visionType = this.visionType
      if (visionType === '高度近视') {
        return '积极治疗，建议就医'
      } else if (visionType === '中度近视') {
        return '注意保护眼睛，建议定期检查视力'
      } else if (visionType === '轻度近视') {
        return '加强锻炼，注意用眼卫生'
      } else if (visionType === '没有近视') {
        return '很好，保持生活习惯，注意保护眼睛'
      }
      return ''
    },

    bodyType() {
      if (!this.bodyInfo) return ''
      const bmiValue = parseFloat(this.bmiM) || 0
      if (isNaN(bmiValue)) return ''
      if (bmiValue >= 28) {
        return '肥胖型'
      } else if (bmiValue > 24 && bmiValue <= 28) {
        return '超重体型'
      } else if (bmiValue >= 0 && bmiValue <= 24) {
        return '正常体型'
      }
      return ''
    },

    determineHealthRisk() {
      if (!this.bodyInfo) return ''
      const bmiValue = parseFloat(this.bmiM) || 0
      if (isNaN(bmiValue)) return ''
      if (bmiValue >= 28) {
        return '您的体重太大了，请马上减肥'
      } else if (bmiValue > 24 && bmiValue <= 28) {
        return '您的体重过大，请及时减肥'
      } else if (bmiValue >= 0 && bmiValue <= 24) {
        return '您的体重正常，请保持健康生活'
      }
      return ''
    },
    Disease_risk() {
      if (!this.bodyInfo) return ''
      const bmiValue = parseFloat(this.bmiM) || 0
      if (isNaN(bmiValue)) return ''
      if (bmiValue >= 28) {
        return '心脏病、中风、高血压和高胆固醇，增加心脏病，还有糖尿病、呼吸系统疾病、关节炎等风险'
      } else if (bmiValue > 24 && bmiValue <= 28) {
        return '容易导致高血压、高胆固醇、心脏病、中风、患糖尿病的风险，胰岛素分泌异常以及呼吸系统疾病'
      } else if (bmiValue >= 0 && bmiValue <= 24) {
        return '风险不大，但是要保证摄入足够的蛋白质、碳水化合物和脂肪'
      }
      return ''
    },

    bodyTypeSuggestion() {
      if (this.bodyType === '肥胖型') {
        return '控制饮食，增加运动量，并寻求专业医师的指导。'
      } else if (this.bodyType === '超重体型') {
        return '注意饮食健康，控制摄入量，并加强有氧运动，提高身体代谢率。'
      } else if (this.bodyType === '正常体型') {
        return '保持良好的生活习惯，适当参加运动，均衡饮食，保持身体健康。'
      }
      return ''
    }
  },

  watch: {
    bodyInfo: {
      deep: true,
      handler() {
        if (!this.bodyInfo) return

        // 重置 risk，避免重复拼接
        this.risk = ''
        this.diseaserisk()
        this.compareAgeAndHeight()
        this.supersession()
        this.habits_customs()
        this.scoreCom()
        // 更新 bmi 值
        if (this.bodyInfo && this.bodyInfo.weight && this.bodyInfo.height) {
          const weight = Number(this.bodyInfo.weight)
          const bmiValue = weight / (this.bodyInfo.height * this.bodyInfo.height)
          this.bmi = bmiValue.toFixed(2)
        }
      }
    }
  },

  created() {
    this.getBodyInfo()
  },

  mounted() {
    // 确保页面滚动位置在顶部
    this.resetScrollPosition()

    // 监听 loading 状态，数据加载完成后再次重置
    this.$watch('loading', (newVal) => {
      if (!newVal) {
        // 数据加载完成后，延迟重置滚动位置
        this.$nextTick(() => {
          setTimeout(() => {
            this.resetScrollPosition()
          }, 100)
        })
      }
    })
  },

  beforeDestroy() {
    // 组件销毁时取消正在进行的请求
    if (this.cancelTokenSource) {
      this.cancelTokenSource.cancel('组件已销毁，取消请求')
    }
  },

  methods: {
    resetScrollPosition() {
      // 重置滚动位置
      this.$nextTick(() => {
        const appMain = document.querySelector('.app-main')
        if (appMain) {
          appMain.scrollTop = 0
        }
        window.scrollTo(0, 0)
      })
    },

    async getBodyInfo() {
      // 取消之前的请求
      if (this.cancelTokenSource) {
        this.cancelTokenSource.cancel('新的请求已发起，取消旧请求')
      }

      // 创建新的取消令牌
      this.cancelTokenSource = axios.CancelToken.source()

      this.loading = true
      try {
        // 使用解构赋值从 userApi.getBodyInfo() 返回的 Promise 对象中提取 data.bodyList 数组的第一个元素（即 bodyInfo 对象）
        const {
          data: {
            bodyList: [bodyInfo]
          }
        } = await userApi.getBodyInfo({
          cancelToken: this.cancelTokenSource.token
        })

        // 检查组件是否还存在（避免在请求返回前组件已销毁）
        if (this._isDestroyed) return

        this.bodyInfo = bodyInfo || null
      } catch (error) {
        // 如果是取消请求，不显示错误
        if (axios.isCancel(error)) {
          console.log('请求已取消:', error.message)
          return
        }

        // 检查组件是否还存在
        if (this._isDestroyed) return

        this.$message.error('获取身体信息失败，请稍后重试')
        console.error('获取身体信息错误:', error)
        this.bodyInfo = null
      } finally {
        if (!this._isDestroyed) {
          this.loading = false
        }
      }
    },

    habits_customs() {
      if (!this.bodyInfo) {
        this.habits = '--'
        return
      }

      const habits = []
      this.habits_count = habits
      // 判断膳食习惯
      if (this.bodyInfo.foodTypes === '蔬菜') {
        habits.push('爱吃蔬菜')
      }
      if (this.bodyInfo.foodTypes === '水果') {
        habits.push('爱吃水果')
      }
      if (this.bodyInfo.foodTypes === '肉类') {
        habits.push('爱吃肉')
      }
      if (this.bodyInfo.foodTypes === '鱼类') {
        habits.push('爱吃鱼')
      }
      if (this.bodyInfo.foodTypes === '豆类') {
        habits.push('爱吃豆类')
      }
      if (this.bodyInfo.foodTypes === '谷物') {
        habits.push('爱吃五谷')
      }

      if (this.bodyInfo.bloodSugar > 7) {
        habits.push('摄入的糖分和生活习惯不好')
      } else {
        habits.push('血糖水平正常')
      }

      if (this.bodyInfo.bloodPressure > 5.2) {
        habits.push('高胆固醇饮食')
      } else {
        habits.push('低胆固醇饮食')
      }

      if (this.bodyInfo.heartRate > 100) {
        habits.push('经常紧张焦虑和压力')
      } else {
        habits.push('心情还不错')
      }

      if (this.bodyInfo.vision > 50) {
        habits.push('熬夜过多过度劳累')
      } else {
        habits.push('准时睡觉')
      }

      if (this.bodyInfo.sleepDuration < 8) {
        habits.push('睡眠不足')
      } else {
        habits.push('睡眠充足')
      }

      if (this.bodyInfo.sleepQuality === '好') {
        habits.push('熬夜过多过度劳累')
      } else if (this.bodyInfo.sleepQuality === '一般') {
        habits.push('需要注意睡眠质量')
      } else if (this.bodyInfo.sleepQuality === '差') {
        habits.push('需要改善睡眠质量')
      }
      if (this.bodyInfo.smoking === true) {
        habits.push('吸烟')
      } else {
        habits.push('不吸烟')
      }

      if (this.bodyInfo.drinking === true) {
        habits.push('饮酒')
      } else {
        habits.push('未饮酒')
      }
      if (this.bodyInfo.exercise === true) {
        habits.push('积极锻炼')
      } else {
        habits.push('缺乏运动')
      }

      if (this.bodyInfo.waterConsumption < 1000) {
        habits.push('饮水不足')
      } else {
        habits.push('饮水充足')
      }

      this.habits = habits.join('，')
    },

    diseaserisk() {
      if (!this.bodyInfo) return

      // risk 已在 watch 中重置，这里直接拼接
      if (this.bodyInfo.bloodPressure >= 90) {
        this.risk += '高血压，'
      }
      if (this.bodyInfo.bloodLipid > 3) {
        this.risk += '高血脂，'
      }
      if (this.bodyInfo.bloodSugar > 6.1) {
        this.risk += '糖尿病，'
      }
      if (this.bodyInfo.drinking > 3) {
        this.risk += '酗酒，'
      }
      if (this.bodyInfo.exercise < 3) {
        this.risk += '缺乏运动，'
      }
      if (this.bodyInfo.heartRate > 100) {
        this.risk += '心率过快，'
      }
      if (this.bodyInfo.sleepDuration < 7 || this.bodyInfo.sleepQuality < 3) {
        this.risk += '睡眠不足，'
      }
      if (this.bodyInfo.smoking > 0) {
        this.risk += '肺炎，'
      }
      if (this.bodyInfo.vision <= 300) {
        this.risk += '近视，'
      }
      if (this.bodyInfo.waterConsumption < 1500) {
        this.risk += '结石'
      }

      // 移除末尾的逗号
      if (this.risk.endsWith('，')) {
        this.risk = this.risk.slice(0, -1)
      }
    },

    compareAgeAndHeight() {
      if (!this.bodyInfo || !this.bodyInfo.height || !this.bodyInfo.sex) {
        this.Standard_hight = null
        return
      }
      const height = this.bodyInfo.height
      const sex = this.bodyInfo.sex
      let Standardhight = null
      if (sex === '男') {
        Standardhight = (height - 80) * 0.7 + 160
        this.Standard_hight = ((Standardhight / height) * 1).toFixed(2)
      } else {
        Standardhight = (height - 70) * 0.6 + 160
        this.Standard_hight = ((Standardhight / height) * 1).toFixed(2)
      }
    },

    supersession() {
      if (!this.bodyInfo || !this.bodyInfo.height || !this.bodyInfo.weight || !this.bodyInfo.age) {
        this.BMR = null
        return
      }
      const height = this.bodyInfo.height
      const weight = this.bodyInfo.weight
      const age = this.bodyInfo.age
      const BMR1 = (88.36 + 13.4 * weight + 4.8 * height - 5.7 * age).toFixed(
        2
      )
      const reference = 1200 // 假设BMR的参考值为每天所需热量的1200卡路里

      this.BMR = Math.round((BMR1 / reference) * 100) // 计算BMR占参考值的百分比
    },

    scoreCom() {
      // 重置分数
      this.score = 100

      if (!this.bodyInfo) return

      if (this.bodyInfo.vision > 30) {
        this.score = this.score - 25
      }
      if ((this.risk.match(/，/g) || []).length > 6) {
        this.score = this.score - 25
      }

      if (this.bmi > 24) {
        this.score = this.score - 25
      }
      if (this.Standard_hight < 80) {
        this.score = this.score - 25
      }
    }
  }
}
</script>

<style scoped>
.analysis-container {
  /* 抵消 App.vue 中 .app-main 的 padding: 10px */
  margin: -10px;
  /* 调整 min-height，考虑 App.vue 的 padding 和 AppMain 的高度计算 */
  min-height: calc(100vh - 84px + 20px);
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 50%, #f0f4f8 100%);
  box-sizing: border-box;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  /* 明确指定行高，确保布局从一开始就稳定，避免位置跳动 */
  grid-template-rows: auto auto auto auto auto;
  gap: 20px;
  /* 确保内容从顶部开始排列 */
  align-content: start;
}

.analysis-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  /* 设置最小高度，确保卡片在数据加载前就有占位空间，避免布局跳动 */
  min-height: 120px;
}

.analysis-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 2px solid #e9ecef;
}

.card-icon {
  font-size: 22px;
  color: #409EFF;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-content {
  padding: 16px 18px;
  flex: 1;
}

/* 健康评分卡片 */
.score-card {
  grid-column: 1 / 2;
  grid-row: 1 / 2;
  min-height: 160px;
}

/* 身体信息卡片 */
.body-info-card {
  grid-column: 1 / 2;
  grid-row: 2 / 4;
  min-height: 400px;
}

/* 疾病分析卡片 */
.disease-card {
  grid-column: 2 / 3;
  grid-row: 1 / 2;
  min-height: 160px;
}

/* 能量消耗卡片 */
.energy-card {
  grid-column: 2 / 3;
  grid-row: 2 / 3;
  min-height: 190px;
}

/* 肥胖分析卡片 */
.obesity-card {
  grid-column: 2 / 3;
  grid-row: 3 / 4;
  min-height: 190px;
}

/* 生活习惯卡片 */
.habit-card {
  grid-column: 1 / 2;
  grid-row: 4 / 5;
  min-height: 150px;
}

/* 视力分析卡片 */
.vision-card {
  grid-column: 2 / 3;
  grid-row: 4 / 5;
  min-height: 150px;
}

/* 体型判断卡片 */
.bodytype-card {
  grid-column: 1 / 3;
  grid-row: 5 / 6;
  min-height: 120px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item:hover {
  background-color: #f8f9fa;
  padding-left: 8px;
  padding-right: 8px;
  border-radius: 6px;
}

.info-label {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  min-width: 80px;
}

.info-value {
  font-size: 15px;
  font-weight: 700;
  color: #303133;
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.bmi-value {
  color: #E6A23C;
  font-size: 16px;
}

.info-unit {
  font-size: 12px;
  font-weight: 500;
  color: #909399;
  margin-left: 4px;
}

/* 分析项样式 */
.analysis-item {
  margin-bottom: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #409EFF;
  transition: all 0.3s ease;
}

.analysis-item:last-child {
  margin-bottom: 0;
}

.analysis-item:hover {
  background: #f0f2f5;
  transform: translateX(3px);
}

.analysis-label {
  font-size: 14px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.analysis-value {
  font-size: 13px;
  line-height: 1.6;
  color: #606266;
  word-break: break-word;
}

.warning-item {
  background: #fff3e0;
  border-left-color: #ff9800;
}

.warning-label {
  color: #f56c6c;
}

.warning-text {
  color: #e65100;
  font-weight: 500;
}

/* 能量消耗卡片 */
.energy-card .card-icon {
  color: #67C23A;
}

.energy-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  padding: 20px 10px;
}

.progress-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.progress-label {
  font-size: 12px;
  font-weight: 600;
  color: #606266;
  text-align: center;
}

/* 肥胖分析卡片 */
.obesity-card .card-icon {
  color: #E6A23C;
}

.progress-bar-wrapper {
  margin-bottom: 16px;
  padding: 14px;
  background: #f8f9fa;
  border-radius: 8px;
}

/* 生活习惯卡片 */
.habit-card .card-icon {
  color: #909399;
}

.habits-text {
  line-height: 1.8;
  color: #606266;
}

/* 视力分析卡片 */
.vision-card .card-icon {
  color: #409EFF;
}

/* 体型判断卡片 */
.bodytype-card .card-icon {
  color: #67C23A;
}

.bodytype-value {
  font-size: 16px;
  font-weight: 700;
  color: #67C23A;
}

/* 健康评分卡片 */
.score-card {
  background: linear-gradient(135deg, #a8e6cf 0%, #7fcdbb 50%, #6bc4a8 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 160px;
}

.score-wrapper {
  text-align: center;
  padding: 20px;
}

.score-icon {
  font-size: 48px;
  color: #ffd700;
  margin-bottom: 12px;
  animation: rotate 3s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.score-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #fff;
}

.score-value {
  font-size: 56px;
  font-weight: 700;
  color: #ffd700;
  line-height: 1;
  margin-bottom: 6px;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.score-unit {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .analysis-container {
    padding: 16px;
    gap: 16px;
  }

  .card-header {
    padding: 12px 16px;
  }

  .card-content {
    padding: 14px 16px;
  }

  .energy-content {
    flex-direction: column;
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .analysis-container {
    grid-template-columns: 1fr;
    padding: 12px;
    gap: 12px;
  }

  .score-card {
    grid-column: 1;
    grid-row: 1;
  }

  .body-info-card {
    grid-column: 1;
    grid-row: 2;
  }

  .card-title {
    font-size: 16px;
  }

  .card-icon {
    font-size: 20px;
  }

  .info-label {
    font-size: 13px;
    min-width: 70px;
  }

  .info-value {
    font-size: 14px;
  }

  .analysis-label {
    font-size: 13px;
  }

  .analysis-value {
    font-size: 12px;
  }

  .score-value {
    font-size: 48px;
  }

  .score-icon {
    font-size: 40px;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #909399;
}

.empty-state i {
  font-size: 64px;
  margin-bottom: 20px;
  color: #c0c4cc;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 20px;
}
</style>
