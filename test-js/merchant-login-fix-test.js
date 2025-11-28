const axios = require('axios');

// 商家端API基础URL
const BASE_URL = 'http://localhost:8080/merchant';

// 测试账户信息
const TEST_MERCHANT = {
    "username": "admin_chuanweixuan",
    "password": "jj123456"
};

// 设置axios默认配置
axios.defaults.timeout = 10000;
axios.defaults.headers.post['Content-Type'] = 'application/json';

/**
 * 测试商户登录功能
 */
async function testMerchantLogin() {
    try {
        console.log('🚀 开始测试商户登录修复...\n');
        
        console.log('🔐 尝试商户登录...');
        const response = await axios.post(`${BASE_URL}/auth/login`, TEST_MERCHANT);
        
        if (response.data.success === true) {
            console.log('✅ 商户登录成功!');
            console.log('商家信息:');
            console.log('- 商家ID:', response.data.data.merchantId);
            console.log('- 用户名:', response.data.data.username);
            console.log('- 姓名:', response.data.data.name);
            console.log('- 餐厅ID:', response.data.data.restaurantId);
            console.log('- 角色:', response.data.data.role);
            console.log('- JWT令牌前10位:', response.data.data.token.substring(0, 10) + '...');
            
            // 测试获取当前商家信息
            console.log('\n👤 测试获取当前商家信息...');
            const authHeader = `Bearer ${response.data.data.token}`;
            const profileResponse = await axios.get(`${BASE_URL}/auth/profile`, {
                headers: { Authorization: authHeader }
            });
            
            if (profileResponse.data.success === true) {
                console.log('✅ 获取商家信息成功!');
                console.log('商家信息:', profileResponse.data.data);
            } else {
                console.error('❌ 获取商家信息失败:', profileResponse.data.error?.message);
            }
            
        } else {
            console.error('❌ 商户登录失败:', response.data.error?.message);
        }
        
    } catch (error) {
        if (error.response) {
            console.error('❌ 请求失败 - 状态码:', error.response.status);
            console.error('错误信息:', error.response.data);
        } else if (error.request) {
            console.error('❌ 网络错误 - 无法连接到服务器');
        } else {
            console.error('❌ 其他错误:', error.message);
        }
    }
}

// 运行测试
testMerchantLogin().catch(console.error);
