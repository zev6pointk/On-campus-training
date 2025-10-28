# 师生助手系统

基于SpringBoot + Vue3的综合性师生助手系统，集成了数据可视化、用户管理、大模型对话和即时通讯功能。

## 🚀 项目特色

- **前后端分离架构**：SpringBoot后端 + Vue3前端
- **现代化技术栈**：使用最新稳定版本的主流框架
- **功能完善**：数据统计、用户管理、AI对话、即时通讯四大核心模块
- **响应式设计**：适配多种屏幕尺寸
- **权限控制**：基于JWT的Token认证机制

## 📋 技术栈

### 后端技术
- **框架**：SpringBoot 3.1.5
- **安全**：Spring Security + JWT
- **数据库**：MySQL 8.0 + MyBatis-Plus
- **实时通讯**：WebSocket
- **API调用**：RestTemplate
- **其他**：Lombok、Validation

### 前端技术
- **框架**：Vue 3.4.0
- **路由**：Vue Router 4
- **状态管理**：Pinia
- **UI组件**：Element Plus
- **图表**：ECharts + vue-echarts
- **HTTP客户端**：Axios
- **实时通讯**：Socket.IO Client
- **构建工具**：Vite

## 🏗️ 项目结构

```
师生助手系统/
├── backend/                    # SpringBoot后端
│   ├── src/main/java/com/teacher/assistant/
│   │   ├── controller/         # 控制器层
│   │   ├── service/            # 服务层
│   │   ├── mapper/             # MyBatis映射
│   │   ├── entity/             # 实体类
│   │   ├── config/             # 配置类
│   │   └── AssistantApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml     # 配置文件
│   │   └── mapper/             # MyBatis XML映射
│   └── pom.xml                 # Maven配置
├── frontend/                   # Vue3前端
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   │   ├── Statistics/     # 数据统计
│   │   │   ├── UserManage/     # 用户管理
│   │   │   ├── Chat/           # 大模型对话
│   │   │   └── Message/        # 即时通讯
│   │   ├── api/                # API接口
│   │   ├── stores/             # Pinia状态管理
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具函数
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 🔧 功能模块

### 1. 数据统计模块
- **用户数量统计**：总用户数、用户类型分布
- **数据可视化**：ECharts图表展示
  - 饼图：用户类型分布
  - 柱状图：院系分布
  - 折线图：活跃用户趋势
- **概览卡片**：关键指标一目了然

### 2. 用户管理模块
- **CRUD操作**：新增、查询、更新、删除用户
- **高级搜索**：支持用户名、用户类型筛选
- **分页展示**：大数据量分页处理
- **表单验证**：完整的前后端数据校验

### 3. 大模型对话模块
- **AI助手**：集成DeepSeek API
- **会话管理**：支持多会话切换
- **历史记录**：保存并查看对话历史
- **流式响应**：实时显示AI回复

### 4. 即时通讯模块
- **实时聊天**：基于WebSocket的实时消息
- **联系人列表**：显示在线用户
- **消息状态**：发送中、已发送、已读状态
- **聊天记录**：保存并加载历史消息

## 📦 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 后端启动

1. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户表（示例）
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `gender` tinyint DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `user_type` tinyint NOT NULL COMMENT '1-系统管理员 2-教师 3-学生',
  `department` varchar(100) DEFAULT NULL,
  `major` varchar(100) DEFAULT NULL,
  `grade` varchar(20) DEFAULT NULL,
  `student_id` varchar(50) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `last_login_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

2. **修改配置文件**
编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/assistant?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

3. **安装依赖并启动**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 前端启动

1. **安装依赖**
```bash
cd frontend
npm install
# 或使用 yarn
yarn install
```

2. **启动开发服务器**
```bash
npm run dev
# 或使用 yarn
yarn dev
```

3. **访问系统**
- 前端：http://localhost:3000
- 后端API：http://localhost:8080/api

## 🔑 默认账号

系统提供以下测试账号：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 教师 | teacher | teacher123 |
| 学生 | student | student123 |

## 📝 API文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `GET /api/auth/me` - 获取当前用户信息

### 用户管理
- `GET /api/auth/users` - 获取用户列表（分页）
- `PUT /api/auth/users/{id}` - 更新用户信息
- `DELETE /api/auth/users/{id}` - 删除用户

### 聊天接口
- `POST /api/chat/send` - 发送消息
- `GET /api/chat/history/{sessionId}` - 获取聊天历史
- `GET /api/chat/sessions` - 获取会话列表

### 统计接口
- `GET /api/statistics/overview` - 获取概览统计
- `GET /api/statistics/users/count` - 用户数量统计
- `GET /api/statistics/users/distribution` - 用户类型分布
- `GET /api/statistics/department/distribution` - 院系分布

## 🔧 配置说明

### DeepSeek API配置
在 `application.yml` 中配置：
```yaml
deepseek:
  api:
    key: YOUR_API_KEY
    base-url: https://api.deepseek.com
    model: deepseek-chat
```

### JWT配置
```yaml
jwt:
  secret: MySecretKeyForTeacherAssistantSystem2025
  expiration: 604800  # 7天
  header: Authorization
  prefix: "Bearer "
```

## 🎨 项目亮点

1. **架构清晰**：分层架构，职责明确
2. **代码规范**：遵循阿里巴巴Java开发手册
3. **安全可靠**：JWT认证、密码加密、SQL防注入
4. **用户友好**：响应式设计、操作便捷
5. **可维护性强**：模块化开发、注释完整

## 📄 开发文档

- [项目需求文档](项目需求文档.md)
- [业务流程与产品设计](业务流程与产品设计.md)
- [Claude开发指南](CLAUDE.md)

## 🚧 待优化项

- [ ] 添加单元测试
- [ ] 实现文件上传功能
- [ ] 优化数据库索引
- [ ] 添加日志系统
- [ ] 实现缓存机制
- [ ] 添加API限流
- [ ] 完善错误处理

## 👥 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 👨‍💻 作者

**Teacher Assistant Team**

---

⭐ 如果这个项目对你有帮助，请给它一个星标！
