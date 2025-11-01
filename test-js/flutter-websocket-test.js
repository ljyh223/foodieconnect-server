const axios = require('axios');
const WebSocket = require('ws');

// 配置
const config = {
  serverUrl: 'ws://localhost:8080/api/v1/ws/chat',
  apiUrl: 'http://localhost:8080/api/v1',
  // 测试用户凭据（需要先注册或使用现有用户）
  loginUrl: '/auth/login',
  testRoomId: 1, // 测试聊天室ID
  testVerificationCode: '888888' // 测试验证码
};

// 存储JWT token和用户信息
let jwtToken = null;
let userId = null;
let ws = null;

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

// 1. 登录获取JWT token
async function login() {
  try {
    log('=== 步骤1: 登录获取JWT token ===', 'blue');
    
    // 使用测试用户登录
    const loginData = {
      email: '3439426154@qq.com', // 根据数据库中的用户修改
      password: 'jj123456'      // 根据实际密码修改
    };
    
    const response = await axios.post(`${config.apiUrl}${config.loginUrl}`, loginData);
    
    if (response.data) {
      jwtToken = response.data.data.token;
      userId = response.data.data.user.id;
      log(`登录成功！用户ID: ${userId}`, 'green');
      log(`JWT Token: ${jwtToken.substring(0, 20)}...`, 'green');
      return true;
    } else {
      log(`登录失败: ${response.data.message}`, 'red');
      return false;
    }
  } catch (error) {
    log(`登录错误: ${error.message}`, 'red');
    if (error.response) {
      log(`响应数据: ${JSON.stringify(error.response.data, null, 2)}`, 'red');
    }
    return false;
  }
}

// 2. 连接WebSocket
function connectWebSocket() {
  return new Promise((resolve, reject) => {
    log('=== 步骤2: 连接WebSocket ===', 'blue');
    
    // 创建原生WebSocket连接
    ws = new WebSocket(config.serverUrl);
    
    // 连接打开事件
    ws.on('open', function() {
      log('WebSocket连接成功！', 'green');
      
      // 发送认证信息
      const authMessage = {
        type: 'auth',
        token: jwtToken
      };
      ws.send(JSON.stringify(authMessage));
      
      resolve();
    });
    
    // 连接错误事件
    ws.on('error', function(error) {
      log(`WebSocket连接失败: ${error}`, 'red');
      reject(error);
    });
    
    // 连接关闭事件
    ws.on('close', function() {
      log('WebSocket连接已关闭', 'yellow');
    });
  });
}

// 3. 处理WebSocket消息
function handleWebSocketMessage(data) {
  try {
    const message = JSON.parse(data);
    
    switch (message.type) {
      case 'chat_message':
        log('--- 收到聊天室消息 ---', 'green');
        log(`消息内容: ${JSON.stringify(message.data, null, 2)}`, 'green');
        break;
        
      case 'room_joined':
        log('--- 成功加入聊天室 ---', 'yellow');
        log(`房间信息: ${JSON.stringify(message.data, null, 2)}`, 'yellow');
        break;
        
      case 'room_left':
        log('--- 成功离开聊天室 ---', 'yellow');
        log(`房间信息: ${JSON.stringify(message.data, null, 2)}`, 'yellow');
        break;
        
      case 'error':
        log('--- 收到错误消息 ---', 'red');
        log(`错误信息: ${JSON.stringify(message.data, null, 2)}`, 'red');
        break;
        
      case 'auth_success':
        log('--- 认证成功 ---', 'green');
        log(`用户信息: ${JSON.stringify(message.data, null, 2)}`, 'green');
        break;
        
      default:
        log('--- 未知消息类型 ---', 'yellow');
        log(`消息内容: ${JSON.stringify(message.data, null, 2)}`, 'yellow');
        break;
    }
  } catch (error) {
    log(`处理WebSocket消息错误: ${error.message}`, 'red');
  }
}

// 4. 加入聊天室
function joinRoom() {
  log('=== 步骤4: 加入聊天室 ===', 'blue');
  
  const message = {
    type: 'join_room',
    roomId: config.testRoomId
  };
  
  ws.send(JSON.stringify(message));
  log(`已发送加入聊天室请求，房间ID: ${config.testRoomId}`, 'green');
}

// 5. 发送测试消息
function sendTestMessage() {
  log('=== 步骤5: 发送测试消息 ===', 'blue');
  
  const message = {
    type: 'send_message',
    roomId: config.testRoomId,
    content: `这是一条来自Flutter测试脚本的测试消息 - ${new Date().toLocaleTimeString()}`
  };
  
  ws.send(JSON.stringify(message));
  log('已发送测试消息', 'green');
}

// 6. 离开聊天室
function leaveRoom() {
  log('=== 步骤6: 离开聊天室 ===', 'blue');
  
  const message = {
    type: 'leave_room',
    roomId: config.testRoomId
  };
  
  ws.send(JSON.stringify(message));
  log(`已发送离开聊天室请求，房间ID: ${config.testRoomId}`, 'yellow');
}

// 7. 断开连接
function disconnect() {
  log('=== 步骤7: 断开连接 ===', 'blue');
  
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.close();
    log('WebSocket连接已断开', 'yellow');
  }
}

// 主测试函数
async function runTest() {
  try {
    log('开始Flutter WebSocket聊天室功能测试...', 'blue');
    log('=====================================', 'blue');
    
    // 步骤1: 登录
    const loginSuccess = await login();
    if (!loginSuccess) {
      log('登录失败，测试终止', 'red');
      return;
    }
    
    // 等待1秒
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // 步骤2: 连接WebSocket
    await connectWebSocket();
    
    // 等待1秒
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // 步骤3: 加入聊天室
    joinRoom();
    
    // 等待2秒让加入操作完成
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 步骤4: 发送测试消息
    sendTestMessage();
    
    // 等待3秒让消息传播
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // 再发送一条消息
    sendTestMessage();
    
    // 等待3秒
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    // 步骤5: 离开聊天室
    leaveRoom();
    
    // 等待2秒
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 步骤6: 断开连接
    disconnect();
    
    log('=====================================', 'green');
    log('测试完成！', 'green');
    
  } catch (error) {
    log(`测试过程中发生错误: ${error.message}`, 'red');
    log(`错误堆栈: ${error.stack}`, 'red');
  }
}

// 交互式测试模式
function startInteractiveMode() {
  log('进入Flutter WebSocket交互式测试模式...', 'blue');
  log('可用命令:', 'blue');
  log('  send <message>  - 发送消息', 'blue');
  log('  join            - 加入聊天室', 'blue');
  log('  leave           - 离开聊天室', 'blue');
  log('  quit            - 退出程序', 'blue');
  log('=====================================', 'blue');
  
  const readline = require('readline');
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
  });
  
  rl.on('line', async (input) => {
    const args = input.trim().split(' ');
    const command = args[0];
    const message = args.slice(1).join(' ');
    
    switch (command) {
      case 'send':
        if (message) {
          const msg = {
            type: 'send_message',
            roomId: config.testRoomId,
            content: message
          };
          ws.send(JSON.stringify(msg));
          log(`发送消息: ${message}`, 'green');
        } else {
          log('请输入消息内容', 'yellow');
        }
        break;
        
      case 'join':
        joinRoom();
        break;
        
      case 'leave':
        leaveRoom();
        break;
        
      case 'quit':
        disconnect();
        rl.close();
        process.exit(0);
        break;
        
      default:
        log('未知命令', 'red');
        break;
    }
  });
}

// 检查命令行参数
const args = process.argv.slice(2);
if (args.includes('--interactive') || args.includes('-i')) {
  // 交互式模式
  (async () => {
    await login();
    await connectWebSocket();
    startInteractiveMode();
  })();
} else {
  // 自动测试模式
  runTest();
}

// 处理程序退出
process.on('SIGINT', () => {
  log('\n收到中断信号，正在断开连接...', 'yellow');
  disconnect();
  process.exit(0);
});