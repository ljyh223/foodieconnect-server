# 菜品评价图片功能说明

## 概述
现在菜品评价接口支持添加多张图片，用户可以在发表评论时上传图片链接。

## API 接口

### 发表评论（支持图片）
- **URL**: `POST /api/v1/restaurants/{restaurantId}/reviews`
- **方法**: POST
- **参数**:
  - `restaurantId` (路径参数): 餐厅ID
  - `userId` (请求参数): 用户ID
  - `rating` (请求参数): 评分(1-5)
  - `comment` (请求参数): 评论内容
  - `imageUrls` (可选请求参数): 图片URL列表，多个URL用逗号分隔

### 请求示例
```bash
curl -X POST "http://localhost:8080/api/v1/restaurants/1/reviews" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&rating=5&comment=这家餐厅很棒！&imageUrls=/uploads/image1.jpg,/uploads/image2.jpg"
```

### 响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "restaurantId": 1,
    "userId": 1,
    "rating": 5,
    "comment": "这家餐厅很棒！",
    "createdAt": "2025-10-31T13:00:00",
    "updatedAt": "2025-10-31T13:00:00",
    "images": [
      {
        "id": 1,
        "reviewId": 1,
        "imageUrl": "/uploads/image1.jpg",
        "sortOrder": 1,
        "createdAt": "2025-10-31T13:00:00",
        "updatedAt": "2025-10-31T13:00:00"
      },
      {
        "id": 2,
        "reviewId": 1,
        "imageUrl": "/uploads/image2.jpg",
        "sortOrder": 2,
        "createdAt": "2025-10-31T13:00:00",
        "updatedAt": "2025-10-31T13:00:00"
      }
    ]
  }
}
```

## 图片上传接口

### 单张图片上传
- **URL**: `POST /api/v1/upload/image`
- **方法**: POST
- **参数**: `file` (MultipartFile)
- **需要认证**: 是

### 批量图片上传
- **URL**: `POST /api/v1/upload/images`
- **方法**: POST
- **参数**: `files` (MultipartFile数组，最多5个文件)
- **需要认证**: 是

## 图片访问
上传的图片可以通过以下URL公开访问：
- `http://localhost:8080/api/v1/uploads/{filename}`
- `http://localhost:8080/uploads/{filename}`

## 数据库结构
图片信息存储在 `review_images` 表中，包含以下字段：
- `id`: 图片ID
- `review_id`: 评论ID
- `image_url`: 图片URL
- `sort_order`: 图片排序
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 注意事项
1. 图片上传需要用户登录认证
2. 图片文件最大支持10MB
3. 支持的图片格式：JPEG、PNG、GIF、WebP
4. 图片资源可以公开访问，无需认证
5. 每个评论可以添加多张图片