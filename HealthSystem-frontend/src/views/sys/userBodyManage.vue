<!-- 搜索框和添加按钮，搜索框，以及用户名和手机号的输入框 -->
<template>
  <div class="page-container">
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <!-- v-model绑定组件实现双向数据绑定，页面上用户输入的值会同步更新到该属性中 -->
          <el-input
            v-model="searchModel.name"
            placeholder="请输入昵称"
            clearable
          />
          <el-input
            v-model="searchModel.id"
            placeholder="请输入ID"
            clearable
          />
          <el-button
            type="primary"
            round
            icon="el-icon-search"
            @click="getBodyList"
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
      <el-table :data="bodyList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="昵称" width="80" />
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

        <el-table-column label="操作" width="180" fixed="right">
          <!-- 删除和修改按钮 -->
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
              @click="deleteBody(scope.row)"
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
    this.getBodyList()
    console.log(this.bodyList)
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
        // 判断是新增还是编辑
        const isAdd = !this.bodyForm.id || this.bodyForm.id === null || this.bodyForm.id === undefined
        const apiCall = isAdd ? userApi.addBody(this.bodyForm) : userApi.updateBody(this.bodyForm)

        // 提交验证给后台
        apiCall.then((response) => {
          // 成功提示
          this.$message({
            message: response.message || (isAdd ? '新增成功' : '修改成功'),
            type: 'success'
          })
          // 关闭对话框
          this.dialogFormVisible = false
          // 刷新表格数据
          this.getBodyList()
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
      this.getBodyList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getBodyList()
    },

    // 用于查询用户列表
    getBodyList() {
      userApi.getBodyList(this.searchModel).then((response) => {
        console.log(response)

        this.bodyList = response.data.rows
        // 将查询结果中的 total 属性赋值给 total
        this.total = response.data.total
        console.log(this.bodyList)
      })
    },
    resetSearch() {
      this.searchModel = {
        name: '',
        id: '',
        pageNo: 1,
        pageSize: 10
      }
      this.getBodyList()
    },

    openEditUi(id) {
      if (id == null) {
        this.title = '新增身体信息'
        this.bodyForm = {}
      } else {
        this.title = '修改身体信息'
        // 根据id查询用户数据
        userApi.getBodyById(id).then((response) => {
          this.bodyForm = response.data
        })
      }

      this.dialogFormVisible = true
    },

    deleteBody(body) {
      this.$confirm(`确认删除 ${body.name} 这个身体信息吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          userApi.deleteBodyById(body.id).then((response) => {
            this.$message({
              type: 'success',
              message: response.message
            })
            this.getBodyList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
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

/* 很美观的CSS样式 */
/* body {
    background: linear-gradient(to right, lightblue, lightpink);
    margin: 0;
    padding: 0;
    font-family: Arial, Helvetica, sans-serif;
  } */

/* 页面容器样式 - 占满整个区域 */
.page-container {
  width: 100%;
  height: 100%;
  margin: -10px;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

/* 很美观的CSS卡片 - 占满整个容器宽度 */
.page-container .el-card {
  width: 100% !important;
  margin: 0 0 20px 0 !important;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

/* 很美观的CSS表格 */
.el-table {
  width: 100%;
  border-collapse: collapse;
}

/* 很美观的CSS表格标题 */
.el-table-column {
  background-color: lightblue;
  color: white;
  padding: 10px;
  border: 1px solid white;
  text-align: center;
}

/* 很美观的CSS表格数据 */
.el-table-column[type="index"],
.el-table-column[prop="id"],
.el-table-column[prop="username"],
.el-table-column[prop="phone"],
.el-table-column[prop="email"] {
  background-color: white;
  color: black;
  padding: 10px;
  border: 1px solid lightblue;
  text-align: center;
}

/* 很美观的CSS表格数据悬停效果 */
.el-table-column[type="index"]:hover,
.el-table-column[prop="id"]:hover,
.el-table-column[prop="username"]:hover,
.el-table-column[prop="phone"]:hover,
.el-table-column[prop="email"]:hover {
  background-color: lightpink;
  color: white;
}

/* 很美观的CSS按钮悬停效果 */
.el-button:hover {
  transform: scale(1.2);
}

/* 和这个代码一样的CSS */
.el-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px;
}

/* 和这个代码一样的CSS总数 */
.el-pagination__total {
  color: #606266;
  margin-right: 20px;
}

/* 和这个代码一样的CSS每页显示条数 */
.el-pagination__sizes {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

/* 和这个代码一样的CSS每页显示条数选择器 */
.el-pagination__sizes .el-select {
  width: 100px;
}

/* 和这个代码一样的CSS上一页按钮 */
.el-pagination__prev {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

/* 和这个代码一样的CSS上一页按钮图标 */
.el-pagination__prev .el-icon {
  font-size: 20px;
  color: #409eff;
}

/* 和这个代码一样的CSS页码 */
.el-pagination__pager {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

/* 和这个代码一样的CSS页码按钮 */
.el-pagination__pager button {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  border: none;
  background-color: white;
  color: #606266;
  margin: 2px;
  transition: all 0.3s ease-in-out;
}

/* 和这个代码一样的CSS页码按钮悬停效果 */
.el-pagination__pager button:hover {
  background-color: #409eff;
  color: white;
}

/* 和这个代码一样的CSS当前页码按钮 */
.el-pagination__pager button.is-active {
  background-color: #409eff;
  color: white;
}

/* 和这个代码一样的CSS下一页按钮 */
.el-pagination__next {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

/* 和这个代码一样的CSS下一页按钮图标 */
.el-pagination__next .el-icon {
  font-size: 20px;
  color: #409eff;
}

/* 和这个代码一样的CSS跳转输入框 */
.el-pagination__jump {
  display: flex;
  align-items: center;
}

/* 和这个代码一样的CSS跳转输入框标签 */
.el-pagination__jump label {
  color: #606266;
}

/* 和这个代码一样的CSS跳转输入框输入框 */
.el-pagination__jump input {
  width: 50px;
  height: 30px;
  border-radius: 4px;
  border: none;
}

/* 操作列按钮样式 */
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
