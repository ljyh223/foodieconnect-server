
# TableTalk API 接口规范文档

## 概述

本文档详细定义了 TableTalk 后端系统的所有 API 接口规范，包括请求格式、响应格式、错误码等。

## 通用规范

### 基础信息
- **API 版本**: v1
- **基础路径**: `/api/v1`
- **字符编码**: UTF-8
- **时间格式**: ISO 8601 (YYYY-MM-DDTHH:mm:ssZ)

### 请求头
```http
Content-Type: application/json
Authorization: Bearer {jwt_token}
Accept: application/json
```

### 响应格式
#### 成功响应
```json
{
  "success": true,
  "data": {
    // 具体数据
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 分页响应
```json
{
  "success": true,
  "data": {
    "content": [],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0,
    "size": 20,
    "first": true,
    "last": false
  }
}
```

#### 错误响应
```json
{
  "success": false,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "用户不存在",
    "details": "用户ID: 123 不存在",
    "timestamp": "2024-01-15T10:30:00Z"
  }
}
```

## 认证模块

### 用户登录
```http
POST /api/v1/auth/login
```

**请求体:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "displayName": "张三",
      "phone": "13800138000",
      "avatarUrl": "https://example.com/avatar.jpg",
      "status": "ACTIVE"
    }
  }
}
```

### 用户注册
```http
POST /api/v1/auth/register
```

**请求体:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "displayName": "张三",
  "phone": "13800138000"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "displayName": "张三",
    "phone": "13800138000",
    "status": "ACTIVE"
  }
}
```

### 刷新令牌
```http
POST /api/v1/auth/refresh
Authorization: Bearer {refresh_token}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "token": "new_access_token",
    "refreshToken": "new_refresh_token"
  }
}
```

### 获取当前用户信息
```http
GET /api/v1/auth/me
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "displayName": "张三",
    "phone": "13800138000",
    "avatarUrl": "https://example.com/avatar.jpg",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

## 餐厅模块

### 获取餐厅列表
```http
GET /api/v1/restaurants
```

**查询参数:**
- `page` (可选): 页码，默认 0
- `size` (可选): 每页大小，默认 20
- `type` (可选): 餐厅类型过滤
- `lat` (可选): 纬度，用于距离计算
- `lng` (可选): 经度，用于距离计算
- `radius` (可选): 搜索半径(米)，默认 5000
- `keyword` (可选): 关键词搜索
- `sort` (可选): 排序字段 (rating, distance, reviewCount)

**响应:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "川味轩",
        "type": "川菜",
        "distance": "500m",
        "description": "正宗川菜，麻辣鲜香，环境优雅",
        "rating": 4.8,
        "reviewCount": 128,
        "isOpen": true,
        "avatar": "川",
        "address": "市中心街道123号",
        "phone": "(021) 1234-5678",
        "hours": "10:00 - 22:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0,
    "size": 20
  }
}
```

### 获取餐厅详情
```http
GET /api/v1/restaurants/{id}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "川味轩",
    "type": "川菜",
    "distance": "500m",
    "description": "正宗川菜，麻辣鲜香，环境优雅",
    "address": "市中心街道123号",
    "phone": "(021) 1234-5678",
    "hours": "10:00 - 22:00",
    "rating": 4.8,
    "reviewCount": 128,
    "isOpen": true,
    "avatar": "川",
    "recommendedDishes": [
      {
        "name": "宫保鸡丁",
        "description": "经典川菜，麻辣鲜香",
        "price": 48.0
      },
      {
        "name": "麻婆豆腐", 
        "description": "麻辣鲜香，豆腐嫩滑",
        "price": 32.0
      }
    ],
    "features": [
      "免费WiFi",
      "包间预订",
      "停车位",
      "外卖服务"
    ]
  }
}
```

### 搜索餐厅
```http
GET /api/v1/restaurants/search
```

**查询参数:**
- `q` (必需): 搜索关键词
- `page` (可选): 页码
- `size` (可选): 每页大小

**响应:** 同餐厅列表响应格式

## 评论模块

### 获取餐厅评论
```http
GET /api/v1/restaurants/{restaurantId}/reviews
```

**查询参数:**
- `page` (可选): 页码，默认 0
- `size` (可选): 每页大小，默认 10
- `sort` (可选): 排序方式 (latest, highest_rating, lowest_rating)

**响应:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userName": "李四",
        "userAvatar": "https://example.com/avatar.jpg",
        "rating": 5,
        "comment": "菜品非常好吃，服务也很周到，强烈推荐！",
        "date": "2024-01-15T10:30:00Z",
        "helpfulCount": 5,
        "reply": {
          "content": "感谢您的评价，我们会继续努力！",
          "date": "2024-01-15T11:00:00Z"
        }
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "currentPage": 0
  }
}
```

### 发表评论
```http
POST /api/v1/restaurants/{restaurantId}/reviews
Authorization: Bearer {token}
```

**请求体:**
```json
{
  "rating": 5,
  "comment": "菜品非常好吃，服务也很周到"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "rating": 5,
    "comment": "菜品非常好吃，服务也很周到",
    "date": "2024-01-15T10:30:00Z"
  }
}
```

### 点赞评论
```http
POST /api/v1/reviews/{reviewId}/helpful
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "helpfulCount": 6
  }
}
```

## 店员模块

### 获取餐厅店员列表
```http
GET /api/v1/restaurants/{restaurantId}/staff
```

**响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "小张",
      "position": "服务员",
      "status": "在线",
      "experience": "3年",
      "rating": 4.9,
      "avatar": "张",
      "skills": ["菜品推荐", "过敏咨询", "口味调整"],
      "languages": ["中文", "英文"]
    }
  ]
}
```

### 获取店员详情
```http
GET /api/v1/staff/{staffId}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "小张",
    "position": "服务员",
    "status": "在线",
    "experience": "3年",
    "rating": 4.9,
    "avatar": "张",
    "skills": ["菜品推荐", "过敏咨询", "口味调整", "订座服务"],
    "languages": ["中文", "英文"],
    "description": "热情周到的服务，熟悉各类菜品特点",
    "reviews": [
      {
        "id": 1,
        "userName": "王五",
        "content": "服务态度很好，推荐菜品很专业",
        "rating": 5.0,
        "date": "2024-01-10T14:30:00Z"
      }
    ]
  }
}
```

## 聊天模块

### 获取聊天会话列表
```http
GET /api/v1/chat/sessions
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "restaurantId": 1,
      "restaurantName": "川味轩",
      "staffId": 1,
      "staffName": "小张",
      "staffAvatar": "张",
      "lastMessage": "您好！很高兴为您服务",
      "lastMessageTime": "2024-01-15T10:30:00Z",
      "unreadCount": 2,
      "status": "ACTIVE"
    }
  ]
}
```

### 创建聊天会话
```http
POST /api/v1/chat/sessions
Authorization: Bearer {token}
```

**请求体:**
```json
{
  "restaurantId": 1,
  "staffId": 1
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "restaurantId": 1,
    "staffId": 1,
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 获取聊天消息
```http
GET /api/v1/chat/sessions/{sessionId}/messages
Authorization: Bearer {token}
```

**查询参数:**
- `page` (可选): 页码，默认 0
- `size` (可选): 每页大小，默认 50

**响应:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "senderId": "staff_1",
        "senderType": "STAFF",
        "content": "您好！很高兴为您服务。请问有什么可以帮助您的吗？",
        "timestamp": "2024-01-15T10:25:00Z",
        "isSentByUser": false,
        "staffName": "小张",
        "staffAvatar": "张"
      },
      {
        "id": 2,
        "senderId": "user_1", 
        "senderType": "USER",
        "content": "你好，我想了解一下你们店的特色菜品",
        "timestamp": "2024-01-15T10:26:00Z",
        "isSentByUser": true
      }
    ],
    "totalElements": 25
  }
}
```

### 发送消息
```http
POST /api/v1/chat/sessions/{sessionId}/messages
Authorization: Bearer {token}
```

**请求体:**
```json
{
  "content": "我想预订今晚的座位",
  "messageType": "TEXT"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "content": "我想预订今晚的座位",
    "timestamp": "2024-01-15T10:30:00Z",
    "isSentByUser": true
  }
}
```

### 标记消息为已读
```http
PUT /api/v1/chat/sessions/{sessionId}/read
Authorization: Bearer {token}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "unreadCount": 0
  }
}
```

## 用户模块

### 更新用户信息
```http
PUT /api/v1/users/profile
Authorization: Bearer {token}
```

**请求体:**
```json
{
  "displayName": "新的显示名称",
  "phone": "13800138000",
  "avatarUrl": "https://example.com/new-avatar.jpg"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "displayName": "新的显示名称",
    "phone": "13800138000",
    "avatarUrl": "https://example.com/new-avatar.jpg",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

### 修改密码
```http
PUT /api/v1/users/password
Authorization: Bearer {token}
```

**请求体:**
```json
{
  "oldPassword": "old_password",
  "newPassword": "new_password"
}
```

**响应:**
```json
{
  "success": true,
  "data": {
    "message": "密码修改成功"
  }
}
```

## 错误码说明

### 认证相关错误
| 错误码 | HTTP状态码 | 描述 |
|--------|------------|------|
| AUTH_INVALID_TOKEN | 401 | 无效的认证令牌 |
| AUTH_TOKEN_EXPIRED | 401 | 令牌已过期 |
| AUTH_ACCESS_DENIED | 403 | 访问被拒绝 |
| AUTH_INVALID_CREDENTIALS | 401 | 无效的登录凭据 |

### 用户相关错误
| 错误码 | HTTP状态码 | 描述 |
|--------|------------|------|
| USER_NOT_FOUND | 404 | 用户不存在 |
| USER_EMAIL_EXISTS | 409 | 邮箱已存在 |
| USER_PHONE_EXISTS | 409 | 手机号已存在 |
| USER_INACTIVE | 403 | 用户账户未激活 |

### 餐厅相关错误
| 错误码 | HTTP状态码 | 描述 |
|--------|------------|------|
| RESTAURANT_NOT_FOUND | 404 | 餐厅不存在 |
| RESTAURANT_CLOSED | 400 | 餐厅已关闭 |
| RESTAURANT_REVIEW_EXISTS | 409 | 用户已对该餐厅发表过评论 |

### 聊天相关错误
| 错误码 | HTTP状态码 | 描述 |
|--------|------------|------|
| CHAT_SESSION_NOT_FOUND | 404 | 聊天会话不存在 |
| CHAT_STAFF_OFFLINE | 400 | 店员不在线 |
| CHAT_MESSAGE_TOO_LONG | 400 | 消息过长 |
| CHAT_SESSION_CLOSED | 400 | 聊天会话已关闭 |

### 系统错误
| 错误码 | HTTP状态码 | 描述 |
|--------|------------|------|
| INTERNAL_SERVER_ERROR | 500 | 服务器内部错误 |
| SERVICE_UNAVAILABLE | 503 | 服务暂时不可用 |
| RATE_LIMIT_EXCEEDED | 429 | 请求频率超限 |

## 数据验证规则

### 用户数据验证
- **邮箱**: 必须符合邮箱格式，长度 5-255 字符
- **密码**: 长度 6-20 字符，必须包含字母和数字
- **手机号**: 必须符合中国手机号格式
- **显示名称**: 长度 2-50 字符

### 评论数据验证
- **评分**: 必须为 1-5 的整数
- **评论内容**: 长度 1-500 字符

### 聊天数据验证
- **消息内容**: 长度 1-500 字符

## 接口限流策略

### 认证接口
- 登录: 5次/分钟
- 注册: 3次/分钟

### 业务接口
- 发表评论: 10次/小时
- 发送消息: 30次/分钟
- 搜索餐厅: 60次/分钟

### 管理接口
- 所有管理接口: 100次/分钟

## 版本管理

### API 版本控制
- 通过 URL 路径进行版本控制: `/api/v1/`
- 向后兼容