# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于SpringBoot + Vue3的综合性师生助手系统，集成了数据可视化、用户管理、大模型对话和即时通讯功能。系统采用前后端分离架构，使用JWT进行身份验证。

**核心功能模块：**
1. **数据统计页面** (Statistics.vue) - 使用ECharts展示用户统计数据，包括用户类型分布、院系分布、活跃用户趋势
2. **用户管理页面** (Users.vue) - 师生信息的增删改查，支持分页和搜索
3. **大模型对话界面** (Chat.vue) - 集成DeepSeek API，提供智能问答服务，支持会话管理
4. **即时通讯页面** (Message.vue) - 基于WebSocket的实时聊天功能

## 实际代码架构

### 后端架构 (SpringBoot 3.1.5)

```
backend/src/main/java/com/teacher/assistant/
├── AssistantApplication.java      # SpringBoot启动类
├── config/                        # 配置类
│   ├── JwtAuthenticationFilter.java    # JWT认证过滤器
│   ├── JwtConfig.java                   # JWT配置
│   ├── MybatisPlusConfig.java          # MyBatis-Plus配置
│   ├── ResultVO.java                    # 统一响应VO
│   ├── SecurityConfig.java             # Spring Security配置
│   ├── WebSocketConfig.java            # WebSocket配置
│   ├── WebSocketHandler.java           # WebSocket处理器
│   └── WebSocketHandshakeInterceptor.java # WebSocket握手拦截器
├── controller/                    # 控制器层 (REST API)
│   ├── ChatController.java       # 聊天接口 (DeepSeek API调用)
│   ├── MessageController.java    # 消息接口 (即时通讯)
│   ├── StatisticsController.java # 统计接口 (数据可视化)
│   └── UserController.java       # 用户接口 (CRUD + 认证)
├── service/                       # 服务层接口
├── mapper/                        # MyBatis数据访问层
├── entity/                        # 实体类
│   ├── User.java                 # 用户实体
│   ├── Role.java                 # 角色实体
│   ├── Permission.java           # 权限实体
│   ├── ChatRecord.java           # 聊天记录实体
│   ├── Message.java              # 即时消息实体
│   └── dto/                      # 数据传输对象
│       ├── ChatMessageDTO.java
│       ├── DeepSeekResponseDTO.java
│       └── LoginDTO.java
├── util/                          # 工具类
└── AssistantApplication.java
```

**技术栈：**
- SpringBoot 3.1.5 + Spring Security
- MyBatis-Plus 3.5.4.1 (ORM框架)
- MySQL 8.0 (数据库)
- JWT 0.12.3 (身份认证)
- Redis (缓存，可选)
- WebSocket (实时通讯)

### 前端架构 (Vue 3.4.0)

```
frontend/src/
├── main.js                       # 应用入口
├── App.vue                       # 根组件
├── router/index.js              # Vue Router配置
├── stores/user.js               # Pinia用户状态管理
├── utils/request.js             # Axios HTTP客户端封装
├── api/                         # API接口
│   ├── auth.js                  # 认证相关API
│   ├── chat.js                  # 聊天API
│   ├── message.js               # 消息API
│   └── statistics.js            # 统计API
└── views/                       # 页面组件
    ├── Layout.vue               # 布局组件 (导航栏 + 侧边栏)
    ├── Login.vue                # 登录页
    ├── Statistics/              # 数据统计页
    │   └── Statistics.vue       # ECharts图表展示
    ├── UserManage/              # 用户管理页
    │   └── Users.vue            # 用户CRUD界面
    ├── Chat/                    # 大模型对话页
    │   └── Chat.vue             # ChatGPT风格对话界面
    └── Message/                 # 即时通讯页
        └── Message.vue          # WebSocket聊天界面
```

**技术栈：**
- Vue 3.4.0 + Vue Router 4
- Pinia (状态管理)
- Element Plus (UI组件库)
- ECharts + vue-echarts (图表库)
- Axios (HTTP客户端)
- Socket.IO Client (WebSocket客户端)
- Vite (构建工具)

## 数据库配置

当前数据库配置 (`backend/src/main/resources/application.yml:11-15`)：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/assistant?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 123456
```

**数据库名**: assistant

## 核心配置

### JWT配置 (`application.yml:53-57`)
```yaml
jwt:
  secret: MySecretKeyForTeacherAssistantSystem2025
  expiration: 604800  # 7天，单位秒
  header: Authorization
  prefix: "Bearer "
```

### DeepSeek API配置 (`application.yml:60-66`)
```yaml
deepseek:
  api:
    key: ${DEEPSEEK_API_KEY:your_api_key_here}
    base-url: https://api.deepseek.com
    model: deepseek-chat
    max-tokens: 2000
    temperature: 0.7
```

### WebSocket配置 (`application.yml:93-95`)
```yaml
app:
  websocket:
    allowed-origins: "*"
    path: /ws
```

### 前端代理配置 (`frontend/vite.config.js:28-33`)
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 常用开发命令

### 后端命令

```bash
cd backend

# 清理并编译
mvn clean compile

# 打包 (生成 jar 文件)
mvn clean package -DskipTests

# 运行测试
mvn test

# 启动开发服务器 (热重载)
mvn spring-boot:run

# 使用指定配置文件启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 启动应用 (生产模式)
java -jar target/assistant-0.0.1-SNAPSHOT.jar
```

**Maven项目信息:**
- GroupId: com.teacher
- ArtifactId: assistant
- Version: 0.0.1-SNAPSHOT
- Java版本: 17

### 前端命令

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器 (http://localhost:3000)
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview

# 代码检查 + 自动修复
npm run lint
```

**Vite配置特点:**
- 自动导入 Element Plus 组件
- 路径别名: `@` 指向 `frontend/src`
- 预构建优化: `echarts`, `vue-echarts`

## API端点汇总

### 认证接口 (`/api/auth`)
- `POST /login` - 用户登录 (返回JWT Token)
- `GET /users` - 获取用户列表 (分页)
- `PUT /users/{id}` - 更新用户
- `DELETE /users/{id}` - 删除用户

### 聊天接口 (`/api/chat`)
- `POST /send` - 发送消息给DeepSeek
- `GET /history/{sessionId}` - 获取聊天历史
- `GET /sessions` - 获取会话列表

### 统计接口 (`/api/statistics`)
- `GET /overview` - 获取概览统计
- `GET /users/count` - 用户数量统计
- `GET /users/distribution` - 用户类型分布
- `GET /department/distribution` - 院系分布

### 消息接口 (`/api/message`)
- `GET /list` - 获取消息列表
- `POST /send` - 发送消息 (WebSocket)

## 测试账户

**统一密码**: `123456`

**预置账户:**
- **管理员**: admin
- **教师**: teacher001, teacher002, teacher003
- **学生**: student001, student002, student003, student004, student005

详细账户信息见: `测试账户信息.md`

## 当前开发状态

**最新提交**: `6c24752 feat: 测试账户信息`

**当前工作**: 修改了 `frontend/src/views/Login.vue` (未提交)

**待优化项** (来自README.md):
- [ ] 添加单元测试
- [ ] 实现文件上传功能
- [ ] 优化数据库索引
- [ ] 添加日志系统
- [ ] 实现缓存机制
- [ ] 添加API限流
- [ ] 完善错误处理

## 关键实现细节

### 1. 大模型对话实现

**后端流程** (`backend/src/main/java/com/teacher/assistant/controller/ChatController.java`):
1. 接收用户消息
2. 构建DeepSeek API请求
3. 使用RestTemplate调用API
4. 返回AI响应

**前端流程** (`frontend/src/views/Chat/Chat.vue`):
1. 展示会话列表
2. 发送消息到 `/api/chat/send`
3. 实时显示AI回复
4. 保存会话历史

### 2. 即时通讯实现

**后端流程**:
1. `WebSocketConfig.java` - 配置WebSocket
2. `WebSocketHandler.java` - 处理连接、消息、断开
3. `MessageController.java` - REST API管理消息历史

**前端流程** (`frontend/src/views/Message/Message.vue`):
1. 使用Socket.IO Client连接WebSocket
2. 显示在线用户列表
3. 实时收发消息
4. 维护消息状态

### 3. 数据可视化实现

**后端** (`backend/src/main/java/com/teacher/assistant/controller/StatisticsController.java`):
1. 聚合用户数据 (MyBatis-Plus查询)
2. 计算统计数据
3. 返回JSON格式

**前端** (`frontend/src/views/Statistics/Statistics.vue`):
1. 使用ECharts渲染图表
2. 饼图: 用户类型分布
3. 柱状图: 院系分布
4. 折线图: 活跃用户趋势

### 4. 用户认证实现

**流程** (`backend/src/main/java/com/teacher/assistant/controller/UserController.java:42-50`):
1. 接收登录请求 (`/api/auth/login`)
2. 验证用户名密码 (Spring Security)
3. 生成JWT Token (`JwtConfig.generateToken()`)
4. 返回Token + 用户信息

**前端** (`frontend/src/stores/user.js`):
1. 登录后保存Token到localStorage
2. 请求拦截器自动添加Token到请求头
3. Pinia状态管理用户信息

## 常见开发任务

### 添加新API接口

1. **后端**:
   - 在`controller`包下创建新的Controller类
   - 在`service`包下创建Service接口和实现
   - 在`mapper`包下创建Mapper接口 (如需数据库操作)
   - 在`entity`包下创建或更新DTO

2. **前端**:
   - 在`api`目录下创建新的API文件
   - 在`views`目录下创建新页面组件
   - 在`router/index.js`中注册新路由

### 修改数据库结构

1. 在MySQL中直接修改表结构
2. 更新对应实体类 (`entity/User.java`等)
3. 如需复杂查询，更新Mapper XML文件

### 添加新页面

1. 在`frontend/src/views`下创建目录和Vue文件
2. 在`frontend/src/router/index.js`中导入并注册路由
3. 创建对应的API调用函数
4. 更新布局组件的导航菜单 (`frontend/src/views/Layout.vue`)

## 注意事项

1. **安全**: 所有接口通过`JwtAuthenticationFilter`验证Token
2. **密码**: 使用Spring Security的PasswordEncoder加密
3. **跨域**: Controller使用`@CrossOrigin`注解允许跨域
4. **日志**: 使用`@Slf4j`注解记录日志
5. **事务**: 使用`@Transactional`保证数据一致性
6. **分页**: 使用MyBatis-Plus的`Page`插件实现分页
7. **文件上传**: 配置在`application.yml:97-100`，默认路径`/tmp/uploads`

## 项目文档

- `README.md` - 项目介绍和快速开始
- `项目需求文档.md` - 功能需求说明
- `业务流程与产品设计.md` - 设计规范
- `测试账户信息.md` - 测试数据详情
- `todo.md` - 开发任务清单

## 目录结构统计

- **后端Java文件**: 35个
- **前端Vue/JS文件**: 约15个
- **数据库表**: user, role, permission, chat_record, message
- **总代码行数**: 约3000+ 行
