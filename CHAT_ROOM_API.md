# 聊天室功能说明

## 概述
聊天室是餐厅级别的群聊功能，用户需要通过商家设置的验证码才能进入聊天室。聊天室支持文本消息，使用WebSocket进行实时通信。

## API 接口

### 通过验证码加入聊天室
- **URL**: `POST /api/v1/chat-rooms/join`
- **方法**: POST
- **需要认证**: 是
- **参数**:
  - `restaurantId` (请求参数): 餐厅ID
  - `verificationCode` (请求参数): 验证码
- **说明**: 用户ID从JWT token中自动获取

### 请求示例
```bash
curl -X POST "http://localhost:8080/api/v1/chat-rooms/join" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d "restaurantId=1&verificationCode=123456"
```

### 获取聊天室信息
- **URL**: `GET /api/v1/chat-rooms/{roomId}`
- **方法**: GET
- **参数**:
  - `roomId` (路径参数): 聊天室ID

### 获取餐厅聊天室
- **URL**: `GET /api/v1/chat-rooms/restaurant/{restaurantId}`
- **方法**: GET
- **参数**:
  - `restaurantId` (路径参数): 餐厅ID

### 发送聊天室消息
- **URL**: `POST /api/v1/chat-rooms/{roomId}/messages`
- **方法**: POST
- **需要认证**: 是
- **参数**:
  - `roomId` (路径参数): 聊天室ID
  - `content` (请求参数): 消息内容
- **说明**: 只支持文本消息，用户ID从JWT token中自动获取

### 请求示例
```bash
curl -X POST "http://localhost:8080/api/v1/chat-rooms/1/messages" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d "content=大家好！"
```

### 获取聊天室消息列表
- **URL**: `GET /api/v1/chat-rooms/{roomId}/messages`
- **方法**: GET
- **参数**:
  - `roomId` (路径参数): 聊天室ID
  - `page` (查询参数): 页码，默认0
  - `size` (查询参数): 每页大小，默认50

### 获取聊天室成员列表
- **URL**: `GET /api/v1/chat-rooms/{roomId}/members`
- **方法**: GET
- **参数**:
  - `roomId` (路径参数): 聊天室ID

### 离开聊天室
- **URL**: `POST /api/v1/chat-rooms/{roomId}/leave`
- **方法**: POST
- **需要认证**: 是
- **参数**:
  - `roomId` (路径参数): 聊天室ID
- **说明**: 用户ID从JWT token中自动获取

## WebSocket 接口

### 发送聊天室消息
- **目的地**: `/app/chat-room.sendMessage`
- **请求格式**:
  ```json
  {
    "roomId": 1,
    "content": "大家好！"
  }
  ```

### 加入聊天室
- **目的地**: `/app/chat-room.join`
- **请求格式**:
  ```json
  {
    "roomId": 1
  }
  ```

### 离开聊天室
- **目的地**: `/app/chat-room.leave`
- **请求格式**:
  ```json
  {
    "roomId": 1
  }
  ```

### 订阅聊天室消息
- **目的地**: `/topic/chat-room/{roomId}`

### 订阅个人通知
- **目的地**: `/user/{userId}/queue/notifications`

## 数据库结构

### 聊天室表 (chat_rooms)
- `id`: 聊天室ID
- `restaurant_id`: 餐厅ID
- `name`: 聊天室名称
- `verification_code`: 验证码
- `status`: 聊天室状态
- `last_message`: 最后一条消息
- `last_message_time`: 最后一条消息时间
- `online_user_count`: 在线用户数量

### 聊天室成员表 (chat_room_members)
- `id`: 成员ID
- `room_id`: 聊天室ID
- `user_id`: 用户ID
- `joined_at`: 加入时间
- `is_online`: 是否在线

### 聊天室消息表 (chat_room_messages)
- `id`: 消息ID
- `room_id`: 聊天室ID
- `sender_id`: 发送者ID
- `content`: 消息内容
- `message_type`: 消息类型
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 注意事项
1. 加入聊天室需要用户登录认证和正确的验证码
2. 每个用户只能加入一次同一个聊天室
3. 聊天室只支持文本消息
4. 使用WebSocket进行实时消息传输
5. 用户离开聊天室后会自动设置为离线状态
6. 聊天室会显示在线用户数量
7. 消息列表包含发送者信息（用户名和头像）