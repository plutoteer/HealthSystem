<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">医保信息管理</h2>
      <p class="page-subtitle">查看医保项目，在线参保报名并完成缴费</p>
    </div>

    <el-card v-loading="loadingInfo" class="section-card">
      <div slot="header" class="card-header">可参保医保项目</div>
      <el-table :data="insuranceList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="title" label="项目名称" min-width="180" />
        <el-table-column prop="insuranceYear" label="参保年度" width="110" />
        <el-table-column prop="description" label="项目说明" min-width="260" show-overflow-tooltip />
        <el-table-column prop="amount" label="缴费金额(元)" width="130" />
        <el-table-column label="开始时间" width="170">
          <template slot-scope="scope">{{ formatDate(scope.row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="截止时间" width="170">
          <template slot-scope="scope">{{ formatDate(scope.row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="openEnrollDialog(scope.row)">参保报名</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-loading="loadingMy" class="section-card">
      <div slot="header" class="card-header">我的参保缴费记录</div>
      <el-table :data="myEnrollments" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="insuranceInfoId" label="项目ID" width="90" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="payAmount" label="缴费金额(元)" width="130" />
        <el-table-column label="缴费状态" width="110">
          <template slot-scope="scope">
            <el-tag :type="scope.row.payStatus === 1 ? 'success' : 'warning'">
              {{ scope.row.payStatus === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缴费时间" min-width="170">
          <template slot-scope="scope">{{ formatDate(scope.row.payTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      :title="`参保报名 - ${currentInsurance ? currentInsurance.title : ''}`"
      :visible.sync="dialogVisible"
      width="620px"
      @close="resetForm"
    >
      <el-alert
        title="提交后将模拟完成在线缴费，生成参保记录。"
        type="info"
        :closable="false"
        style="margin-bottom: 16px;"
      />

      <el-form ref="enrollFormRef" :model="enrollForm" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="enrollForm.realName" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="enrollForm.studentNo" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="enrollForm.idCard" maxlength="18" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="enrollForm.phone" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="enrollForm.major" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-input v-model="enrollForm.grade" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="缴费金额">
          <el-input :value="currentInsurance ? currentInsurance.amount : 0" disabled>
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitEnroll">提交报名并缴费</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import insuranceApi from '@/api/insurance'

export default {
  name: 'UserInsuranceManage',
  data() {
    return {
      loadingInfo: false,
      loadingMy: false,
      submitting: false,
      dialogVisible: false,
      insuranceList: [],
      myEnrollments: [],
      currentInsurance: null,
      enrollForm: {
        insuranceInfoId: null,
        realName: '',
        studentNo: '',
        idCard: '',
        phone: '',
        major: '',
        grade: ''
      },
      rules: {
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
        idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadInsuranceList()
    this.loadMyEnrollments()
  },
  methods: {
    formatDate(value) {
      if (!value) return '--'
      return new Date(value).toLocaleString('zh-CN', { hour12: false })
    },
    async loadInsuranceList() {
      this.loadingInfo = true
      try {
        const res = await insuranceApi.getInsuranceList()
        this.insuranceList = res.data || []
      } finally {
        this.loadingInfo = false
      }
    },
    async loadMyEnrollments() {
      this.loadingMy = true
      try {
        const res = await insuranceApi.getMyEnrollments()
        this.myEnrollments = res.data || []
      } finally {
        this.loadingMy = false
      }
    },
    openEnrollDialog(row) {
      this.currentInsurance = row
      this.enrollForm = {
        insuranceInfoId: row.id,
        realName: '',
        studentNo: '',
        idCard: '',
        phone: '',
        major: '',
        grade: ''
      }
      this.dialogVisible = true
    },
    resetForm() {
      this.submitting = false
      if (this.$refs.enrollFormRef) {
        this.$refs.enrollFormRef.clearValidate()
      }
    },
    submitEnroll() {
      if (this.submitting) return
      this.$refs.enrollFormRef.validate(async(valid) => {
        if (!valid) return
        this.submitting = true
        try {
          const res = await insuranceApi.enroll(this.enrollForm)
          this.$message.success(res.message || '参保成功')
          this.dialogVisible = false
          await this.loadMyEnrollments()
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>

<style scoped>
.page-container {
  width: 100%;
  height: 100%;
  margin: -10px;
  padding: 20px;
  box-sizing: border-box;
  background: #fff;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 12px;
}

.page-title {
  margin: 0;
  font-size: 30px;
  color: #303133;
}

.page-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: #909399;
}

.section-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
}
</style>
