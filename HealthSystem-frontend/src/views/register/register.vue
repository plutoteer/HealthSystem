<template>
  <div class="register-container">
    <div class="register-wrapper">
      <el-form
        ref="registerForm"
        :model="form"
        :rules="rules"
        class="register-form"
        auto-complete="off"
        label-position="left"
      >
        <div class="title-container">
          <h3 class="title">用户注册</h3>
          <p class="subtitle">创建您的个人健康管理账户</p>
        </div>

        <el-form-item prop="username">
          <div class="input-wrapper">
            <span class="svg-container">
              <svg-icon icon-class="user" />
            </span>
            <el-input
              ref="username"
              v-model="form.username"
              placeholder="请输入用户名"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="off"
              autocomplete="off"
              class="custom-input"
            />
          </div>
        </el-form-item>

        <el-form-item prop="phone">
          <div class="input-wrapper">
            <span class="svg-container">
              <i class="el-icon-phone" style="font-size: 20px;" />
            </span>
            <el-input
              ref="phone"
              v-model="form.phone"
              placeholder="请输入手机号码"
              name="phone"
              type="text"
              tabindex="2"
              auto-complete="off"
              autocomplete="off"
              class="custom-input"
            />
          </div>
        </el-form-item>

        <el-form-item prop="email">
          <div class="input-wrapper">
            <span class="svg-container">
              <i class="el-icon-message" style="font-size: 20px;" />
            </span>
            <el-input
              ref="email"
              v-model="form.email"
              placeholder="请输入邮箱"
              name="email"
              type="text"
              tabindex="3"
              auto-complete="off"
              autocomplete="off"
              class="custom-input"
            />
          </div>
        </el-form-item>

        <el-form-item prop="password">
          <div class="input-wrapper">
            <span class="svg-container">
              <svg-icon icon-class="password" />
            </span>
            <el-input
              :key="passwordType"
              ref="password"
              v-model="form.password"
              :type="passwordType"
              placeholder="请输入密码"
              name="password"
              tabindex="4"
              auto-complete="off"
              autocomplete="new-password"
              class="custom-input"
            />
            <span class="show-pwd" @click="showPwd">
              <svg-icon
                :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"
              />
            </span>
          </div>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <div class="input-wrapper">
            <span class="svg-container">
              <svg-icon icon-class="password" />
            </span>
            <el-input
              :key="confirmPasswordType"
              ref="confirmPassword"
              v-model="form.confirmPassword"
              :type="confirmPasswordType"
              placeholder="请确认密码"
              name="confirmPassword"
              tabindex="5"
              auto-complete="off"
              autocomplete="new-password"
              class="custom-input"
            />
            <span class="show-pwd" @click="showConfirmPwd">
              <svg-icon
                :icon-class="confirmPasswordType === 'password' ? 'eye' : 'eye-open'"
              />
            </span>
          </div>
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          class="register-button"
          @click.native.prevent="submitForm"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注册中...</span>
        </el-button>

        <el-button
          type="default"
          class="back-login-button"
          @click.native.prevent="handleBackLogin"
        >返回登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script>
import userApi from '@/api/userManage'
import { validUsername } from '@/utils/validate'

export default {
  name: 'Register',
  data() {
    // 定义验证用户名函数
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('请输入正确的用户名'))
      } else {
        callback()
      }
    }

    // 定义验证密码函数
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('输入的密码不能少于6位'))
      } else {
        callback()
      }
    }

    // 定义验证确认密码函数
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    // 定义验证电话函数
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号码'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码格式'))
      } else {
        callback()
      }
    }

    // 定义验证邮箱函数
    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮箱'))
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        callback(new Error('请输入正确的邮箱格式'))
      } else {
        callback()
      }
    }

    return {
      form: {
        username: '',
        phone: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        username: [
          { required: true, trigger: 'blur', validator: validateUsername }
        ],
        phone: [
          { required: true, trigger: 'blur', validator: validatePhone }
        ],
        email: [
          { required: true, trigger: 'blur', validator: validateEmail }
        ],
        password: [
          { required: true, trigger: 'blur', validator: validatePassword }
        ],
        confirmPassword: [
          { required: true, trigger: 'blur', validator: validateConfirmPassword }
        ]
      },
      loading: false,
      passwordType: 'password',
      confirmPasswordType: 'password'
    }
  },
  methods: {
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },

    showConfirmPwd() {
      if (this.confirmPasswordType === 'password') {
        this.confirmPasswordType = ''
      } else {
        this.confirmPasswordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.confirmPassword.focus()
      })
    },

    submitForm() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          // 构造请求体
          const requestBody = {
            username: this.form.username,
            phone: this.form.phone,
            email: this.form.email,
            password: this.form.password
          }
          // 提交验证给后台
          userApi.register(requestBody).then(response => {
            // 成功提示
            this.$message({
              message: response.message || '注册成功',
              type: 'success'
            })
            this.loading = false
            // 延迟跳转，让用户看到成功提示
            setTimeout(() => {
              this.$router.push('/login')
            }, 1000)
          }).catch(error => {
            this.loading = false
            this.$message({
              message: error.message || '注册失败，请重试',
              type: 'error'
            })
          })
        } else {
          return false
        }
      })
    },

    handleBackLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss">
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;
$primary-color: #409eff;
$primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
$secondary-gradient: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);

.register-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: url('~@/assets/login_bg.png');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    opacity: 0.1;
    z-index: 0;
  }

  .register-wrapper {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 480px;
    padding: 20px;
  }

  .register-form {
    position: relative;
    width: 100%;
    padding: 50px 40px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    animation: slideUp 0.6s ease-out;
  }

  .title-container {
    text-align: center;
    margin-bottom: 40px;

    .title {
      font-size: 32px;
      font-weight: 700;
      color: #2c3e50;
      margin: 0 0 10px 0;
      letter-spacing: 1px;
      background: $primary-gradient;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .subtitle {
      font-size: 16px;
      color: #7f8c8d;
      margin: 0;
      font-weight: 400;
    }
  }

  .input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background: #f8f9fa;
    border-radius: 12px;
    border: 2px solid transparent;
    transition: all 0.3s ease;

    &:hover {
      border-color: rgba(102, 126, 234, 0.3);
      background: #fff;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
    }

    &:focus-within {
      border-color: #667eea;
      background: #fff;
      box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
    }

    .svg-container {
      padding: 0 15px;
      color: #667eea;
      font-size: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 50px;
      flex-shrink: 0;
    }
  }

  .svg-container {
    color: #667eea;
  }

  .show-pwd {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    color: #95a5a6;
    cursor: pointer;
    user-select: none;
    z-index: 10;
    padding: 5px;
    transition: color 0.3s ease;

    &:hover {
      color: #667eea;
    }
  }

  .el-form-item {
    margin-bottom: 20px;

    &:last-of-type {
      margin-bottom: 30px;
    }
  }

  .custom-input {
    flex: 1;

    ::v-deep .el-input__inner {
      border: none;
      background: transparent;
      padding: 18px 15px;
      font-size: 16px;
      color: #2c3e50;
      height: auto;
      line-height: 1.5;

      &::placeholder {
        color: #bdc3c7;
        font-size: 15px;
      }

      &:focus {
        border: none;
        box-shadow: none;
      }
    }
  }

  .register-button {
    width: 100%;
    height: 56px;
    margin-bottom: 20px;
    border-radius: 12px;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 2px;
    background: $primary-gradient;
    border: none;
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
    transition: all 0.3s ease;
    color: #fff;

    &:hover,
    &:focus {
      transform: translateY(-2px);
      box-shadow: 0 12px 30px rgba(102, 126, 234, 0.5);
      background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .back-login-button {
    width: 100%;
    height: 56px;
    border-radius: 12px;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 2px;
    background: #fff;
    border: 2px solid #e0e0e0;
    color: #667eea;
    transition: all 0.3s ease;

    &:hover,
    &:focus {
      border-color: #667eea;
      background: #f8f9ff;
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .register-container {
    .register-wrapper {
      padding: 10px;
    }

    .register-form {
      padding: 40px 30px;
      border-radius: 15px;
    }

    .title-container {
      .title {
        font-size: 28px;
      }

      .subtitle {
        font-size: 14px;
      }
    }
  }
}
</style>
