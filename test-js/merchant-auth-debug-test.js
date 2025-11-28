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
        console.log(response.data)
        if (response.data.success === true) {
            const token = response.data.data.token;
            setAuthHeader(token);
            console.log('✅ 登录成功!');
            console.log('商家信息:', response.data.data);
            console.log('JWT令牌前10位:', token.substring(0, 10) + '...');
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

// 2. 测试JWT token解析
async function testJwtParsing(token) {
    try {
        console.log('\n🔍 测试JWT token解析...');
        
        // 解析JWT的payload部分（不验证签名）
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.error('❌ JWT格式不正确');
            return;
        }
        
        const payload = JSON.parse(Buffer.from(parts[1], 'base64').toString());
        console.log('✅ JWT payload解析成功:');
        console.log('- 用户名:', payload.sub);
        console.log('- 商家ID:', payload.merchantId);
        console.log('- 餐厅ID:', payload.restaurantId);
        console.log('- 角色:', payload.role);
        console.log('- 姓名:', payload.name);
        console.log('- 签发时间:', new Date(payload.iat * 1000));
        console.log('- 过期时间:', new Date(payload.exp * 1000));
        
        return payload;
    } catch (error) {
        console.error('❌ JWT解析失败:', error.message);
        return null;
    }
}

// 3. 获取当前商家信息
async function getCurrentMerchant() {
    try {
        console.log('\n👤 获取当前商家信息...');
        const response = await axios.get(`${BASE_URL}/auth/profile`);
        
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

// 4. 测试无效token
async function testInvalidToken() {
    try {
        console.log('\n🚫 测试无效token...');
        
        // 保存原始token
        const originalToken = axios.defaults.headers.common['Authorization'];
        
        // 设置无效token
        setAuthHeader('Bearer invalid.token.here');
        
        const response = await axios.get(`${BASE_URL}/auth/profile`);
        console.log('❌ 无效token测试意外成功:', response.data);
        
        // 恢复原始token
        setAuthHeader(originalToken);
    } catch (error) {
        console.log('✅ 无效token正确被拒绝:', error.response?.data?.message || error.message);
    }
}

// 主测试函数
async function runDebugTests() {
    console.log('🚀 开始商家认证调试测试...\n');
    
    // 步骤1: 登录获取token
    const token = await login();
    if (!token) {
        console.log('\n❌ 登录失败，无法继续测试');
        return;
    }
    
    // 步骤2: 解析JWT token
    const jwtPayload = await testJwtParsing(token);
    if (!jwtPayload) {
        console.log('\n❌ JWT解析失败，无法继续测试');
        return;
    }
    
    // 步骤3: 获取商家信息
    const merchant = await getCurrentMerchant();
    if (!merchant) {
        console.log('\n❌ 获取商家信息失败');
        return;
    }
    
    // 步骤4: 验证JWT payload与返回的商家信息是否一致
    console.log('\n🔍 验证数据一致性...');
    if (jwtPayload.sub === merchant.username && 
        jwtPayload.merchantId === merchant.id && 
        jwtPayload.restaurantId === merchant.restaurantId) {
        console.log('✅ JWT payload与商家信息一致');
    } else {
        console.log('❌ JWT payload与商家信息不一致');
        console.log('- JWT用户名:', jwtPayload.sub, ', 商家用户名:', merchant.username);
        console.log('- JWT商家ID:', jwtPayload.merchantId, ', 商家ID:', merchant.id);
        console.log('- JWT餐厅ID:', jwtPayload.restaurantId, ', 商家餐厅ID:', merchant.restaurantId);
    }
    
    // 步骤5: 测试无效token
    await testInvalidToken();
    
    console.log('\n✅ 调试测试完成!');
}

// 运行测试
runDebugTests().catch(console.error);
