<!-- 搜索框和添加按钮，搜索框，以及用户名和手机号的输入框 -->
<template>
  <div class="page-container">
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <!-- v-model绑定组件实现双向数据绑定，页面上用户输入的值会同步更新到该属性中 -->
          <el-input
            v-model="searchModel.roleName"
            placeholder="角色名称"
            clearable
          />
          <el-button
            type="primary"
            round
            icon="el-icon-search"
            @click="getRoleList"
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
      <el-table :data="roleList" stripe style="width: 100%">
        <el-table-column prop="roleId" label="角色ID" width="180" />
        <el-table-column prop="roleName" label="角色名称" width="180" />
        <el-table-column prop="roleDesc" label="角色描述" />
        <el-table-column label="操作" width="180">
          <!-- 删除和修改按钮 -->
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
              class="action-btn edit-btn"
              @click="openEditUi(scope.row.roleId)"
            >编辑</el-button>
            <el-button
              type="danger"
              size="small"
              class="action-btn delete-btn"
              @click="deleteRole(scope.row)"
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

    <!-- 角色编辑信息弹出框 -->
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      @close="clearForm"
    >
      <el-form ref="roleFormRef" :model="roleForm" :rules="rules">
        <el-form-item label="角色名称" prop="roleName" :label-width="formLabelWidth">
          <el-input v-model="roleForm.roleName" autocomplete="off" />
        </el-form-item>

        <el-form-item label="角色描述" prop="roleDesc" :label-width="formLabelWidth">
          <el-input v-model="roleForm.roleDesc" autocomplete="off" />
        </el-form-item>

        <el-form-item label="权限设置" prop="menuIdList" :label-width="formLabelWidth">
          <el-tree ref="menuRef" :data="menuList" :props="menuProps" show-checkbox default-expand-all node-key="menuId" />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveRole">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import roleApi from '@/api/roleManage'
import menuApi from '@/api/menuManage'
export default {
  data() {
    return {
      menuList: [],
      // 左边宽度
      formLabelWidth: '135px',
      // 设置默认值不可见
      dialogFormVisible: false,
      roleForm: {},
      title: '',
      total: 0,
      roleList: [],

      menuProps: {
        children: 'children',
        label: 'title'
      },

      searchModel: {
        pageNo: 1,
        // 默认显示数量
        pageSize: 10
      },

      // 表单规则配置
      rules: {
        roleName: [
          { required: true, message: '请输入角色名', trigger: 'blur' },
          {
            min: 2,
            max: 20,
            message: '长度需要在 2 到18 个字符',
            trigger: 'blur'
          }
        ],
        roleDesc: [
          { required: true, message: '请输入角色描述', trigger: 'blur' },
          {
            min: 2,
            max: 20,
            message: '长度需要在 2 到 18 个字符',
            trigger: 'blur'
          }
        ]
      }
    }
  },

  // 加载时就查询一次
  created() {
    this.getRoleList()
    this.getAllMenu()
  },

  methods: {
    getAllMenu() {
      menuApi.getAllMenu().then((response) => {
        this.menuList = response.data
      })
    },

    saveRole() {
      let isOk = true
      // 触发表单的验证
      this.$refs.roleFormRef.validate((valid) => {
        // 校验表单是否通过，如果校验失败，设置 isOk 为 false
        isOk = valid
      })
      // 如果表单验证通过
      if (isOk) {
        // 获取选中的菜单节点和半选中的菜单节点
        const checkedKeys = this.$refs.menuRef.getCheckedKeys()
        const halfCheckedKeys = this.$refs.menuRef.getHalfCheckedKeys()

        // 将选中的和半选中的菜单节点合并为一个数组
        this.roleForm.menuIdList = checkedKeys.concat(halfCheckedKeys)
        // 提交验证给后台，保存角色
        roleApi.saveRole(this.roleForm).then(response => {
          // 保存成功提示
          this.$message({
            message: response.message,
            type: 'success'
          })
          // 关闭对话框
          this.dialogFormVisible = false
          // 刷新表格数据
          this.getRoleList()
        })
      } else {
        // 表单验证不通过
        console.log('表单验证不通过')
        return false
      }
    },

    // 清理表单数据
    clearForm() {
      this.roleForm = {}
      // 清除表单校验的提示信息
      this.$refs.roleFormRef.clearValidate()
      this.$refs.menuRef.setCheckedKeys([])
    },
    handleSizeChange(pageSize) {
      // 数据更新
      this.searchModel.pageSize = pageSize
      this.getRoleList()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getRoleList()
    },
    // 查询用户列表
    getRoleList() {
      roleApi.getRoleList(this.searchModel).then((response) => {
        this.roleList = response.data.rows
        this.total = response.data.total
      })
    },
    resetSearch() {
      this.searchModel = {
        roleName: '',
        pageNo: 1,
        pageSize: 10
      }
      this.getRoleList()
    },

    openEditUi(id) {
      if (id == null) {
        this.title = '新增角色'
      } else {
        this.title = '修改角色'
        // 根据id查询角色数据
        roleApi.getRoleById(id).then(response => {
          this.roleForm = response.data
          this.$refs.menuRef.setCheckedKeys(response.data.menuIdList)
        })
      }
      this.dialogFormVisible = true
    },

    deleteRole(role) {
      this.$confirm(`确认删除 ${role.roleName} 这个角色吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        roleApi.deleteRoleById(role.roleId).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          })
          this.getRoleList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
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
