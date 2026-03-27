# HealthSystem 前端系统

## 项目简介

HealthSystem 前端系统是一个基于 Vue.js 的健康管理系统前端应用，采用 Element UI 组件库，提供用户友好的界面和丰富的交互功能。

## 系统技术架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      用户界面层                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   登录页     │  │   首页       │  │   用户管理   │      │
│  │   注册页     │  │   仪表盘     │  │   角色管理   │      │
│  │   详情页     │  │   数据展示   │  │   菜单管理   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                    Vue 组件层                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Layout     │  │  Components  │  │    Views     │      │
│  │   (布局)     │  │  (公共组件)  │  │  (页面视图)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                   状态管理层                                   │
│                    Vuex Store                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   user       │  │  permission  │  │    app       │      │
│  │  (用户状态)  │  │  (权限状态)  │  │  (应用状态)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                   路由层                                       │
│                  Vue Router                                    │
│              (路由配置、权限控制)                              │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                   网络请求层                                  │
│                    Axios                                       │
│              (HTTP 请求封装、拦截器)                           │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                   后端 API                                     │
│              HealthSystem-backend                              │
│              http://localhost:8080                             │
└────────────────────────────────────────────────────────────────┘
```

## 技术栈

### 核心框架
- **Vue**: 2.6.10 - 渐进式 JavaScript 框架
- **Vue Router**: 3.0.6 - 官方路由管理器
- **Vuex**: 3.1.0 - 状态管理模式

### UI 组件库
- **Element UI**: 2.13.2 - 基于 Vue 的组件库

### 数据可视化
- **ECharts**: 5.5.0 - 数据可视化图表库
- **G2**: 4.2.10 - 数据驱动的图形语法
- **@antv/data-set**: 0.11.8 - 数据转换工具

### 工具库
- **Axios**: 0.18.1 - HTTP 客户端
- **js-cookie**: 2.2.0 - Cookie 操作
- **nprogress**: 0.2.0 - 进度条
- **path-to-regexp**: 2.4.0 - 路径匹配

### 构建工具
- **Vue CLI**: 4.4.4 - Vue.js 开发工具
- **Webpack**: 通过 Vue CLI 集成
- **Babel**: JavaScript 编译器
- **Sass**: CSS 预处理器

### 开发工具
- **ESLint**: 代码检查工具
- **Jest**: 单元测试框架
- **MockJS**: 1.0.1-beta3 - 数据模拟

## 目录结构

```
HealthSystem-frontend/
├── public/                          # 静态资源目录
│   └── index.html                   # HTML 模板
├── src/
│   ├── api/                         # API 接口定义
│   │   ├── user.js                  # 用户相关接口
│   │   ├── userManage.js            # 用户管理接口
│   │   ├── roleManage.js            # 角色管理接口
│   │   ├── menuManage.js            # 菜单管理接口
│   │   ├── Function_Menu.js         # 功能菜单接口
│   │   └── table.js                 # 表格相关接口
│   ├── assets/                      # 静态资源
│   │   ├── 404_images/              # 404 页面图片
│   │   └── ...
│   ├── components/                  # 公共组件
│   │   ├── Breadcrumb/              # 面包屑组件
│   │   ├── Hamburger/               # 菜单折叠组件
│   │   ├── HeadImage/               # 头像上传组件
│   │   └── SvgIcon/                 # SVG 图标组件
│   ├── icons/                       # 图标资源
│   │   ├── svg/                     # SVG 图标文件
│   │   ├── index.js                 # 图标导出
│   │   └── svgo.yml                 # SVG 优化配置
│   ├── images/                      # 图片资源
│   ├── layout/                      # 布局组件
│   │   ├── components/              # 布局子组件
│   │   │   ├── AppMain.vue          # 主内容区
│   │   │   ├── Navbar.vue           # 导航栏
│   │   │   ├── Sidebar/             # 侧边栏
│   │   │   │   ├── index.vue
│   │   │   │   ├── Item.vue
│   │   │   │   ├── Link.vue
│   │   │   │   ├── Logo.vue
│   │   │   │   └── SidebarItem.vue
│   │   │   └── TagsView/            # 标签页视图
│   │   │       ├── index.vue
│   │   │       └── ScrollPane.vue
│   │   ├── index.vue                # 主布局
│   │   └── mixin/                   # 布局混入
│   │       └── ResizeHandler.js
│   ├── router/                      # 路由配置
│   │   └── index.js                 # 路由定义
│   ├── store/                       # Vuex 状态管理
│   │   ├── getters.js               # 全局 getters
│   │   ├── index.js                 # Store 入口
│   │   └── modules/                 # 模块化 Store
│   │       ├── app.js               # 应用状态
│   │       ├── permission.js        # 权限状态
│   │       ├── settings.js          # 设置状态
│   │       ├── tagsView.js          # 标签页状态
│   │       └── user.js              # 用户状态
│   ├── styles/                      # 样式文件
│   │   ├── element-ui.scss          # Element UI 样式覆盖
│   │   ├── index.scss               # 全局样式
│   │   ├── mixin.scss               # 样式混入
│   │   ├── sidebar.scss             # 侧边栏样式
│   │   ├── transition.scss          # 过渡动画样式
│   │   └── variables.scss           # 样式变量
│   ├── utils/                       # 工具函数
│   │   ├── auth.js                  # 认证工具
│   │   ├── get-page-title.js        # 页面标题工具
│   │   ├── index.js                 # 通用工具函数
│   │   ├── request.js                # Axios 请求封装
│   │   └── validate.js               # 表单验证工具
│   ├── views/                       # 页面视图
│   │   ├── 404.vue                  # 404 页面
│   │   ├── login/                   # 登录页
│   │   │   └── index.vue
│   │   ├── register/                # 注册页
│   │   │   └── register.vue
│   │   ├── dashboard/               # 首页/仪表盘
│   │   │   └── index.vue
│   │   ├── Detail/                  # 详情页
│   │   │   └── Detail.vue
│   │   ├── Change_Password/         # 修改密码页
│   │   │   └── Change_Password.vue
│   │   ├── sys/                     # 系统管理页面
│   │   │   ├── user.vue             # 用户管理
│   │   │   ├── role.vue             # 角色管理
│   │   │   ├── DetailsManage.vue    # 详情管理
│   │   │   ├── sportDetails.vue     # 运动详情
│   │   │   └── userBodyManage.vue   # 用户身体数据管理
│   │   ├── form/                    # 表单示例
│   │   ├── table/                   # 表格示例
│   │   ├── tree/                    # 树形组件示例
│   │   ├── nested/                  # 嵌套路由示例
│   │   └── test/                    # 测试页面
│   ├── App.vue                      # 根组件
│   ├── main.js                      # 应用入口
│   ├── permission.js                # 路由权限控制
│   └── settings.js                  # 应用配置
├── mock/                            # Mock 数据（可选）
├── tests/                           # 测试文件
├── .eslintrc.js                     # ESLint 配置
├── .eslintignore                    # ESLint 忽略文件
├── .editorconfig                    # 编辑器配置
├── .gitignore                       # Git 忽略文件
├── babel.config.js                  # Babel 配置
├── jest.config.js                   # Jest 测试配置
├── jsconfig.json                    # JavaScript 配置
├── postcss.config.js                # PostCSS 配置
├── vue.config.js                    # Vue CLI 配置
├── package.json                     # 项目依赖配置
└── README.md                        # 项目说明文档
```

## 核心功能模块

### 1. 用户认证模块
- **登录功能**: 用户登录、JWT Token 管理
- **注册功能**: 新用户注册
- **密码修改**: 用户密码修改
- **权限控制**: 基于角色的访问控制

### 2. 用户管理模块
- 用户列表查询（分页）
- 用户信息新增、编辑、删除
- 用户状态管理
- 用户角色分配

### 3. 角色管理模块
- 角色列表查询（分页）
- 角色信息新增、编辑、删除
- 角色权限配置

### 4. 菜单管理模块
- 菜单树形结构展示
- 菜单权限配置
- 动态菜单生成

### 5. 身体数据管理模块
- 用户身体数据录入
- 身体数据查询和统计
- 健康指标展示

### 6. 运动信息管理模块
- 运动知识信息展示
- 运动详情查看
- 运动类型分类

### 7. 详情管理模块
- 详情信息列表（分页）
- 详情信息增删改查
- 按类型筛选

### 8. 系统功能
- **布局管理**: 响应式布局、侧边栏折叠
- **标签页**: 多标签页管理
- **面包屑导航**: 路径导航
- **权限路由**: 基于权限的动态路由

## 开发环境部署

### 环境要求
- 推荐使用 **Node.js 14.x** 或更高版本

### 安装依赖

```bash
# 进入项目目录
cd HealthSystem-frontend

# 安装依赖
npm install


### 配置后端 API 地址

修改 `src/utils/request.js` 中的 `baseURL` 配置：

```javascript
// 开发环境
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080',
  timeout: 5000
})
```

或在项目根目录创建 `.env.development` 文件：

```env
VUE_APP_BASE_API=http://localhost:8080
```

## 运行

### 开发模式

```bash
npm run dev
```

启动后访问: http://localhost:3000

### 生产构建

```bash
# 生产环境构建
npm run build:prod

构建产物将输出到 `dist/` 目录。


### 代码检查

```bash
# ESLint 检查
npm run lint
```

## 开发说明

### 路由配置

路由配置文件位于 `src/router/index.js`，支持：
- 静态路由配置
- 动态路由（基于权限）
- 路由守卫

### 状态管理

使用 Vuex 进行状态管理，主要模块：
- `user`: 用户信息、Token
- `permission`: 路由权限
- `app`: 应用设置（侧边栏、设备类型等）
- `settings`: 系统设置
- `tagsView`: 标签页状态

### API 请求

所有 API 请求通过 `src/utils/request.js` 封装，支持：
- 请求拦截器（添加 Token）
- 响应拦截器（统一错误处理）
- 请求/响应日志

### 样式规范

- 使用 SCSS 预处理器
- 全局样式变量定义在 `src/styles/variables.scss`
- Element UI 样式覆盖在 `src/styles/element-ui.scss`

## 注意事项

### 1. 后端服务
- 确保后端服务已启动（默认端口 8080）
- 检查后端 API 地址配置是否正确
- 注意跨域问题，确保后端已配置 CORS

### 2. 浏览器兼容性
- 支持现代浏览器（Chrome、Firefox、Safari、Edge）
- 不支持 IE 浏览器

### 3. 环境变量
- 开发环境使用 `.env.development`
- 生产环境使用 `.env.production`
- 变量名必须以 `VUE_APP_` 开头

### 4. 路由模式
- 默认使用 Hash 模式（`#`）
- 如需使用 History 模式，需要后端配置支持

### 5. 权限控制
- 路由权限通过 `permission.js` 控制
- 菜单权限通过后端返回的菜单数据动态生成
- Token 存储在 Cookie 中

### 6. 图标使用
- SVG 图标放在 `src/icons/svg/` 目录
- 使用 `<svg-icon>` 组件引入图标
- 图标名称作为 `icon-class` 属性传入

### 7. 代码规范
- 遵循 ESLint 规则
- 使用 2 空格缩进
- 组件名使用 PascalCase
- 文件名使用 kebab-case

### 8. 性能优化
- 路由懒加载
- 组件按需引入
- 图片资源优化
- 生产环境代码压缩

## 常见问题

### Q: npm install 失败？
A: 尝试清除缓存后重新安装：
```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### Q: 启动后页面空白？
A: 检查：
1. 后端服务是否启动
2. API 地址配置是否正确
3. 浏览器控制台是否有错误信息
4. 网络请求是否正常

### Q: 路由跳转 404？
A: 
- Hash 模式：检查路由配置
- History 模式：需要后端配置支持，或切换回 Hash 模式

### Q: Element UI 样式不生效？
A: 检查 `main.js` 中是否正确引入 Element UI 样式：
```javascript
import 'element-ui/lib/theme-chalk/index.css'
```

### Q: 跨域问题？
A: 确保后端已配置 CORS，或使用代理配置（在 `vue.config.js` 中配置 `proxy`）


## 许可证

MIT License

