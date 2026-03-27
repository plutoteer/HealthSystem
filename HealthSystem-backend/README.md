# HealthSystem 后端系统

## 项目简介

HealthSystem 后端系统是一个基于 Spring Boot+Vue 的健康管理系统后端服务，提供用户管理、角色权限管理、身体数据管理、运动信息管理等核心功能。

## 系统技术架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Vue.js)                        │
│                    HealthSystem-frontend                      │
└────────────────────────┬──────────────────────────────────────┘
                         │ HTTP/REST API
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                    API 网关层                                  │
│              Spring Boot REST Controller                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│  │  User    │  │  Role    │  │  Menu    │  │  Body    │     │
│  │Controller│  │Controller│  │Controller│  │Controller│     │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘     │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                    业务逻辑层                                  │
│                    Service Layer                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│  │  User    │  │  Role    │  │  Menu    │  │  Body    │     │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │     │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘     │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                    数据访问层                                  │
│              MyBatis Plus Mapper Layer                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐     │
│  │  User    │  │  Role    │  │  Menu    │  │  Body    │     │
│  │  Mapper  │  │  Mapper  │  │  Mapper  │  │  Mapper  │     │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘     │
└────────────────────────┬──────────────────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────────────────┐
│                    数据存储层                                  │
│  ┌──────────────┐          ┌──────────────┐                  │
│  │    MySQL     │          │    Redis     │                  │
│  │  数据库      │          │   缓存       │                  │
│  └──────────────┘          └──────────────┘                  │
└────────────────────────────────────────────────────────────────┘
```

## 技术栈

### 核心框架
- **Spring Boot**: 2.7.8
- **Java**: 1.8

### 数据持久化
- **MyBatis Plus**: 3.5.2 - ORM 框架
- **MySQL**: 数据库
- **Spring Data JPA**: 数据访问抽象层
- **Spring Data Redis**: Redis 缓存支持

### 工具库
- **Lombok**: 简化 Java 代码
- **FastJSON2**: 2.0.7 - JSON 处理
- **JWT (jjwt)**: 0.9.1 - 身份认证
- **FreeMarker**: 模板引擎
- **iText HTML2PDF**: 4.0.5 - PDF 生成

### 开发工具
- **Swagger**: 3.0.0 - API 文档生成
- **Maven**: 项目构建工具

## 目录结构

```
HealthSystem-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── muzi/
│   │   │           ├── HealthApplication.java          # 启动类
│   │   │           ├── config/                          # 配置类
│   │   │           │   ├── CORSConfig.java              # 跨域配置
│   │   │           │   ├── JwtConfig.java               # JWT 配置
│   │   │           │   ├── MybatisPlus_Config.java      # MyBatis Plus 配置
│   │   │           │   ├── MyRedisConfig.java           # Redis 配置
│   │   │           │   └── SwaggerConfig.java           # Swagger 配置
│   │   │           ├── Data_unification/                # 统一响应封装
│   │   │           │   └── Unification.java
│   │   │           └── healthsys/                        # 核心业务模块
│   │   │               ├── controller/                  # 控制器层
│   │   │               │   ├── UserController.java      # 用户管理（包含身体数据管理）
│   │   │               │   ├── RoleController.java      # 角色管理
│   │   │               │   ├── MenuController.java      # 菜单管理
│   │   │               │   ├── BodyController.java      # 身体数据管理（预留）
│   │   │               │   ├── DetailController.java    # 详情管理（包含图片上传）
│   │   │               │   ├── SportInfoController.java # 运动信息管理
│   │   │               │   ├── AIConsultationController.java # AI健康咨询
│   │   │               │   ├── HealthReportController.java   # 健康报告
│   │   │               │   ├── UserRoleController.java  # 用户角色关联
│   │   │               │   └── RoleMenuController.java  # 角色菜单关联
│   │   │               ├── dto/                          # 数据传输对象
│   │   │               │   └── HealthReportDTO.java    # 健康报告DTO
│   │   │               ├── entity/                      # 实体类
│   │   │               │   ├── User.java
│   │   │               │   ├── Role.java
│   │   │               │   ├── Menu.java
│   │   │               │   ├── Body.java
│   │   │               │   ├── BodyNotes.java
│   │   │               │   ├── Detail.java
│   │   │               │   ├── SportInfo.java
│   │   │               │   ├── AIConsultation.java
│   │   │               │   ├── UserRole.java
│   │   │               │   └── RoleMenu.java
│   │   │               ├── mapper/                      # 数据访问层
│   │   │               │   ├── UserMapper.java
│   │   │               │   ├── RoleMapper.java
│   │   │               │   ├── MenuMapper.java
│   │   │               │   ├── BodyMapper.java
│   │   │               │   ├── BodyNotesMapper.java
│   │   │               │   ├── DetailMapper.java
│   │   │               │   ├── SportInfoMapper.java
│   │   │               │   ├── AIConsultationMapper.java
│   │   │               │   ├── UserRoleMapper.java
│   │   │               │   └── RoleMenuMapper.java
│   │   │               └── service/                     # 业务逻辑层
│   │   │                   ├── IUserService.java
│   │   │                   ├── IRoleService.java
│   │   │                   ├── IMenuService.java
│   │   │                   ├── IBodyService.java
│   │   │                   ├── IBodyNotesService.java
│   │   │                   ├── IDetailService.java
│   │   │                   ├── ISportInfoService.java
│   │   │                   ├── IAIConsultationService.java
│   │   │                   ├── IHealthReportService.java
│   │   │                   ├── IUserRoleService.java
│   │   │                   ├── IRoleMenuService.java
│   │   │                   └── impl/                    # 服务实现类
│   │   │                       ├── UserServiceImpl.java
│   │   │                       ├── RoleServiceImpl.java
│   │   │                       ├── MenuServiceImpl.java
│   │   │                       ├── BodyServiceImpl.java
│   │   │                       ├── BodyNotesServiceImpl.java
│   │   │                       ├── DetailServiceImpl.java
│   │   │                       ├── SportInfoServiceImpl.java
│   │   │                       ├── AIConsultationServiceImpl.java
│   │   │                       ├── HealthReportServiceImpl.java
│   │   │                       ├── UserRoleServiceImpl.java
│   │   │                       ├── RoleMenuServiceImpl.java
│   │   │                       └── ...
│   │   └── resources/
│   │       ├── application.yml                           # 应用配置文件
│   │       └── mapper/                                   # MyBatis XML 映射文件
│   │           └── healthsys/
│   │               ├── UserMapper.xml
│   │               ├── RoleMapper.xml
│   │               ├── MenuMapper.xml
│   │               └── ...
│   └── test/                                             # 测试代码
│       └── java/
│           └── com/
│               └── muzi/
│                   ├── HealthApplicationTests.java
│                   └── CodeGenertor.java
├── pom.xml                                               # Maven 配置文件
├── mvnw                                                  # Maven Wrapper (Unix)
├── mvnw.cmd                                              # Maven Wrapper (Windows)
└── README.md                                             # 项目说明文档
```

## 核心功能模块

### 1. 用户管理模块 (User)
- 用户注册、登录、登出
- 用户信息查询、更新、删除
- 用户列表分页查询（支持用户名、手机号筛选）
- JWT Token 认证
- 微信登录支持
- 密码修改功能
- 身体基础数据管理（身高、体重、血压、血糖等）
- 身体数据记录管理（BodyNotes）
- 身体数据列表查询（分页）
- 身体数据记录增删改查

### 2. 角色管理模块 (Role)
- 角色的增删改查
- 角色列表分页查询（支持角色名筛选）
- 角色信息管理
- 获取所有角色列表

### 3. 菜单管理模块 (Menu)
- 菜单数据查询
- 菜单权限管理

### 4. 身体数据管理模块 (Body & BodyNotes)
- 身体基础数据管理（身高、体重、血压、血糖等）
- 用户身体数据记录管理
- 身体数据查询和更新

### 5. 运动信息管理模块 (SportInfo)
- 运动知识信息管理
- 运动信息列表查询（分页，支持运动类型筛选）
- 运动信息增删改查
- 获取所有运动信息

### 6. 详情管理模块 (Detail)
- 运动详情信息管理
- 详情信息增删改查
- 按运动类型查询（分页）
- 根据运动名称查询详情
- 根据运动信息ID查询详情
- 运动详情图片上传功能（支持 jpg、jpeg、png格式，最大 10MB）

### 7. AI健康咨询模块 (AIConsultation)
- AI健康咨询问答功能
- 问答知识库管理（增删改查）
- 问答列表分页查询
- 支持关键词、分类、问题搜索
- 优先级排序

### 8. 健康报告模块 (HealthReport)
- 健康报告数据生成
- 健康报告PDF导出
- 支持选择特定身体数据记录
- 报告数据统计分析

### 9. 权限关联模块
- **用户角色关联 (UserRole)**: 用户与角色的多对多关系
- **角色菜单关联 (RoleMenu)**: 角色与菜单的权限关联

## 开发环境部署

### 环境要求
- **JDK**: 1.8 或以上
- **Maven**: 3.6 或以上
- **MySQL**: 5.7 或以上8.0
- **Redis**: 5.0 或以上（可选，用于缓存）

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE db_health CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改配置文件 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_health?useUnicode=true&characterEncoding=UTF-8&useSSL=false
    username: root          # 修改为你的数据库用户名
    password: 123456        # 修改为你的数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    port: 6379              # Redis 端口
    host: localhost         # Redis 主机地址
```

### 安装依赖

使用 Maven 安装项目依赖：

```bash
mvn install
```

## 运行

### 方式一：使用 Maven 运行

```bash
# Windows
mvn spring-boot:run

```

### 方式二：使用 IDE 运行

1. 使用 IntelliJ IDEA 或 Eclipse 导入项目
2. 找到主启动类 `com.muzi.HealthApplication.java`
3. 右键运行 `main` 方法

### 验证运行

启动成功后，访问以下地址验证：

- **应用地址**: http://localhost:8080

## API 接口说明

### 主要接口路径

#### 用户管理接口 (`/user/*`)
- `POST /user/login` - 用户登录
- `POST /user/Wxlogin` - 微信登录
- `POST /user/register` - 用户注册
- `GET /user/info` - 获取用户信息（根据Token）
- `POST /user/logout` - 用户登出
- `GET /user/list` - 用户列表（分页，支持用户名、手机号筛选）
- `GET /user/all` - 获取所有用户
- `GET /user/{id}` - 根据ID获取用户信息
- `POST /user/add` - 新增用户
- `PUT /user/update` - 更新用户信息
- `DELETE /user/{id}` - 删除用户
- `PUT /user/changePassword` - 修改密码
- `POST /user/BodyInformation` - 上传身体基础数据
- `POST /user/BodyInformationNotes` - 上传身体数据记录
- `GET /user/getBodyNotes/{id}` - 获取用户身体数据记录
- `GET /user/WxgetBodyNotes/{token}` - 微信端获取身体数据记录
- `GET /user/getBodyList` - 身体数据列表（分页）
- `GET /user/getBodyById/{id}` - 根据ID获取身体数据
- `GET /user/getUserBodyList` - 获取当前用户身体数据记录列表（分页）
- `GET /user/getUserBodyById/{notesid}` - 根据记录ID获取身体数据
- `DELETE /user/deleteBodyById/{id}` - 删除身体数据
- `DELETE /user/deleteUserBodyById/{notesid}` - 删除用户身体数据记录

#### 角色管理接口 (`/role/*`)
- `GET /role/list` - 角色列表（分页，支持角色名筛选）
- `GET /role/all` - 获取所有角色
- `GET /role/{id}` - 根据ID获取角色信息
- `POST /role` - 新增角色
- `PUT /role` - 更新角色信息
- `DELETE /role/{id}` - 删除角色

#### 菜单管理接口 (`/menu/*`)
- `GET /menu` - 获取所有菜单数据

#### 运动信息接口 (`/sport/*`)
- `GET /sport/getAllSportInfo` - 获取所有运动信息
- `GET /sport/getSportList` - 运动信息列表（分页，支持运动类型筛选）
- `GET /sport/{id}` - 根据ID获取运动信息
- `POST /sport/add` - 新增运动信息
- `PUT /sport/update` - 更新运动信息
- `DELETE /sport/{id}` - 删除运动信息

#### 详情管理接口 (`/detail/*`)
- `GET /detail/getDetailList` - 详情列表（分页，支持运动类型筛选）
- `GET /detail/DetailInfo/{sportName}` - 根据运动名称获取详情
- `GET /detail/getDetailById/{id}` - 根据ID获取详情
- `GET /detail/getDetailBySportInfoId/{sportInfoId}` - 根据运动信息ID获取详情
- `GET /detail/getSportInfoList` - 获取所有运动类型列表
- `POST /detail/addDetail` - 新增详情
- `PUT /detail/updateDetail` - 更新详情
- `DELETE /detail/deleteDetailById/{id}` - 删除详情
- `POST /detail/uploadImage` - 上传运动详情图片

#### AI健康咨询接口 (`/ai/*`)
- `POST /ai/consult` - AI健康咨询问答
- `GET /ai/qa/list` - 获取所有问答列表
- `GET /ai/qa/page` - 问答列表（分页，支持关键词、分类、问题搜索）
- `GET /ai/qa/{id}` - 根据ID获取问答
- `POST /ai/qa` - 新增问答
- `PUT /ai/qa` - 更新问答
- `DELETE /ai/qa/{id}` - 删除问答

#### 健康报告接口 (`/healthReport/*`)
- `POST /healthReport/data` - 获取健康报告数据（JSON格式）
- `POST /healthReport/export` - 导出健康报告为PDF

### 统一响应格式

所有接口使用 `Unification` 统一响应格式：

```json
{
  "code": 20000,
  "message": "success",
  "data": {}
}
```

## 注意事项

### 1. 数据库配置
- 确保 MySQL 服务已启动
- 数据库名称默认为 `db_health`，可根据需要修改
- 建议使用 UTF-8 编码，避免中文乱码

### 2. Redis 配置
- Redis 为可选组件，如不使用可注释相关配置
- 如使用 Redis，确保 Redis 服务已启动

### 3. JWT Token
- Token 有效期需要根据业务需求调整
- 生产环境建议使用更安全的密钥

### 4. 跨域配置
- 前端开发时可能需要配置 CORS
- 生产环境建议限制允许的域名

### 5. 日志配置
- 默认日志级别为 `debug`，生产环境建议改为 `info` 或 `warn`
- 日志输出位置可在 `application.yml` 中配置

### 6. 逻辑删除
- 系统使用逻辑删除，删除操作不会真正删除数据
- 逻辑删除字段为 `deleted`，值为 1 表示已删除，0 表示未删除

### 7. 端口配置
- 默认端口为 8080，可在 `application.yml` 中修改
- 确保端口未被其他应用占用

### 8. 编码问题
- 确保所有文件使用 UTF-8 编码
- 数据库连接 URL 中包含 `useUnicode=true&characterEncoding=UTF-8`

### 9. 文件上传
- 图片上传目录：`upload/img/`
- 支持的图片格式：jpg、jpeg、png
- 最大文件大小：10MB
- 上传文件会自动生成UUID文件名，避免文件名冲突
- 确保 `upload/img/` 目录有写入权限

### 10. PDF 导出
- 健康报告支持导出为PDF格式
- 使用 iText HTML2PDF 库生成PDF
- PDF文件名格式：`健康报告_{userId}_{timestamp}.pdf`
- 支持选择特定身体数据记录生成报告

## 开发规范

1. **代码风格**: 遵循 Java 编码规范
2. **命名规范**: 
   - Controller: `XxxController`
   - Service: `IXxxService` / `XxxServiceImpl`
   - Mapper: `XxxMapper`
   - Entity: `Xxx`
3. **注释规范**: 类和方法需要添加 JavaDoc 注释
4. **异常处理**: 统一使用 `Unification` 封装响应

## 常见问题

### Q: 启动时提示端口被占用？
A: 修改 `application.yml` 中的 `server.port` 配置，或关闭占用 8080 端口的程序。

### Q: 数据库连接失败？
A: 检查数据库服务是否启动，用户名密码是否正确，数据库是否存在。

### Q: Swagger 文档无法访问？
A: 确认 Swagger 依赖已正确引入，检查 `SwaggerConfig` 配置类是否正确。

### Q: 图片上传失败？
A: 检查：
1. `upload/img/` 目录是否存在且有写入权限
2. 文件格式是否支持（jpg、jpeg、png）
3. 文件大小是否超过 10MB
4. 查看后端日志获取详细错误信息

### Q: PDF 导出失败？
A: 检查：
1. iText HTML2PDF 依赖是否正确引入
2. 健康报告数据是否完整
3. 查看后端日志获取详细错误信息

## 许可证

MIT License

## 作者
木子技术工作室 
muzi

