<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">体检预约</h2>
      <p class="page-subtitle">查看管理员发布的体检场次并完成预约</p>
    </div>

    <el-card class="section-card" v-loading="slotLoading">
      <div slot="header" class="card-header">可预约体检场次</div>
      <el-table :data="slotList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="id" label="场次ID" width="90" />
        <el-table-column prop="region" label="体检地区" min-width="180" />
        <el-table-column label="体检时间" min-width="200">
          <template slot-scope="scope">{{ formatDate(scope.row.examTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="book(scope.row)">立即预约</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="section-card" v-loading="myLoading">
      <div slot="header" class="card-header">我的预约记录</div>
      <el-table :data="myAppointments" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="id" label="预约ID" width="90" />
        <el-table-column prop="slotId" label="场次ID" width="90" />
        <el-table-column prop="location" label="体检地区" min-width="160" />
        <el-table-column label="体检时间" min-width="180">
          <template slot-scope="scope">{{ formatDate(scope.row.examTime) }}</template>
        </el-table-column>
        <el-table-column label="预约时间" min-width="180">
          <template slot-scope="scope">{{ formatDate(scope.row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import examSlotApi from '@/api/examSlot'
import appointmentApi from '@/api/appointment'

export default {
  name: 'UserExamAppointment',
  data() {
    return {
      slotLoading: false,
      myLoading: false,
      submittingId: null,
      slotList: [],
      myAppointments: []
    }
  },
  created() {
    this.loadSlots()
    this.loadMyAppointments()
  },
  methods: {
    formatDate(value) {
      if (!value) return '--'
      return new Date(value).toLocaleString('zh-CN', { hour12: false })
    },

    async loadSlots() {
      this.slotLoading = true
      try {
        const res = await examSlotApi.getAll()
        this.slotList = res.data || []
      } finally {
        this.slotLoading = false
      }
    },

    async loadMyAppointments() {
      this.myLoading = true
      try {
        const res = await appointmentApi.getMyAppointments()
        this.myAppointments = res.data || []
      } finally {
        this.myLoading = false
      }
    },

    async book(slot) {
      if (this.submittingId) return
      this.submittingId = slot.id
      try {
        const res = await appointmentApi.createAppointment({ slotId: slot.id })
        this.$message.success(res.message || '预约成功')
        await this.loadMyAppointments()
      } catch (e) {
        this.$message.error((e && e.message) || '预约失败，请稍后重试')
      } finally {
        this.submittingId = null
      }
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
