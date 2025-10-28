# 师生助手系统优化任务清单

## 项目概述
基于SpringBoot的综合性师生助手系统，包含数据可视化、用户管理、大模型对话和即时通讯功能。本次优化将解决UI体验问题、修复权限错误、完善功能模块。

---

## 任务列表

### 任务一：实现个人资料页面

**问题描述**
- 目前点击右上角头像下拉菜单中的"个人资料"选项，仅显示"个人资料功能开发中"的提示信息
- 需要实现完整的个人资料展示和编辑功能

**任务目标**
1. 创建个人资料页面组件（Profile.vue）
2. 实现以下功能模块：
   - 个人信息展示（姓名、用户名、邮箱、电话、头像等）
   - 个人信息编辑功能
   - 头像上传功能（支持本地文件选择和预览）
   - 密码修改功能
3. 在路由中注册个人资料页面
4. 修改Layout.vue中的handleCommand方法，实现页面跳转
5. 保持与现有UI风格一致（Element Plus组件库）

**技术要点**
- 使用el-form实现表单验证
- 文件上传使用el-upload组件
- 密码修改需要前端验证和后端接口支持

**相关文件**
- frontend/src/views/Profile/Profile.vue（需要新建）
- frontend/src/views/Layout.vue（修改handleCommand方法）

---

### 任务三：修复数据统计页面403权限错误

**问题描述**
- 管理员账户登录后访问数据统计页面时，弹出"没有权限访问数据"提示
- 控制台报错：api/statistics/users/trend?startTime=2025-01-01&endTime=2025-12-31:1 Failed to load resource: the server responded with a status of 403 (Forbidden)

**问题分析**
1. 检查StatisticsController中的权限验证逻辑
2. 分析SecurityConfig中的权限配置
3. 确认JWT认证过滤器的实现
4. 检查用户角色与权限映射关系

**修复方案**
1. **检查后端权限控制逻辑**：
   - 确认StatisticsController是否使用了@PreAuthorize注解
   - 检查是否有自定义权限检查方法
   - 分析SecurityFilterChain配置

2. **验证JWT认证流程**：
   - 确认JwtAuthenticationFilter正确解析token
   - 检查用户信息查询是否成功
   - 验证角色设置是否正确

3. **权限配置修正**：
   - 根据用户角色类型（1-管理员，2-教师，3-学生）配置访问权限
   - 数据统计接口应允许管理员和教师访问
   - 学生用户限制访问敏感统计数据

4. **前端错误处理优化**：
   - 改善403错误的用户提示信息
   - 添加更详细的错误日志输出

**相关文件**
- backend/src/main/java/com/teacher/assistant/config/SecurityConfig.java
- backend/src/main/java/com/teacher/assistant/config/JwtAuthenticationFilter.java
- backend/src/main/java/com/teacher/assistant/controller/StatisticsController.java
- frontend/src/views/Statistics/Statistics.vue（错误处理优化）

---

### 任务四：梳理即时通讯页面业务逻辑

**现状分析**
即时通讯页面(Message.vue)已实现基础功能，包括：
- 联系人列表展示和选择
- 实时WebSocket通信
- 聊天消息显示和发送
- 打字状态指示器

**业务逻辑梳理**

**1. 页面结构**
```
即时通讯页面
├── 联系人列表面板（左侧280px）
│   ├── 面板头部（标题 + 刷新按钮）
│   └── 联系人列表（可滚动区域）
│       ├── 联系人项
│       │   ├── 头像
│       │   ├── 姓名和状态
│       │   └── 未读消息数量徽章
│       └── 空状态提示
└── 聊天面板（右侧自适应）
    ├── 空状态（未选择联系人）
    └── 聊天界面
        ├── 聊天头部（选中联系人信息）
        ├── 消息列表（可滚动）
        │   ├── 接收消息（左对齐）
        │   └── 发送消息（右对齐）
        └── 输入区域（输入框 + 发送按钮）
```

**2. 核心业务流程**

**加载联系人列表**：
1. 组件初始化时调用loadContacts()
2. 通过getUsers API获取用户列表
3. 过滤掉当前登录用户
4. 显示联系人卡片，包含头像、姓名、在线状态

**选择联系人**：
1. 点击联系人项触发selectContact()
2. 记录选中的联系人
3. 清零未读消息计数
4. 加载与该联系人的聊天历史

**加载聊天历史**：
1. 调用getChatHistory(userId1, userId2)
2. 分页获取最近50条消息
3. 按时间顺序显示消息气泡
4. 滚动到底部

**发送消息**：
1. 用户输入文本并点击发送
2. 构建消息对象（senderId, receiverId, content等）
3. 同步调用sendMessage API存储消息
4. 立即在聊天界面显示发送的消息
5. 通过WebSocket发送实时消息

**接收实时消息**：
1. WebSocket监听'chat'事件
2. 判断消息来源
3. 如果是当前选中联系人，显示消息
4. 如果是其他联系人，更新未读计数
5. 自动滚动到底部

**WebSocket连接管理**：
1. 组件挂载时建立WebSocket连接
2. 连接地址：http://localhost:8080/ws/chat
3. 监听connect、chat、typing、disconnect事件
4. 组件卸载时断开连接

**3. 数据结构**

**联系人对象**：
```
{
  id: number,
  username: string,
  realName: string,
  avatar: string,
  status: number,  // 1-正常
  userType: number, // 1-管理员 2-教师 3-学生
  unreadCount: number  // 未读消息数
}
```

**消息对象**：
```
{
  id: number,
  senderId: number,
  receiverId: number,
  content: string,
  messageType: number,  // 1-文本
  status: number,       // 1-已发送
  createTime: datetime
}
```

**4. 待优化方向**

**功能增强**：
- 支持消息类型扩展（图片、文件、语音）
- 实现消息撤回功能
- 添加消息搜索功能
- 支持群组聊天
- 消息已读/未读状态同步

**性能优化**：
- 消息列表虚拟滚动（大量消息时）
- 聊天记录分页加载
- WebSocket断线重连机制
- 消息缓存策略

**用户体验**：
- 消息发送状态显示（发送中、已发送、失败）
- 输入框高度自适应
- 快捷键支持（Enter发送、Ctrl+Enter换行）
- 消息复制功能

**相关文件**
- frontend/src/views/Message/Message.vue（已有实现）
- backend/src/main/java/com/teacher/assistant/controller/MessageController.java
- backend/src/main/java/com/teacher/assistant/config/WebSocketConfig.java
- backend/src/main/java/com/teacher/assistant/config/WebSocketHandler.java

---

## 任务优先级

### 高优先级（立即处理）
1. 任务三：修复数据统计页面403权限错误（影响系统核心功能）

### 中优先级（近期处理）
2. 任务一：实现个人资料页面（用户功能完整性）

### 低优先级（长期规划）
3. 任务四：即时通讯页面业务逻辑梳理及优化（已有基础实现）

---

## 验收标准

### 任务一验收标准
- [ ] 个人资料页面可以正常打开
- [ ] 个人信息正确显示
- [ ] 个人信息可以编辑保存
- [ ] 头像可以上传更换
- [ ] 密码可以修改
- [ ] 表单验证正确工作
- [ ] UI风格与系统一致

### 任务二验收标准
- [x] 菜单项在深蓝色背景下清晰可见
- [x] 激活状态有明显的视觉区分
- [x] 鼠标悬停有平滑的过渡效果
- [x] 图标和文字颜色搭配协调
- [x] 整体视觉效果美观统一

### 任务三验收标准
- [ ] 管理员账户可以正常访问数据统计页面
- [ ] 不再出现403权限错误
- [ ] 统计数据正确显示
- [ ] 控制台无权限相关报错
- [ ] 其他用户角色权限符合预期

### 任务四验收标准
- [ ] 即时通讯业务流程清晰完整
- [ ] 联系人列表加载正确
- [ ] 消息发送接收正常
- [ ] WebSocket连接稳定
- [ ] 界面交互流畅
- [ ] 文档描述的业务逻辑准确

---

## 技术栈说明

**前端技术**
- Vue 3 + Composition API
- Element Plus UI组件库
- ECharts图表库
- Socket.IO客户端（WebSocket）

**后端技术**
- SpringBoot框架
- Spring Security安全框架
- MyBatis-Plus ORM
- MySQL数据库
- JWT Token认证

---

## 注意事项

1. **数据安全**：个人资料和密码修改涉及敏感信息，需要前后端双重验证
2. **权限控制**：确保不同用户角色只能访问授权的功能和数据
3. **WebSocket稳定性**：即时通讯依赖WebSocket，需要处理断线重连和异常情况
4. **UI一致性**：所有修改应保持与现有系统的视觉风格一致
5. **向后兼容**：确保修改不影响现有功能的正常运行

---

## 附录：相关文档链接

- 项目需求文档.md - 详细的功能需求说明
- 业务流程与产品设计.md - 业务流程和设计规范
- CLAUDE.md - 项目开发指南和技术规范
