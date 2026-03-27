<template>
  <div class="login-container">
    <div class="login-wrapper">
      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        auto-complete="on"
        label-position="left"
      >
        <div class="title-container">
          <div class="logo-wrapper">
            <svg-icon icon-class="user" class="logo-icon" />
          </div>
          <h3 class="title">个人健康管理系统</h3>
          <p class="subtitle">欢迎使用个人健康管理系统</p>
        </div>

        <el-form-item prop="username">
          <div class="input-wrapper">
            <span class="svg-container">
              <svg-icon icon-class="user" />
            </span>
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="请输入用户名"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="on"
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
              v-model="loginForm.password"
              :type="passwordType"
              placeholder="请输入密码"
              name="password"
              tabindex="2"
              auto-complete="on"
              class="custom-input"
              @keyup.enter.native="handleLogin"
            />
            <span class="show-pwd" @click="showPwd">
              <svg-icon
                :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"
              />
            </span>
          </div>
        </el-form-item>

        <el-button
          :loading="loading"
          type="primary"
          class="login-button"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登录中...</span>
        </el-button>

        <el-button
          type="default"
          class="register-button"
          @click.native.prevent="handleRegister"
        >注 册</el-button>
      </el-form>
    </div>
  </div>
</template>

<script>
import { validUsername } from '@/utils/validate'

export default {
  name: 'Login',
  data() {
    // 定义验证用户名函数
    const validateUsername = (rule, value, callback) => {
      // 调用 validUsername 函数判断用户名是否合法
      if (!validUsername(value)) {
        // 如果不合法则返回错误信息
        callback(new Error('请输入正确的用户名'))
      } else {
        // 合法则调用 callback() 函数返回验证成功信息
        callback()
      }
    }
    // 定义验证密码函数
    const validatePassword = (rule, value, callback) => {
      // 判断密码是否小于6位
      if (value.length < 6) {
        // 如果小于6位则返回错误信息
        callback(new Error('输入的密码不能少于6位'))
      } else {
        // 合法则调用 callback() 函数返回验证成功信息
        callback()
      }
    }
    return {
      // 定义表单数据对象
      loginForm: {
        username: '',
        password: ''
      },
      // 定义表单验证规则
      loginRules: {
        username: [
          { required: true, trigger: 'blur', validator: validateUsername }
        ],
        password: [
          { required: true, trigger: 'blur', validator: validatePassword }
        ]
      },
      // 定义 loading 状态
      loading: false,
      // 定义密码输入框类型，初始为密码框
      passwordType: 'password',
      // 定义重定向路径，初始为 undefined
      redirect: undefined
    }
  },

  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
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

    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true // 显示 loading 状态圈
          this.$store
            .dispatch('user/login', this.loginForm)
            .then(() => {
              // 登录成功，跳转到目标路由
              this.$router.push({ path: this.redirect || '/' })
              this.loading = false // 隐藏 loading 状态
            })
            .catch(() => {
              this.loading = false // 隐藏 loading 状态
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },

    handleRegister() {
      this.$router.push({ path: '/register' })
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

.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('~@/assets/login_bg.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;

  .login-wrapper {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 480px;
    padding: 20px;
  }

  .login-form {
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
    margin-bottom: 50px;

    .logo-wrapper {
      width: 80px;
      height: 80px;
      margin: 0 auto 20px;
      background: $primary-gradient;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
      animation: pulse 2s ease-in-out infinite;

      .logo-icon {
        font-size: 40px;
        color: #fff;
      }
    }

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
    margin-bottom: 25px;

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

  .login-button {
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

  .register-button {
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
  .login-container {
    .login-wrapper {
      padding: 10px;
    }

    .login-form {
      padding: 40px 30px;
      border-radius: 15px;
    }

    .title-container {
      .logo-wrapper {
        width: 70px;
        height: 70px;

        .logo-icon {
          font-size: 35px;
        }
      }

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
