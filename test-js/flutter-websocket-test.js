const SockJS = require('sockjs-client');
const { Stomp } = require('@stomp/stompjs');
const axios = require('axios');

// 测试账号配置
const testAccounts = {
    1: {
        email: '3439426154@qq.com',
        password: 'jj123456',
        name: '账号1'
    },
    2: {
        email: 'ljyh223@163.com',
        password: 'jj123456',
        name: '账号2'
    }
};

// 解析命令行参数
const args = process.argv.slice(2);
const accountParam = args.find(arg => arg.startsWith('--account='));
const accountNumber = accountParam ? parseInt(accountParam.split('=')[1]) : 1;

// 验证账号参数
if (!testAccounts[accountNumber]) {
    console.error(`无效的账号参数: ${accountNumber}，请使用 1 或 2`);
    process.exit(1);
}

const account = testAccounts[accountNumber];
console.log(`使用${account.name}登录: ${account.email}`);

// 配置
const config = {
    baseUrl: 'http://localhost:8080/api/v1',
    restaurantId: 1,
    verificationCode: '888888'
};

// 全局变量
let jwtToken = null;
let tempToken = null;
let stompClient = null;

// 步骤1: 登录获取JWT token
async function login() {
    console.log('\n=== 步骤1: 登录获取JWT token ===');
    try {
        const response = await axios.post(`${config.baseUrl}/auth/login`, {
            email: account.email,
            password: account.password
        });
        
        if (response.data.success) {
            jwtToken = response.data.data.token;
            console.log(`${account.name}登录成功！用户ID: ${response.data.data.user.id}`);
            console.log(`JWT Token: ${jwtToken.substring(0, 50)}...`);
            return true;
        } else {
            console.error('登录失败:', response.data.message);
            return false;
        }
    } catch (error) {
        console.error('登录请求失败:', error.message);
        return false;
    }
}

// 步骤2: 验证聊天室验证码并获取临时token
async function verifyChatRoom() {
    console.log('\n=== 步骤2: 验证聊天室验证码并获取临时token ===');
    try {
        const response = await axios.get(
            `${config.baseUrl}/chat-rooms/verify?restaurantId=${config.restaurantId}&verificationCode=${config.verificationCode}`,
            {
                headers: {
                    'Authorization': `Bearer ${jwtToken}`
                }
            }
        );
        
        if (response.data.success) {
            tempToken = response.data.data.tempToken;
            console.log(`验证码验证成功！聊天室: ${response.data.data.chatRoom.name}`);
            console.log(`临时Token: ${tempToken.substring(0, 50)}...`);
            console.log(`Token过期时间: ${response.data.data.expiresIn}ms`);
            return true;
        } else {
            console.error('验证码验证失败:', response.data.message);
            return false;
        }
    } catch (error) {
        console.error('验证码验证请求失败:', error.response?.data?.message || error.message);
        return false;
    }
}

// 步骤3: 连接STOMP WebSocket
function connectWebSocket() {
    console.log('\n=== 步骤3: 连接STOMP WebSocket ===');
    return new Promise((resolve, reject) => {
        // 创建SockJS连接
        const socket = new SockJS(`${config.baseUrl}/ws/chat`);
        
        // 创建STOMP客户端
        stompClient = Stomp.over(socket);
        
        // 启用调试日志
        stompClient.debug = function(str) {
            console.log('STOMP调试:', str);
        };
        
        // 连接成功回调
        stompClient.connect(
            {
                'Authorization': `Bearer ${tempToken}`,
                'accept-version': '1.1,1.0',
                'heart-beat': '10000,10000'
            },
            function(frame) {
                console.log('STOMP连接成功！');
                console.log('连接帧:', frame);
                resolve();
            },
            function(error) {
                console.error('STOMP连接失败:', error);
                reject(error);
            }
        );
    });
}

// 步骤4: 订阅聊天室主题
function subscribeToChatRoom() {
    console.log('\n=== 步骤4: 订阅聊天室主题 ===');
    
    // 订阅聊天室消息
    const subscription = stompClient.subscribe('/topic/chat-room/1', function(message) {
        const messageData = JSON.parse(message.body);
        console.log('\n收到聊天室消息:', messageData);
    });
    
    console.log('已订阅聊天室主题: /topic/chat-room/1');
    
    // 订阅用户通知队列
    const notificationSubscription = stompClient.subscribe('/user/queue/notifications', function(message) {
        const notificationData = JSON.parse(message.body);
        console.log('\n收到用户通知:', notificationData);
    });
    
    console.log('已订阅用户通知队列: /user/queue/notifications');
}

// 步骤5: 加入聊天室
function joinChatRoom() {
    console.log('\n=== 步骤5: 加入聊天室 ===');
    
    const joinMessage = {
        roomId: 1
    };
    
    stompClient.send('/app/chat-room.join', {}, JSON.stringify(joinMessage));
    console.log(`${account.name}已发送加入聊天室请求，房间ID: 1`);
}

// 步骤6: 发送测试消息
function sendTestMessage() {
    console.log('\n=== 步骤6: 发送测试消息 ===');
    
    const message = {
        roomId: 1,
        content: `这是一条来自${account.name}的测试消息 - ${new Date().toLocaleTimeString()}`
    };
    
    stompClient.send('/app/chat-room.sendMessage', {}, JSON.stringify(message));
    console.log(`${account.name}已发送测试消息`);
}

// 步骤7: 离开聊天室
function leaveChatRoom() {
    console.log('\n=== 步骤7: 离开聊天室 ===');
    
    const leaveMessage = {
        roomId: 1
    };
    
    stompClient.send('/app/chat-room.leave', {}, JSON.stringify(leaveMessage));
    console.log(`${account.name}已发送离开聊天室请求，房间ID: 1`);
}

// 步骤8: 断开连接
function disconnectWebSocket() {
    console.log('\n=== 步骤8: 断开连接 ===');
    
    if (stompClient && stompClient.connected) {
        stompClient.disconnect();
        console.log('STOMP连接已断开');
    }
}

// 主函数
async function main() {
    console.log('开始Flutter WebSocket聊天室功能测试...');
    console.log('=====================================');
    
    try {
        // 步骤1: 登录
        const loginSuccess = await login();
        if (!loginSuccess) {
            process.exit(1);
        }
        
        // 步骤2: 验证聊天室
        const verifySuccess = await verifyChatRoom();
        if (!verifySuccess) {
            process.exit(1);
        }
        
        // 步骤3: 连接WebSocket
        await connectWebSocket();
        
        // 步骤4: 订阅聊天室主题
        subscribeToChatRoom();
        
        // 等待一秒确保订阅成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 步骤5: 加入聊天室
        joinChatRoom();
        
        // 等待一秒确保加入成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 步骤6: 发送测试消息
        sendTestMessage();
        
        // 等待一秒确保消息发送成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 再次发送一条消息
        sendTestMessage();
        
        // 等待一秒确保消息发送成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 步骤7: 离开聊天室
        leaveChatRoom();
        
        // 等待一秒确保离开成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 步骤8: 断开连接
        disconnectWebSocket();
        
        console.log('\n=====================================');
        console.log('测试完成！');
        
    } catch (error) {
        console.error('测试过程中发生错误:', error.message);
        process.exit(1);
    }
}

// 运行主函数
main();