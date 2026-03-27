<!-- 搜索框和添加按钮，搜索框，以及用户名和手机号的输入框 -->
<template>
  <div class="page-container">
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <!-- v-model绑定组件实现双向数据绑定，页面上用户输入的值会同步更新到该属性中 -->
          <el-input
            v-model="searchModel.sportType"
            placeholder="运动类型"
            clearable
          />
          <el-button
            type="primary"
            round
            icon="el-icon-search"
            @click="getDetailList"
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
      <el-table :data="detailList" stripe style="width: 100%">
        <el-table-column
          type="index"
          label="序号"
          width="60"
        />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column
          prop="sportType"
          label="运动类型"
          width="130"
        />
        <el-table-column
          prop="disease"
          label="禁忌疾病"
          width="160"
        />

        <el-table-column
          prop="method"
          label="运动方法"
          width="200"
        />

        <el-table-column
          prop="notes"
          label="注意事项"
          width="260"
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
              @click="deleteDetail(scope.row)"
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
      <el-form ref="detailFormRef" :model="detailForm" :rules="rules">
        <el-form-item label="运动类型" prop="sportInfoId" :label-width="formLabelWidth">
          <el-select v-model="detailForm.sportInfoId" placeholder="请选择运动类型" style="width: 100%" @change="handleSportTypeChange">
            <el-option
              v-for="item in sportInfoList"
              :key="item.id"
              :label="item.sportType"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item
          label="禁忌疾病"
          prop="disease"
          :label-width="formLabelWidth"
        >
          <el-input v-model="detailForm.disease" autocomplete="off" />
        </el-form-item>

        <el-form-item label="运动方法" prop="method" :label-width="formLabelWidth">
          <el-input
            v-model="detailForm.method"
            type="textarea"
            :rows="5"
            autocomplete="off"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item
          label="注意事项"
          prop="notes"
          :label-width="formLabelWidth"
        >
          <el-input
            v-model="detailForm.notes"
            type="textarea"
            :rows="5"
            autocomplete="off"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item
          label="运动图片"
          prop="imageUrl"
          :label-width="formLabelWidth"
        >
          <el-upload
            ref="imageUpload"
            class="image-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleImageChange"
            :before-upload="beforeImageUpload"
            :on-remove="removeImage"
            :on-exceed="handleExceed"
            :limit="1"
            accept="image/*"
            :file-list="imageFileList"
          >
            <img v-if="detailForm.imageUrl" :src="detailForm.imageUrl" class="uploaded-image">
            <i v-else class="el-icon-plus image-uploader-icon" />
          </el-upload>
          <div v-if="imageFile" class="image-info">
            <span>{{ imageFile.name }}</span>
            <el-button type="text" style="margin-left: 10px;" @click="removeImage">删除</el-button>
          </div>
          <div class="upload-tip">只能上传1张图片，且不超过10MB</div>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveDetail">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import sportApi from '@/api/Function_Menu'
export default {
  data() {
    return {
      detailForm: {}, // 初始化为一个空对象
      detailList: [],
      sportInfoList: [], // 运动类型列表
      // 左边宽度
      formLabelWidth: '135px',
      // 设置默认值不可见
      dialogFormVisible: false,
      title: '',
      total: 0,
      imageFile: null, // 上传的图片文件
      imageFileList: [], // el-upload组件的文件列表
      searchModel: {
        pageNo: 1,
        // 默认显示数量
        pageSize: 10
      },
      // 表单规则配置
      rules: {
        sportInfoId: [
          { required: true, message: '请选择运动类型', trigger: 'change' }
        ],
        disease: [
          { required: true, message: '请输入禁忌疾病', trigger: 'blur' }
        ],
        method: [
          { required: true, message: '请输入运动方法', trigger: 'blur' }
        ],
        notes: [
          { required: true, message: '请输入注意事项', trigger: 'blur' }
        ]
      }
    }
  },

  // 加载时就查询一次
  created() {
    this.getDetailList()
    this.getSportInfoList()
  },

  methods: {
    saveDetail() {
      let isOk = true
      // 触发表单的验证
      this.$refs.detailFormRef.validate((valid) => {
        // 这边只有校验失败的时候才会进来,在外面定义一个 isok,校验失败会将他改成 false
        isOk = valid
      })

      if (isOk) {
        // 如果有新上传的图片文件，先上传图片获取相对路径
        if (this.imageFile) {
          console.log('开始上传图片', this.imageFile.name, '文件大小:', this.imageFile.size)

          // 创建FormData上传图片
          const formData = new FormData()
          formData.append('file', this.imageFile)

          sportApi.uploadImage(formData).then((response) => {
            console.log('图片上传成功，返回数据：', response)
            // 释放预览URL
            if (this.detailForm.imageUrl && this.detailForm.imageUrl.startsWith('blob:')) {
              URL.revokeObjectURL(this.detailForm.imageUrl)
            }
            // 从响应中获取图片URL（URL在message字段中，因为后端返回的是Unification.success(relativePath)）
            // response格式：{code: 20000, message: "upload/img/xxx.jpg", data: null}
            const imageUrl = response.message || response.data || ''
            console.log('从响应中获取的imageUrl：', imageUrl)
            if (!imageUrl || imageUrl.trim() === '') {
              console.error('图片URL为空，无法保存')
              this.$message.error('图片上传成功，但URL为空，请重试')
              return
            }
            // 将返回的相对路径设置到detailForm（确保是字符串类型）
            this.detailForm.imageUrl = String(imageUrl).trim()
            console.log('设置后的imageUrl：', this.detailForm.imageUrl, '类型：', typeof this.detailForm.imageUrl)
            // 清空imageFile，表示图片已上传
            this.imageFile = null
            // 清除原始图片路径，因为已经上传了新图片
            delete this.detailForm._originalImageUrl
            // 继续保存详情
            this.submitDetail()
          }).catch((error) => {
            console.error('图片上传失败', error)
            this.$message({
              message: '图片上传失败：' + (error.message || '请重试'),
              type: 'error',
              duration: 5000
            })
          })
        } else {
          console.log('没有新图片，直接保存')
          // 没有新图片，直接保存
          this.submitDetail()
        }
      } else {
        console.log('表单验证失败')
        return false
      }
    },

    // 提交详情数据
    submitDetail() {
      // 如果imageUrl是blob:开头，说明是新选择的图片但还没上传，不应该保存
      if (this.detailForm.imageUrl && this.detailForm.imageUrl.startsWith('blob:')) {
        this.$message.error('请先上传图片')
        return
      }

      // 处理imageUrl：确保是相对路径
      // 1. 如果imageUrl是http开头的完整URL（编辑时加载的），需要转换为相对路径
      if (this.detailForm.imageUrl && this.detailForm.imageUrl.startsWith('http')) {
        // 尝试从完整URL中提取相对路径（如：http://localhost:8080/upload/img/xxx.jpg -> upload/img/xxx.jpg）
        const urlMatch = this.detailForm.imageUrl.match(/upload\/img\/[^/]+$/)
        if (urlMatch) {
          this.detailForm.imageUrl = urlMatch[0]
        } else if (this.detailForm._originalImageUrl) {
          // 如果无法提取，使用原始路径
          this.detailForm.imageUrl = this.detailForm._originalImageUrl
        }
        // 注意：如果无法提取且没有原始路径，保持http开头的URL（不应该发生）
      }

      // 2. 如果有原始图片路径（编辑时未修改图片），且imageUrl不存在或为空，使用原始路径
      if (this.detailForm._originalImageUrl && (!this.detailForm.imageUrl || this.detailForm.imageUrl.trim() === '') && !this.imageFile) {
        this.detailForm.imageUrl = this.detailForm._originalImageUrl
      }

      // 清理临时字段
      delete this.detailForm._originalImageUrl

      // 确保imageUrl是字符串类型（如果存在）
      if (this.detailForm.imageUrl && typeof this.detailForm.imageUrl !== 'string') {
        this.detailForm.imageUrl = String(this.detailForm.imageUrl)
      }

      console.log('提交详情数据：', JSON.stringify(this.detailForm, null, 2))
      console.log('imageUrl值：', this.detailForm.imageUrl, '类型：', typeof this.detailForm.imageUrl)
      sportApi.saveDetail(this.detailForm).then((response) => {
        // 成功提示
        this.$message({
          message: response.message,
          type: 'success'
        })
        // 关闭对话框
        this.dialogFormVisible = false
        // 刷新表格数据
        this.getDetailList()
      })
    },

    // 清理表单数据
    clearForm() {
      // 释放之前创建的URL对象，避免内存泄漏
      if (this.detailForm.imageUrl && this.detailForm.imageUrl.startsWith('blob:')) {
        URL.revokeObjectURL(this.detailForm.imageUrl)
      }
      this.detailForm = {}
      this.imageFile = null
      this.imageFileList = [] // 清空el-upload组件的文件列表
      // 清除表单校验的提示信息
      if (this.$refs.detailFormRef) {
        this.$refs.detailFormRef.clearValidate()
      }
      // 清空el-upload组件的文件列表
      if (this.$refs.imageUpload) {
        this.$refs.imageUpload.clearFiles()
      }
    },
    handleSizeChange(pageSize) {
      // 数据更新
      this.searchModel.pageSize = pageSize
      this.getDetailList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getDetailList()
    },

    // 用于查询用户列表
    getDetailList() {
      sportApi.getDetailList(this.searchModel).then((response) => {
        this.detailList = response.data.rows
        this.total = response.data.total
      })
    },
    resetSearch() {
      this.searchModel = {
        sportType: '',
        pageNo: 1,
        pageSize: 10
      }
      this.getDetailList()
    },

    openEditUi(id) {
      console.log(id)
      // 先清空图片相关状态
      this.imageFile = null
      this.imageFileList = []
      // 清空el-upload组件的文件列表
      this.$nextTick(() => {
        if (this.$refs.imageUpload) {
          this.$refs.imageUpload.clearFiles()
        }
      })

      if (id == null) {
        this.title = '新增运动详情'
        this.detailForm = {}
      } else {
        this.title = '修改运动详情'
        // 根据id查询用户数据
        sportApi.getDetailById(id).then((response) => {
          this.detailForm = response.data
          // 如果detailForm有sportInfoId，直接使用；如果没有但有sportType，则根据sportType查找对应的id
          if (!this.detailForm.sportInfoId && this.detailForm.sportType) {
            const sportInfo = this.sportInfoList.find(item => item.sportType === this.detailForm.sportType)
            if (sportInfo) {
              this.detailForm.sportInfoId = sportInfo.id
            }
          }
          // 如果有图片URL，拼接完整路径用于预览
          if (this.detailForm.imageUrl) {
            // 如果是相对路径，拼接完整URL
            if (!this.detailForm.imageUrl.startsWith('http') && !this.detailForm.imageUrl.startsWith('data:') && !this.detailForm.imageUrl.startsWith('blob:')) {
              const baseUrl = process.env.VUE_APP_BASE_API || 'http://localhost:8080'
              const imageUrl = baseUrl.replace('/api', '') + '/' + this.detailForm.imageUrl
              // 保存原始相对路径，但预览时使用完整URL
              const originalPath = this.detailForm.imageUrl
              this.detailForm.imageUrl = imageUrl
              // 保存原始路径，用于后续保存时使用
              this.detailForm._originalImageUrl = originalPath
            }
            this.imageFile = null
          }
          console.log(this.detailForm)
        })
      }
      this.dialogFormVisible = true
    },

    // 获取运动类型列表
    getSportInfoList() {
      sportApi.getSportInfoList().then((response) => {
        this.sportInfoList = response.data || []
      })
    },

    // 运动类型选择变化时，自动填充sportType字段（用于向后兼容）
    handleSportTypeChange(value) {
      const selectedSport = this.sportInfoList.find(item => item.id === value)
      if (selectedSport) {
        this.detailForm.sportType = selectedSport.sportType
      }
    },

    deleteDetail(detail) {
      this.$confirm(`确认删除 ${detail.sportType} 这个运动详情吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          sportApi.deleteDetailById(detail.id).then((response) => {
            this.$message({
              type: 'success',
              message: response.message
            })
            this.getDetailList()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    // 图片上传前验证
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt10M) {
        this.$message.error('图片大小不能超过10MB!')
        return false
      }
      return true
    },

    // 图片选择变化处理
    handleImageChange(file, fileList) {
      console.log('handleImageChange 被调用', file, fileList)

      // 检查文件是否有效
      if (!file || !file.raw) {
        console.warn('文件无效或已被阻止')
        return
      }

      // 如果fileList长度大于1，说明用户尝试上传多张图片，只保留最后一张
      if (fileList.length > 1) {
        // 移除之前的文件，只保留当前文件
        const currentFile = fileList[fileList.length - 1]
        // 释放之前创建的URL对象
        if (this.detailForm.imageUrl && this.detailForm.imageUrl.startsWith('blob:')) {
          URL.revokeObjectURL(this.detailForm.imageUrl)
        }
        // 只处理当前文件
        file = currentFile
        fileList = [currentFile]
      }

      // 再次验证文件（双重验证，确保文件通过验证）
      const isImage = file.raw.type.startsWith('image/')
      const isLt10M = file.raw.size / 1024 / 1024 < 10

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        this.imageFile = null
        this.detailForm.imageUrl = ''
        return
      }

      if (!isLt10M) {
        this.$message.error('图片大小不能超过10MB!')
        this.imageFile = null
        this.detailForm.imageUrl = ''
        return
      }

      // 释放之前创建的URL对象，避免内存泄漏
      if (this.detailForm.imageUrl && this.detailForm.imageUrl.startsWith('blob:')) {
        URL.revokeObjectURL(this.detailForm.imageUrl)
      }

      // 验证通过，设置文件
      this.imageFile = file.raw

      // 使用本地URL预览（不转换为base64）
      try {
        const url = URL.createObjectURL(file.raw)
        this.detailForm.imageUrl = url
        console.log('图片预览URL创建成功', url, '文件大小:', file.raw.size)
      } catch (error) {
        console.error('创建预览URL失败', error)
        this.$message.error('图片预览失败，请重试')
        this.imageFile = null
        this.detailForm.imageUrl = ''
      }
    },

    // 删除图片
    removeImage() {
      // 释放之前创建的URL对象，避免内存泄漏
      if (this.detailForm.imageUrl && this.detailForm.imageUrl.startsWith('blob:')) {
        URL.revokeObjectURL(this.detailForm.imageUrl)
      }
      this.imageFile = null
      this.imageFileList = [] // 清空el-upload组件的文件列表
      this.detailForm.imageUrl = ''
      // 清空el-upload组件的文件列表
      if (this.$refs.imageUpload) {
        this.$refs.imageUpload.clearFiles()
      }
    },

    // 处理文件超出限制
    handleExceed(files, fileList) {
      // 检查是否真的有图片需要删除
      // 只有当fileList长度大于1时才提示（说明用户尝试上传多张图片）
      // 或者当已经有新选择的图片文件时才提示
      if (fileList.length > 1 || this.imageFile) {
        this.$message.warning('只能上传1张图片，请先删除当前图片')
      } else {
        // 如果fileList为空或只有1个文件，可能是el-upload内部状态问题，不提示
        console.warn('handleExceed被触发，但fileList长度:', fileList.length, 'imageFile:', this.imageFile)
      }
    }
  }
}
</script>
  <style>
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

  /* 图片上传样式 */
  .image-uploader {
    display: inline-block;
  }

  .image-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s;
  }

  .image-uploader .el-upload:hover {
    border-color: #409EFF;
  }

  .image-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
    background-color: #fafafa;
  }

  .uploaded-image {
    width: 178px;
    height: 178px;
    display: block;
    object-fit: cover;
    border-radius: 6px;
  }

  .image-info {
    margin-top: 10px;
    font-size: 12px;
    color: #606266;
  }

  .upload-tip {
    margin-top: 5px;
    font-size: 12px;
    color: #909399;
  }
  </style>
