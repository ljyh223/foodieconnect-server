# Flutter WebSocket 测试工具

这是一个用于测试 Flutter WebSocket 聊天室功能的 Node.js 脚本。

## 功能

- 支持两个测试账号的登录
- 自动测试模式和交互式测试模式
- WebSocket 连接和消息处理
- 聊天室加入、发送消息、离开聊天室等功能

## 使用方法

### 基本用法

```bash
# 使用默认账号1进行自动测试
node flutter-websocket-test.js

# 使用账号2进行自动测试
node flutter-websocket-test.js --account=2

# 启动交互式模式
node flutter-websocket-test.js --interactive

# 使用账号2启动交互式模式
node flutter-websocket-test.js --account=2 --interactive

# 显示帮助信息
node flutter-websocket-test.js --help
```

### 交互式模式命令

在交互式模式下，可以使用以下命令：

- `send <message>` - 发送消息到聊天室
- `join` - 加入聊天室
- `leave` - 离开聊天室
- `quit` - 退出程序

## 测试账号

当前配置了两个测试账号：

- **账号1**: ljyh223@163.com
- **账号2**: test2@example.com

## 配置

可以在脚本中的 `config` 对象中修改以下配置：

- `serverUrl`: WebSocket 服务器地址
- `apiUrl`: HTTP API 服务器地址
- `testRoomId`: 测试聊天室 ID
- `accounts`: 测试账号信息

## 示例输出

```
=== 步骤1: 登录获取JWT token ===
使用账号1登录: ljyh223@163.com
账号1登录成功！用户ID: 1
JWT Token: eyJhbGciOiJIUzI1NiJ9...

=== 步骤2: 连接WebSocket ===
WebSocket连接成功！

--- 认证成功 ---
用户信息: {
  "id": 1,
  "email": "ljyh223@163.com",
  ...
}

=== 步骤4: 加入聊天室 ===
账号1已发送加入聊天室请求，房间ID: 1

--- 成功加入聊天室 ---
房间信息: {
  "roomId": 1,
  "userId": 1,
  ...
}

=== 步骤5: 发送测试消息 ===
账号1已发送测试消息

--- 收到聊天室消息 ---
消息内容: {
  "id": 123,
  "content": "这是一条来自账号1的测试消息 - 14:30:25",
  "userId": 1,
  ...
}
```

## 注意事项

1. 确保 WebSocket 服务器正在运行并且可访问
2. 测试账号需要在系统中存在且密码正确
3. 如果使用自定义账号，请修改 `config.accounts` 中的配置
4. 交互式模式需要先成功连接 WebSocket 才能使用命令