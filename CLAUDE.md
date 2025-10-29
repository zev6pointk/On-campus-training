# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个**师生助手系统**，基于 Spring Boot + Vue 3 的全栈应用，旨在为教育场景提供综合性的数字化助手功能。

### 核心功能模块

1. **用户管理系统** - 支持系统管理员、教师、学生三种角色
2. **大模型智能对话** - 集成 DeepSeek API，提供 AI 助手服务
3. **即时通讯功能** - 基于 WebSocket 的实时消息系统
4. **数据统计分析** - 用户数据、对话统计、活跃度趋势等可视化图表
5. **个人资料管理** - 用户信息维护和角色权限管理

## 技术栈

### 后端 (backend)
- **框架**: Spring Boot 3.1.5
- **Java版本**: 17
- **安全**: Spring Security + JWT 认证
- **数据层**: MyBatis Plus + MySQL 8.0
- **缓存**: Redis
- **实时通信**: Spring WebSocket
- **构建工具**: Maven

### 前端 (frontend)
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5.0
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **图表**: ECharts
- **实时通信**: Socket.io-client
- **样式**: Sass
- **代码规范**: ESLint + Vue插件

## 开发环境配置

### 数据库配置
- **数据库**: MySQL 8.0
- **用户名**: root
- **密码**: 123456
- **数据库名**: assistant
- **Redis**: localhost:6379 (密码为空)

### 必需的环境变量
- `DEEPSEEK_API_KEY`: DeepSeek API 密钥 (在 application.yml 中配置)

### 测试账户
统一密码: `123456`

**账户列表**:
- **管理员**: admin
- **教师**: teacher001, teacher002, teacher003
- **学生**: student001, student002, student003, student004, student005

详细信息见 `测试账户信息.md`

## 常用开发命令

### 后端开发 (backend/)

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package

# 运行应用
mvn spring-boot:run
```

或者直接运行 JAR 文件:
```bash
java -jar target/assistant-0.0.1-SNAPSHOT.jar
```

### 前端开发 (frontend/)

```bash
# 安装依赖
npm install

# 开发模式 (支持热重载)
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 代码检查和修复
npm run lint
```

### 数据库操作
- **连接**: localhost:3306/assistant
- **SQL文件**: backend/src/main/resources/mapper/
- **配置**: backend/src/main/resources/application.yml:11-15

## 代码架构

### 后端架构 (backend/src/main/java/com/teacher/assistant/)

```
├── config/           # 配置类
│   ├── MybatisPlusConfig.java
│   ├── JwtConfig.java
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   ├── WebSocketHandler.java
│   └── WebSocketHandshakeInterceptor.java
├── controller/       # 控制器层 (REST API)
│   ├── ChatController.java      # 大模型对话接口
│   ├── MessageController.java   # 消息管理接口
│   ├── StatisticsController.java # 统计接口
│   ├── UserController.java      # 用户管理接口
│   └── FileController.java      # 文件操作接口
├── service/          # 业务逻辑层
│   ├── UserService.java
│   ├── DeepSeekService.java     # DeepSeek API集成
│   ├── ChatRecordService.java
│   ├── MessageService.java
│   └── StatisticsService.java
├── service/impl/     # 服务实现
├── mapper/           # 数据访问层 (MyBatis)
├── entity/           # 实体类
│   ├── User.java
│   ├── Role.java
│   ├── Permission.java
│   ├── UserRole.java
│   ├── ChatRecord.java
│   ├── Message.java
│   └── Statistics.java
└── entity/dto/       # 数据传输对象
    ├── LoginDTO.java
    ├── ChatMessageDTO.java
    └── DeepSeekResponseDTO.java
```

**关键接口**:
- `/api/auth/*` - 认证相关
- `/api/chat/*` - 大模型对话
- `/api/message/*` - 即时消息
- `/api/statistics/*` - 数据统计
- `/api/users/*` - 用户管理

### 前端架构 (frontend/src/)

```
├── api/              # API请求封装
│   ├── auth.js
│   ├── chat.js
│   ├── message.js
│   └── statistics.js
├── stores/           # Pinia状态管理
│   └── user.js
├── router/           # 路由配置
│   └── index.js
├── utils/            # 工具函数
│   └── request.js    # Axios封装
├── views/            # 页面组件
│   ├── Chat/Chat.vue
│   ├── Layout.vue
│   ├── Login.vue
│   ├── Message/Message.vue
│   ├── Profile/Profile.vue
│   ├── Statistics/Statistics.vue
│   └── UserManage/Users.vue
├── App.vue
└── main.js
```

**路由页面**:
- `/` - 重定向到 `/chat`
- `/login` - 登录页
- `/chat` - 大模型对话页
- `/message` - 即时通讯页
- `/statistics` - 数据统计页
- `/profile` - 个人资料页
- `/users` - 用户管理页 (需管理员权限)

## 重要配置说明

### application.yml 关键配置
- **服务器端口**: 8080
- **上下文路径**: /api
- **JWT密钥**: MySecretKeyForTeacherAssistantSystem2025
- **JWT过期时间**: 7天 (604800秒)
- **DeepSeek API配置**: deepseek.api.*

### WebSocket配置
- **路径**: /ws
- **支持跨域**: 允许所有来源
- **协议**: 基于 Spring WebSocket

### MyBatis Plus配置
- **主键策略**: AUTO
- **逻辑删除**: 字段 deleted，删除值为1
- **SQL日志**: 开启控制台输出 (生产环境需关闭)

## 当前任务状态

项目存在以下待解决的问题，详见 `todo.md`:

1. **任务五**: 修复大模型助手页面输入框被截断问题
2. **任务七**: 整理并输出所有SQL文件
3. **任务八**: 修复管理员登录后用户管理页面刷新消失问题
4. **任务九**: 数据统计页面数据为0问题排查
5. **任务十**: 在线用户数统计机制分析
6. **任务十一**: 活跃用户趋势数据来源验证
7. **任务十二**: 页面布局全屏化改造

## 开发注意事项

### 安全相关
- 密码使用 BCrypt 加密存储
- JWT Token 用于API认证
- 前端通过 `Authorization` 头传递 Token
- 敏感信息不要提交到代码仓库

### 数据库操作
- 使用 MyBatis Plus 的 BaseMapper 减少样板代码
- 实体类已配置逻辑删除，无需物理删除
- 查询时注意处理 `deleted` 字段

### 前端开发
- 使用 Pinia 进行状态管理，用户信息存储在 localStorage
- Element Plus 组件库用于UI构建
- 使用 Vue 3 Composition API 编写组件
- API请求通过 axios 封装，统一处理 token 和错误

### 实时通信
- WebSocket 用于即时消息和在线用户状态
- 前端使用 socket.io-client 连接
- 后端通过 WebSocketHandler 处理消息推送

### 大模型集成
- 使用 DeepSeek Chat API
- 配置在 application.yml 的 deepseek 部分
- ChatController 提供对话接口

## 常见问题排查

### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 验证 application.yml 中的数据库配置
- 确认数据库和用户存在

### 2. Redis 连接失败
- 检查 Redis 服务是否启动
- 验证 application.yml 中的 Redis 配置

### 3. 前端无法访问后端 API
- 检查后端是否在 8080 端口运行
- 验证 CORS 配置
- 检查 JWT Token 是否有效

### 4. WebSocket 连接失败
- 检查 WebSocket 配置
- 确认防火墙和网络设置
- 验证前端 socket.io-client 版本兼容性

### 5. DeepSeek API 调用失败
- 验证 DEEPSEEK_API_KEY 环境变量
- 检查网络连接和 API 配额
- 查看后端日志获取详细错误信息

## 代码风格和规范

### 后端
- 使用 Java 17 新特性
- 遵循 Spring Boot 最佳实践
- Controller → Service → Mapper 分层架构
- 使用 Lombok 减少样板代码

### 前端
- Vue 3 Composition API
- ESLint 代码检查
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case

## 调试和日志

### 后端日志
- 日志级别: DEBUG
- 日志文件: logs/assistant.log
- 控制台输出开启 MyBatis Plus SQL 日志

### 前端调试
- 使用 Vite 的 dev server
- Vue DevTools 可用
- 浏览器开发者工具

## 性能优化建议

1. **后端**
   - 合理使用 Redis 缓存
   - 数据库查询优化
   - 分页查询避免全表扫描

2. **前端**
   - 路由懒加载
   - 组件按需引入
   - 图片资源压缩

## 部署说明

### 生产环境配置
1. 修改 application.yml:
   - 关闭 SQL 日志输出
   - 配置生产数据库地址
   - 更改 JWT 密钥
   - 配置日志级别为 INFO 或 WARN

2. 前端构建:
   ```bash
   npm run build
   ```

3. 后端打包:
   ```bash
   mvn clean package
   ```

4. 数据库迁移:
   - 执行 SQL 脚本创建表结构
   - 插入初始数据

5. 启动应用:
   - 运行 JAR 文件
   - 或使用 Docker 部署
