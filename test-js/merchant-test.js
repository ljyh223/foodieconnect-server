const axios = require('axios');

// 商家端API基础URL
const BASE_URL = 'http://localhost:8080/merchant';

// 测试账户信息
const TEST_MERCHANT = {
    username: 'admin_chuanweixuan',
    password: '123456',
    restaurantId: 1
};

// 测试用的JWT令牌（需要先登录获取）
let JWT_TOKEN = '';

// 设置axios默认配置
axios.defaults.timeout = 10000;
axios.defaults.headers.post['Content-Type'] = 'application/json';

// 设置认证头
function setAuthHeader(token) {
    if (token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
        delete axios.defaults.headers.common['Authorization'];
    }
}

// 1. 商家登录
async function login() {
    try {
        console.log('🔐 商家登录测试...');
        const response = await axios.post(`${BASE_URL}/auth/login`, TEST_MERCHANT);
        
        if (response.data.code === 200) {
            JWT_TOKEN = response.data.data.token;
            setAuthHeader(JWT_TOKEN);
            console.log('✅ 登录成功!');
            console.log('商家信息:', response.data.data);
            console.log('JWT令牌:', JWT_TOKEN);
        } else {
            console.error('❌ 登录失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 登录请求失败:', error.message);
    }
}

// 2. 获取当前商家信息
async function getCurrentMerchant() {
    try {
        console.log('👤 获取当前商家信息...');
        const response = await axios.get(`${BASE_URL}/auth/profile`);
        
        if (response.data.code === 200) {
            console.log('✅ 获取商家信息成功!');
            console.log('商家信息:', response.data.data);
        } else {
            console.error('❌ 获取商家信息失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 获取商家信息请求失败:', error.message);
    }
}

// 3. 获取餐厅信息
async function getRestaurant() {
    try {
        console.log('🏪 获取餐厅信息...');
        const response = await axios.get(`${BASE_URL}/restaurants`);
        
        if (response.data.code === 200) {
            console.log('✅ 获取餐厅信息成功!');
            console.log('餐厅信息:', response.data.data);
        } else {
            console.error('❌ 获取餐厅信息失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 获取餐厅信息请求失败:', error.message);
    }
}

// 4. 获取菜单分类
async function getMenuCategories() {
    try {
        console.log('📋 获取菜单分类...');
        const response = await axios.get(`${BASE_URL}/menu/categories`);
        
        if (response.data.code === 200) {
            console.log('✅ 获取菜单分类成功!');
            console.log('分类列表:', response.data.data);
        } else {
            console.error('❌ 获取菜单分类失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 获取菜单分类请求失败:', error.message);
    }
}

// 5. 获取菜品列表
async function getMenuItems() {
    try {
        console.log('🍜 获取菜品列表...');
        const response = await axios.get(`${BASE_URL}/menu/items`);
        
        if (response.data.code === 200) {
            console.log('✅ 获取菜品列表成功!');
            console.log('菜品列表:', response.data.data);
        } else {
            console.error('❌ 获取菜品列表失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 获取菜品列表请求失败:', error.message);
    }
}

// 6. 获取店员列表
async function getStaff() {
    try {
        console.log('👥 获取店员列表...');
        const response = await axios.get(`${BASE_URL}/staff`);
        
        if (response.data.code === 200) {
            console.log('✅ 获取店员列表成功!');
            console.log('店员列表:', response.data.data);
        } else {
            console.error('❌ 获取店员列表失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 获取店员列表请求失败:', error.message);
    }
}

// 7. 获取统计数据
async function getStatistics() {
    try {
        console.log('📊 获取统计数据...');
        const response = await axios.get(`${BASE_URL}/statistics/today`);
        
        if (response.data.code === 200) {
            console.log('✅ 获取统计数据成功!');
            console.log('今日统计:', response.data.data);
        } else {
            console.error('❌ 获取统计数据失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 获取统计数据请求失败:', error.message);
    }
}

// 8. 创建菜单分类
async function createMenuCategory() {
    try {
        console.log('📝 创建菜单分类...');
        
        const categoryData = {
            name: '测试分类',
            description: '这是一个测试分类',
            sortOrder: 1,
            isActive: true
        };
        
        const response = await axios.post(`${BASE_URL}/menu/categories`, categoryData);
        
        if (response.data.code === 200) {
            console.log('✅ 创建菜单分类成功!');
            console.log('新分类:', response.data.data);
        } else {
            console.error('❌ 创建菜单分类失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 创建菜单分类请求失败:', error.message);
    }
}

// 9. 创建菜品
async function createMenuItem() {
    try {
        console.log('🍽 创建菜品...');
        
        const itemData = {
            categoryId: 1,
            name: '测试菜品',
            description: '这是一个测试菜品',
            price: 38.00,
            isAvailable: true,
            isRecommended: false,
            sortOrder: 1,
            spiceLevel: 'MILD',
            preparationTime: 10,
            calories: 200
        };
        
        const response = await axios.post(`${BASE_URL}/menu/items`, itemData);
        
        if (response.data.code === 200) {
            console.log('✅ 创建菜品成功!');
            console.log('新菜品:', response.data.data);
        } else {
            console.error('❌ 创建菜品失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 创建菜品请求失败:', error.message);
    }
}

// 10. 创建排班
async function createSchedule() {
    try {
        console.log('📅 创建排班...');
        
        const scheduleData = {
            staffId: 1,
            shiftDate: '2025-11-25',
            startTime: '09:00',
            endTime: '18:00',
            shiftType: 'FULL_DAY',
            notes: '测试排班'
        };
        
        const response = await axios.post(`${BASE_URL}/staff/schedules`, scheduleData);
        
        if (response.data.code === 200) {
            console.log('✅ 创建排班成功!');
            console.log('新排班:', response.data.data);
        } else {
            console.error('❌ 创建排班失败:', response.data.message);
        }
    } catch (error) {
        console.error('❌ 创建排班请求失败:', error.message);
    }
}

// 主测试函数
async function runTests() {
    console.log('🚀 开始商家端API测试...\n');
    
    // 步骤1: 登录
    await login();
    
    // 步骤2: 获取商家信息
    await getCurrentMerchant();
    
    // 步骤3: 获取餐厅信息
    await getRestaurant();
    
    // 步骤4: 获取菜单分类
    await getMenuCategories();
    
    // 步骤5: 获取菜品列表
    await getMenuItems();
    
    // 步骤6: 获取店员列表
    await getStaff();
    
    // 步骤7: 获取统计数据
    await getStatistics();
    
    // 步骤8: 创建菜单分类
    await createMenuCategory();
    
    // 步骤9: 创建菜品
    await createMenuItem();
    
    // 步骤10: 创建排班
    await createSchedule();
    
    console.log('\n✅ 所有测试完成!');
    console.log('\n📝 测试说明:');
    console.log('1. 确保数据库已执行迁移脚本');
    console.log('2. 确保应用服务已启动');
    console.log('3. 使用测试账户: admin_chuanweixuan / 123456');
    console.log('4. 商家端API基础URL:', BASE_URL);
}

// 运行测试
runTests().catch(console.error);
