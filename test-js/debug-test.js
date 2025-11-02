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
  reset: '\x1b[0m'
};

function log(message, color = 'reset') {
  console.log(`${colors[color]}${message}${colors.reset}`);
}

async function debugTest() {
  try {
    log('=== 调试测试开始 ===', 'blue');
    
    // 1. 登录
    const loginData = {
      email: config.accounts[1].email,
      password: config.accounts[1].password
    };
    
    const loginResponse = await axios.post(`${config.apiUrl}${config.loginUrl}`, loginData);
    const jwtToken = loginResponse.data.data.token;
    const userId = loginResponse.data.data.user.id;
    
    log(`登录成功！用户ID: ${userId}`, 'green');
    log(`JWT Token: ${jwtToken.substring(0, 20)}...`, 'green');
    
    // 2. 连接STOMP
    const socket = new SockJS(config.serverUrl);
    const stompClient = Stomp.over(socket);
    
    // 启用调试信息
    stompClient.debug = function(str) {
      log(`STOMP调试: ${str}`, 'yellow');
    };
    
    // 连接配置
    const connectHeaders = {
      'Authorization': `Bearer ${jwtToken}`
    };
    
    // 连接成功回调
    stompClient.connect(connectHeaders, function(frame) {
      log('STOMP连接成功！', 'green');
      log(`连接帧: ${frame}`, 'green');
      
      // 订阅聊天室主题
      stompClient.subscribe('/topic/chat-room/1', function(message) {
        log('--- 收到聊天室消息 ---', 'green');
        log(`消息体: ${message.body}`, 'green');
      });
      
      // 订阅用户通知队列
      stompClient.subscribe('/user/queue/notifications', function(message) {
        log('--- 收到用户通知 ---', 'blue');
        log(`消息体: ${message.body}`, 'blue');
      });
      
      // 等待2秒后加入聊天室
      setTimeout(() => {
        log('发送加入聊天室请求...', 'yellow');
        stompClient.send('/app/chat-room.join', {}, JSON.stringify({roomId: 1}));
      }, 2000);
      
      // 等待5秒后发送消息
      setTimeout(() => {
        log('发送测试消息...', 'yellow');
        stompClient.send('/app/chat-room.sendMessage', {}, JSON.stringify({
          roomId: 1,
          content: '这是一条调试测试消息'
        }));
      }, 5000);
      
      // 等待10秒后断开连接
      setTimeout(() => {
        log('断开连接...', 'yellow');
        stompClient.disconnect();
        log('=== 调试测试结束 ===', 'blue');
      }, 10000);
      
    }, function(error) {
      log(`STOMP连接失败: ${error}`, 'red');
    });
    
  } catch (error) {
    log(`调试测试失败: ${error.message}`, 'red');
    if (error.response) {
      log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
    }
  }
}

debugTest();