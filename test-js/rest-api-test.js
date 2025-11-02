const axios = require('axios');

// 配置
const config = {
  apiUrl: 'http://localhost:8080/api/v1',
  loginUrl: '/auth/login',
  testRoomId: 1,
  accounts: {
    1: {
      email: '3439426154@qq.com',
      password: 'jj123456'
    },
    2: {
      email: 'ljyh223@163.com',
      password: 'jj123456'
    }
  }
};

// 颜色输出函数
const colors = {
  green: '\x1b[32m',
  red: '\x1b[31m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  cyan: '\x1b[36m',
  reset: '\x1b[0m'
};

function log(message, color = 'reset') {
  console.log(`${colors[color]}${new Date().toISOString()} - ${message}${colors.reset}`);
}

async function restApiTest() {
  try {
    log('=== REST API测试开始 ===', 'blue');
    
    // 1. 登录两个账号
    log('登录账号1...', 'cyan');
    const login1Response = await axios.post(`${config.apiUrl}${config.loginUrl}`, {
      email: config.accounts[1].email,
      password: config.accounts[1].password
    });
    const token1 = login1Response.data.data.token;
    const userId1 = login1Response.data.data.user.id;
    log(`账号1登录成功！用户ID: ${userId1}`, 'green');
    
    log('登录账号2...', 'cyan');
    const login2Response = await axios.post(`${config.apiUrl}${config.loginUrl}`, {
      email: config.accounts[2].email,
      password: config.accounts[2].password
    });
    const token2 = login2Response.data.data.token;
    const userId2 = login2Response.data.data.user.id;
    log(`账号2登录成功！用户ID: ${userId2}`, 'green');
    
    // 2. 尝试获取聊天室信息
    try {
      log('尝试获取聊天室信息...', 'cyan');
      const roomResponse = await axios.get(`${config.apiUrl}/chat-rooms/${config.testRoomId}`, {
        headers: { 'Authorization': `Bearer ${token1}` }
      });
      log(`聊天室信息: ${JSON.stringify(roomResponse.data, null, 2)}`, 'green');
    } catch (error) {
      log(`获取聊天室信息失败: ${error.message}`, 'red');
      if (error.response) {
        log(`响应状态: ${error.response.status}`, 'red');
        log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
      }
    }
    
    // 3. 尝试获取聊天室消息
    try {
      log('尝试获取聊天室消息...', 'cyan');
      const messagesResponse = await axios.get(`${config.apiUrl}/chat-rooms/${config.testRoomId}/messages`, {
        headers: { 'Authorization': `Bearer ${token1}` }
      });
      log(`聊天室消息: ${JSON.stringify(messagesResponse.data, null, 2)}`, 'green');
    } catch (error) {
      log(`获取聊天室消息失败: ${error.message}`, 'red');
      if (error.response) {
        log(`响应状态: ${error.response.status}`, 'red');
        log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
      }
    }
    
    // 4. 尝试通过验证码加入聊天室
    try {
      log('尝试通过验证码加入聊天室...', 'cyan');
      const joinResponse = await axios.post(`${config.apiUrl}/chat-rooms/join`, {
        restaurantId: 1,
        verificationCode: '888888'
      }, {
        headers: { 'Authorization': `Bearer ${token1}` }
      });
      log(`加入聊天室成功: ${JSON.stringify(joinResponse.data, null, 2)}`, 'green');
    } catch (error) {
      log(`加入聊天室失败: ${error.message}`, 'red');
      if (error.response) {
        log(`响应状态: ${error.response.status}`, 'red');
        log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
      }
    }
    
    // 5. 尝试发送消息
    try {
      log('尝试发送消息...', 'cyan');
      const sendResponse = await axios.post(`${config.apiUrl}/chat-rooms/${config.testRoomId}/messages`, {
        content: '这是一条REST API测试消息'
      }, {
        headers: { 'Authorization': `Bearer ${token1}` }
      });
      log(`发送消息成功: ${JSON.stringify(sendResponse.data, null, 2)}`, 'green');
    } catch (error) {
      log(`发送消息失败: ${error.message}`, 'red');
      if (error.response) {
        log(`响应状态: ${error.response.status}`, 'red');
        log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
      }
    }
    
    // 6. 再次获取聊天室消息
    try {
      log('再次获取聊天室消息...', 'cyan');
      const messagesResponse = await axios.get(`${config.apiUrl}/chat-rooms/${config.testRoomId}/messages`, {
        headers: { 'Authorization': `Bearer ${token1}` }
      });
      log(`聊天室消息: ${JSON.stringify(messagesResponse.data, null, 2)}`, 'green');
    } catch (error) {
      log(`获取聊天室消息失败: ${error.message}`, 'red');
      if (error.response) {
        log(`响应状态: ${error.response.status}`, 'red');
        log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
      }
    }
    
    log('=== REST API测试结束 ===', 'blue');
    
  } catch (error) {
    log(`REST API测试失败: ${error.message}`, 'red');
    if (error.response) {
      log(`响应状态: ${error.response.status}`, 'red');
      log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
    }
  }
}

restApiTest();