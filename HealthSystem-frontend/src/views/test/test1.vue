<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">身体信息上传</h2>
      <p class="page-subtitle">请填写您的个人健康信息，包括基本身体指标、生活习惯等数据</p>
    </div>
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="form"
    >
      <el-row :gutter="15">
        <el-col :xs="24" :sm="8">
          <el-form-item label="姓名" prop="name">
            <el-input
              v-model="form.name"
              :placeholder="'请输入姓名'"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="年龄" prop="age">
            <el-input-number v-model="form.age" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="15">
        <el-col :xs="24" :sm="8">
          <el-form-item label="身高/m" prop="height">
            <el-input-number v-model="form.height" :min="0" :step="0.1" style="width: 100%" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="体重/kg" prop="weight">
            <el-input-number v-model="form.weight" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="血糖" prop="bloodSugar">
            <el-input-number
              v-model="form.bloodSugar"
              :min="0"
              :step="0.1"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="15">
        <el-col :xs="24" :sm="8">
          <el-form-item label="血压" prop="bloodPressure">
            <el-input-number
              v-model="form.bloodPressure"
              :min="0"
              :step="0.1"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="胆固醇" prop="bloodLipid">
            <el-input-number
              v-model="form.bloodLipid"
              :min="0"
              :step="0.1"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="心率/BPM" prop="heartRate">
            <el-input-number
              v-model="form.heartRate"
              :min="0"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="15">
        <el-col :xs="24" :sm="8">
          <el-form-item label="视力/d" prop="vision">
            <el-input-number v-model="form.vision" :min="0" :step="0.1" style="width: 100%" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="睡眠时长/h" prop="sleepDuration">
            <el-input-number
              v-model="form.sleepDuration"
              :min="0"
              :step="0.1"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="睡眠质量" prop="sleepQuality">
            <el-radio-group v-model="form.sleepQuality">
              <el-radio :label="'好'">好</el-radio>
              <el-radio :label="'一般'">一般</el-radio>
              <el-radio :label="'差'">差</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="15">
        <el-col :xs="24" :sm="8">
          <el-form-item label="是否吸烟" prop="smoking">
            <el-switch v-model="form.smoking" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="是否饮酒" prop="drinking">
            <el-switch v-model="form.drinking" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="是否运动" prop="exercise">
            <el-switch v-model="form.exercise" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="15">
        <el-col :xs="24" :sm="8">
          <el-form-item label="喜好食物种类" prop="foodTypes">
            <el-select v-model="form.foodTypes" placeholder="请选择" style="width: 100%">
              <el-option label="蔬菜" value="蔬菜" />
              <el-option label="水果" value="水果" />
              <el-option label="肉类" value="肉类" />
              <el-option label="鱼类" value="鱼类" />
              <el-option label="豆类" value="豆类" />
              <el-option label="谷物" value="谷物" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="8">
          <el-form-item label="饮水量/ml" prop="waterConsumption">
            <el-input-number
              v-model="form.waterConsumption"
              :min="0"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item class="form-actions">
        <el-button type="primary" @click="submitForm()">提交上传</el-button>
        <el-button @click="resetForm('form')">重置表单</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import FunctionApi from '@/api/Function_Menu'
import userApi from '@/api/userManage'

export default {
  name: 'InfoUpload',
  data() {
    return {
      form: {
        id: null,
        name: '',
        age: null,
        gender: '',
        height: '',
        weight: '',
        bloodSugar: '',
        bloodPressure: '',
        bloodLipid: '',
        heartRate: null,
        vision: null,
        sleepDuration: null,
        sleepQuality: '',
        smoking: null,
        drinking: null,
        exercise: null,
        foodTypes: '',
        waterConsumption: null
      },

      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          {
            min: 2,
            max: 10,
            message: '长度在 2 到 10 个字符',
            trigger: 'blur'
          }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' },
          { type: 'number', message: '年龄必须为数字值' }
        ],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        height: [
          { required: true, message: '请输入身高', trigger: 'blur' },
          { type: 'number', message: '身高必须为数字值' }
        ],
        weight: [
          { required: true, message: '请输入体重', trigger: 'blur' },
          { type: 'number', message: '体重必须为数字值' }
        ],
        bloodSugar: [
          { required: true, message: '请输入血糖', trigger: 'blur' },
          { type: 'number', message: '血糖必须为数字值' }
        ],
        bloodPressure: [
          { required: true, message: '请输入血压', trigger: 'blur' },
          { type: 'number', message: '血压必须为数字值' }
        ],
        bloodLipid: [
          { required: true, message: '请输入血脂', trigger: 'blur' },
          { type: 'number', message: '血脂必须为数字值' }
        ],

        heartRate: [
          { required: true, message: '请输入心率', trigger: 'blur' },
          { type: 'number', message: '心率必须为数字值' }
        ],
        vision: [
          { required: true, message: '请输入视力', trigger: 'blur' },
          { type: 'number', message: '视力必须为数字值' }
        ],
        sleepQuality: [
          {
            required: true,
            message: '请选择睡眠质量',
            trigger: 'change'
          }
        ],
        smoking: [
          { required: true, message: '请选择是否吸烟', trigger: 'change' }
        ],
        drinking: [
          { required: true, message: '请选择是否饮酒', trigger: 'change' }
        ],
        exercise: [
          { required: true, message: '请选择是否运动', trigger: 'change' }
        ],
        foodTypes: [
          { required: true, message: '请选择摄入较多的食物种类', trigger: 'blur' }
        ],
        waterConsumption: [
          { required: true, message: '请输入饮水量', trigger: 'blur' },
          { type: 'number', message: '饮水量必须为数字值' }
        ]
      }
    }
  },

  created() {
    this.getUserId()
  },

  methods: {
    submitForm() {
      // 如果表单数据中没有 id 属性，则将组件的 id 属性赋值给表单数据的 id 属性
      if (!this.form.id) {
        this.form.id = this.id
      }

      this.$refs.form.validate((valid) => {
        if (valid) {
          FunctionApi.BodyInformation(this.form)
            .then((response) => {
              this.$message({
                type: 'success',
                message: response.message
              })
            })
            .catch((error) => {
              this.$message({
                type: 'error',
                message: error.response?.data?.message || '操作失败'
              })
            })

          FunctionApi.BodyInformationNotes(this.form)
            .then((response) => {
              // 成功处理
            })
            .catch((error) => {
              this.$message({
                type: 'error',
                message: error.response?.data?.message || '操作失败'
              })
            })
        } else {
          return false
        }
      })
    },

    // 重置表单数据的方法
    resetForm(formName) {
      this.$refs[formName].resetFields()
      console.log(this.id)
      this.$message({
        message: '表单已重置',
        type: 'info'
      })
    },

    async getUserId() {
      await userApi.getUserId().then((response) => {
      // 如果请求成功并且返回的数据包含 id 属性，则将其转换为整数并赋值给组件的 id 属性
        if (response && response.data) {
          this.id = parseInt(response.data.id)
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

.form {
  padding: 0;
}

.form ::v-deep .el-form-item {
  margin-bottom: 18px;
}

.form ::v-deep .el-form-item__label {
  font-weight: 500;
  color: #606266;
  font-size: 14px;
  padding-right: 8px;
  white-space: nowrap;
}

.form ::v-deep .el-form-item__content {
  margin-left: 0;
}

.form-actions {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

.form-actions ::v-deep .el-button {
  margin: 0 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-container {
    padding: 15px;
  }

  .form-actions ::v-deep .el-button {
    width: 100%;
    margin: 5px 0;
  }
}
</style>
