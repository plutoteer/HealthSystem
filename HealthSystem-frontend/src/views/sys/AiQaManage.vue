<!-- AI咨询问题管理页面 -->
<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">AI咨询问题管理</h1>
      <p class="page-subtitle">管理和维护AI健康咨询问答数据，支持增删改查操作</p>
    </div>

    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input
            v-model="searchModel.keywords"
            placeholder="关键词"
            clearable
            style="width: 200px; margin-right: 10px;"
          />
          <el-select
            v-model="searchModel.category"
            placeholder="问题分类"
            clearable
            style="width: 150px; margin-right: 10px;"
          >
            <el-option label="减肥" value="减肥" />
            <el-option label="运动" value="运动" />
            <el-option label="饮食" value="饮食" />
            <el-option label="睡眠" value="睡眠" />
            <el-option label="疾病" value="疾病" />
            <el-option label="其他" value="其他" />
          </el-select>
          <el-input
            v-model="searchModel.question"
            placeholder="示例问题"
            clearable
            style="width: 200px; margin-right: 10px;"
          />
          <el-button
            type="primary"
            round
            icon="el-icon-search"
            @click="getQAList"
          >查询</el-button>
          <el-button
            type="info"
            round
            icon="el-icon-refresh-left"
            @click="resetSearch"
          >重置</el-button>
        </el-col>
        <el-col :span="4" style="text-align: right;">
          <el-button
            type="success"
            round
            icon="el-icon-plus"
            @click="openEditUi(null)"
          >新增</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 结果列表 -->
    <el-card>
      <el-table :data="qaList" stripe style="width: 100%">
        <el-table-column
          type="index"
          label="序号"
          width="60"
        />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column
          prop="keywords"
          label="关键词"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="category"
          label="分类"
          width="100"
        />
        <el-table-column
          prop="question"
          label="示例问题"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="answer"
          label="回答内容"
          min-width="300"
          show-overflow-tooltip
        />
        <el-table-column
          prop="priority"
          label="优先级"
          width="80"
        />
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
              class="action-btn edit-btn"
              @click="openEditUi(scope.row.id)"
            >编辑</el-button>
            <el-button
              type="danger"
              size="small"
              class="action-btn delete-btn"
              @click="deleteQA(scope.row)"
            >删除</el-button>
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

    <!-- AI咨询问题编辑弹出框 -->
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      width="70%"
      @close="clearForm"
    >
      <el-form ref="qaFormRef" :model="qaForm" :rules="rules">
        <el-form-item
          label="关键词"
          prop="keywords"
          :label-width="formLabelWidth"
        >
          <el-input
            v-model="qaForm.keywords"
            placeholder="多个关键词用逗号分隔，如：减肥,减重,瘦身"
            autocomplete="off"
          />
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            提示：多个关键词用逗号分隔
          </div>
        </el-form-item>

        <el-form-item
          label="问题分类"
          prop="category"
          :label-width="formLabelWidth"
        >
          <el-select v-model="qaForm.category" placeholder="请选择问题分类" style="width: 100%">
            <el-option label="减肥" value="减肥" />
            <el-option label="运动" value="运动" />
            <el-option label="饮食" value="饮食" />
            <el-option label="睡眠" value="睡眠" />
            <el-option label="疾病" value="疾病" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item
          label="示例问题"
          prop="question"
          :label-width="formLabelWidth"
        >
          <el-input
            v-model="qaForm.question"
            type="textarea"
            :rows="2"
            placeholder="请输入示例问题"
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item
          label="回答内容"
          prop="answer"
          :label-width="formLabelWidth"
        >
          <el-input
            v-model="qaForm.answer"
            type="textarea"
            :rows="8"
            placeholder="请输入回答内容"
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item
          label="优先级"
          prop="priority"
          :label-width="formLabelWidth"
        >
          <el-input-number
            v-model="qaForm.priority"
            :min="0"
            :max="100"
            placeholder="数字越大优先级越高"
            style="width: 100%"
          />
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            提示：数字越大优先级越高，范围0-100
          </div>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveQA">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import aiConsultationApi from '@/api/aiConsultation'

export default {
  data() {
    return {
      qaForm: {},
      qaList: [],
      formLabelWidth: '135px',
      dialogFormVisible: false,
      title: '',
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10,
        keywords: '',
        category: '',
        question: ''
      },
      rules: {
        keywords: [
          { required: true, message: '请输入关键词', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择问题分类', trigger: 'change' }
        ],
        answer: [
          { required: true, message: '请输入回答内容', trigger: 'blur' }
        ],
        priority: [
          { required: true, message: '请输入优先级', trigger: 'blur' }
        ]
      }
    }
  },

  created() {
    this.getQAList()
  },

  methods: {
    // 获取AI咨询问题列表
    getQAList() {
      aiConsultationApi.getQAList(this.searchModel).then((response) => {
        this.qaList = response.data.rows
        this.total = response.data.total
      })
    },

    // 重置搜索
    resetSearch() {
      this.searchModel = {
        keywords: '',
        category: '',
        question: '',
        pageNo: 1,
        pageSize: 10
      }
      this.getQAList()
    },

    // 打开编辑界面
    openEditUi(id) {
      if (id == null) {
        this.title = '新增AI咨询问题'
        this.qaForm = {
          priority: 0
        }
      } else {
        this.title = '修改AI咨询问题'
        aiConsultationApi.getQAById(id).then((response) => {
          this.qaForm = response.data
        })
      }
      this.dialogFormVisible = true
    },

    // 保存AI咨询问题
    saveQA() {
      this.$refs.qaFormRef.validate((valid) => {
        if (valid) {
          aiConsultationApi.saveQA(this.qaForm).then((response) => {
            this.$message({
              type: 'success',
              message: response.message
            })
            this.dialogFormVisible = false
            this.getQAList()
          })
        } else {
          return false
        }
      })
    },

    // 删除AI咨询问题
    deleteQA(qa) {
      this.$confirm(`确认删除该AI咨询问题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          aiConsultationApi.deleteQAById(qa.id).then((response) => {
            this.$message({
              type: 'success',
              message: response.message
            })
            this.getQAList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    // 清理表单
    clearForm() {
      this.qaForm = {}
      if (this.$refs.qaFormRef) {
        this.$refs.qaFormRef.clearValidate()
      }
    },

    // 分页大小改变
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getQAList()
    },

    // 当前页改变
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getQAList()
    }
  }
}
</script>

<style>
#search .el-input {
  width: 200px;
  margin-right: 10px;
}

.page-container {
  width: 100%;
  height: 100%;
  margin: -10px;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

/* 页面标题样式 */
.page-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e4e7ed;
  text-align: center;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
  line-height: 1.5;
}

.page-container .el-card {
  width: 100% !important;
  margin: 0 0 20px 0 !important;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.el-table {
  width: 100%;
  border-collapse: collapse;
}

.el-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px;
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

.action-btn.edit-btn:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
}

.action-btn.delete-btn {
  background-color: #F56C6C;
  color: #fff;
  border: none;
}

.action-btn.delete-btn:hover {
  background-color: #f78989;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(245, 108, 108, 0.3);
}
</style>

