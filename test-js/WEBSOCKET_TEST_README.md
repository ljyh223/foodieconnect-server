# WebSocket聊天室测试脚本

这个目录包含两个测试脚本，用于测试TableTalk应用的WebSocket聊天室功能。

## 安装依赖

```bash
npm install
```

或者手动安装：

```bash
npm install sockjs-client @stomp/stompjs axios ws
```

## 配置

在运行测试之前，请确保：

1. **TableTalk应用正在运行**：
   ```bash
   mvn spring-boot:run
   ```

2. **数据库中有测试用户**：
   - 默认测试用户：`user1@example.com`
   - 密码：`password123`
   - 或者修改`websocket-test.js`中的登录凭据

3. **聊天室存在**：
   - 默认测试聊天室ID：`1`
   - 验证码：`888888`
   - 或者修改脚本中的配置

## 测试脚本

### 1. SockJS + STOMP测试（适用于浏览器环境）

运行使用SockJS的测试：

```bash
npm test
# 或者
node websocket-test.js
```

### 2. 原生WebSocket测试（适用于Flutter等原生WebSocket环境）

运行使用原生WebSocket的测试：

```bash
npm run flutter-test
```

### 交互式测试模式

进入交互式模式，可以手动发送命令：

```bash
npm run interactive
# 或者
node websocket-test.js --interactive
# 或者
node flutter-websocket-test.js --interactive
```

可用命令：
- `send <message>` - 发送消息到聊天室
- `join` - 加入聊天室
- `leave` - 离开聊天室
- `quit` - 退出程序

## 测试步骤

两种测试脚本都会执行以下步骤：
1. 登录获取JWT token
2. 连接WebSocket
3. 订阅聊天室消息和通知
4. 加入聊天室
5. 发送测试消息
6. 离开聊天室
7. 断开连接

## 测试脚本功能

### 主要功能

1. **JWT认证**：
   - 自动登录获取JWT token
   - 在所有WebSocket请求中包含认证头

2. **连接管理**：
   - SockJS + STOMP协议（浏览器环境）
   - 原生WebSocket协议（Flutter/移动应用）
   - 支持连接状态监控
   - 优雅的连接断开

3. **消息订阅**：
   - 订阅聊天室消息：`/topic/chat-room/{roomId}`
   - 订阅个人通知：`/user/{userId}/queue/notifications`
   - 订阅错误消息：`/user/{userId}/queue/errors`

4. **消息发送**：
   - 发送聊天消息：`/app/chat-room.sendMessage`
   - 加入聊天室：`/app/chat-room.join`
   - 离开聊天室：`/app/chat-room.leave`

5. **错误处理**：
   - 连接错误处理
   - 消息发送失败处理
   - 详细的错误日志

### 输出示例

```
=== 步骤1: 登录获取JWT token ===
登录成功！用户ID: 1
JWT Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

=== 步骤2: 连接WebSocket ===
WebSocket连接成功！

=== 步骤3: 订阅消息和通知 ===
订阅完成！

=== 步骤4: 加入聊天室 ===
已发送加入聊天室请求，房间ID: 1

--- 收到个人通知 ---
通知内容: {"type":"ROOM_JOINED","roomId":1}

=== 步骤5: 发送测试消息 ===
已发送测试消息

--- 收到聊天室消息 ---
消息内容: {"id":123,"roomId":1,"senderId":1,"content":"这是一条来自Node.js测试脚本的测试消息...","messageType":"TEXT","senderName":"张三","senderAvatar":"/uploads/avatar.jpg","timestamp":"2025-11-01T11:30:00"}
```

## 故障排除

### 常见问题

1. **连接失败**：
   - 检查TableTalk应用是否在8080端口运行
   - 确认防火墙设置
   - 检查WebSocket端点URL是否正确

2. **认证失败**：
   - 确认用户凭据正确
   - 检查JWT token是否有效
   - 确认用户在数据库中存在

3. **消息发送失败**：
   - 确认用户已加入聊天室
   - 检查验证码是否正确
   - 查看服务器日志获取详细错误信息

4. **依赖安装问题**：
   ```bash
   # 清除npm缓存
   npm cache clean --force
   
   # 删除node_modules和package-lock.json
   rm -rf node_modules package-lock.json
   
   # 重新安装
   npm install
   ```

### 调试技巧

1. **启用详细日志**：
   - SockJS脚本会自动显示STOMP调试信息
   - 原生WebSocket脚本会显示连接和消息详情
   - 服务器端查看`application.properties`中的日志配置

2. **检查网络连接**：
   ```bash
   # 测试HTTP连接
   curl http://localhost:8080/api/v1/auth/login
   
   # 测试WebSocket端点
   curl -i -N -H "Connection: Upgrade" \
        -H "Upgrade: websocket" \
        -H "Sec-WebSocket-Key: test" \
        -H "Sec-WebSocket-Version: 13" \
        http://localhost:8080/api/v1/ws/chat
   ```

3. **浏览器测试**：
   - 打开浏览器开发者工具
   - 访问WebSocket测试页面
   - 查看Network面板中的WebSocket连接

4. **Flutter调试**：
   - 使用Flutter的开发者工具查看网络请求
   - 检查WebSocket连接状态和消息流
   - 使用日志输出调试连接问题

## 协议说明

### SockJS + STOMP协议（浏览器环境）
- 使用SockJS库提供WebSocket兼容性
- 支持自动重连和心跳检测
- 适合在浏览器环境中运行

### 原生WebSocket协议（Flutter/移动应用）
- 使用浏览器原生WebSocket API
- 更轻量级，性能更好
- 适合在Flutter、React Native等移动应用中运行

## 自定义配置

可以修改脚本中的配置对象：

```javascript
const config = {
  serverUrl: 'http://localhost:8080/api/v1',
  wsUrl: 'ws://localhost:8080/api/v1/ws/chat',
  apiUrl: 'http://localhost:8080/api/v1',
  loginUrl: '/auth/login',
  testRoomId: 1, // 测试聊天室ID
  testVerificationCode: '888888' // 测试验证码
};
```

## 扩展功能

可以基于此脚本扩展以下功能：

1. **多用户测试**：模拟多个用户同时在线
2. **压力测试**：测试大量消息发送
3. **消息历史测试**：测试消息分页获取
4. **文件上传测试**：测试图片消息发送
5. **连接重连测试**：测试网络中断后的自动重连

## 许可证

MIT License