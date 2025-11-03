const axios = require('axios');

// 配置
const BASE_URL = 'http://localhost:8080';
let authToken = '';

// 登录获取token
async function login() {
    try {
        const response = await axios.post(`${BASE_URL}/auth/login`, {
            email: 'user1@example.com',
            password: 'password123'
        });
        
        authToken = response.data.data.token;
        console.log('登录成功，获取到token');
        return authToken;
    } catch (error) {
        console.error('登录失败:', error.response?.data || error.message);
        throw error;
    }
}

// 设置请求头
function getAuthHeaders() {
    return {
        'Authorization': `Bearer ${authToken}`,
        'Content-Type': 'application/json'
    };
}

// 测试修改用户信息
async function testUpdateUserProfile() {
    try {
        console.log('\n=== 测试修改用户信息 ===');
        const response = await axios.put(`${BASE_URL}/api/users/profile`, {
            displayName: '新昵称',
            bio: '这是我的个人简介，我喜欢美食和旅行'
        }, { headers: getAuthHeaders() });
        
        console.log('修改用户信息成功:', response.data);
    } catch (error) {
        console.error('修改用户信息失败:', error.response?.data || error.message);
    }
}

// 测试添加喜好食物
async function testAddFavoriteFood() {
    try {
        console.log('\n=== 测试添加喜好食物 ===');
        const response = await axios.post(`${BASE_URL}/api/users/favorite-foods`, {
            foodName: '宫保鸡丁',
            foodType: '川菜'
        }, { headers: getAuthHeaders() });
        
        console.log('添加喜好食物成功:', response.data);
    } catch (error) {
        console.error('添加喜好食物失败:', error.response?.data || error.message);
    }
}

// 测试获取喜好食物列表
async function testGetFavoriteFoods() {
    try {
        console.log('\n=== 测试获取喜好食物列表 ===');
        const response = await axios.get(`${BASE_URL}/api/users/favorite-foods`, {
            headers: getAuthHeaders()
        });
        
        console.log('获取喜好食物列表成功:', response.data);
    } catch (error) {
        console.error('获取喜好食物列表失败:', error.response?.data || error.message);
    }
}

// 测试关注用户
async function testFollowUser() {
    try {
        console.log('\n=== 测试关注用户 ===');
        const response = await axios.post(`${BASE_URL}/api/follows/2`, {}, {
            headers: getAuthHeaders()
        });
        
        console.log('关注用户成功:', response.data);
    } catch (error) {
        console.error('关注用户失败:', error.response?.data || error.message);
    }
}

// 测试获取关注列表
async function testGetFollowingList() {
    try {
        console.log('\n=== 测试获取关注列表 ===');
        const response = await axios.get(`${BASE_URL}/api/follows/following`, {
            headers: getAuthHeaders()
        });
        
        console.log('获取关注列表成功:', response.data);
    } catch (error) {
        console.error('获取关注列表失败:', error.response?.data || error.message);
    }
}

// 测试推荐餐厅
async function testRecommendRestaurant() {
    try {
        console.log('\n=== 测试推荐餐厅 ===');
        const response = await axios.post(`${BASE_URL}/api/recommendations`, {
            restaurantId: 1,
            reason: '这家餐厅的川菜非常正宗，环境也很好，服务态度一流',
            rating: 4.8
        }, { headers: getAuthHeaders() });
        
        console.log('推荐餐厅成功:', response.data);
    } catch (error) {
        console.error('推荐餐厅失败:', error.response?.data || error.message);
    }
}

// 测试获取用户推荐列表
async function testGetMyRecommendations() {
    try {
        console.log('\n=== 测试获取我的推荐列表 ===');
        const response = await axios.get(`${BASE_URL}/api/recommendations/my`, {
            headers: getAuthHeaders()
        });
        
        console.log('获取我的推荐列表成功:', response.data);
    } catch (error) {
        console.error('获取我的推荐列表失败:', error.response?.data || error.message);
    }
}

// 测试获取用户个人信息
async function testGetUserProfile() {
    try {
        console.log('\n=== 测试获取用户个人信息 ===');
        const response = await axios.get(`${BASE_URL}/api/users/1`, {
            headers: getAuthHeaders()
        });
        
        console.log('获取用户个人信息成功:', response.data);
    } catch (error) {
        console.error('获取用户个人信息失败:', error.response?.data || error.message);
    }
}

// 主测试函数
async function runTests() {
    try {
        // 先登录获取token
        await login();
        
        // 测试各项功能
        await testUpdateUserProfile();
        await testAddFavoriteFood();
        await testGetFavoriteFoods();
        await testFollowUser();
        await testGetFollowingList();
        await testRecommendRestaurant();
        await testGetMyRecommendations();
        await testGetUserProfile();
        
        console.log('\n=== 所有测试完成 ===');
    } catch (error) {
        console.error('测试过程中发生错误:', error.message);
    }
}

// 运行测试
runTests();