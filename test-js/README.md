# Flutter WebSocket 聊天室测试

这个项目包含用于测试Flutter WebSocket聊天室功能的Node.js脚本。

## 功能特性

- 基于验证码的临时JWT token认证
- STOMP协议WebSocket连接
- 在线用户管理
- 实时消息传递
- 支持多用户测试

## 系统架构

### 认证流程

1. **用户登录**：使用用户名和密码获取标准JWT token
2. **验证码验证**：使用验证码验证聊天室并获取临时JWT token（用于WebSocket连接）
3. **WebSocket连接**：使用临时token建立STOMP WebSocket连接
4. **消息传递**：通过WebSocket发送和接收实时消息

### 在线用户管理

- 用户连接时自动添加到在线用户表
- 用户断开连接时自动从在线用户表移除
- 实时更新用户最后活动时间
- 维护聊天室在线用户数量

## 使用方法

### 1. 安装依赖

```bash
cd test-js
npm install
```

### 2. 启动后端服务

确保Spring Boot应用正在运行在 `http://localhost:8080`

### 3. 运行测试脚本

#### 单用户测试

```bash
# 使用账号1测试
node flutter-websocket-test.js --account=1

# 使用账号2测试
node flutter-websocket-test.js --account=2
```

#### 多用户测试

```bash
# 在两个不同的终端中分别运行
# 终端1
node flutter-websocket-test.js --account=1

# 终端2
node flutter-websocket-test.js --account=2
```

## 测试账号

| 账号 | 邮箱 | 密码 | 说明 |
|------|------|------|------|
| 账号1 | 3439426154@qq.com | 123456 | 测试用户1 |
| 账号2 | 3176994988@qq.com | 123456 | 测试用户2 |

## 测试流程

测试脚本会按以下步骤执行：

1. **登录获取JWT token**
   - 使用指定账号登录系统
   - 获取标准JWT token用于后续API调用

2. **验证聊天室验证码并获取临时token**
   - 使用验证码验证聊天室
   - 获取临时JWT token用于WebSocket连接

3. **连接STOMP WebSocket**
   - 使用临时token建立WebSocket连接
   - 启用心跳机制保持连接

4. **订阅聊天室主题**
   - 订阅聊天室消息主题
   - 订阅用户通知队列

5. **加入聊天室**
   - 发送加入聊天室请求
   - 更新用户在线状态

6. **发送测试消息**
   - 发送测试消息到聊天室
   - 接收其他用户的消息

7. **离开聊天室**
   - 发送离开聊天室请求
   - 更新用户离线状态

8. **断开连接**
   - 断开WebSocket连接
   - 清理在线用户记录

## API接口

### 验证聊天室验证码

```http
POST /api/v1/chat-rooms/verify
Authorization: Bearer <jwt_token>
Content-Type: application/json

参数:
- restaurantId: 餐厅ID
- verificationCode: 验证码

响应:
{
  "success": true,
  "data": {
    "chatRoom": {
      "id": 1,
      "name": "川味轩聊天室",
      "verificationCode": "888888"
    },
    "tempToken": "临时JWT token",
    "expiresIn": 1800000
  }
}
```

### WebSocket连接

```javascript
// 连接地址
const socket = new SockJS('http://localhost:8080/api/v1/ws/chat');

// 连接配置
const headers = {
    'Authorization': 'Bearer <temp_token>',
    'accept-version': '1.1,1.0',
    'heart-beat': '10000,10000'
};

// 建立连接
stompClient.connect(headers, onConnected, onError);
```

## 消息格式

### 加入聊天室

```javascript
// 发送
{
    "roomId": 1
}

// 接收通知
{
    "success": true,
    "data": {
        "type": "ROOM_JOINED",
        "roomId": 1
    }
}
```

### 发送消息

```javascript
// 发送
{
    "roomId": 1,
    "content": "消息内容"
}

// 接收消息
{
    "success": true,
    "data": {
        "id": 1,
        "roomId": 1,
        "senderId": 3,
        "content": "消息内容",
        "messageType": "TEXT",
        "senderName": "用户名",
        "senderAvatar": "头像URL",
        "timestamp": "2025-11-02T19:18:56"
    }
}
```

## 故障排除

### 常见问题

1. **连接失败**
   - 检查后端服务是否启动
   - 确认端口8080未被占用
   - 检查防火墙设置

2. **认证失败**
   - 确认验证码正确（默认：888888）
   - 检查用户账号密码
   - 确认JWT token未过期

3. **消息无法发送**
   - 确认用户已加入聊天室
   - 检查用户是否为聊天室成员
   - 查看后端日志获取详细错误信息

### 调试模式

启用详细日志输出：

```bash
# 使用调试脚本
node debug-test.js --account=1

# 或使用详细模式
node flutter-websocket-test.js --account=1 --verbose
```

## 数据库表结构

### 在线用户表 (online_users)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| room_id | bigint | 聊天室ID |
| session_id | varchar(255) | WebSocket会话ID |
| connected_at | timestamp | 连接时间 |
| last_active_at | timestamp | 最后活动时间 |

## 开发说明

### 添加新测试账号

1. 在 `flutter-websocket-test.js` 中的 `testAccounts` 对象添加新账号
2. 确保用户已在数据库中存在
3. 使用 `--account=N` 参数指定账号

### 自定义测试流程

1. 修改 `main()` 函数中的步骤顺序
2. 添加自定义消息发送逻辑
3. 实现特定的测试场景

## 许可证

MIT License