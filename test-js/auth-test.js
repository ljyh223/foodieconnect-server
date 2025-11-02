const axios = require('axios');
const Stomp = require('stompjs');
const SockJS = require('sockjs-client');

// 配置
const config = {
  serverUrl: 'http://localhost:8080/api/v1/ws/chat',
  apiUrl: 'http://localhost:8080/api/v1',
  loginUrl: '/auth/login',
  testRoomId: 1,
  accounts: {
    1: {
      email: '3439426154@qq.com',
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

async function authTest() {
  try {
    log('=== 认证测试开始 ===', 'blue');
    
    // 1. 登录
    log('尝试登录...', 'cyan');
    const loginData = {
      email: config.accounts[1].email,
      password: config.accounts[1].password
    };
    
    const loginResponse = await axios.post(`${config.apiUrl}${config.loginUrl}`, loginData);
    const jwtToken = loginResponse.data.data.token;
    const userId = loginResponse.data.data.user.id;
    
    log(`登录成功！用户ID: ${userId}`, 'green');
    log(`JWT Token: ${jwtToken.substring(0, 20)}...`, 'green');
    
    // 2. 尝试不同的连接方式
    await testConnectionWithAuth('无认证', {});
    await testConnectionWithAuth('Authorization头', {'Authorization': `Bearer ${jwtToken}`});
    await testConnectionWithAuth('X-Authorization头', {'X-Authorization': `Bearer ${jwtToken}`});
    await testConnectionWithAuth('token参数', {}, `?token=${jwtToken}`);
    
  } catch (error) {
    log(`认证测试失败: ${error.message}`, 'red');
    if (error.response) {
      log(`响应状态: ${error.response.status}`, 'red');
      log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
    }
  }
}

async function testConnectionWithAuth(name, headers, urlSuffix = '') {
  return new Promise((resolve) => {
    log(`\n=== 测试连接方式: ${name} ===`, 'blue');
    
    const socket = new SockJS(config.serverUrl + urlSuffix);
    const stompClient = Stomp.over(socket);
    
    // 启用调试信息
    stompClient.debug = function(str) {
      log(`STOMP调试: ${str}`, 'yellow');
    };
    
    // 连接配置
    const connectHeaders = headers;
    
    log(`连接头: ${JSON.stringify(connectHeaders)}`, 'cyan');
    log(`URL后缀: ${urlSuffix}`, 'cyan');
    
    // 连接成功回调
    stompClient.connect(connectHeaders, function(frame) {
      log(`${name}: STOMP连接成功！`, 'green');
      log(`连接帧: ${frame}`, 'green');
      
      // 订阅聊天室主题
      stompClient.subscribe('/topic/chat-room/1', function(message) {
        log(`${name}: --- 收到聊天室消息 ---`, 'green');
        log(`消息体: ${message.body}`, 'green');
      });
      
      // 订阅用户通知队列
      stompClient.subscribe('/user/queue/notifications', function(message) {
        log(`${name}: --- 收到用户通知 ---`, 'blue');
        log(`消息体: ${message.body}`, 'blue');
      });
      
      // 等待2秒后加入聊天室
      setTimeout(() => {
        log(`${name}: 发送加入聊天室请求...`, 'yellow');
        stompClient.send('/app/chat-room.join', {}, JSON.stringify({roomId: 1}));
        
        // 再等待2秒后发送消息
        setTimeout(() => {
          log(`${name}: 发送测试消息...`, 'yellow');
          stompClient.send('/app/chat-room.sendMessage', {}, JSON.stringify({
            roomId: 1,
            content: `这是一条${name}的测试消息`
          }));
          
          // 再等待3秒后断开连接
          setTimeout(() => {
            log(`${name}: 断开连接...`, 'yellow');
            stompClient.disconnect();
            resolve();
          }, 3000);
        }, 2000);
      }, 2000);
      
    }, function(error) {
      log(`${name}: STOMP连接失败: ${error}`, 'red');
      resolve();
    });
  });
}

authTest();