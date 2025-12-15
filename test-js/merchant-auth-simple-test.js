const axios = require('axios');

// 商家端API基础URL
const BASE_URL = 'http://localhost:8080/api/v1/merchant';

// 测试账户信息
const TEST_MERCHANT = {
    username: 'admin_chuanweixuan',
    password: 'jj123456'
};

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
        console.log('登录响应:', JSON.stringify(response.data, null, 2));
        
        if (response.data.success === true) {
            const token = response.data.data.token;
            setAuthHeader(token);
            console.log('✅ 登录成功!');
            console.log('JWT令牌:', token);
            return token;
        } else {
            console.error('❌ 登录失败:', response.data.error?.message || '未知错误');
            return null;
        }
    } catch (error) {
        console.error('❌ 登录请求失败:', error.response?.data || error.message);
        return null;
    }
}

// 2. 获取当前商家信息
async function getCurrentMerchant() {
    try {
        console.log('\n👤 获取当前商家信息...');
        const response = await axios.get(`${BASE_URL}/auth/profile`);
        console.log('Profile响应:', JSON.stringify(response.data, null, 2));
        
        if (response.data.success === true) {
            console.log('✅ 获取商家信息成功!');
            console.log('商家信息:', response.data.data);
            return response.data.data;
        } else {
            console.error('❌ 获取商家信息失败:', response.data.error?.message || '未知错误');
            return null;
        }
    } catch (error) {
        console.error('❌ 获取商家信息请求失败:', error.response?.data || error.message);
        return null;
    }
}

// 主测试函数
async function runSimpleTest() {
    console.log('🚀 开始简单商家认证测试...\n');
    
    // 步骤1: 登录获取token
    const token = await login();
    if (!token) {
        console.log('\n❌ 登录失败，无法继续测试');
        return;
    }
    
    // 步骤2: 获取商家信息
    const merchant = await getCurrentMerchant();
    if (!merchant) {
        console.log('\n❌ 获取商家信息失败');
        return;
    }
    
    console.log('\n✅ 测试完成!');
}

// 运行测试
runSimpleTest().catch(console.error);
