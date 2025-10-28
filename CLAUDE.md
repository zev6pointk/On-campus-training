# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于SpringBoot的综合性师生助手系统，集成了数据可视化、用户管理、大模型对话和即时通讯功能。

**核心功能模块：**
1. **数据统计页面** - 使用ECharts展示用户活跃度、师生比例、专业分布等统计数据
2. **后台CRUD页面** - 师生信息的增删改查，支持批量操作
3. **大模型对话界面** - 集成DeepSeek API，提供智能问答服务
4. **即时通讯页面** - 基于WebSocket的实时聊天功能

## 技术栈

**后端:**
- SpringBoot
- MyBatis-Plus
- MySQL 8.0
- JWT Token认证

**前端:**
- Vue 3
- Element Plus
- ECharts
- WebSocket

**外部服务:**
- DeepSeek API (大模型)
- MinIO或本地存储 (文件存储)

## 数据库设计

核心数据表：
- `user` - 用户基本信息
- `role` - 角色信息
- `permission` - 权限信息
- `chat_record` - 大模型对话记录
- `message` - 即时通讯消息
- `statistics` - 统计数据

## 项目结构预期

```
师生助手系统/
├── backend/                 # SpringBoot后端
│   ├── src/main/java/
│   │   └── com/teacher/assistant/
│   │       ├── controller/    # 控制器层
│   │       ├── service/       # 服务层
│   │       ├── mapper/        # MyBatis映射
│   │       ├── entity/        # 实体类
│   │       └── config/        # 配置类
│   ├── src/main/resources/
│   │   ├── mapper/           # MyBatis XML映射
│   │   └── application.yml   # 配置文件
│   └── pom.xml
├── frontend/                # Vue3前端
│   ├── src/
│   │   ├── components/      # 公共组件
│   │   ├── views/          # 页面组件
│   │   │   ├── Statistics/   # 数据统计页面
│   │   │   ├── UserManage/   # 用户管理页面
│   │   │   ├── Chat/        # 大模型对话页面
│   │   │   └── Message/     # 即时通讯页面
│   │   ├── router/         # 路由配置
│   │   ├── api/            # API调用
│   │   └── utils/          # 工具函数
│   └── package.json
└── docs/                   # 项目文档
```

## 开发环境设置

### 后端启动

```bash
# 进入backend目录
cd backend

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
# 或
java -jar target/assistant-0.0.1-SNAPSHOT.jar

# 指定配置文件启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 前端启动

```bash
# 进入frontend目录
cd frontend

# 安装依赖
npm install
# 或使用yarn
yarn install

# 开发环境启动
npm run dev
# 或
yarn dev

# 构建生产版本
npm run build
# 或
yarn build

# 代码检查
npm run lint
# 或
yarn lint
```

### 数据库配置

在`application.yml`中配置MySQL连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/assistant
    username: root
    password: your_password
```

### DeepSeek API配置

在配置文件或环境变量中设置：
```yaml
deepseek:
  api:
    key: YOUR_API_KEY
    base-url: https://api.deepseek.com
```

## 关键开发指南

### 大模型对话实现

1. 后端使用WebClient或RestTemplate调用DeepSeek API
2. 实现流式响应处理，实时返回Token
3. 前端使用WebSocket或Server-Sent Events接收流式数据
4. 对话记录存储到数据库，支持历史查询

### 即时通讯实现

1. 使用Spring WebSocket或STOMP实现WebSocket连接
2. 消息实时推送机制
3. 支持一对一私聊和群组聊天
4. 消息类型：文本、图片、文件
5. 消息状态管理（发送中、已发送、已读）

### 数据统计实现

1. 后端提供统计数据API，支持时间范围和维度筛选
2. 前端使用ECharts实现多种图表类型（折线图、饼图、柱状图）
3. 支持数据导出（Excel/PDF）
4. 数据实时更新机制

### 权限控制

实现基于JWT的认证和RBAC权限控制：
- 系统管理员：全部权限
- 教师：院系数据、自己的学生、大模型和通讯
- 学生：个人数据、大模型和通讯

### 性能优化策略

**前端优化:**
- 路由懒加载
- 组件按需引入
- 大列表使用虚拟滚动
- 合理使用缓存

**后端优化:**
- 数据库索引优化
- SQL查询优化
- 连接池配置
- 异步处理大模型API调用

## 测试命令

### 后端测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 生成测试覆盖率报告
mvn jacoco:report
```

### 前端测试

```bash
# 运行单元测试
npm run test:unit

# 运行端到端测试
npm run test:e2e

# 组件测试
npm run test:component
```

## 常见任务

### 创建新实体

1. 在`entity`包下创建实体类
2. 在`mapper`包下创建MyBatis映射接口
3. 在`mapper/xml`下创建SQL映射文件
4. 在`service`包下实现业务逻辑
5. 在`controller`包下创建REST接口

### 添加新页面

1. 在`frontend/src/views`下创建页面组件
2. 在路由配置中注册新路由
3. 创建API调用函数
4. 添加必要的权限控制

### 集成新API

1. 在`application.yml`中配置API密钥
2. 创建服务类封装API调用
3. 实现错误处理和重试机制
4. 添加单元测试

## 注意事项

1. **安全性**: 所有接口必须验证JWT Token和用户权限
2. **密码存储**: 使用BCrypt加密存储密码
3. **SQL注入**: 使用参数化查询防止SQL注入
4. **XSS防护**: 前端输入输出需要过滤
5. **接口限流**: 防止恶意请求，特别是大模型API调用
6. **日志记录**: 重要操作需要记录日志
7. **异常处理**: 提供友好的错误提示给前端
8. **文档维护**: 保持API文档和代码同步

## 项目文档

- `项目需求文档.md` - 详细的功能需求说明
- `业务流程与产品设计.md` - 业务流程和设计规范

参考这些文档了解详细的功能要求和设计理念。
