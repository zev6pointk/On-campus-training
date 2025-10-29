# 师生助手系统

一个基于 Spring Boot + Vue 3 的全栈教育辅助平台，为教师和学生提供智能对话、即时通讯、数据统计等功能。

## 📋 项目简介

本项目是一个专为教育场景设计的数字化助手系统，支持三种用户角色（管理员、教师、学生），提供以下核心功能：

- 👥 **用户管理系统** - 完整的角色权限管理
- 🤖 **大模型智能对话** - 集成 DeepSeek API 的 AI 助手
- 💬 **即时通讯功能** - 基于 WebSocket 的实时消息系统
- 📊 **数据统计分析** - 用户活跃度、对话统计等可视化图表
- 👤 **个人资料管理** - 用户信息维护和权限管理

## 🛠 技术栈

### 后端
- **框架**: Spring Boot 3.1.5
- **语言**: Java 17
- **安全**: Spring Security + JWT
- **数据库**: MyBatis Plus + MySQL 8.0
- **缓存**: Redis
- **实时通信**: Spring WebSocket
- **构建**: Maven

### 前端
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5.0
- **UI库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **图表**: ECharts
- **实时通信**: Socket.io-client
- **样式**: Sass

## 🚀 快速开始

### 环境要求

- Java 17+
- Node.js 16+
- MySQL 8.0
- Redis 6.0+

### 1. 克隆项目

```bash
git clone <项目地址>
cd on-campus-training
```

### 2. 后端启动

#### 配置数据库
创建 MySQL 数据库：
```sql
CREATE DATABASE assistant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

配置 `backend/src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/assistant
    username: root
    password: 123456
```

#### 启动后端服务
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

或直接运行 JAR：
```bash
mvn clean package
java -jar target/assistant-0.0.1-SNAPSHOT.jar
```

### 3. 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问：http://localhost:5173

### 4. 环境变量配置

在 `backend/src/main/resources/application.yml` 中配置：
```yaml
deepseek:
  api:
    key: YOUR_DEEPSEEK_API_KEY
```

## 👤 测试账户

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 教师 | teacher001 | 123456 |
| 教师 | teacher002 | 123456 |
| 教师 | teacher003 | 123456 |
| 学生 | student001 | 123456 |
| 学生 | student002 | 123456 |
| 学生 | student003 | 123456 |
| 学生 | student004 | 123456 |
| 学生 | student005 | 123456 |

## 📁 项目结构

```
on-campus-training/
├── backend/                    # 后端代码
│   ├── src/main/java/
│   │   └── com/teacher/assistant/
│   │       ├── config/        # 配置类
│   │       ├── controller/    # 控制器
│   │       ├── service/       # 业务逻辑
│   │       ├── mapper/        # 数据访问层
│   │       ├── entity/        # 实体类
│   │       └── entity/dto/    # 数据传输对象
│   └── src/main/resources/
│       ├── mapper/           # SQL 映射文件
│       └── application.yml   # 配置文件
├── frontend/                  # 前端代码
│   ├── src/
│   │   ├── api/              # API 请求
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── router/           # 路由配置
│   │   ├── utils/            # 工具函数
│   │   ├── views/            # 页面组件
│   │   └── assets/           # 静态资源
│   ├── public/               # 公共文件
│   └── package.json          # 依赖配置
└── docs/                     # 项目文档
```

## 🔌 API 文档

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/info` - 获取用户信息

### 聊天对话
- `POST /api/chat/send` - 发送消息给 AI
- `GET /api/chat/history` - 获取聊天历史

### 即时消息
- `GET /api/message/list` - 获取消息列表
- `POST /api/message/send` - 发送消息

### 数据统计
- `GET /api/statistics/overview` - 获取统计数据概览
- `GET /api/statistics/users` - 用户统计
- `GET /api/statistics/chats` - 对话统计

### 用户管理
- `GET /api/users/list` - 获取用户列表
- `POST /api/users/create` - 创建用户
- `PUT /api/users/update` - 更新用户信息
- `DELETE /api/users/delete` - 删除用户

## 🧪 开发命令

### 后端开发
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package
```

### 前端开发
```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build

# 代码检查和修复
npm run lint

# 预览生产构建
npm run preview
```

## ⚙️ 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/assistant
    username: root
    password: 123456
```

### Redis 配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
```

### JWT 配置
```yaml
jwt:
  secret: MySecretKeyForTeacherAssistantSystem2025
  expiration: 604800  # 7天
```

### WebSocket 配置
- 路径: `/ws`
- 支持跨域
- 基于 Spring WebSocket

## 🔒 安全说明

- 密码使用 BCrypt 加密存储
- JWT Token 用于 API 认证
- 前端通过 `Authorization` 头传递 Token
- 敏感信息不要提交到代码仓库

## 🐛 常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 验证数据库配置
- 确认数据库和用户存在

### 2. Redis 连接失败
- 检查 Redis 服务是否启动
- 验证 Redis 配置

### 3. 前端无法访问后端
- 检查后端是否在 8080 端口运行
- 验证 CORS 配置
- 检查 JWT Token 是否有效

### 4. WebSocket 连接失败
- 检查 WebSocket 配置
- 确认防火墙设置
- 验证 socket.io-client 版本

### 5. DeepSeek API 调用失败
- 验证 DEEPSEEK_API_KEY 环境变量
- 检查网络连接和 API 配额
- 查看后端日志获取错误信息

## 📊 性能优化建议

### 后端
- 合理使用 Redis 缓存
- 数据库查询优化
- 分页查询避免全表扫描

### 前端
- 路由懒加载
- 组件按需引入
- 图片资源压缩

## 📝 开发规范

### 代码风格
- 后端：遵循 Spring Boot 最佳实践，使用 Java 17 新特性
- 前端：Vue 3 Composition API，ESLint 代码检查
- 组件命名：PascalCase
- 文件命名：kebab-case

### 分层架构
- Controller → Service → Mapper 分层设计
- 使用 Lombok 减少样板代码
- 实体类配置逻辑删除

## 📄 许可证

本项目仅供学习和研究使用。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

## 📞 联系方式

如有问题，请通过以下方式联系：
- 项目 Issue：https://github.com/your-repo/issues
- 邮箱：your-email@example.com

---

**注意**：首次使用请确保所有依赖服务（MySQL、Redis）已正确安装和配置。
