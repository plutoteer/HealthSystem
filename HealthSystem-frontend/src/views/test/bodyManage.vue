<!-- 搜索框和添加按钮，搜索框，以及用户名和手机号的输入框 -->
<template>
  <div class="page-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <h2 class="page-title">体检信息管理</h2>
      <p class="page-subtitle">管理您的身体信息数据，支持导出健康报告PDF</p>
    </div>

    <!-- 结果列表 -->
    <el-card>
      <el-table :data="bodyList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column prop="date" label="上传时间" width="100" :formatter="formatDate" />
        <el-table-column prop="name" label="姓名" width="80" />
        <el-table-column prop="age" label="年龄" width="80" />

        <el-table-column
          prop="gender"
          label="性别"
          width="80"
        />

        <el-table-column
          prop="height"
          label="身高/cm"
          width="80"
        />

        <el-table-column
          prop="weight"
          label="体重/kg"
          width="80"
        />

        <el-table-column
          prop="bloodSugar"
          label="血糖"
          width="80"
        />

        <el-table-column
          prop="bloodPressure"
          label="血压"
          width="80"
        />

        <el-table-column
          prop="weight"
          label="体重/kg"
          width="80"
        />

        <el-table-column
          prop="bloodLipid"
          label="血脂"
          width="80"
        />

        <el-table-column
          prop="heartRate"
          label="心率/分钟"
          width="80"
        />

        <el-table-column
          prop="vision"
          label="视力"
          width="80"
        />

        <el-table-column
          prop="sleepDuration"
          label="睡眠时长"
          width="80"
        />

        <el-table-column
          prop="sleepQuality"
          label="睡眠质量"
          width="80"
        />

        <el-table-column
          prop="heartRate"
          label="心率/分钟"
          width="80"
        />

        <el-table-column
          prop="heartRate"
          label="心率/分钟"
          width="80"
        />

        <el-table-column prop="smoking" label="是否抽烟" width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.smoking">是</span>
            <span v-else>否</span>
          </template></el-table-column>

        <el-table-column prop="drinking" label="是否喝酒" width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.smoking">是</span>
            <span v-else>否</span>
          </template></el-table-column>

        <el-table-column prop="exercise" label="是否运动" width="80">
          <template slot-scope="scope">
            <span v-if="scope.row.smoking">是</span>
            <span v-else>否</span>
          </template></el-table-column>

        <el-table-column
          prop="foodTypes"
          label="喜好食物"
          width="80"
        />

        <el-table-column
          prop="waterConsumption"
          label="饮水量"
          width="80"
        />

        <el-table-column label="操作" width="320" fixed="right">
          <!-- 删除和修改按钮 -->
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
              class="action-btn edit-btn"
              @click="openEditUi(scope.row.notesid)"
            >编辑</el-button>
            <el-button
              type="danger"
              size="small"
              class="action-btn delete-btn"
              @click="deleteUserBody(scope.row)"
            >删除</el-button>
            <el-button
              type="success"
              size="small"
              class="action-btn export-btn"
              icon="el-icon-download"
              :loading="exportLoading"
              @click="exportHealthReport(scope.row)"
            >导出报告PDF</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页功能 -->
    <el-pagination
      :current-page="searchModel.pageNo"
      :page-sizes="[5, 10, 20, 30]"
      :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 用户编辑信息弹出框 -->
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      @close="clearForm"
    >
      <el-form ref="bodyFormRef" :model="bodyForm" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="昵称" prop="name">
              <el-input v-model="bodyForm.name" autocomplete="off" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input v-model="bodyForm.age" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-input v-model="bodyForm.gender" autocomplete="off" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="身高/cm" prop="height">
              <el-input v-model="bodyForm.height" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体重/kg" prop="weight">
              <el-input v-model="bodyForm.weight" autocomplete="off" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="血糖" prop="bloodSugar">
              <el-input v-model="bodyForm.bloodSugar" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血压" prop="bloodPressure">
              <el-input v-model="bodyForm.bloodPressure" autocomplete="off" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="血脂" prop="bloodLipid">
              <el-input v-model="bodyForm.bloodLipid" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="心率/分钟" prop="heartRate">
              <el-input v-model="bodyForm.heartRate" autocomplete="off" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="视力" prop="vision">
              <el-input v-model="bodyForm.vision" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="睡眠时长/h" prop="sleepDuration">
              <el-input v-model="bodyForm.sleepDuration" autocomplete="off" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="睡眠质量" prop="sleepQuality">
              <el-radio-group v-model="bodyForm.sleepQuality">
                <el-radio :label="1">好</el-radio>
                <el-radio :label="2">一般</el-radio>
                <el-radio :label="3">差</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否吸烟" prop="smoking">
              <el-switch v-model="bodyForm.smoking" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="是否喝酒" prop="drinking">
              <el-switch v-model="bodyForm.drinking" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否运动" prop="exercise">
              <el-switch v-model="bodyForm.exercise" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="喜好食物" prop="foodTypes">
              <el-select
                v-model="bodyForm.foodTypes"
                placeholder="请选择摄入较多的食物种类"
                style="width: 100%"
              >
                <el-option label="蔬菜" value="蔬菜" />
                <el-option label="水果" value="水果" />
                <el-option label="肉类" value="肉类" />
                <el-option label="鱼类" value="鱼类" />
                <el-option label="豆类" value="豆类" />
                <el-option label="谷物" value="谷物" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="饮水量/ml" prop="waterConsumption">
              <el-input v-model="bodyForm.waterConsumption" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateBody">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import userApi from '@/api/userManage'
import healthReportApi from '@/api/healthReport'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'
export default {
  data() {
    return {
      bodyForm: {}, // 初始化为一个空对象
      bodyList: [],
      // 左边宽度
      formLabelWidth: '135px',
      // 设置默认值不可见
      dialogFormVisible: false,
      title: '',
      total: 0,
      exportLoading: false, // 导出加载状态
      currentUserId: null, // 当前用户ID
      searchModel: {
        pageNo: 1,
        // 默认显示数量
        pageSize: 10
      },
      // 表单规则配置
      rules: {
        name: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' },
          { pattern: /^\d+$/, message: '年龄必须为数字', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请输入性别', trigger: 'blur' }
        ],
        height: [
          { required: true, message: '请输入身高', trigger: 'blur' },
          { pattern: /^\d+(\.\d+)?$/, message: '身高必须为数字', trigger: 'blur' }
        ],
        weight: [
          { required: true, message: '请输入体重', trigger: 'blur' },
          { pattern: /^\d+(\.\d+)?$/, message: '体重必须为数字', trigger: 'blur' }
        ],
        bloodSugar: [
          { required: true, message: '请输入血糖', trigger: 'blur' },
          { pattern: /^\d+(\.\d+)?$/, message: '血糖必须为数字', trigger: 'blur' }
        ],
        bloodPressure: [
          { required: true, message: '请输入血压', trigger: 'blur' }
        ],
        bloodLipid: [
          { required: true, message: '请输入血脂', trigger: 'blur' },
          { pattern: /^\d+(\.\d+)?$/, message: '血脂必须为数字', trigger: 'blur' }
        ],
        heartRate: [
          { required: true, message: '请输入心率', trigger: 'blur' },
          { pattern: /^\d+$/, message: '心率必须为数字', trigger: 'blur' }
        ],
        vision: [
          { required: true, message: '请输入视力', trigger: 'blur' },
          { pattern: /^\d+(\.\d+)?$/, message: '视力必须为数字', trigger: 'blur' }
        ],
        sleepDuration: [
          { required: true, message: '请输入睡眠时长', trigger: 'blur' },
          { pattern: /^\d+(\.\d+)?$/, message: '睡眠时长必须为数字', trigger: 'blur' }
        ],
        sleepQuality: [
          { required: true, message: '请选择睡眠质量', trigger: 'change' }
        ]
      }
    }
  },

  // 加载时就查询一次
  created() {
    this.getUserBodyList()
    this.getCurrentUserId()
  },

  methods: {
    updateBody() {
      let isOk = true
      // 触发表单的验证
      this.$refs.bodyFormRef.validate((valid) => {
        // 这边只有校验失败的时候才会进来,在外面定义一个 isok,校验失败会将他改成 false
        isOk = valid
      })

      if (isOk) {
        // 提交验证给后台
        userApi.updateUserBody(this.bodyForm).then((response) => {
          // 成功提示
          this.$message({
            message: response.message,
            type: 'success'
          })
          // 关闭对话框
          this.dialogFormVisible = false
          // 刷新表格数据
          this.getUserBodyList()
        })
      } else {
        console.log('error submit!!')
        return false
      }
    },

    // 清理表单数据
    clearForm() {
      this.bodyForm = {}
      // 清除表单校验的提示信息
      this.$refs.bodyFormRef.clearValidate()
    },
    handleSizeChange(pageSize) {
      // 数据更新
      this.searchModel.pageSize = pageSize
      this.getUserBodyList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getUserBodyList()
    },

    // 用于查询用户列表
    getUserBodyList() {
      userApi.getUserBodyList(this.searchModel).then((response) => {
        console.log(response)
        this.bodyList = response.data.rows
        // 将查询结果中的 total 属性赋值给 total
        this.total = response.data.total
        console.log(this.bodyList)
      })
    },

    openEditUi(notesid) {
      console.log(notesid)
      this.title = '修改身体信息'
      // 根据id查询用户数据
      userApi.getUserBodyById(notesid).then((response) => {
        this.bodyForm = response.data
        // 将 sleepQuality 从字符串转换为数字，以便 el-radio 正确显示选中状态
        if (this.bodyForm.sleepQuality !== null && this.bodyForm.sleepQuality !== undefined) {
          this.bodyForm.sleepQuality = parseInt(this.bodyForm.sleepQuality)
        }
      })

      this.dialogFormVisible = true
    },

    deleteUserBody(body) {
      this.$confirm(`确认删除 ${body.name} 这个身体信息吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          userApi.deleteUserBodyById(body.notesid).then((response) => {
            this.$message({
              type: 'success',
              message: response.message
            })
            this.getUserBodyList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    formatDate(row, column, cellValue, index) {
    // 将时间戳转换成日期字符串
      const date = new Date(cellValue)
      return date.toLocaleDateString()
    },

    // 获取当前用户ID
    async getCurrentUserId() {
      try {
        const response = await userApi.getUserId()
        if (response && response.data) {
          // response.data 是 {id: 1} 格式的对象，需要提取 id 字段
          if (typeof response.data === 'object' && response.data.id) {
            this.currentUserId = response.data.id
          } else if (typeof response.data === 'number') {
            this.currentUserId = response.data
          } else {
            // 尝试直接使用，可能是字符串格式的数字
            this.currentUserId = parseInt(response.data)
          }
        }
      } catch (error) {
        console.error('获取用户ID失败：', error)
      }
    },

    // 生成HTML报告
    generateReportHTML(reportData) {
      const formatValue = (value) => {
        return value !== null && value !== undefined ? value : '--'
      }

      const formatNumber = (value, decimals = 2) => {
        if (value === null || value === undefined) return '--'
        return typeof value === 'number' ? value.toFixed(decimals) : value
      }

      const formatDate = (dateStr) => {
        return dateStr || '--'
      }

      const exportTime = reportData.exportTime || new Date().toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })

      let html = `
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <title>健康报告</title>
          <style>
            body {
              font-family: 'Microsoft YaHei', Arial, sans-serif;
              padding: 20px;
              background: #f5f5f5;
              margin: 0;
            }
            .container {
              max-width: 1200px;
              margin: 0 auto;
              background: #fff;
              padding: 30px;
              box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            .header {
              text-align: center;
              margin-bottom: 30px;
              padding-bottom: 20px;
              border-bottom: 3px solid #409EFF;
            }
            .header h1 {
              color: #303133;
              margin: 0 0 10px 0;
              font-size: 32px;
              font-weight: bold;
            }
            .header p {
              color: #909399;
              margin: 0;
              font-size: 16px;
            }
            .section {
              margin-bottom: 30px;
              page-break-inside: avoid; /* 避免在section内部分页 */
              break-inside: avoid; /* CSS3标准，避免在元素内部分页 */
            }
            .section-title {
              font-size: 20px;
              font-weight: bold;
              margin-bottom: 15px;
              padding: 10px 0;
              border-bottom: 2px solid #409EFF;
              color: #303133;
            }
            .info-grid {
              display: grid;
              grid-template-columns: repeat(3, 1fr);
              gap: 15px;
              margin-top: 15px;
            }
            .info-item {
              padding: 15px;
              background: #f5f7fa;
              border-radius: 8px;
              border-left: 4px solid #409EFF;
              display: flex;
              flex-direction: column;
            }
            .info-label {
              color: #606266;
              font-size: 14px;
              font-weight: 600;
              margin-bottom: 8px;
              display: block;
            }
            .info-value {
              color: #303133;
              font-size: 18px;
              font-weight: bold;
              display: block;
            }
            .info-unit {
              color: #909399;
              font-size: 14px;
              font-weight: normal;
              margin-left: 5px;
            }
            table {
              width: 100%;
              border-collapse: collapse;
              margin-top: 15px;
              font-size: 14px;
            }
            th, td {
              border: 1px solid #dcdfe6;
              padding: 12px;
              text-align: center;
            }
            th {
              background: #f5f7fa;
              font-weight: bold;
              color: #303133;
            }
            tr:nth-child(even) {
              background: #fafafa;
            }
            tr:hover {
              background: #ecf5ff;
            }
            .footer {
              margin-top: 30px;
              padding-top: 20px;
              border-top: 1px solid #dcdfe6;
              text-align: center;
              color: #909399;
              font-size: 12px;
            }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <h1>个人健康报告</h1>
              <p>导出时间：${exportTime}</p>
            </div>

            <div class="section">
              <div class="section-title">基本信息</div>
              <div class="info-grid">
                <div class="info-item">
                  <div class="info-label">姓名</div>
                  <div class="info-value">${formatValue(reportData.username)}</div>
                </div>
                <div class="info-item">
                  <div class="info-label">年龄</div>
                  <div class="info-value">${formatValue(reportData.age)}<span class="info-unit">岁</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">性别</div>
                  <div class="info-value">${formatValue(reportData.gender)}</div>
                </div>
                <div class="info-item">
                  <div class="info-label">身高</div>
                  <div class="info-value">${formatValue(reportData.height)}<span class="info-unit">cm</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">体重</div>
                  <div class="info-value">${formatValue(reportData.weight)}<span class="info-unit">kg</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">BMI</div>
                  <div class="info-value">${formatValue(reportData.bmi)} (${formatValue(reportData.bmiStatus)})</div>
                </div>
              </div>
            </div>

            <div class="section">
              <div class="section-title">身体信息</div>
              <div class="info-grid">
                <div class="info-item">
                  <div class="info-label">体重</div>
                  <div class="info-value">${formatValue(reportData.weight)}<span class="info-unit">kg</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">身高</div>
                  <div class="info-value">${formatValue(reportData.height)}<span class="info-unit">cm</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">BMI</div>
                  <div class="info-value">${formatValue(reportData.bmi)} (${formatValue(reportData.bmiStatus)})</div>
                </div>
                <div class="info-item">
                  <div class="info-label">血糖</div>
                  <div class="info-value">${formatValue(reportData.bloodSugar)}<span class="info-unit">mmol/L</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">血压</div>
                  <div class="info-value">${formatValue(reportData.bloodPressure)}<span class="info-unit">mmHg</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">胆固醇</div>
                  <div class="info-value">${formatValue(reportData.bloodLipid)}<span class="info-unit">mmol/l</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">心率</div>
                  <div class="info-value">${formatValue(reportData.heartRate)}<span class="info-unit">次/分钟</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">视力</div>
                  <div class="info-value">${formatValue(reportData.vision)}<span class="info-unit">度</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">健康评分</div>
                  <div class="info-value">${formatValue(reportData.score)}<span class="info-unit">分</span></div>
                </div>
              </div>
            </div>

            <div class="section">
              <div class="section-title">疾病分析</div>
              <div class="info-item" style="margin-bottom: 15px;">
                <div class="info-label">可能的疾病：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.diseaseRisk)}</div>
              </div>
              <div class="info-item" style="margin-top: 15px; padding: 15px; background: #fff3cd; border-left: 4px solid #ffc107; border-radius: 8px;">
                <div class="info-label" style="color: #856404; font-weight: bold; margin-bottom: 8px;"><strong>注意：</strong></div>
                <div class="info-value" style="color: #856404; font-size: 14px; font-weight: normal;">以上风险只是根据您上传的身体数据进行最基本的分析，并不能作为真正的结果，不管有没有风险，都需要保持运动，如有不舒服的地方请马上就医。</div>
              </div>
            </div>

            <div class="section">
              <div class="section-title">基础能量消耗状况</div>
              <div class="info-grid">
                <div class="info-item">
                  <div class="info-label">到达身体年龄的百分比</div>
                  <div class="info-value">${formatNumber(reportData.standardHeight)}<span class="info-unit">%</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">基本能量消耗</div>
                  <div class="info-value">${formatNumber(reportData.bmr)}<span class="info-unit">%</span></div>
                </div>
              </div>
            </div>

            <div class="section">
              <div class="section-title">肥胖分析</div>
              <div class="info-item" style="margin-bottom: 15px;">
                <div class="info-label">肥胖百分比</div>
                <div class="info-value">${formatValue(reportData.obesityPercentage)}<span class="info-unit">%</span></div>
              </div>
              <div class="info-item" style="margin-bottom: 15px;">
                <div class="info-label">根据计算：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.healthRisk)}</div>
              </div>
              <div class="info-item">
                <div class="info-label">肥胖可能的风险：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.obesityRisk)}</div>
              </div>
            </div>

            <div class="section">
              <div class="section-title">生活习惯分析</div>
              <div class="info-item" style="margin-bottom: 15px;">
                <div class="info-label">您的习惯如下：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.habits)}</div>
              </div>
              <div class="info-item">
                <div class="info-label">建议：</div>
                <div class="info-value" style="font-size: 14px; font-weight: normal;">阅读运动知识，更好地了解运动的正确姿势和方法，通过了解运动的原理和科学知识，我们可以更好地制定运动计划，减少运动中的风险和不适，避免受伤和疾病的发生。</div>
              </div>
            </div>

            <div class="section">
              <div class="section-title">视力分析</div>
              <div class="info-item" style="margin-bottom: 15px;">
                <div class="info-label">您的视力为：</div>
                <div class="info-value">${formatValue(reportData.vision)}<span class="info-unit">度</span></div>
              </div>
              <div class="info-item" style="margin-bottom: 15px;">
                <div class="info-label">近视等级：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.visionType)}</div>
              </div>
              <div class="info-item">
                <div class="info-label">建议：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.visionSuggestion)}</div>
              </div>
            </div>

            <div class="section">
              <div class="section-title">体型判断</div>
              <div class="info-item" style="margin-bottom: 15px;">
                <div class="info-label">您的体型属于：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.bodyType)}</div>
              </div>
              <div class="info-item">
                <div class="info-label">建议：</div>
                <div class="info-value" style="font-size: 16px;">${formatValue(reportData.bodyTypeSuggestion)}</div>
              </div>
            </div>
      `

      // 历史数据
      if (reportData.historyData && reportData.historyData.length > 0) {
        html += `
            <div class="section">
              <div class="section-title">历史数据明细</div>
              <table>
                <tr>
                  <th>日期</th>
                  <th>视力(度)</th>
                  <th>饮水量(ml)</th>
                  <th>血糖(mmol/L)</th>
                  <th>血压(mmHg)</th>
                  <th>心率(次/分钟)</th>
                </tr>
        `
        reportData.historyData.forEach(note => {
          html += `
                <tr>
                  <td>${formatDate(note.date)}</td>
                  <td>${formatValue(note.vision)}</td>
                  <td>${formatValue(note.waterConsumption)}</td>
                  <td>${formatValue(note.bloodSugar)}</td>
                  <td>${formatValue(note.bloodPressure)}</td>
                  <td>${formatValue(note.heartRate)}</td>
                </tr>
          `
        })
        html += `
              </table>
            </div>

            <div class="section">
              <div class="section-title">统计数据</div>
              <div class="info-grid">
                <div class="info-item">
                  <div class="info-label">平均心率</div>
                  <div class="info-value">${formatNumber(reportData.avgHeartRate)}<span class="info-unit">次/分钟</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">平均视力</div>
                  <div class="info-value">${formatNumber(reportData.avgVision)}<span class="info-unit">度</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">平均血糖</div>
                  <div class="info-value">${formatNumber(reportData.avgBloodSugar)}<span class="info-unit">mmol/L</span></div>
                </div>
                <div class="info-item">
                  <div class="info-label">平均血压</div>
                  <div class="info-value">${formatNumber(reportData.avgBloodPressure)}<span class="info-unit">mmHg</span></div>
                </div>
              </div>
            </div>
        `
      }

      html += `
            <div class="footer">
              <p>本报告由健康管理系统自动生成，仅供参考</p>
            </div>
          </div>
        </body>
        </html>
      `

      return html
    },

    // 导出健康报告（使用前端生成PDF）
    async exportHealthReport(row) {
      // 如果没有获取到用户ID，先获取
      if (!this.currentUserId) {
        await this.getCurrentUserId()
      }

      if (!this.currentUserId) {
        this.$message.warning('无法获取用户信息，请重新登录')
        return
      }

      if (!row || !row.notesid) {
        this.$message.warning('请选择要导出的记录')
        return
      }

      this.exportLoading = true
      try {
        // 获取报告数据
        const params = {
          userId: this.currentUserId,
          selectedNotesIds: [row.notesid]
        }

        const response = await healthReportApi.getHealthReportData(params)
        const reportData = response.data

        // 生成HTML报告
        const htmlContent = this.generateReportHTML(reportData)

        // 创建临时DOM元素来渲染HTML
        const tempDiv = document.createElement('div')
        tempDiv.style.position = 'absolute'
        tempDiv.style.left = '-9999px'
        tempDiv.style.width = '1200px'
        tempDiv.innerHTML = htmlContent
        document.body.appendChild(tempDiv)

        // 等待图片加载完成
        await new Promise(resolve => setTimeout(resolve, 500))

        // 等待DOM完全渲染
        await new Promise(resolve => setTimeout(resolve, 200))

        // 获取所有section元素及其位置信息
        const sections = tempDiv.querySelectorAll('.section')
        const sectionPositions = []

        // 计算每个section在DOM中的位置（像素）
        sections.forEach((section, index) => {
          const rect = section.getBoundingClientRect()
          const containerRect = tempDiv.getBoundingClientRect()
          const relativeTop = rect.top - containerRect.top
          const relativeBottom = rect.bottom - containerRect.top
          sectionPositions.push({
            index,
            top: relativeTop,
            bottom: relativeBottom,
            height: rect.height
          })
        })

        // 使用html2canvas将HTML转换为Canvas
        const canvas = await html2canvas(tempDiv, {
          scale: 2, // 提高清晰度
          useCORS: true,
          logging: false,
          width: tempDiv.scrollWidth,
          height: tempDiv.scrollHeight
        })

        // 清理临时元素
        document.body.removeChild(tempDiv)

        // 计算PDF尺寸（A4纸：210mm x 297mm）
        const imgWidth = 210 // A4宽度（mm）
        const imgHeight = (canvas.height * imgWidth) / canvas.width
        // 下面的构造函数来自第三方库，名称为 jsPDF（库导出的小写开头构造器）
        // eslint-disable-next-line new-cap
        const pdf = new jsPDF('p', 'mm', 'a4') /* eslint-disable-line new-cap */

        // 如果内容超过一页，需要分页
        const pageHeight = 297 // A4高度（mm）
        const imgData = canvas.toDataURL('image/png')

        // 计算缩放比例（Canvas像素到PDF毫米）
        const scaleRatio = imgHeight / canvas.height

        if (imgHeight <= pageHeight) {
          // 内容不超过一页，直接添加
          pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight)
        } else {
          // 内容超过一页，需要智能分页
          // 计算每个section在PDF中的位置（毫米）
          const sectionPositionsMM = sectionPositions.map(section => ({
            top: section.top * scaleRatio,
            bottom: section.bottom * scaleRatio,
            height: section.height * scaleRatio
          }))

          // 确定分页点：在section之间分页，避免在section内部分页
          const pageBreaks = [0] // 第一页从0开始
          let currentPageEnd = pageHeight

          for (let i = 0; i < sectionPositionsMM.length; i++) {
            const section = sectionPositionsMM[i]
            const sectionStart = section.top
            const sectionEnd = section.bottom
            const sectionHeight = section.height

            // 如果section会被截断（section开始位置在当前页内，但结束位置超过当前页）
            if (sectionStart < currentPageEnd && sectionEnd > currentPageEnd) {
              // section会被截断
              if (sectionHeight < pageHeight) {
                // section高度小于一页，在section开始前分页，确保section完整显示
                const lastBreak = pageBreaks[pageBreaks.length - 1]
                if (sectionStart > lastBreak) {
                  pageBreaks.push(sectionStart)
                  currentPageEnd = sectionStart + pageHeight
                }
              } else {
                // section太大，无法在一页内显示，正常分页
                // 在section开始前分页
                const lastBreak = pageBreaks[pageBreaks.length - 1]
                if (sectionStart > lastBreak) {
                  pageBreaks.push(sectionStart)
                  currentPageEnd = sectionStart + pageHeight
                }
                // 如果section结束位置超过当前页，继续分页
                while (sectionEnd > currentPageEnd) {
                  pageBreaks.push(currentPageEnd)
                  currentPageEnd += pageHeight
                }
              }
            } else if (sectionStart >= currentPageEnd) {
              // section在新页，在section开始前分页
              const lastBreak = pageBreaks[pageBreaks.length - 1]
              if (sectionStart > lastBreak) {
                pageBreaks.push(sectionStart)
                currentPageEnd = sectionStart + pageHeight
              }

              // 如果section结束位置超过当前页，继续分页
              while (sectionEnd > currentPageEnd) {
                pageBreaks.push(currentPageEnd)
                currentPageEnd += pageHeight
              }
            }

            // 更新当前页结束位置
            if (sectionEnd > currentPageEnd) {
              currentPageEnd = sectionEnd
            }
          }

          // 确保所有内容都被包含
          // 如果最后一个分页点加上页高小于总高度，需要添加更多分页点
          let lastBreak = pageBreaks[pageBreaks.length - 1]
          while (lastBreak + pageHeight < imgHeight) {
            lastBreak += pageHeight
            pageBreaks.push(lastBreak)
          }

          // 按分页点绘制每一页
          for (let i = 0; i < pageBreaks.length; i++) {
            if (i > 0) {
              pdf.addPage()
            }
            const pageStartY = pageBreaks[i]
            pdf.addImage(imgData, 'PNG', 0, -pageStartY, imgWidth, imgHeight)
          }
        }

        // 生成文件名并下载
        const username = this.$store.getters.name || 'user'
        const timestamp = new Date().getTime()
        const fileName = `健康报告_${username}_${timestamp}.pdf`
        pdf.save(fileName)

        this.$message.success('健康报告导出成功')
      } catch (error) {
        console.error('导出报告错误：', error)
        this.$message.error('导出报告失败：' + (error.message || '未知错误'))
      } finally {
        this.exportLoading = false
      }
    }
  }
}
</script>
  <style scoped>
  #search .el-input {
    width: 200px;
    margin-right: 20px;
  }
  .el-dialog .el-input {
    width: 43%;
  }

  /* 页面容器样式 - 占满整个区域 */
  .page-container {
    width: 100%;
    height: 100%;
    margin: -10px;
    padding: 20px;
    box-sizing: border-box;
    background: #fff;
  }

  /* 页面标题区域 */
  .page-header {
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ebeef5;
    text-align: center;
  }

  .page-title {
    font-size: 32px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 10px 0;
  }

  .page-subtitle {
    font-size: 16px;
    color: #909399;
    margin: 0;
    font-weight: 400;
  }

  /* 卡片样式 - 占满整个容器宽度 */
  .page-container .el-card {
    width: 100% !important;
    margin: 0 0 20px 0 !important;
    border: 1px solid #ebeef5;
    box-shadow: none;
  }

  /* 分页样式 */
  .el-pagination {
    margin-top: 20px;
    text-align: center;
  }

  /* 操作列按钮样式 */
  .action-btn {
    font-weight: 500;
    padding: 7px 15px;
    margin-right: 8px;
    border-radius: 4px;
    white-space: nowrap;
  }

  .action-btn.edit-btn {
    background-color: #409EFF;
    color: #fff;
    border: none;
  }

  .action-btn.edit-btn:hover {
    background-color: #66b1ff;
  }

  .action-btn.delete-btn {
    background-color: #F56C6C;
    color: #fff;
    border: none;
  }

  .action-btn.delete-btn:hover {
    background-color: #f78989;
  }

  .action-btn.export-btn {
    background-color: #67C23A;
    color: #fff;
    border: none;
  }

  .action-btn.export-btn:hover {
    background-color: #85ce61;
  }
  </style>
