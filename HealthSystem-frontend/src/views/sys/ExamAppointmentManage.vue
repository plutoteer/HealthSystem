<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="体检场次发布" name="slots">
        <el-card id="search">
          <el-row>
            <el-col :span="20">
              <el-input v-model="slotSearch.region" placeholder="体检地区" clearable />
              <el-button type="primary" round icon="el-icon-search" @click="getSlotList">查询</el-button>
              <el-button type="info" round icon="el-icon-refresh-left" @click="resetSlotSearch">重置</el-button>
            </el-col>
            <el-col :span="4" style="text-align: right;">
              <el-button type="success" round icon="el-icon-plus" @click="openSlotDialog(null)">发布</el-button>
            </el-col>
          </el-row>
        </el-card>

        <el-card v-loading="slotLoading" element-loading-text="加载中...">
          <el-table :data="slotList" stripe style="width: 100%">
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="region" label="体检地区" />
            <el-table-column label="体检时间" width="200">
              <template slot-scope="scope">
                {{ formatDate(scope.row.examTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template slot-scope="scope">
                <el-button type="primary" size="small" class="action-btn edit-btn" @click="openSlotDialog(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" class="action-btn delete-btn" @click="deleteSlot(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-pagination
          :current-page="slotSearch.pageNo"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="slotSearch.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="slotTotal"
          @size-change="handleSlotSizeChange"
          @current-change="handleSlotCurrentChange"
        />

        <el-dialog :title="slotDialogTitle" :visible.sync="slotDialogVisible" @close="resetSlotForm">
          <el-form ref="slotFormRef" :model="slotForm" :rules="slotRules" label-width="100px">
            <el-form-item label="体检地区" prop="region">
              <el-input v-model="slotForm.region" maxlength="50" show-word-limit placeholder="如：北京/上海/广州..." />
            </el-form-item>
            <el-form-item label="体检时间" prop="examTime">
              <el-date-picker
                v-model="slotForm.examTime"
                type="datetime"
                placeholder="选择体检时间"
                value-format="timestamp"
                style="width: 100%;"
              />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="slotDialogVisible = false">取 消</el-button>
            <el-button type="primary" :loading="slotSubmitting" @click="saveSlot">确 定</el-button>
          </div>
        </el-dialog>
      </el-tab-pane>

      <el-tab-pane label="用户预约记录" name="appointments">
        <el-card id="search2">
          <el-row>
            <el-col :span="20">
              <el-input v-model="apptSearch.userId" placeholder="用户ID(可选)" clearable />
              <el-button type="primary" round icon="el-icon-search" @click="getAppointmentList">查询</el-button>
              <el-button type="info" round icon="el-icon-refresh-left" @click="resetApptSearch">重置</el-button>
            </el-col>
          </el-row>
        </el-card>

        <el-card v-loading="apptLoading" element-loading-text="加载中...">
          <el-table :data="appointmentList" stripe style="width: 100%">
            <el-table-column type="index" label="序号" width="80" />
            <el-table-column prop="id" label="预约ID" width="90" />
            <el-table-column prop="userId" label="用户ID" width="90" />
            <el-table-column prop="slotId" label="场次ID" width="90" />
            <el-table-column prop="location" label="体检地区" />
            <el-table-column label="体检时间" width="200">
              <template slot-scope="scope">
                {{ formatDate(scope.row.examTime) }}
              </template>
            </el-table-column>
            <el-table-column label="预约创建时间" width="200">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-pagination
          :current-page="apptSearch.pageNo"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="apptSearch.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="apptTotal"
          @size-change="handleApptSizeChange"
          @current-change="handleApptCurrentChange"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import examSlotApi from '@/api/examSlot'
import appointmentApi from '@/api/appointment'

export default {
  name: 'ExamAppointmentManage',
  data() {
    return {
      activeTab: 'slots',

      // slots
      slotLoading: false,
      slotSubmitting: false,
      slotDialogVisible: false,
      slotDialogTitle: '发布体检场次',
      slotList: [],
      slotTotal: 0,
      slotSearch: { region: '', pageNo: 1, pageSize: 10 },
      slotForm: { id: null, region: '', examTime: null }, // examTime timestamp
      slotRules: {
        region: [{ required: true, message: '请输入体检地区', trigger: 'blur' }],
        examTime: [{ required: true, message: '请选择体检时间', trigger: 'change' }]
      },

      // appointments
      apptLoading: false,
      appointmentList: [],
      apptTotal: 0,
      apptSearch: { userId: '', pageNo: 1, pageSize: 10 }
    }
  },
  created() {
    this.getSlotList()
    this.getAppointmentList()
  },
  methods: {
    formatDate(value) {
      if (!value) return '--'
      const d = new Date(value)
      return d.toLocaleString('zh-CN', { hour12: false })
    },

    // slots
    getSlotList() {
      this.slotLoading = true
      examSlotApi.getList(this.slotSearch).then(res => {
        this.slotList = res.data.rows || []
        this.slotTotal = res.data.total || 0
      }).finally(() => {
        this.slotLoading = false
      })
    },
    resetSlotSearch() {
      this.slotSearch = { region: '', pageNo: 1, pageSize: 10 }
      this.getSlotList()
    },
    handleSlotSizeChange(size) {
      this.slotSearch.pageSize = size
      this.getSlotList()
    },
    handleSlotCurrentChange(page) {
      this.slotSearch.pageNo = page
      this.getSlotList()
    },
    openSlotDialog(row) {
      if (!row) {
        this.slotDialogTitle = '发布体检场次'
        this.slotForm = { id: null, region: '', examTime: null }
      } else {
        this.slotDialogTitle = '编辑体检场次'
        this.slotForm = {
          id: row.id,
          region: row.region,
          examTime: row.examTime ? new Date(row.examTime).getTime() : null
        }
      }
      this.slotDialogVisible = true
    },
    resetSlotForm() {
      this.slotSubmitting = false
      if (this.$refs.slotFormRef) this.$refs.slotFormRef.clearValidate()
    },
    saveSlot() {
      if (this.slotSubmitting) return
      this.$refs.slotFormRef.validate(async(valid) => {
        if (!valid) return
        this.slotSubmitting = true
        try {
          await examSlotApi.save({
            id: this.slotForm.id,
            region: this.slotForm.region,
            examTime: this.slotForm.examTime // timestamp(ms)
          })
          this.$message.success('操作成功')
          this.slotDialogVisible = false
          this.getSlotList()
        } finally {
          this.slotSubmitting = false
        }
      })
    },
    deleteSlot(row) {
      this.$confirm(`确认删除该场次吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        await examSlotApi.deleteById(row.id)
        this.$message.success('删除成功')
        this.getSlotList()
      }).catch(() => {})
    },

    // appointments
    getAppointmentList() {
      this.apptLoading = true
      const q = {
        ...this.apptSearch,
        userId: this.apptSearch.userId ? Number(this.apptSearch.userId) : undefined
      }
      appointmentApi.getAppointmentList(q).then(res => {
        this.appointmentList = res.data.rows || []
        this.apptTotal = res.data.total || 0
      }).finally(() => {
        this.apptLoading = false
      })
    },
    resetApptSearch() {
      this.apptSearch = { userId: '', pageNo: 1, pageSize: 10 }
      this.getAppointmentList()
    },
    handleApptSizeChange(size) {
      this.apptSearch.pageSize = size
      this.getAppointmentList()
    },
    handleApptCurrentChange(page) {
      this.apptSearch.pageNo = page
      this.getAppointmentList()
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

