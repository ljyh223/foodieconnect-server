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

// 配置
const config = {
    baseUrl: 'http://localhost:8080/api/v1',
    restaurantId: 1,
    verificationCode: '888888'
};

// 用户连接状态
const userConnections = {};

// 登录获取JWT token
async function login(account) {
    try {
        const response = await axios.post(`${config.baseUrl}/auth/login`, {
            email: account.email,
            password: account.password
        });
        
        if (response.data.success) {
            return response.data.data.token;
        } else {
            throw new Error(`登录失败: ${response.data.message}`);
        }
    } catch (error) {
        throw new Error(`登录请求失败: ${error.message}`);
    }
}

// 验证聊天室验证码并获取临时token
async function verifyChatRoom(jwtToken) {
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
            return response.data.data.tempToken;
        } else {
            throw new Error(`验证码验证失败: ${response.data.message}`);
        }
    } catch (error) {
        throw new Error(`验证码验证请求失败: ${error.response?.data?.message || error.message}`);
    }
}

// 连接WebSocket并设置消息处理
function connectWebSocket(account, tempToken) {
    return new Promise((resolve, reject) => {
        // 创建SockJS连接
        const socket = new SockJS(`${config.baseUrl}/ws/chat`);
        
        // 创建STOMP客户端
        const stompClient = Stomp.over(socket);
        
        // 启用调试日志
        stompClient.debug = function(str) {
            console.log(`[${account.name}] STOMP调试:`, str);
        };
        
        // 连接成功回调
        stompClient.connect(
            {
                'Authorization': `Bearer ${tempToken}`,
                'accept-version': '1.1,1.0',
                'heart-beat': '10000,10000'
            },
            function(frame) {
                console.log(`[${account.name}] STOMP连接成功！`);
                
                // 订阅聊天室消息
                const subscription = stompClient.subscribe('/topic/chat-room/1', function(message) {
                    const messageData = JSON.parse(message.body);
                    console.log(`\n[${account.name}] 收到聊天室消息:`, messageData.data.content);
                });
                
                // 订阅用户通知
                const notificationSubscription = stompClient.subscribe('/user/queue/notifications', function(message) {
                    const notificationData = JSON.parse(message.body);
                    console.log(`\n[${account.name}] 收到用户通知:`, notificationData.data.type);
                });
                
                // 存储连接信息
                userConnections[account.name] = {
                    stompClient: stompClient,
                    socket: socket,
                    subscription: subscription,
                    notificationSubscription: notificationSubscription
                };
                
                resolve();
            },
            function(error) {
                console.error(`[${account.name}] STOMP连接失败:`, error);
                reject(error);
            }
        );
    });
}

// 加入聊天室
function joinChatRoom(account) {
    const connection = userConnections[account.name];
    if (!connection) {
        throw new Error(`[${account.name}] WebSocket连接未建立`);
    }
    
    const joinMessage = { roomId: 1 };
    connection.stompClient.send('/app/chat-room.join', {}, JSON.stringify(joinMessage));
    console.log(`[${account.name}] 已发送加入聊天室请求`);
}

// 发送消息
function sendMessage(account, content) {
    const connection = userConnections[account.name];
    if (!connection) {
        throw new Error(`[${account.name}] WebSocket连接未建立`);
    }
    
    const message = {
        roomId: 1,
        content: content
    };
    
    connection.stompClient.send('/app/chat-room.sendMessage', {}, JSON.stringify(message));
    console.log(`[${account.name}] 发送消息: ${content}`);
}

// 离开聊天室
function leaveChatRoom(account) {
    const connection = userConnections[account.name];
    if (!connection) {
        throw new Error(`[${account.name}] WebSocket连接未建立`);
    }
    
    const leaveMessage = { roomId: 1 };
    connection.stompClient.send('/app/chat-room.leave', {}, JSON.stringify(leaveMessage));
    console.log(`[${account.name}] 已发送离开聊天室请求`);
}

// 断开连接
function disconnectWebSocket(account) {
    const connection = userConnections[account.name];
    if (connection && connection.stompClient && connection.stompClient.connected) {
        connection.stompClient.disconnect();
        console.log(`[${account.name}] WebSocket连接已断开`);
    }
}

// 主函数
async function main() {
    console.log('开始双用户同时在线测试...');
    console.log('=====================================');
    
    try {
        // 步骤1: 两个用户登录并获取临时token
        console.log('\n=== 步骤1: 用户登录并获取临时token ===');
        
        for (const [accountNumber, account] of Object.entries(testAccounts)) {
            console.log(`\n[${account.name}] 开始登录...`);
            const jwtToken = await login(account);
            console.log(`[${account.name}] 登录成功！`);
            
            const tempToken = await verifyChatRoom(jwtToken);
            console.log(`[${account.name}] 验证码验证成功！`);
            
            account.jwtToken = jwtToken;
            account.tempToken = tempToken;
        }
        
        // 步骤2: 两个用户连接WebSocket
        console.log('\n=== 步骤2: 用户连接WebSocket ===');
        
        const connectPromises = Object.values(testAccounts).map(account => 
            connectWebSocket(account, account.tempToken)
        );
        
        await Promise.all(connectPromises);
        console.log('\n所有用户WebSocket连接成功！');
        
        // 等待一秒确保连接稳定
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 步骤3: 两个用户加入聊天室
        console.log('\n=== 步骤3: 用户加入聊天室 ===');
        
        for (const account of Object.values(testAccounts)) {
            joinChatRoom(account);
        }
        
        // 等待一秒确保加入成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 步骤4: 用户1发送消息
        console.log('\n=== 步骤4: 用户1发送消息 ===');
        sendMessage(testAccounts[1], '大家好，我是账号1！');
        
        // 等待2秒确保消息传递
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 步骤5: 用户2发送消息
        console.log('\n=== 步骤5: 用户2发送消息 ===');
        sendMessage(testAccounts[2], '你好账号1，我是账号2，很高兴认识你！');
        
        // 等待2秒确保消息传递
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 步骤6: 用户1回复消息
        console.log('\n=== 步骤6: 用户1回复消息 ===');
        sendMessage(testAccounts[1], '你好账号2，我也很高兴认识你！');
        
        // 等待2秒确保消息传递
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 步骤7: 用户2再次发送消息
        console.log('\n=== 步骤7: 用户2再次发送消息 ===');
        sendMessage(testAccounts[2], '这个聊天室功能真不错！');
        
        // 等待2秒确保消息传递
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 步骤8: 用户1最后发送消息
        console.log('\n=== 步骤8: 用户1最后发送消息 ===');
        sendMessage(testAccounts[1], '是的，实时聊天体验很好！');
        
        // 等待2秒确保消息传递
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 步骤9: 用户离开聊天室
        console.log('\n=== 步骤9: 用户离开聊天室 ===');
        
        for (const account of Object.values(testAccounts)) {
            leaveChatRoom(account);
        }
        
        // 等待一秒确保离开成功
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // 步骤10: 断开所有连接
        console.log('\n=== 步骤10: 断开所有连接 ===');
        
        for (const account of Object.values(testAccounts)) {
            disconnectWebSocket(account);
        }
        
        console.log('\n=====================================');
        console.log('双用户同时在线测试完成！');
        
    } catch (error) {
        console.error('测试过程中发生错误:', error.message);
        
        // 清理连接
        for (const account of Object.values(testAccounts)) {
            try {
                disconnectWebSocket(account);
            } catch (e) {
                console.error(`清理${account.name}连接失败:`, e.message);
            }
        }
        
        process.exit(1);
    }
}

// 运行主函数
main();