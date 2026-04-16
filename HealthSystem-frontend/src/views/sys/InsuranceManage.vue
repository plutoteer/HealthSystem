<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="医保信息发布" name="insuranceInfo">
        <el-card id="search">
          <el-row>
            <el-col :span="20">
              <el-input v-model="searchModel.title" placeholder="项目名称" clearable />
              <el-input v-model="searchModel.insuranceYear" placeholder="参保年度" clearable />
              <el-button type="primary" round icon="el-icon-search" @click="getInsurancePage">查询</el-button>
              <el-button type="info" round icon="el-icon-refresh-left" @click="resetSearch">重置</el-button>
            </el-col>
            <el-col :span="4" style="text-align: right;">
              <el-button type="success" round icon="el-icon-plus" @click="openDialog(null)">发布</el-button>
            </el-col>
          </el-row>
        </el-card>

        <el-card v-loading="loading" element-loading-text="加载中...">
          <el-table :data="insuranceList" stripe style="width: 100%">
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="项目名称" min-width="180" />
            <el-table-column prop="insuranceYear" label="参保年度" width="120" />
            <el-table-column prop="description" label="项目说明" min-width="220" show-overflow-tooltip />
            <el-table-column prop="amount" label="金额(元)" width="110" />
            <el-table-column label="开始时间" width="180">
              <template slot-scope="scope">{{ formatDate(scope.row.startTime) }}</template>
            </el-table-column>
            <el-table-column label="截止时间" width="180">
              <template slot-scope="scope">{{ formatDate(scope.row.endTime) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '开放' : '关闭' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="190" fixed="right">
              <template slot-scope="scope">
                <el-button type="primary" size="small" class="action-btn edit-btn" @click="openDialog(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" class="action-btn delete-btn" @click="deleteById(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-pagination
          :current-page="searchModel.pageNo"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="searchModel.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />

        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" @close="resetForm">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="项目名称" prop="title">
              <el-input v-model="form.title" maxlength="100" show-word-limit />
            </el-form-item>
            <el-form-item label="参保年度" prop="insuranceYear">
              <el-input v-model="form.insuranceYear" maxlength="20" />
            </el-form-item>
            <el-form-item label="项目说明" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="金额(元)" prop="amount">
                  <el-input-number v-model="form.amount" :min="0" :precision="2" :step="10" style="width: 100%;" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态" style="width:100%;">
                    <el-option :value="1" label="开放" />
                    <el-option :value="0" label="关闭" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="开始时间" prop="startTime">
                  <el-date-picker
                    v-model="form.startTime"
                    type="datetime"
                    placeholder="开始时间"
                    value-format="timestamp"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="截止时间" prop="endTime">
                  <el-date-picker
                    v-model="form.endTime"
                    type="datetime"
                    placeholder="截止时间"
                    value-format="timestamp"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" :loading="submitting" @click="save">确 定</el-button>
          </div>
        </el-dialog>
      </el-tab-pane>

      <el-tab-pane label="用户参保缴费信息" name="enrollments">
        <el-card id="search2">
          <el-row>
            <el-col :span="20">
              <el-input v-model="enrollSearch.userId" placeholder="用户ID(可选)" clearable />
              <el-input v-model="enrollSearch.insuranceInfoId" placeholder="医保项目ID(可选)" clearable />
              <el-button type="primary" round icon="el-icon-search" @click="getEnrollmentPage">查询</el-button>
              <el-button type="info" round icon="el-icon-refresh-left" @click="resetEnrollSearch">重置</el-button>
            </el-col>
          </el-row>
        </el-card>

        <el-card v-loading="enrollLoading" element-loading-text="加载中...">
          <el-table :data="enrollmentList" stripe style="width: 100%">
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="id" label="记录ID" width="90" />
            <el-table-column prop="userId" label="用户ID" width="90" />
            <el-table-column prop="insuranceInfoId" label="项目ID" width="90" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="studentNo" label="学号" width="140" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column prop="major" label="专业" min-width="140" />
            <el-table-column prop="grade" label="年级" width="100" />
            <el-table-column prop="payAmount" label="缴费金额" width="110" />
            <el-table-column label="缴费状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.payStatus === 1 ? 'success' : 'warning'">{{ scope.row.payStatus === 1 ? '已缴费' : '待缴费' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="缴费时间" width="180">
              <template slot-scope="scope">{{ formatDate(scope.row.payTime) }}</template>
            </el-table-column>
            <el-table-column label="报名时间" width="180">
              <template slot-scope="scope">{{ formatDate(scope.row.createdAt) }}</template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-pagination
          :current-page="enrollSearch.pageNo"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="enrollSearch.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="enrollTotal"
          @size-change="handleEnrollSizeChange"
          @current-change="handleEnrollCurrentChange"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import insuranceApi from '@/api/insurance'

export default {
  name: 'InsuranceManage',
  data() {
    return {
      activeTab: 'insuranceInfo',

      loading: false,
      submitting: false,
      dialogVisible: false,
      dialogTitle: '发布医保项目',

      insuranceList: [],
      total: 0,
      searchModel: {
        title: '',
        insuranceYear: '',
        pageNo: 1,
        pageSize: 10
      },

      form: {
        id: null,
        title: '',
        insuranceYear: '',
        description: '',
        amount: 0,
        startTime: null,
        endTime: null,
        status: 1
      },

      rules: {
        title: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        insuranceYear: [{ required: true, message: '请输入参保年度', trigger: 'blur' }],
        amount: [{ required: true, message: '请输入金额', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      },

      enrollLoading: false,
      enrollmentList: [],
      enrollTotal: 0,
      enrollSearch: {
        userId: '',
        insuranceInfoId: '',
        pageNo: 1,
        pageSize: 10
      }
    }
  },
  created() {
    this.getInsurancePage()
    this.getEnrollmentPage()
  },
  methods: {
    formatDate(value) {
      if (!value) return '--'
      const d = new Date(value)
      return d.toLocaleString('zh-CN', { hour12: false })
    },

    getInsurancePage() {
      this.loading = true
      insuranceApi.getInsurancePage(this.searchModel).then(res => {
        this.insuranceList = res.data.rows || []
        this.total = res.data.total || 0
      }).finally(() => {
        this.loading = false
      })
    },

    resetSearch() {
      this.searchModel = {
        title: '',
        insuranceYear: '',
        pageNo: 1,
        pageSize: 10
      }
      this.getInsurancePage()
    },

    handleSizeChange(size) {
      this.searchModel.pageSize = size
      this.getInsurancePage()
    },

    handleCurrentChange(page) {
      this.searchModel.pageNo = page
      this.getInsurancePage()
    },

    openDialog(row) {
      if (!row) {
        this.dialogTitle = '发布医保项目'
        this.form = {
          id: null,
          title: '',
          insuranceYear: '',
          description: '',
          amount: 0,
          startTime: null,
          endTime: null,
          status: 1
        }
      } else {
        this.dialogTitle = '编辑医保项目'
        this.form = {
          id: row.id,
          title: row.title,
          insuranceYear: row.insuranceYear,
          description: row.description,
          amount: Number(row.amount || 0),
          startTime: row.startTime ? new Date(row.startTime).getTime() : null,
          endTime: row.endTime ? new Date(row.endTime).getTime() : null,
          status: row.status
        }
      }
      this.dialogVisible = true
    },

    resetForm() {
      this.submitting = false
      if (this.$refs.formRef) {
        this.$refs.formRef.clearValidate()
      }
    },

    save() {
      if (this.submitting) return
      this.$refs.formRef.validate(async(valid) => {
        if (!valid) return
        if (this.form.startTime && this.form.endTime && this.form.startTime > this.form.endTime) {
          this.$message.warning('开始时间不能晚于截止时间')
          return
        }

        this.submitting = true
        try {
          await insuranceApi.saveInsuranceInfo({ ...this.form })
          this.$message.success('操作成功')
          this.dialogVisible = false
          this.getInsurancePage()
        } finally {
          this.submitting = false
        }
      })
    },

    deleteById(id) {
      this.$confirm('确认删除该医保项目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        await insuranceApi.deleteInsuranceInfo(id)
        this.$message.success('删除成功')
        this.getInsurancePage()
      }).catch(() => {})
    },

    getEnrollmentPage() {
      this.enrollLoading = true
      const q = {
        ...this.enrollSearch,
        userId: this.enrollSearch.userId ? Number(this.enrollSearch.userId) : undefined,
        insuranceInfoId: this.enrollSearch.insuranceInfoId ? Number(this.enrollSearch.insuranceInfoId) : undefined
      }
      insuranceApi.getEnrollmentPage(q).then(res => {
        this.enrollmentList = res.data.rows || []
        this.enrollTotal = res.data.total || 0
      }).finally(() => {
        this.enrollLoading = false
      })
    },

    resetEnrollSearch() {
      this.enrollSearch = {
        userId: '',
        insuranceInfoId: '',
        pageNo: 1,
        pageSize: 10
      }
      this.getEnrollmentPage()
    },

    handleEnrollSizeChange(size) {
      this.enrollSearch.pageSize = size
      this.getEnrollmentPage()
    },

    handleEnrollCurrentChange(page) {
      this.enrollSearch.pageNo = page
      this.getEnrollmentPage()
    }
  }
}
</script>

<style scoped>
#search .el-input,
#search2 .el-input {
  width: 200px;
  margin-right: 20px;
}

.page-container {
  width: 100%;
  height: 100%;
  margin: -10px;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

.page-container .el-card {
  width: 100% !important;
  margin: 0 0 20px 0 !important;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.action-btn {
  font-weight: 600;
  padding: 8px 16px;
  margin-right: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-btn.edit-btn {
  background-color: #409EFF;
  color: #fff;
  border: none;
}

.action-btn.delete-btn {
  background-color: #F56C6C;
  color: #fff;
  border: none;
}
</style>
