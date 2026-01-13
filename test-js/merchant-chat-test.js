const axios = require('axios');
const WebSocket = require('ws');
const path = require('path');
const protobuf = require('protobufjs');

// 配置信息
const config = {
    apiUrl: 'http://localhost:8080',
    wsBaseUrl: 'ws://localhost:8080/api/v1/ws/chat-bin', // WebSocket基础URL
    merchant: {
        username: 'admin_chuanweixuan',
        password: 'jj123456'
    },
    restaurantId: 1,
    verificationCode: '972889'
};

// 测试用的用户账号（用于发送消息）
const testUser = {
    username: 'alex@gmail.com',
    password: 'jj123456'
};

// 日志工具
const log = (message, type = 'info', source = '') => {
    const timestamp = new Date().toISOString();
    const colors = {
        info: '\x1b[32m', // 绿色
        warning: '\x1b[33m', // 黄色
        error: '\x1b[31m', // 红色
        debug: '\x1b[36m', // 青色
        merchant: '\x1b[34m', // 蓝色
        user: '\x1b[35m', // 紫色
        observer: '\x1b[37m' // 白色
    };
    
    const prefix = source ? `[${source}] ` : '';
    const color = colors[source] || colors[type];
    
    console.log(`${color}[${timestamp}] ${prefix}${message}\x1b[0m`);
};

// 加载protobuf定义
async function loadProto() {
    log('加载protobuf定义...', 'debug');
    try {
        const protoPath = path.join(__dirname, '../src/main/proto/chat.proto');
        const root = await protobuf.load(protoPath);
        const ns = 'com.ljyh.foodieconnect.protobuf';
        
        const types = {
            WebSocketMessage: root.lookupType(`${ns}.WebSocketMessage`),
            SendMessageRequest: root.lookupType(`${ns}.SendMessageRequest`),
            JoinRoomRequest: root.lookupType(`${ns}.JoinRoomRequest`),
            LeaveRoomRequest: root.lookupType(`${ns}.LeaveRoomRequest`),
            ChatResponse: root.lookupType(`${ns}.ChatResponse`),
            ChatMessage: root.lookupType(`${ns}.ChatMessage`),
        };
        
        log('protobuf定义加载成功', 'debug');
        return types;
    } catch (error) {
        log(`加载protobuf定义失败: ${error.message}`, 'error');
        throw error;
    }
}

// 包装WebSocket消息
function wrapMessage(types, typeStr, payloadBytes) {
    const msg = types.WebSocketMessage.create({ type: typeStr, payload: payloadBytes });
    return types.WebSocketMessage.encode(msg).finish();
}

// 商户登录
async function merchantLogin() {
    log('开始商户登录...', 'info', 'merchant');
    try {
        const response = await axios.post(`${config.apiUrl}/api/v1/merchant/auth/login`, {
            username: config.merchant.username,
            password: config.merchant.password
        });
        log('商户登录成功', 'info', 'merchant');
        return response.data.data.token;
    } catch (error) {
        log(`商户登录失败: ${error.response?.data?.message || error.message}`, 'error', 'merchant');
        throw error;
    }
}

// 用户登录（用于发送测试消息）
async function userLogin() {
    log('开始用户登录...', 'info', 'user');
    try {
        const response = await axios.post(`${config.apiUrl}/api/v1/auth/login`, {
            email: testUser.username,
            password: testUser.password
        });
        log('用户登录成功', 'info', 'user');
        return response.data.data.token;
    } catch (error) {
        log(`用户登录失败: ${error.response?.data?.message || error.message}`, 'error', 'user');
        throw error;
    }
}

// 获取聊天室信息
async function getChatRoomInfo(merchantToken) {
    log('获取聊天室信息...', 'info', 'merchant');
    try {
        const response = await axios.get(`${config.apiUrl}/api/v1/merchant/chat-rooms`, {
            headers: {
                Authorization: `Bearer ${merchantToken}`
            }
        });
        log('获取聊天室信息成功', 'info', 'merchant');
        return response.data.data;
    } catch (error) {
        log(`获取聊天室信息失败: ${error.response?.data?.message || error.message}`, 'error', 'merchant');
        throw error;
    }
}



// 获取当前聊天室的验证码
async function getCurrentVerificationCode(merchantToken) {
    log('获取当前聊天室验证码...', 'info', 'merchant');
    try {
        const response = await axios.get(
            `${config.apiUrl}/api/v1/merchant/chat-rooms/verification-code`,
            {
                headers: {
                    Authorization: `Bearer ${merchantToken}`
                }
            }
        );
        log('获取当前聊天室验证码成功', 'info', 'merchant');
        return response.data.data.verificationCode;
    } catch (error) {
        log(`获取当前聊天室验证码失败: ${error.response?.data?.message || error.message}`, 'error', 'merchant');
        throw error;
    }
}

// 获取用户临时令牌（用于WebSocket连接）
async function getUserTempToken(userToken, verificationCode) {
    log('获取用户临时令牌...', 'info', 'user');
    try {
        const response = await axios.get(
            `${config.apiUrl}/api/v1/chat-rooms/verify?restaurantId=${config.restaurantId}&verificationCode=${verificationCode}`,
            {
                headers: {
                    Authorization: `Bearer ${userToken}`
                }
            }
        );
        log('获取用户临时令牌成功', 'info', 'user');
        return response.data.data;
    } catch (error) {
        log(`获取用户临时令牌失败: ${error.response?.data?.message || error.message}`, 'error', 'user');
        throw error;
    }
}

// 建立商户端WebSocket连接（只能接收消息）
function connectMerchantWebSocket(types, roomId) {
    log('建立商户端WebSocket连接...', 'info', 'merchant');
    
    const wsUrl = `${config.wsBaseUrl}/${roomId}`;
    const ws = new WebSocket(wsUrl);

    ws.on('open', () => {
        log('商户端WebSocket连接成功', 'info', 'merchant');
        
        // 发送加入房间请求
        const joinReq = types.JoinRoomRequest.create({ roomId });
        const joinBytes = types.JoinRoomRequest.encode(joinReq).finish();
        const wsBytes = wrapMessage(types, 'JOIN_ROOM', joinBytes);
        ws.send(wsBytes);
        log(`商户端已发送加入房间请求: roomId=${roomId}`, 'info', 'merchant');
    });

    ws.on('message', (data) => {
        try {
            const resp = types.ChatResponse.decode(new Uint8Array(data));
            if (!resp.success) {
                log(`商户端收到错误响应: ${resp.errorMessage}`, 'error', 'merchant');
                return;
            }
            
            if (resp.message) {
                const m = resp.message;
                log(`商户端收到消息: [${m.roomId}] ${m.senderName}: ${m.content}`, 'info', 'merchant');
            } else if (resp.joinResponse) {
                log(`商户端加入房间成功: roomId=${resp.joinResponse.roomId}`, 'info', 'merchant');
            } else if (resp.leaveResponse) {
                log(`商户端离开房间成功: roomId=${resp.leaveResponse.roomId}`, 'info', 'merchant');
            }
        } catch (e) {
            log(`商户端解析响应失败: ${e.message}`, 'error', 'merchant');
        }
    });

    ws.on('error', (err) => {
        log(`商户端WebSocket错误: ${err.message}`, 'error', 'merchant');
    });

    ws.on('close', () => {
        log('商户端WebSocket连接关闭', 'warning', 'merchant');
    });

    return ws;
}

// 建立用户端WebSocket连接（用于发送消息）
function connectUserWebSocket(types, tempToken, roomId) {
    log('建立用户端WebSocket连接...', 'info', 'user');
    
    const wsUrl = `${config.wsBaseUrl}/${roomId}`;
    const ws = new WebSocket(wsUrl, {
        headers: { Authorization: `Bearer ${tempToken}` }
    });

    ws.on('open', () => {
        log('用户端WebSocket连接成功', 'info', 'user');
        
        // 发送加入房间请求
        const joinReq = types.JoinRoomRequest.create({ roomId });
        const joinBytes = types.JoinRoomRequest.encode(joinReq).finish();
        const wsBytes = wrapMessage(types, 'JOIN_ROOM', joinBytes);
        ws.send(wsBytes);
        log(`用户端已发送加入房间请求: roomId=${roomId}`, 'info', 'user');
    });

    ws.on('message', (data) => {
        try {
            const resp = types.ChatResponse.decode(new Uint8Array(data));
            if (!resp.success) {
                log(`用户端收到错误响应: ${resp.errorMessage}`, 'error', 'user');
                return;
            }
            
            if (resp.message) {
                const m = resp.message;
                log(`用户端收到消息: [${m.roomId}] ${m.senderName}: ${m.content}`, 'info', 'user');
            } else if (resp.joinResponse) {
                log(`用户端加入房间成功: roomId=${resp.joinResponse.roomId}`, 'info', 'user');
            } else if (resp.leaveResponse) {
                log(`用户端离开房间成功: roomId=${resp.leaveResponse.roomId}`, 'info', 'user');
            }
        } catch (e) {
            log(`用户端解析响应失败: ${e.message}`, 'error', 'user');
        }
    });

    ws.on('error', (err) => {
        log(`用户端WebSocket错误: ${err.message}`, 'error', 'user');
    });

    ws.on('close', () => {
        log('用户端WebSocket连接关闭', 'warning', 'user');
    });

    return ws;
}

// 建立观察者WebSocket连接（无token，只能接收消息）
function connectObserverWebSocket(types, roomId, observerType = null) {
    log(`建立观察者WebSocket连接... (类型: ${observerType || 'default'})`, 'info', 'observer');
    
    let wsUrl = `${config.wsBaseUrl}/${roomId}`;
    if (observerType) {
        wsUrl += `?observer=${observerType}`;
    }
    
    const ws = new WebSocket(wsUrl);

    ws.on('open', () => {
        log(`观察者WebSocket连接成功 (类型: ${observerType || 'default'})`, 'info', 'observer');
        
        // 发送加入房间请求
        const joinReq = types.JoinRoomRequest.create({ roomId });
        const joinBytes = types.JoinRoomRequest.encode(joinReq).finish();
        const wsBytes = wrapMessage(types, 'JOIN_ROOM', joinBytes);
        ws.send(wsBytes);
        log(`观察者已发送加入房间请求: roomId=${roomId}`, 'info', 'observer');
    });

    ws.on('message', (data) => {
        try {
            const resp = types.ChatResponse.decode(new Uint8Array(data));
            if (!resp.success) {
                log(`观察者收到错误响应: ${resp.errorMessage}`, 'error', 'observer');
                return;
            }
            
            if (resp.message) {
                const m = resp.message;
                log(`观察者收到消息: [${m.roomId}] ${m.senderName}: ${m.content}`, 'info', 'observer');
            } else if (resp.joinResponse) {
                log(`观察者加入房间成功: roomId=${resp.joinResponse.roomId}`, 'info', 'observer');
            } else if (resp.leaveResponse) {
                log(`观察者离开房间成功: roomId=${resp.leaveResponse.roomId}`, 'info', 'observer');
            }
        } catch (e) {
            log(`观察者解析响应失败: ${e.message}`, 'error', 'observer');
        }
    });

    ws.on('error', (err) => {
        log(`观察者WebSocket错误: ${err.message}`, 'error', 'observer');
    });

    ws.on('close', () => {
        log('观察者WebSocket连接关闭', 'warning', 'observer');
    });

    return ws;
}

// 通过WebSocket发送消息（用户端）
function sendMessageViaWebSocket(types, ws, roomId, content) {
    if (ws.readyState === WebSocket.OPEN) {
        log(`用户端发送消息: ${content}`, 'info', 'user');
        const sendReq = types.SendMessageRequest.create({ roomId, content });
        const sendBytes = types.SendMessageRequest.encode(sendReq).finish();
        const wsSend = wrapMessage(types, 'SEND_MESSAGE', sendBytes);
        ws.send(wsSend);
    } else {
        log('用户端WebSocket连接未就绪，无法发送消息', 'error', 'user');
    }
}

// 主测试函数
async function runTest() {
    log('=== 双端聊天室测试开始 ===');
    
    try {
        // 1. 加载protobuf定义
        const types = await loadProto();
        
        // 2. 商户登录
        const merchantToken = await merchantLogin();
        
        // 3. 用户登录（用于发送测试消息）
        const userToken = await userLogin();
        
        // 4. 获取聊天室信息
        const chatRoom = await getChatRoomInfo(merchantToken);
        const roomId = chatRoom.id;
        log(`聊天室ID: ${roomId}`);
        
        // 5. 获取当前聊天室的验证码
        const currentVerificationCode = await getCurrentVerificationCode(merchantToken);
        
        // 6. 获取用户临时令牌
        const userTempTokenInfo = await getUserTempToken(userToken, currentVerificationCode);
        const userTempToken = userTempTokenInfo.tempToken;
        
        // 6. 建立商户端WebSocket连接（只能接收消息） - 不需要token
        const merchantWs = connectMerchantWebSocket(types, roomId);
        
        // 7. 建立用户端WebSocket连接（用于发送消息）
        const userWs = connectUserWebSocket(types, userTempToken, roomId);
        
        // 8. 建立默认观察者WebSocket连接（无token）
        const observerWs1 = connectObserverWebSocket(types, roomId);
        
        // 9. 建立指定类型的观察者WebSocket连接（guest类型）
        const observerWs2 = connectObserverWebSocket(types, roomId, 'guest');
        
        // 等待连接建立
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // 10. 通过用户端WebSocket发送测试消息
        setTimeout(() => {
            sendMessageViaWebSocket(types, userWs, roomId, '这是一条通过WebSocket发送的测试消息，来自用户端');
        }, 3000);
        
        // 11. 发送第二条测试消息
        setTimeout(() => {
            sendMessageViaWebSocket(types, userWs, roomId, '这是第二条测试消息，所有观察者都应该能收到');
        }, 5000);
        
        // 12. 发送第三条测试消息
        setTimeout(() => {
            sendMessageViaWebSocket(types, userWs, roomId, '测试完成，这是最后一条消息');
        }, 7000);
        
        // 13. 测试持续时间
        setTimeout(() => {
            log('=== 双端聊天室测试结束 ===');
            
            // 发送离开房间请求
            if (userWs.readyState === WebSocket.OPEN) {
                const leaveReq = types.LeaveRoomRequest.create({ roomId });
                const leaveBytes = types.LeaveRoomRequest.encode(leaveReq).finish();
                const wsLeave = wrapMessage(types, 'LEAVE_ROOM', leaveBytes);
                userWs.send(wsLeave);
                log('用户端已发送离开房间请求', 'info', 'user');
            }
            
            if (merchantWs.readyState === WebSocket.OPEN) {
                const leaveReq = types.LeaveRoomRequest.create({ roomId });
                const leaveBytes = types.LeaveRoomRequest.encode(leaveReq).finish();
                const wsLeave = wrapMessage(types, 'LEAVE_ROOM', leaveBytes);
                merchantWs.send(wsLeave);
                log('商户端已发送离开房间请求', 'info', 'merchant');
            }
            
            if (observerWs1.readyState === WebSocket.OPEN) {
                const leaveReq = types.LeaveRoomRequest.create({ roomId });
                const leaveBytes = types.LeaveRoomRequest.encode(leaveReq).finish();
                const wsLeave = wrapMessage(types, 'LEAVE_ROOM', leaveBytes);
                observerWs1.send(wsLeave);
                log('观察者1已发送离开房间请求', 'info', 'observer');
            }
            
            if (observerWs2.readyState === WebSocket.OPEN) {
                const leaveReq = types.LeaveRoomRequest.create({ roomId });
                const leaveBytes = types.LeaveRoomRequest.encode(leaveReq).finish();
                const wsLeave = wrapMessage(types, 'LEAVE_ROOM', leaveBytes);
                observerWs2.send(wsLeave);
                log('观察者2已发送离开房间请求', 'info', 'observer');
            }
            
            // 关闭连接
            setTimeout(() => {
                userWs.close();
                merchantWs.close();
                observerWs1.close();
                observerWs2.close();
                process.exit(0);
            }, 1000);
            
        }, 10000);
        
    } catch (error) {
        log(`测试失败: ${error.message}`, 'error');
        process.exit(1);
    }
}

// 运行测试
runTest();