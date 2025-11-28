const axios = require('axios');

const BASE_URL = 'http://localhost:8080/api/v1';
const MERCHANT_USERNAME = 'admin_chuanweixuan';
const MERCHANT_PASSWORD = 'admin123';

let authToken = null;

async function testMerchantLogin() {
    console.log('=== 测试商户登录 ===');
    try {
        const response = await axios.post(`${BASE_URL}/merchant/auth/login`, {
            username: MERCHANT_USERNAME,
            password: MERCHANT_PASSWORD
        });

        if (response.data.success) {
            authToken = response.data.data.token;
            console.log('✅ 登录成功');
            console.log('Token:', authToken.substring(0, 50) + '...');
            console.log('商家信息:', {
                id: response.data.data.merchantId,
                name: response.data.data.name,
                role: response.data.data.role
            });
            return true;
        } else {
            console.log('❌ 登录失败:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('❌ 登录请求失败:', error.response?.data || error.message);
        return false;
    }
}

async function testMerchantProfile() {
    console.log('\n=== 测试获取商户信息 ===');
    if (!authToken) {
        console.log('❌ 没有有效的token');
        return false;
    }

    try {
        const response = await axios.get(`${BASE_URL}/merchant/auth/profile`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.data.success) {
            console.log('✅ 获取商户信息成功');
            console.log('商户信息:', {
                id: response.data.data.id,
                username: response.data.data.username,
                name: response.data.data.name
            });
            return true;
        } else {
            console.log('❌ 获取商户信息失败:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('❌ 获取商户信息请求失败:', error.response?.data || error.message);
        return false;
    }
}

async function testMerchantRestaurant() {
    console.log('\n=== 测试商户餐厅接口 ===');
    if (!authToken) {
        console.log('❌ 没有有效的token');
        return false;
    }

    try {
        const response = await axios.get(`${BASE_URL}/merchant/restaurants/1`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.data.success) {
            console.log('✅ 获取餐厅信息成功');
            console.log('餐厅信息:', {
                id: response.data.data.id,
                name: response.data.data.name
            });
            return true;
        } else {
            console.log('❌ 获取餐厅信息失败:', response.data.message);
            return false;
        }
    } catch (error) {
        console.log('❌ 获取餐厅信息请求失败:', error.response?.data || error.message);
        return false;
    }
}

async function testWithoutToken() {
    console.log('\n=== 测试无token访问商户接口 ===');
    try {
        await axios.get(`${BASE_URL}/merchant/auth/profile`);
        console.log('❌ 无token访问应该失败，但成功了');
        return false;
    } catch (error) {
        if (error.response?.status === 401 || error.response?.status === 403) {
            console.log('✅ 无token访问正确被拒绝');
            return true;
        } else {
            console.log('❌ 无token访问返回了意外的状态码:', error.response?.status);
            return false;
        }
    }
}

async function runAllTests() {
    console.log('开始商户端完整测试...\n');

    const loginSuccess = await testMerchantLogin();
    if (!loginSuccess) {
        console.log('\n❌ 登录失败，停止测试');
        return;
    }

    const profileSuccess = await testMerchantProfile();
    const restaurantSuccess = await testMerchantRestaurant();
    const noTokenSuccess = await testWithoutToken();

    console.log('\n=== 测试总结 ===');
    console.log(`登录测试: ${loginSuccess ? '✅ 通过' : '❌ 失败'}`);
    console.log(`商户信息测试: ${profileSuccess ? '✅ 通过' : '❌ 失败'}`);
    console.log(`餐厅接口测试: ${restaurantSuccess ? '✅ 通过' : '❌ 失败'}`);
    console.log(`无token访问测试: ${noTokenSuccess ? '✅ 通过' : '❌ 失败'}`);

    const allPassed = loginSuccess && profileSuccess && restaurantSuccess && noTokenSuccess;
    console.log(`\n总体结果: ${allPassed ? '🎉 所有测试通过！' : '❌ 部分测试失败'}`);
}

runAllTests().catch(console.error);