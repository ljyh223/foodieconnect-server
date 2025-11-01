# 店员评价功能说明

## 概述
现在支持对店员进行评价，用户可以对店员的服务进行评分和评论。

## API 接口

### 发表店员评价
- **URL**: `POST /api/v1/staff/{staffId}/reviews`
- **方法**: POST
- **需要认证**: 是
- **Content-Type**: `application/json`
- **路径参数**:
  - `staffId`: 店员ID
- **请求体**:
  ```json
  {
    "rating": 4.5,
    "content": "服务态度很好，推荐菜品很专业"
  }
  ```
- **说明**: 用户ID从JWT token中自动获取，无需手动传递

### 请求示例
```bash
curl -X POST "http://localhost:8080/api/v1/staff/1/reviews" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "rating": 4.5,
    "content": "服务态度很好，推荐菜品很专业"
  }'
```

### 获取店员评价列表
- **URL**: `GET /api/v1/staff/{staffId}/reviews`
- **方法**: GET
- **参数**:
  - `staffId` (路径参数): 店员ID
  - `page` (查询参数): 页码，默认0
  - `size` (查询参数): 每页大小，默认10

### 获取店员评价详情
- **URL**: `GET /api/v1/staff/{staffId}/reviews/{reviewId}`
- **方法**: GET
- **参数**:
  - `staffId` (路径参数): 店员ID
  - `reviewId` (路径参数): 评价ID

### 检查用户是否已评价店员
- **URL**: `GET /api/v1/staff/{staffId}/reviews/check`
- **方法**: GET
- **需要认证**: 是
- **参数**:
  - `staffId` (路径参数): 店员ID

### 获取店员评价统计
- **URL**: `GET /api/v1/staff/{staffId}/reviews/stats`
- **方法**: GET
- **参数**:
  - `staffId` (路径参数): 店员ID

### 响应示例
```json
{
  "success": true,
  "data": {
    "id": 1,
    "staffId": 1,
    "userId": 1,
    "rating": 4.5,
    "content": "服务态度很好，推荐菜品很专业",
    "createdAt": "2025-10-31T21:00:00",
    "updatedAt": "2025-10-31T21:00:00",
    "userName": "张三",
    "userAvatar": "/uploads/avatar1.jpg"
  },
  "timestamp": "2025-10-31T21:00:00"
}
```

## 数据库结构
店员评价信息存储在 `staff_reviews` 表中，包含以下字段：
- `id`: 评价ID
- `staff_id`: 店员ID
- `user_id`: 用户ID
- `rating`: 评分（1.0-5.0）
- `content`: 评价内容
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 注意事项
1. 发表店员评价需要用户登录认证，用户ID从JWT token中自动获取
2. 每个用户只能对同一个店员评价一次
3. 评分范围为1.0-5.0，支持小数点后一位
4. 发表评价后会自动更新店员的平均评分
5. 评价内容不能为空
6. 店员评价列表会包含用户信息（用户名和头像）