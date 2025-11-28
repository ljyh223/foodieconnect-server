const axios = require('axios');

const BASE_URL = 'http://localhost:8080/api/v1';
const MERCHANT_USERNAME = 'admin_chuanweixuan';
const MERCHANT_PASSWORD = 'jj123456';

async function testMerchantLogin() {
    console.log('=== 测试商户登录获取token ===');
    try {
        const response = await axios.post(`${BASE_URL}/merchant/auth/login`, {
            username: MERCHANT_USERNAME,
            password: MERCHANT_PASSWORD
        });

        if (response.data.success) {
            const token = response.data.data.token;
            console.log('✅ 登录成功');
            console.log('Token:', token);
            console.log('商家信息:', {
                id: response.data.data.merchantId,
                name: response.data.data.name,
                username: response.data.data.username
            });
            return token;
        } else {
            console.log('❌ 登录失败:', response.data.message);
            return null;
        }
    } catch (error) {
        console.log('❌ 登录请求失败:', error.response?.data || error.message);
        return null;
    }
}

async function testWithToken(token) {
    console.log('\n=== 使用token测试商户接口 ===');
    if (!token) {
        console.log('❌ 没有有效的token');
        return;
    }

    try {
        const response = await axios.get(`${BASE_URL}/merchant/auth/profile`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.data.success) {
            console.log('✅ 使用token访问成功');
            console.log('商户信息:', response.data.data);
        } else {
            console.log('❌ 使用token访问失败:', response.data.message);
        }
    } catch (error) {
        console.log('❌ 使用token访问请求失败:', error.response?.data || error.message);
    }
}

async function runTest() {
    console.log('开始商户登录测试...\n');
    
    const token = await testMerchantLogin();
    if (token) {
        await testWithToken(token);
    }
}

runTest().catch(console.error);