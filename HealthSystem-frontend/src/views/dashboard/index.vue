<template>
  <div class="dashboard-container">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="24" style="text-align: right;">
        <el-button type="primary" icon="el-icon-date" @click="openAppointmentDialog">
          预约体检
        </el-button>
      </el-col>
    </el-row>

    <el-row class="stats-row" :gutter="20">
      <el-col :span="6">
        <div class="stat-card stat-card-height">
          <div class="stat-icon-wrapper">
            <i class="el-icon-sort stat-icon" />
          </div>
          <div class="stat-content">
            <div class="stat-label">身高</div>
            <div class="stat-value">
              {{ bodyInfo.height || '--' }}<span class="unit">m</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-weight">
          <div class="stat-icon-wrapper">
            <i class="el-icon-odometer stat-icon" />
          </div>
          <div class="stat-content">
            <div class="stat-label">体重</div>
            <div class="stat-value">
              {{ bodyInfo.weight || '--' }}<span class="unit">kg</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-bmi">
          <div class="stat-icon-wrapper">
            <i class="el-icon-s-data stat-icon" />
          </div>
          <div class="stat-content">
            <div class="stat-label">BMI指数</div>
            <div class="stat-value">
              {{ bmi || '--' }}<span class="unit" />
            </div>
            <div v-if="bmi" class="stat-status">
              <span :class="getBMIClass(bmi)">{{ getBMIStatus(bmi) }}</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card-age">
          <div class="stat-icon-wrapper">
            <i class="el-icon-time stat-icon" />
          </div>
          <div class="stat-content">
            <div class="stat-label">年龄</div>
            <div class="stat-value">
              {{ bodyInfo.age || '--' }}<span class="unit">岁</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row class="charts-row" :gutter="20">
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <i class="el-icon-s-marketing chart-icon" />
            <span class="chart-title">心率变化趋势</span>
          </div>
          <div ref="myChart" class="chart-container" />
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <i class="el-icon-view chart-icon" />
            <span class="chart-title">视力变化趋势</span>
          </div>
          <div id="chart-container" class="chart-container" />
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <i class="el-icon-s-operation chart-icon" />
            <span class="chart-title">血压血糖变化</span>
          </div>
          <div id="chart-containerLine" class="chart-container" />
        </div>
      </el-col>
    </el-row>

    <el-dialog
      title="预约体检"
      :visible.sync="appointmentDialogVisible"
      width="520px"
      @close="resetAppointmentForm"
    >
      <el-form ref="appointmentFormRef" :model="appointmentForm" :rules="appointmentRules" label-width="90px">
        <el-form-item label="体检时间" prop="examTime">
          <el-date-picker
            v-model="appointmentForm.examTime"
            type="datetime"
            placeholder="选择体检时间"
            value-format="timestamp"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="体检地点" prop="location">
          <el-input v-model="appointmentForm.location" placeholder="如：XX医院体检中心" maxlength="100" show-word-limit />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="appointmentDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="appointmentSubmitting" @click="submitAppointment">
          提 交
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'

import userApi from '@/api/userManage'
import FunctionApi from '@/api/Function_Menu'
import appointmentApi from '@/api/appointment'
export default {
  data() {
    return {
      charts: '',
      bodyInfo: '',
      bmi: null,
      score: null,
      BodyNotesInfo: '',

      vision: [],
      waterConsumption: [],
      bloodSugar: [],
      bloodPressure: [],
      date: [],
      heartRate: [],

      appointmentDialogVisible: false,
      appointmentSubmitting: false,
      appointmentForm: {
        examTime: null, // timestamp(ms)
        location: ''
      },
      appointmentRules: {
        examTime: [{ required: true, message: '请选择体检时间', trigger: 'change' }],
        location: [{ required: true, message: '请输入体检地点', trigger: 'blur' }]
      }
    }
  },

  watch: {
    bodyInfo: {
      deep: true, // 监听对象内部属性的变化
      async handler() {
        this.bmiM() // 计算BMI值
        await this.getBodyNotes() // 获取身体数据信息
        this.BarChart()
        this.area()
        const chartDom = this.$refs.myChart
        const myChart = echarts.init(chartDom)

        const option = {
          grid: {
            left: '10%',
            right: '10%',
            bottom: '15%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: this.date,
            axisLabel: {
              fontSize: 11,
              interval: 2,
              color: '#666'
            },
            axisLine: {
              lineStyle: {
                color: '#e0e0e0'
              }
            },
            axisTick: {
              show: false
            }
          },
          yAxis: {
            type: 'value',
            name: '心率(bpm)',
            nameTextStyle: {
              color: '#666',
              fontSize: 12
            },
            axisLine: {
              show: false
            },
            splitLine: {
              lineStyle: {
                type: 'dashed',
                color: '#e8e8e8'
              }
            },
            axisLabel: {
              color: '#666',
              fontSize: 11
            },
            axisTick: {
              show: false
            }
          },
          tooltip: {
            trigger: 'axis',
            backgroundColor: 'rgba(50, 50, 50, 0.9)',
            borderColor: '#4fc3f7',
            borderWidth: 1,
            textStyle: {
              color: '#fff'
            },
            formatter: function(params) {
              return params[0].name + '<br/>心率：' + params[0].value + ' bpm'
            }
          },
          series: [
            {
              name: '心率',
              data: this.heartRate,
              type: 'line',
              smooth: true,
              lineStyle: {
                width: 3,
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 1,
                  y2: 0,
                  colorStops: [
                    { offset: 0, color: '#4fc3f7' },
                    { offset: 1, color: '#29b6f6' }
                  ]
                }
              },
              symbol: 'circle',
              symbolSize: 6,
              itemStyle: {
                color: '#4fc3f7',
                borderColor: '#fff',
                borderWidth: 2
              },
              areaStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 0,
                  y2: 1,
                  colorStops: [
                    { offset: 0, color: 'rgba(79, 195, 247, 0.3)' },
                    { offset: 1, color: 'rgba(79, 195, 247, 0.05)' }
                  ]
                }
              },
              markLine: {
                data: [
                  {
                    type: 'average',
                    name: '平均值',
                    label: {
                      position: 'insideEndBottom',
                      formatter: '平均值：{c}',
                      color: '#666'
                    },
                    lineStyle: {
                      type: 'dashed',
                      color: '#66bb6a',
                      width: 2
                    },
                    symbol: 'none'
                  }
                ]
              },
              animation: true,
              animationDuration: 2000,
              animationEasing: 'cubicOut'
            }
          ]
        }

        myChart.setOption(option)
      }
    }
  },

  created() {
    this.getBodyInfo()
  },

  async mounted() {},
  methods: {
    openAppointmentDialog() {
      this.appointmentDialogVisible = true
    },

    resetAppointmentForm() {
      this.appointmentSubmitting = false
      this.appointmentForm = {
        examTime: null,
        location: ''
      }
      if (this.$refs.appointmentFormRef) {
        this.$refs.appointmentFormRef.clearValidate()
      }
    },

    submitAppointment() {
      if (this.appointmentSubmitting) return
      this.$refs.appointmentFormRef.validate(async(valid) => {
        if (!valid) return
        this.appointmentSubmitting = true
        try {
          const payload = {
            examTime: this.appointmentForm.examTime, // timestamp(ms)
            location: this.appointmentForm.location
          }
          const res = await appointmentApi.createAppointment(payload)
          this.$message.success(res.message || '预约成功')
          this.appointmentDialogVisible = false
          this.resetAppointmentForm()
        } catch (e) {
          this.$message.error((e && e.message) || '预约失败，请稍后重试')
        } finally {
          this.appointmentSubmitting = false
        }
      })
    },

    getBMIStatus(bmi) {
      const bmiValue = parseFloat(bmi)
      if (bmiValue < 18.5) {
        return '偏瘦'
      } else if (bmiValue < 24) {
        return '正常'
      } else if (bmiValue < 28) {
        return '偏胖'
      } else {
        return '肥胖'
      }
    },
    getBMIClass(bmi) {
      const bmiValue = parseFloat(bmi)
      if (bmiValue < 18.5) {
        return 'status-warning'
      } else if (bmiValue < 24) {
        return 'status-success'
      } else if (bmiValue < 28) {
        return 'status-warning'
      } else {
        return 'status-danger'
      }
    },
    async getBodyInfo() {
      try {
        // 使用解构赋值从 userApi.getBodyInfo() 返回的 Promise 对象中提取 data.bodyList 数组的第一个元素（即 bodyInfo 对象）
        const {
          data: {
            bodyList: [bodyInfo]
          }
        } = await userApi.getBodyInfo()
        this.bodyInfo = bodyInfo
      } catch (error) {
        console.log('获取身体信息错误')
      }
    },

    async getBodyNotes() {
      try {
        const response = await FunctionApi.getBodyNotes(this.bodyInfo.id)

        // 从返回结果中获取 BodyNotesInfo，并赋值给组件的 BodyNotesInfo 属性
        this.BodyNotesInfo = response.data

        // 遍历 BodyNotesInfo 数组中的每一个元素，将其各个属性值分别添加到对应的数组中,note包含每一条数据的对象
        this.BodyNotesInfo.forEach((note) => {
          this.vision.push(note.vision)
          this.waterConsumption.push(note.waterConsumption)
          this.bloodSugar.push(note.bloodSugar)
          this.bloodPressure.push(note.bloodPressure)
          this.heartRate.push(note.heartRate)
          const formattedDate = new Date(note.date).toLocaleString('en-US', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
          })
          this.date.push(formattedDate)
        })
      } catch (error) {
        console.log('获取身体信息错误')
      }
    },

    bmiM() {
      // 从bodyInfo中获取身高和体重的值，并转换为 Number 类型
      const weight = Number(this.bodyInfo.weight)
      // 计算BMI值
      const bmiValue = weight / (this.bodyInfo.height * this.bodyInfo.height)
      // 返回计算结果并保留两位小数
      this.bmi = bmiValue.toFixed(2)
      return bmiValue.toFixed(2)
    },

    BarChart() {
      const chartDom = document.getElementById('chart-container')
      const myChart = echarts.init(chartDom)

      const option = {
        color: ['#66bb6a'],
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(50, 50, 50, 0.9)',
          borderColor: '#66bb6a',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(102, 187, 106, 0.1)'
            }
          }
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '20%',
          top: '10%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: this.date,
            axisLine: {
              lineStyle: {
                color: '#e0e0e0'
              }
            },
            axisTick: {
              alignWithLabel: true,
              show: false
            },
            axisLabel: {
              interval: 1,
              rotate: 45,
              fontSize: 11,
              color: '#666'
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '视力',
            nameTextStyle: {
              color: '#666',
              fontSize: 12
            },
            axisLine: {
              show: false
            },
            splitLine: {
              lineStyle: {
                type: 'dashed',
                color: '#e8e8e8'
              }
            },
            axisLabel: {
              fontSize: 11,
              color: '#666'
            },
            axisTick: {
              show: false
            }
          }
        ],
        series: [
          {
            name: '视力',
            type: 'bar',
            barWidth: '50%',
            data: this.vision,
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: '#81c784' },
                  { offset: 1, color: '#66bb6a' }
                ]
              },
              shadowBlur: 8,
              shadowOffsetX: 0,
              shadowOffsetY: 2,
              shadowColor: 'rgba(102, 187, 106, 0.3)',
              barBorderRadius: [8, 8, 0, 0]
            },
            emphasis: {
              itemStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 0,
                  y2: 1,
                  colorStops: [
                    { offset: 0, color: '#a5d6a7' },
                    { offset: 1, color: '#81c784' }
                  ]
                }
              }
            },
            animation: true,
            animationDuration: 2000,
            animationEasing: 'cubicOut'
          }
        ]
      }

      myChart.setOption(option)
    },
    area() {
      const chartDom = document.getElementById('chart-containerLine')
      const myChart = echarts.init(chartDom)

      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(50, 50, 50, 0.9)',
          borderColor: '#ff7043',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          }
        },
        legend: {
          data: ['血压', '血糖'],
          top: '5%',
          textStyle: {
            color: '#666',
            fontSize: 12
          },
          itemGap: 30
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '15%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.date,
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          },
          axisLabel: {
            interval: 1,
            fontSize: 11,
            color: '#666',
            rotate: 45
          },
          axisTick: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          name: '数值',
          nameTextStyle: {
            color: '#666',
            fontSize: 12
          },
          axisLine: {
            show: false
          },
          splitLine: {
            lineStyle: {
              type: 'dashed',
              color: '#e8e8e8'
            }
          },
          axisLabel: {
            color: '#666',
            fontSize: 11
          },
          axisTick: {
            show: false
          }
        },
        series: [
          {
            name: '血压',
            data: this.bloodPressure,
            type: 'line',
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#ff7043'
            },
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#ff7043',
              borderColor: '#fff',
              borderWidth: 2
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(255, 112, 67, 0.3)' },
                  { offset: 1, color: 'rgba(255, 112, 67, 0.05)' }
                ]
              }
            },
            animation: true,
            animationDuration: 2000,
            animationEasing: 'cubicOut'
          },
          {
            name: '血糖',
            data: this.bloodSugar,
            type: 'line',
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#ab47bc'
            },
            symbol: 'circle',
            symbolSize: 6,
            itemStyle: {
              color: '#ab47bc',
              borderColor: '#fff',
              borderWidth: 2
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(171, 71, 188, 0.3)' },
                  { offset: 1, color: 'rgba(171, 71, 188, 0.05)' }
                ]
              }
            },
            animation: true,
            animationDuration: 2000,
            animationEasing: 'cubicOut'
          }
        ]
      }

      myChart.setOption(option)
    }
  }
}
</script>
<style scoped>
.dashboard-container {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  min-height: calc(100vh - 84px);
}

/* 统计卡片行 */
.stats-row {
  margin-bottom: 30px;
}

/* 统计卡片通用样式 */
.stat-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  min-height: 160px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, transparent, currentColor, transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-card:hover::before {
  opacity: 1;
}

/* 身高卡片 */
.stat-card-height {
  border-top: 4px solid #4fc3f7;
}

.stat-card-height .stat-icon {
  color: #4fc3f7;
}

.stat-card-height::before {
  background: linear-gradient(90deg, transparent, #4fc3f7, transparent);
}

/* 体重卡片 */
.stat-card-weight {
  border-top: 4px solid #66bb6a;
}

.stat-card-weight .stat-icon {
  color: #66bb6a;
}

.stat-card-weight::before {
  background: linear-gradient(90deg, transparent, #66bb6a, transparent);
}

/* BMI卡片 */
.stat-card-bmi {
  border-top: 4px solid #ffa726;
}

.stat-card-bmi .stat-icon {
  color: #ffa726;
}

.stat-card-bmi::before {
  background: linear-gradient(90deg, transparent, #ffa726, transparent);
}

/* 年龄卡片 */
.stat-card-age {
  border-top: 4px solid #ab47bc;
}

.stat-card-age .stat-icon {
  color: #ab47bc;
}

.stat-card-age::before {
  background: linear-gradient(90deg, transparent, #ab47bc, transparent);
}

/* 图标容器 */
.stat-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  background: linear-gradient(135deg, rgba(79, 195, 247, 0.1), rgba(79, 195, 247, 0.05));
  flex-shrink: 0;
}

.stat-card-height .stat-icon-wrapper {
  background: linear-gradient(135deg, rgba(79, 195, 247, 0.15), rgba(79, 195, 247, 0.05));
}

.stat-card-weight .stat-icon-wrapper {
  background: linear-gradient(135deg, rgba(102, 187, 106, 0.15), rgba(102, 187, 106, 0.05));
}

.stat-card-bmi .stat-icon-wrapper {
  background: linear-gradient(135deg, rgba(255, 167, 38, 0.15), rgba(255, 167, 38, 0.05));
}

.stat-card-age .stat-icon-wrapper {
  background: linear-gradient(135deg, rgba(171, 71, 188, 0.15), rgba(171, 71, 188, 0.05));
}

.stat-icon {
  font-size: 48px;
  font-weight: normal;
}

/* 统计内容 */
.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
  margin-bottom: 8px;
}

.stat-value .unit {
  font-size: 18px;
  font-weight: 500;
  color: #909399;
  margin-left: 4px;
}

.stat-status {
  margin-top: 8px;
}

.stat-status span {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-success {
  background: linear-gradient(135deg, #66bb6a, #81c784);
  color: #ffffff;
}

.status-warning {
  background: linear-gradient(135deg, #ffa726, #ffb74d);
  color: #ffffff;
}

.status-danger {
  background: linear-gradient(135deg, #ef5350, #e57373);
  color: #ffffff;
}

/* 图表行 */
.charts-row {
  margin-top: 20px;
}

/* 图表卡片 */
.chart-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  height: 500px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.chart-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.chart-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

.chart-icon {
  font-size: 24px;
  margin-right: 10px;
  color: #4fc3f7;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  letter-spacing: 0.5px;
}

.chart-container {
  flex: 1;
  width: 100%;
  min-height: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stat-value {
    font-size: 28px;
  }

  .stat-icon {
    font-size: 40px;
  }

  .stat-icon-wrapper {
    width: 70px;
    height: 70px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 15px;
  }

  .stat-card {
    min-height: 140px;
    padding: 20px;
  }

  .stat-value {
    font-size: 24px;
  }

  .chart-card {
    height: 400px;
    margin-bottom: 20px;
  }
}
</style>
