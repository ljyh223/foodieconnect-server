const SockJS = require('sockjs-client');
const { Stomp } = require('@stomp/stompjs');
const axios = require('axios');
const readline = require('readline');

// 创建命令行接口
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

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
let currentUser = null;
let currentRoomId = null;

// 辅助函数：询问用户输入
function askQuestion(query) {
    return new Promise(resolve => rl.question(query, resolve));
}

// 步骤1: 登录获取JWT token
async function login() {
    console.log('\n=== 步骤1: 登录获取JWT token ===');
    console.log('可用测试账号:');
    Object.keys(testAccounts).forEach(key => {
        console.log(`${key}. ${testAccounts[key].name} (${testAccounts[key].email})`);
    });
    
    const accountChoice = await askQuestion('请选择账号 (1 或 2): ');
    
    if (!testAccounts[accountChoice]) {
        console.error('无效的账号选择');
        return false;
    }
    
    currentUser = testAccounts[accountChoice];
    console.log(`使用${currentUser.name}登录: ${currentUser.email}`);
    
    try {
        const response = await axios.post(`${config.baseUrl}/auth/login`, {
            email: currentUser.email,
            password: currentUser.password
        });
        
        if (response.data.success) {
            jwtToken = response.data.data.token;
            console.log(`${currentUser.name}登录成功！用户ID: ${response.data.data.user.id}`);
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
    
    const customRoomId = await askQuestion(`请输入餐厅ID (默认: ${config.restaurantId}): `);
    const restaurantId = customRoomId ? parseInt(customRoomId) : config.restaurantId;
    
    const customCode = await askQuestion(`请输入验证码 (默认: ${config.verificationCode}): `);
    const verificationCode = customCode || config.verificationCode;
    
    try {
        const response = await axios.get(
            `${config.baseUrl}/chat-rooms/verify?restaurantId=${restaurantId}&verificationCode=${verificationCode}`,
            {
                headers: {
                    'Authorization': `Bearer ${jwtToken}`
                }
            }
        );
        
        if (response.data.success) {
            tempToken = response.data.data.tempToken;
            currentRoomId = response.data.data.chatRoom.id;
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
    const subscription = stompClient.subscribe(`/topic/chat-room/${currentRoomId}`, function(message) {
        const messageData = JSON.parse(message.body);
        console.log('\n收到聊天室消息:', messageData);
    });
    
    console.log(`已订阅聊天室主题: /topic/chat-room/${currentRoomId}`);
    
    // 订阅用户通知队列
    const notificationSubscription = stompClient.subscribe('/user/queue/notifications', function(message) {
        const notificationData = JSON.parse(message.body);
        console.log('\n收到用户通知:', notificationData);
    });
    
    console.log('已订阅用户通知队列: /user/queue/notifications');
    
    // 订阅错误队列
    const errorSubscription = stompClient.subscribe('/user/queue/errors', function(message) {
        const errorData = JSON.parse(message.body);
        console.log('\n收到错误消息:', errorData);
    });
    
    console.log('已订阅错误队列: /user/queue/errors');
}

// 步骤5: 加入聊天室
function joinChatRoom() {
    console.log('\n=== 步骤5: 加入聊天室 ===');
    
    const joinMessage = {
        roomId: currentRoomId
    };
    
    stompClient.send('/app/chat-room.join', {}, JSON.stringify(joinMessage));
    console.log(`${currentUser.name}已发送加入聊天室请求，房间ID: ${currentRoomId}`);
}

// 步骤6: 发送消息
async function sendMessage() {
    const message = await askQuestion('请输入要发送的消息 (输入 /quit 退出): ');
    
    if (message === '/quit') {
        return false;
    }
    
    const messageObj = {
        roomId: currentRoomId,
        content: message
    };
    
    stompClient.send('/app/chat-room.sendMessage', {}, JSON.stringify(messageObj));
    console.log(`${currentUser.name}已发送消息: "${message}"`);
    return true;
}

// 步骤7: 离开聊天室
function leaveChatRoom() {
    console.log('\n=== 步骤7: 离开聊天室 ===');
    
    const leaveMessage = {
        roomId: currentRoomId
    };
    
    stompClient.send('/app/chat-room.leave', {}, JSON.stringify(leaveMessage));
    console.log(`${currentUser.name}已发送离开聊天室请求，房间ID: ${currentRoomId}`);
}

// 步骤8: 断开连接
function disconnectWebSocket() {
    console.log('\n=== 步骤8: 断开连接 ===');
    
    if (stompClient && stompClient.connected) {
        stompClient.disconnect();
        console.log('STOMP连接已断开');
    }
}

// 显示菜单
async function showMenu() {
    console.log('\n===== 聊天室手动测试菜单 =====');
    console.log('1. 发送消息');
    console.log('2. 离开聊天室');
    console.log('3. 重新加入聊天室');
    console.log('4. 断开连接并退出');
    console.log('===============================');
    
    const choice = await askQuestion('请选择操作 (1-4): ');
    
    switch (choice) {
        case '1':
            await sendMessage();
            return true; // 继续显示菜单
        case '2':
            leaveChatRoom();
            return true; // 继续显示菜单
        case '3':
            joinChatRoom();
            return true; // 继续显示菜单
        case '4':
            leaveChatRoom();
            disconnectWebSocket();
            return false; // 退出
        default:
            console.log('无效选择，请重试');
            return true; // 继续显示菜单
    }
}

// 主函数
async function main() {
    console.log('开始手动聊天室功能测试...');
    console.log('============================');
    
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
        
        console.log('\n===== 初始化完成，现在可以开始聊天 =====');
        console.log('您将看到交互菜单，可以发送消息、离开聊天室等操作');
        
        // 显示交互菜单
        let continueMenu = true;
        while (continueMenu) {
            continueMenu = await showMenu();
        }
        
        console.log('\n============================');
        console.log('测试完成！');
        
    } catch (error) {
        console.error('测试过程中发生错误:', error.message);
        process.exit(1);
    } finally {
        rl.close();
    }
}

// 运行主函数
main();