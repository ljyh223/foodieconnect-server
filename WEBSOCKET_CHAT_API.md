# WebSocket聊天室API文档

## 概述

本文档描述了TableTalk应用中聊天室功能的WebSocket实现，前端开发人员可以使用此文档来集成实时聊天功能。

## 技术栈

- **协议**: WebSocket + STOMP
- **框架**: Spring WebSocket + SockJS
- **消息格式**: JSON

## 连接配置

### WebSocket端点

```
ws://localhost:8080/api/v1/ws/chat
```

### 支持的连接方式

1. **带SockJS支持的连接**（推荐，支持浏览器兼容性）:
   ```
   http://localhost:8080/api/v1/ws/chat
   ```

2. **纯WebSocket连接**:
   ```
   ws://localhost:8080/api/v1/ws/chat
   ```

## 认证

所有WebSocket连接都需要在连接头中包含JWT token：

```javascript
const headers = {
  'Authorization': 'Bearer ' + jwtToken
};
```

## 消息类型

### 1. 发送聊天室消息

**目的地**: `/app/chat-room.sendMessage`

**请求格式**:
```json
{
  "roomId": 1,
  "content": "这是一条测试消息"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123,
    "roomId": 1,
    "senderId": 456,
    "content": "这是一条测试消息",
    "messageType": "TEXT",
    "senderName": "张三",
    "senderAvatar": "/uploads/avatar.jpg",
    "timestamp": "2025-11-01T11:30:00"
  }
}
```

### 2. 加入聊天室

**目的地**: `/app/chat-room.join`

**请求格式**:
```json
{
  "roomId": 1
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "type": "ROOM_JOINED",
    "roomId": 1
  }
}
```

### 3. 离开聊天室

**目的地**: `/app/chat-room.leave`

**请求格式**:
```json
{
  "roomId": 1
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "type": "ROOM_LEFT",
    "roomId": 1
  }
}
```

## 订阅消息

### 1. 订阅聊天室消息

**订阅路径**: `/topic/chat-room/{roomId}`

**示例**:
```javascript
stompClient.subscribe('/topic/chat-room/1', function(message) {
  const response = JSON.parse(message.body);
  console.log('收到新消息:', response.data);
});
```

**接收到的消息格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123,
    "roomId": 1,
    "senderId": 456,
    "content": "这是一条测试消息",
    "messageType": "TEXT",
    "senderName": "张三",
    "senderAvatar": "/uploads/avatar.jpg",
    "timestamp": "2025-11-01T11:30:00"
  }
}
```

### 2. 订阅个人通知

**订阅路径**: `/user/{userId}/queue/notifications`

**示例**:
```javascript
stompClient.subscribe('/user/456/queue/notifications', function(message) {
  const response = JSON.parse(message.body);
  console.log('收到通知:', response.data);
});
```

**接收到的通知类型**:
- `ROOM_JOINED`: 成功加入聊天室
- `ROOM_LEFT`: 成功离开聊天室

### 3. 订阅错误消息

**订阅路径**: `/user/{userId}/queue/errors`

**示例**:
```javascript
stompClient.subscribe('/user/456/queue/errors', function(message) {
  const response = JSON.parse(message.body);
  console.error('收到错误:', response);
});
```

## 消息类型枚举

- `TEXT`: 文本消息
- `IMAGE`: 图片消息
- `SYSTEM`: 系统消息

## 前端集成示例

### JavaScript (使用SockJS + STOMP)

```javascript
// 1. 创建连接
const socket = new SockJS('http://localhost:8080/api/v1/ws/chat');
const stompClient = Stomp.over(socket);

// 2. 设置认证头
const headers = {
  'Authorization': 'Bearer ' + jwtToken
};

// 3. 连接成功回调
stompClient.connect(headers, function(frame) {
  console.log('连接成功: ' + frame);
  
  // 4. 订阅聊天室消息
  stompClient.subscribe('/topic/chat-room/1', function(message) {
    const response = JSON.parse(message.body);
    displayMessage(response.data);
  });
  
  // 5. 订阅个人通知
  stompClient.subscribe('/user/' + userId + '/queue/notifications', function(message) {
    const response = JSON.parse(message.body);
    handleNotification(response.data);
  });
  
  // 6. 订阅错误消息
  stompClient.subscribe('/user/' + userId + '/queue/errors', function(message) {
    const response = JSON.parse(message.body);
    handleError(response);
  });
  
}, function(error) {
  console.error('连接失败: ' + error);
});

// 7. 发送消息
function sendMessage(roomId, content) {
  const message = {
    roomId: roomId,
    content: content
  };
  stompClient.send('/app/chat-room.sendMessage', headers, JSON.stringify(message));
}

// 8. 加入聊天室
function joinRoom(roomId) {
  const payload = {
    roomId: roomId
  };
  stompClient.send('/app/chat-room.join', headers, JSON.stringify(payload));
}

// 9. 离开聊天室
function leaveRoom(roomId) {
  const payload = {
    roomId: roomId
  };
  stompClient.send('/app/chat-room.leave', headers, JSON.stringify(payload));
}
```

### React Hook示例

```javascript
import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export const useWebSocket = (jwtToken, userId) => {
  const [messages, setMessages] = useState([]);
  const [connected, setConnected] = useState(false);
  const stompClientRef = useRef(null);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/api/v1/ws/chat');
    const stompClient = Stomp.over(socket);
    stompClientRef.current = stompClient;

    const headers = {
      'Authorization': 'Bearer ' + jwtToken
    };

    stompClient.connect(headers, (frame) => {
      setConnected(true);
      console.log('WebSocket连接成功');

      // 订阅聊天室消息
      stompClient.subscribe('/topic/chat-room/1', (message) => {
        const response = JSON.parse(message.body);
        setMessages(prev => [...prev, response.data]);
      });

      // 订阅个人通知
      stompClient.subscribe(`/user/${userId}/queue/notifications`, (message) => {
        const response = JSON.parse(message.body);
        console.log('收到通知:', response.data);
      });

    }, (error) => {
      console.error('WebSocket连接失败:', error);
      setConnected(false);
    });

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.disconnect();
      }
    };
  }, [jwtToken, userId]);

  const sendMessage = (roomId, content) => {
    if (stompClientRef.current && connected) {
      const message = { roomId, content };
      stompClientRef.current.send(
        '/app/chat-room.sendMessage',
        { 'Authorization': 'Bearer ' + jwtToken },
        JSON.stringify(message)
      );
    }
  };

  const joinRoom = (roomId) => {
    if (stompClientRef.current && connected) {
      stompClientRef.current.send(
        '/app/chat-room.join',
        { 'Authorization': 'Bearer ' + jwtToken },
        JSON.stringify({ roomId })
      );
    }
  };

  const leaveRoom = (roomId) => {
    if (stompClientRef.current && connected) {
      stompClientRef.current.send(
        '/app/chat-room.leave',
        { 'Authorization': 'Bearer ' + jwtToken },
        JSON.stringify({ roomId })
      );
    }
  };

  return { messages, connected, sendMessage, joinRoom, leaveRoom };
};
```

## 错误处理

### 常见错误码

- `MESSAGE_SEND_FAILED`: 消息发送失败
- `INVALID_VERIFICATION_CODE`: 验证码无效
- `CHAT_ROOM_INACTIVE`: 聊天室未激活
- `NOT_ROOM_MEMBER`: 不是聊天室成员
- `CHAT_MESSAGE_TOO_LONG`: 消息过长（超过500字符）

### 错误响应格式

```json
{
  "code": 500,
  "message": "MESSAGE_SEND_FAILED",
  "data": "消息发送失败: 具体错误信息"
}
```

## 注意事项

1. **认证**: 所有WebSocket请求都需要在头部包含有效的JWT token
2. **消息长度**: 消息内容不能超过500个字符
3. **权限**: 用户必须是聊天室成员才能发送消息
4. **连接管理**: 建议实现自动重连机制
5. **资源清理**: 组件卸载时记得断开WebSocket连接

## 调试技巧

1. 开启浏览器开发者工具的Network面板，查看WebSocket连接
2. 在控制台查看连接状态和消息日志
3. 检查JWT token是否有效且未过期
4. 确认服务器端口和路径配置正确

## 端口配置

如果8080端口被占用，可以修改`application.properties`文件中的端口配置：

```properties
server.port=8081
```

然后相应地更新前端连接URL：

```javascript
const socket = new SockJS('http://localhost:8081/api/v1/ws/chat');