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

async function verboseTest() {
  try {
    log('=== 详细调试测试开始 ===', 'blue');
    
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
    
    // 2. 连接STOMP
    log('尝试连接STOMP...', 'cyan');
    const socket = new SockJS(config.serverUrl);
    
    // 添加Socket事件监听器
    socket.onopen = function() {
      log('SockJS连接打开', 'green');
    };
    
    socket.onclose = function(event) {
      log(`SockJS连接关闭: ${event.code} - ${event.reason}`, 'yellow');
    };
    
    socket.onerror = function(error) {
      log(`SockJS连接错误: ${error}`, 'red');
    };
    
    const stompClient = Stomp.over(socket);
    
    // 启用调试信息
    stompClient.debug = function(str) {
      log(`STOMP调试: ${str}`, 'yellow');
    };
    
    // 连接配置
    const connectHeaders = {
      'Authorization': `Bearer ${jwtToken}`
    };
    
    log(`连接头: ${JSON.stringify(connectHeaders)}`, 'cyan');
    
    // 连接成功回调
    stompClient.connect(connectHeaders, function(frame) {
      log('STOMP连接成功！', 'green');
      log(`连接帧: ${frame}`, 'green');
      
      // 订阅聊天室主题
      const chatSubscription = stompClient.subscribe('/topic/chat-room/1', function(message) {
        log('--- 收到聊天室消息 ---', 'green');
        log(`消息头: ${JSON.stringify(message.headers)}`, 'green');
        log(`消息体: ${message.body}`, 'green');
      });
      log(`订阅聊天室主题: ${chatSubscription.id}`, 'cyan');
      
      // 订阅用户通知队列
      const notificationSubscription = stompClient.subscribe('/user/queue/notifications', function(message) {
        log('--- 收到用户通知 ---', 'blue');
        log(`消息头: ${JSON.stringify(message.headers)}`, 'blue');
        log(`消息体: ${message.body}`, 'blue');
      });
      log(`订阅用户通知队列: ${notificationSubscription.id}`, 'cyan');
      
      // 等待2秒后加入聊天室
      setTimeout(() => {
        log('发送加入聊天室请求...', 'yellow');
        const joinMessage = JSON.stringify({roomId: 1});
        log(`加入消息: ${joinMessage}`, 'cyan');
        stompClient.send('/app/chat-room.join', {}, joinMessage);
      }, 2000);
      
      // 等待5秒后发送消息
      setTimeout(() => {
        log('发送测试消息...', 'yellow');
        const chatMessage = JSON.stringify({
          roomId: 1,
          content: '这是一条详细调试测试消息'
        });
        log(`聊天消息: ${chatMessage}`, 'cyan');
        stompClient.send('/app/chat-room.sendMessage', {}, chatMessage);
      }, 5000);
      
      // 等待10秒后断开连接
      setTimeout(() => {
        log('断开连接...', 'yellow');
        stompClient.disconnect();
        log('=== 详细调试测试结束 ===', 'blue');
      }, 10000);
      
    }, function(error) {
      log(`STOMP连接失败: ${error}`, 'red');
      log(`错误类型: ${typeof error}`, 'red');
      log(`错误详情: ${JSON.stringify(error)}`, 'red');
    });
    
  } catch (error) {
    log(`详细调试测试失败: ${error.message}`, 'red');
    if (error.response) {
      log(`响应状态: ${error.response.status}`, 'red');
      log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
    }
    if (error.request) {
      log(`请求详情: ${error.request}`, 'red');
    }
  }
}

verboseTest();