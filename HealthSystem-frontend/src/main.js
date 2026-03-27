import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/zh-CN' // lang i18n

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import '@/icons' // icon
import '@/permission' // permission control

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

// set ElementUI lang to EN
Vue.use(ElementUI, { locale })
// 如果想要中文版 element-ui，按如下方式声明
// Vue.use(ElementUI)

Vue.config.productionTip = false

// 全局错误处理
Vue.config.errorHandler = (err, vm, info) => {
  console.error('全局错误捕获:', err, info)
  // 可以在这里添加错误上报逻辑
  // 例如：发送到错误监控系统
}

// 处理未捕获的 Promise 拒绝
window.addEventListener('unhandledrejection', event => {
  console.error('未处理的 Promise 拒绝:', event.reason)
  // 阻止默认的错误输出，避免控制台报错
  event.preventDefault()
})

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
