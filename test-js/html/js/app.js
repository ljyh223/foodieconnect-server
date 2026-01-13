// Proto文件定义
const protoDefinition = `
syntax = "proto3";

package com.ljyh.foodieconnect.protobuf;

option java_package = "com.ljyh.foodieconnect.protobuf";
option java_outer_classname = "ChatProtos";

enum MessageType {
    TEXT = 0;
    IMAGE = 1;
    SYSTEM = 2;
}

message ChatMessage {
    int64 id = 1;
    int64 room_id = 2;
    int64 sender_id = 3;
    string content = 4;
    MessageType message_type = 5;
    string sender_name = 6;
    string sender_avatar = 7;
    string timestamp = 8;
}

message SendMessageRequest {
    int64 room_id = 1;
    string content = 2;
}

message JoinRoomRequest {
    int64 room_id = 1;
}

message LeaveRoomRequest {
    int64 room_id = 1;
}

message ChatResponse {
    bool success = 1;
    string error_message = 2;
    oneof payload {
        ChatMessage message = 3;
        JoinRoomResponse join_response = 4;
        LeaveRoomResponse leave_response = 5;
    }
}

message JoinRoomResponse {
    int64 room_id = 1;
    string message = 2;
}

message LeaveRoomResponse {
    int64 room_id = 1;
    string message = 2;
}

message WebSocketMessage {
    string type = 1;
    bytes payload = 2;
}
`;

// 等待protobuf库加载完成后初始化
let protobufLoaded = false;
let protobufRoot = null;

async function loadProtoDefinition() {
    if (protobufLoaded && protobufRoot) {
        return protobufRoot;
    }

    return new Promise((resolve, reject) => {
        if (typeof protobuf !== 'undefined') {
            try {
                // 使用protobuf.parse解析字符串定义
                const root = protobuf.parse(protoDefinition).root;
                protobufRoot = root;
                protobufLoaded = true;
                resolve(root);
            } catch (err) {
                console.error('加载protobuf定义失败:', err);
                reject(err);
            }
        } else {
            reject(new Error('protobuf库未加载'));
        }
    });
}

const { createApp, ref, reactive, computed, watch, nextTick, onMounted } = Vue;

const app = createApp({
    setup() {
        // 配置信息
        const config = reactive({
            apiUrl: 'http://localhost:8080/api/v1',
            wsBaseUrl: 'ws://localhost:8080/api/v1/ws/chat-bin'
        });

        // 角色定义
        const roles = [
            {
                id: 'merchant',
                name: '商家端',
                icon: 'M',
                description: '商户管理，只能接收消息'
            },
            {
                id: 'user',
                name: '用户端',
                icon: 'U',
                description: '顾客，可以发送和接收消息'
            },
            {
                id: 'observer',
                name: '观察者',
                icon: 'O',
                description: '无登录，仅接收消息'
            }
        ];

        // 登录状态
        const isLoggedIn = ref(false);
        const currentRole = ref('');
        const currentUserName = ref('');
        const selectedRole = ref('merchant');
        const isProtoLoaded = ref(false);

        // 登录表单
        const merchantLoginForm = reactive({
            username: 'admin_chuanweixuan',
            password: 'jj123456'
        });

        const userLoginForm = reactive({
            email: 'alex@gmail.com',
            password: 'jj123456',
            restaurantId: 1,
            verificationCode: '972889'
        });

        const observerForm = reactive({
            type: ''
        });

        // 认证令牌
        const merchantToken = ref('');
        const userToken = ref('');
        const userTempToken = ref('');

        // WebSocket状态
        const wsConnected = ref(false);
        let ws = null;
        let protoTypes = null;

        // 房间信息
        const roomId = ref('');
        const inRoom = ref(false);

        // 消息相关
        const messages = ref([]);
        const newMessage = ref('');
        const messageFilter = ref('all');

        // 日志
        const logs = ref([]);

        // 快捷消息
        const quickMessages = [
            '你好，请问有什么可以帮助您的？',
            '好的，我知道了',
            '请稍等，我马上处理',
            '感谢您的反馈',
            '这道菜是我们的特色菜',
            '请问您需要什么饮品？',
            '账单已经准备好了',
            '欢迎光临！'
        ];

        // 计算属性
        const filteredMessages = computed(() => {
            if (messageFilter.value === 'all') {
                return messages.value;
            }
            if (messageFilter.value === 'own') {
                return messages.value.filter(msg => msg.direction === 'sent');
            }
            return messages.value.filter(msg => msg.role === messageFilter.value);
        });

        const canSendMessage = computed(() => {
            return currentRole.value === 'user' && wsConnected.value && inRoom.value;
        });

        const canJoinRoom = computed(() => {
            if (currentRole.value === 'observer') {
                return true;
            }
            if (currentRole.value === 'user') {
                return !!roomId.value && !!userTempToken.value;
            }
            return !!roomId.value;
        });

        // 角色名称映射
        const roleNameMap = {
            merchant: '商家端',
            user: '用户端',
            observer: '观察者',
            system: '系统'
        };

        // 方法定义
        const selectRole = (roleId) => {
            selectedRole.value = roleId;
        };

        const addLog = (message, type = 'info', source = 'system') => {
            const timestamp = new Date();
            logs.value.push({
                timestamp,
                message,
                type,
                source,
                sourceName: roleNameMap[source] || '系统'
            });

            // 自动滚动到最新日志
            nextTick(() => {
                const logsContainer = document.querySelector('.logs-list');
                if (logsContainer) {
                    logsContainer.scrollTop = logsContainer.scrollHeight;
                }
            });
        };

        const clearLogs = () => {
            logs.value = [];
        };

        const formatTime = (date) => {
            if (!date) return '';
            const d = new Date(date);
            return d.toLocaleTimeString('zh-CN', {
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
        };

        const getRoleIcon = (role) => {
            const icons = {
                merchant: 'M',
                user: 'U',
                observer: 'O',
                system: 'S'
            };
            return icons[role] || role.charAt(0).toUpperCase();
        };

        // 初始化protobuf
        const initProtobuf = async () => {
            try {
                const root = await loadProtoDefinition();
                protoTypes = {
                    WebSocketMessage: root.lookupType('com.ljyh.foodieconnect.protobuf.WebSocketMessage'),
                    SendMessageRequest: root.lookupType('com.ljyh.foodieconnect.protobuf.SendMessageRequest'),
                    JoinRoomRequest: root.lookupType('com.ljyh.foodieconnect.protobuf.JoinRoomRequest'),
                    LeaveRoomRequest: root.lookupType('com.ljyh.foodieconnect.protobuf.LeaveRoomRequest'),
                    ChatResponse: root.lookupType('com.ljyh.foodieconnect.protobuf.ChatResponse'),
                    ChatMessage: root.lookupType('com.ljyh.foodieconnect.protobuf.ChatMessage')
                };
                isProtoLoaded.value = true;
                addLog('protobuf初始化成功', 'debug', 'system');
            } catch (error) {
                addLog(`protobuf初始化失败: ${error.message}`, 'error', 'system');
            }
        };

        // 包装WebSocket消息
        const wrapMessage = (typeStr, payloadBytes) => {
            if (!protoTypes) {
                throw new Error('protobuf未初始化');
            }
            // 确保payload是Uint8Array
            const payload = payloadBytes instanceof Uint8Array ? payloadBytes : new Uint8Array(payloadBytes);
            const msg = protoTypes.WebSocketMessage.create({ type: typeStr, payload: payload });
            return protoTypes.WebSocketMessage.encode(msg).finish();
        };

        // 商家登录
        const loginAsMerchant = async () => {
            try {
                addLog('开始商家登录...', 'info', 'merchant');
                const response = await fetch(`${config.apiUrl}/merchant/auth/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: merchantLoginForm.username,
                        password: merchantLoginForm.password
                    })
                });

                if (!response.ok) {
                    throw new Error('登录失败');
                }

                const data = await response.json();
                merchantToken.value = data.data.token;
                currentRole.value = 'merchant';
                currentUserName.value = merchantLoginForm.username;
                isLoggedIn.value = true;
                addLog('商家登录成功', 'info', 'merchant');

                // 获取聊天室信息
                await getChatRoomInfo();
            } catch (error) {
                addLog(`商家登录失败: ${error.message}`, 'error', 'merchant');
                alert(`登录失败: ${error.message}`);
            }
        };

        // 用户登录
        const loginAsUser = async () => {
            try {
                addLog('开始用户登录...', 'info', 'user');
                const response = await fetch(`${config.apiUrl}/auth/login`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        email: userLoginForm.email,
                        password: userLoginForm.password
                    })
                });

                if (!response.ok) {
                    throw new Error('登录失败');
                }

                const data = await response.json();
                userToken.value = data.data.token;
                addLog('用户登录成功，正在获取临时令牌...', 'info', 'user');

                // 获取用户临时令牌
                await getUserTempToken();

                // 设置角色和登录状态
                currentRole.value = 'user';
                currentUserName.value = userLoginForm.email;
                isLoggedIn.value = true;
                addLog(`用户端登录成功，房间ID: ${roomId.value}`, 'info', 'user');
            } catch (error) {
                addLog(`用户登录失败: ${error.message}`, 'error', 'user');
                alert(`登录失败: ${error.message}`);
            }
        };

        // 观察者登录
        const loginAsObserver = () => {
            currentRole.value = 'observer';
            currentUserName.value = observerForm.type
                ? `观察者 (${observerForm.type})`
                : '观察者';
            isLoggedIn.value = true;
            addLog(`观察者已加入${observerForm.type ? ` (${observerForm.type})` : ''}`, 'info', 'observer');
        };

        // 获取聊天室信息
        const getChatRoomInfo = async () => {
            try {
                addLog('获取聊天室信息...', 'debug', 'merchant');
                const response = await fetch(`${config.apiUrl}/merchant/chat-rooms`, {
                    headers: {
                        Authorization: `Bearer ${merchantToken.value}`
                    }
                });

                if (!response.ok) {
                    throw new Error('获取聊天室信息失败');
                }

                const data = await response.json();
                roomId.value = data.data.id;
                addLog(`获取聊天室信息成功，房间ID: ${roomId.value}`, 'info', 'merchant');
            } catch (error) {
                addLog(`获取聊天室信息失败: ${error.message}`, 'error', 'merchant');
            }
        };

        // 获取用户临时令牌
        const getUserTempToken = async () => {
            try {
                addLog('获取用户临时令牌...', 'debug', 'user');
                const response = await fetch(
                    `${config.apiUrl}/chat-rooms/verify?restaurantId=${userLoginForm.restaurantId}&verificationCode=${userLoginForm.verificationCode}`,
                    {
                        headers: {
                            Authorization: `Bearer ${userToken.value}`
                        }
                    }
                );

                if (!response.ok) {
                    throw new Error('获取临时令牌失败');
                }

                const data = await response.json();
                userTempToken.value = data.data.tempToken;
                // 现在chatRoom是DTO，所以roomId在chatRoom对象中
                roomId.value = data.data.chatRoom.id || userLoginForm.restaurantId;
                addLog(`获取用户临时令牌成功，房间ID: ${roomId.value}`, 'info', 'user');
            } catch (error) {
                addLog(`获取用户临时令牌失败: ${error.message}`, 'error', 'user');
                throw error;
            }
        };

        // 建立WebSocket连接
        const connectWebSocket = () => {
            if (!roomId.value) {
                addLog('无法连接WebSocket：房间ID为空', 'error', 'system');
                return;
            }

            let wsUrl = `${config.wsBaseUrl}/${roomId.value}`;
            let protocols = null;

            // 根据角色设置WebSocket连接
            if (currentRole.value === 'user' && userTempToken.value) {
                // 用户端：尝试在协议中传递token
                addLog('使用临时令牌建立用户端WebSocket连接...', 'info', 'user');
                // 注意：浏览器WebSocket不支持自定义headers，token通过protocol传递或URL参数
                wsUrl = `${config.wsBaseUrl}/${roomId.value}?token=${encodeURIComponent(userTempToken.value)}`;
            } else if (currentRole.value === 'observer' && observerForm.type) {
                // 观察者带类型
                wsUrl = `${config.wsBaseUrl}/${roomId.x}?observer=${observerForm.type}`;
                addLog(`建立观察者WebSocket连接 (${observerForm.type})...`, 'info', 'observer');
            } else {
                // 商家端或默认观察者
                addLog(`建立${currentRole.value === 'merchant' ? '商家端' : '默认观察者'}WebSocket连接...`, 'info', currentRole.value);
            }

            addLog(`WebSocket URL: ${wsUrl}`, 'debug', currentRole.value);

            // 创建WebSocket连接
            ws = new WebSocket(wsUrl);

            ws.binaryType = 'arraybuffer';

            ws.onopen = () => {
                addLog('WebSocket连接成功', 'info', currentRole.value);
                wsConnected.value = true;
                // 发送加入房间请求
                setTimeout(() => {
                    sendJoinRoomRequest();
                }, 100);
            };

            ws.onmessage = (event) => {
                try {
                    const data = event.data;
                    if (data instanceof ArrayBuffer && protoTypes) {
                        // 二进制protobuf格式
                        const resp = protoTypes.ChatResponse.decode(new Uint8Array(data));
                        handleChatResponse(resp);
                    } else if (typeof data === 'string') {
                        // JSON格式
                        try {
                            const json = JSON.parse(data);
                            handleChatResponseJSON(json);
                        } catch (e) {
                            addLog(`解析JSON消息失败: ${e.message}`, 'error', currentRole.value);
                        }
                    } else {
                        addLog(`收到未知格式消息，类型: ${typeof data}`, 'debug', currentRole.value);
                    }
                } catch (e) {
                    addLog(`解析WebSocket消息失败: ${e.message}`, 'error', currentRole.value);
                }
            };

            ws.onerror = (error) => {
                addLog(`WebSocket错误: ${error.message || '连接错误'}`, 'error', currentRole.value);
            };

            ws.onclose = (event) => {
                addLog(`WebSocket连接关闭 (code: ${event.code})`, 'warning', currentRole.value);
                wsConnected.value = false;
                inRoom.value = false;
            };
        };

        // 处理protobuf格式的响应
        const handleChatResponse = (resp) => {
            if (!resp.success) {
                addLog(`收到错误响应: ${resp.errorMessage}`, 'error', currentRole.value);
                return;
            }

            if (resp.message) {
                const m = resp.message;
                const isOwnMessage = m.senderName === currentUserName.value;
                messages.value.push({
                    role: 'user', // 从message_type判断
                    senderName: m.senderName,
                    content: m.content,
                    timestamp: new Date(m.timestamp),
                    direction: isOwnMessage ? 'sent' : 'received'
                });
                addLog(`收到消息: [${m.roomId}] ${m.senderName}: ${m.content}`, 'info', 'user');
            } else if (resp.joinResponse) {
                inRoom.value = true;
                addLog(`加入房间成功: roomId=${resp.joinResponse.roomId}`, 'info', currentRole.value);
            } else if (resp.leaveResponse) {
                inRoom.value = false;
                addLog(`离开房间成功: roomId=${resp.leaveResponse.roomId}`, 'info', currentRole.value);
            }
        };

        // 处理JSON格式的响应
        const handleChatResponseJSON = (data) => {
            if (data.type === 'CHAT_RESPONSE' || data.type === 'CHAT_RESPONSE_SUCCESS') {
                const response = data.payload || data;
                handleChatResponse(response);
            } else if (data.type === 'JOIN_ROOM_SUCCESS' || data.type === 'JOIN_ROOM') {
                inRoom.value = true;
                addLog(`已加入房间: ${roomId.value}`, 'info', currentRole.value);
            } else if (data.type === 'LEAVE_ROOM_SUCCESS' || data.type === 'LEAVE_ROOM') {
                inRoom.value = false;
                addLog(`已离开房间: ${roomId.value}`, 'info', currentRole.value);
            } else if (data.type === 'SEND_MESSAGE_SUCCESS') {
                addLog('消息发送成功', 'info', currentRole.value);
            } else if (data.success === true && data.message) {
                // 直接的ChatMessage响应
                const m = data.message;
                const isOwnMessage = m.senderName === currentUserName.value;
                messages.value.push({
                    role: m.senderRole || 'user',
                    senderName: m.senderName,
                    content: m.content,
                    timestamp: new Date(),
                    direction: isOwnMessage ? 'sent' : 'received'
                });
                addLog(`收到消息: [${m.roomId || roomId.value}] ${m.senderName}: ${m.content}`, 'info', m.senderRole || 'user');
            }
        };

        // 发送加入房间请求
        const sendJoinRoomRequest = () => {
            if (!ws || ws.readyState !== WebSocket.OPEN) {
                addLog('WebSocket未连接，无法发送加入房间请求', 'warning', currentRole.value);
                return;
            }
            if (!protoTypes) {
                addLog('protobuf未初始化，使用JSON格式发送', 'warning', 'system');
                ws.send(JSON.stringify({
                    type: 'JOIN_ROOM',
                    payload: { roomId: Number(roomId.value) }
                }));
                return;
            }

            try {
                const joinReq = protoTypes.JoinRoomRequest.create({ roomId: Number(roomId.value) });
                const joinBytes = protoTypes.JoinRoomRequest.encode(joinReq).finish();
                const wsBytes = wrapMessage('JOIN_ROOM', joinBytes);
                ws.send(new Uint8Array(wsBytes));
                addLog('发送JOIN_ROOM请求', 'info', currentRole.value);
            } catch (e) {
                addLog(`发送JOIN_ROOM失败: ${e.message}，尝试JSON格式`, 'warning', currentRole.value);
                ws.send(JSON.stringify({
                    type: 'JOIN_ROOM',
                    payload: { roomId: Number(roomId.value) }
                }));
            }
        };

        // 发送离开房间请求
        const sendLeaveRoomRequest = () => {
            if (!ws || ws.readyState !== WebSocket.OPEN) {
                return;
            }
            if (!protoTypes) {
                ws.send(JSON.stringify({
                    type: 'LEAVE_ROOM',
                    payload: { roomId: Number(roomId.value) }
                }));
                return;
            }

            try {
                const leaveReq = protoTypes.LeaveRoomRequest.create({ roomId: Number(roomId.value) });
                const leaveBytes = protoTypes.LeaveRoomRequest.encode(leaveReq).finish();
                const wsBytes = wrapMessage('LEAVE_ROOM', leaveBytes);
                ws.send(new Uint8Array(wsBytes));
                addLog('发送LEAVE_ROOM请求', 'info', currentRole.value);
            } catch (e) {
                addLog(`发送LEAVE_ROOM失败: ${e.message}，尝试JSON格式`, 'warning', currentRole.value);
                ws.send(JSON.stringify({
                    type: 'LEAVE_ROOM',
                    payload: { roomId: Number(roomId.value) }
                }));
            }
        };

        // 发送消息请求
        const sendMessageRequest = (content) => {
            if (!ws || ws.readyState !== WebSocket.OPEN) {
                return;
            }
            if (!protoTypes) {
                ws.send(JSON.stringify({
                    type: 'SEND_MESSAGE',
                    payload: { roomId: Number(roomId.value), content: content }
                }));
                return;
            }

            try {
                const sendReq = protoTypes.SendMessageRequest.create({
                    roomId: Number(roomId.value),
                    content: content
                });
                const sendBytes = protoTypes.SendMessageRequest.encode(sendReq).finish();
                const wsBytes = wrapMessage('SEND_MESSAGE', sendBytes);
                ws.send(new Uint8Array(wsBytes));
                addLog(`发送SEND_MESSAGE请求: ${content}`, 'info', currentRole.value);
            } catch (e) {
                addLog(`发送SEND_MESSAGE失败: ${e.message}，尝试JSON格式`, 'warning', currentRole.value);
                ws.send(JSON.stringify({
                    type: 'SEND_MESSAGE',
                    payload: { roomId: Number(roomId.value), content: content }
                }));
            }
        };

        // 加入房间
        const joinRoom = () => {
            if (!canJoinRoom.value) {
                if (currentRole.value === 'user' && !userTempToken.value) {
                    addLog('无法加入房间：临时令牌获取失败，请重新登录', 'error', 'user');
                } else {
                    addLog('无法加入房间：条件不满足', 'error', 'system');
                }
                return;
            }

            // 连接WebSocket
            connectWebSocket();
        };

        // 离开房间
        const leaveRoom = () => {
            sendLeaveRoomRequest();
        };

        // 发送消息
        const sendMessage = () => {
            if (!newMessage.value.trim()) return;
            if (!ws || ws.readyState !== WebSocket.OPEN) {
                addLog('WebSocket未连接，无法发送消息', 'error', currentRole.value);
                alert('请先加入房间');
                return;
            }

            const content = newMessage.value.trim();

            // 使用protobuf包装消息发送
            sendMessageRequest(content);

            // 显示自己发送的消息
            messages.value.push({
                role: currentRole.value,
                senderName: currentUserName.value,
                content: content,
                timestamp: new Date(),
                direction: 'sent'
            });

            newMessage.value = '';

            // 滚动到最新消息
            nextTick(() => {
                const messagesContainer = document.querySelector('.messages-list');
                if (messagesContainer) {
                    messagesContainer.scrollTop = messagesContainer.scrollHeight;
                }
            });
        };

        // 发送快捷消息
        const sendQuickMessage = (msg) => {
            newMessage.value = msg;
            sendMessage();
        };

        // 退出登录
        const logout = () => {
            // 离开房间
            if (inRoom.value) {
                sendLeaveRoomRequest();
            }

            // 关闭WebSocket
            if (ws) {
                ws.close();
                ws = null;
            }

            // 重置状态
            isLoggedIn.value = false;
            currentRole.value = '';
            currentUserName.value = '';
            merchantToken.value = '';
            userToken.value = '';
            userTempToken.value = '';
            wsConnected.value = false;
            roomId.value = '';
            inRoom.value = false;
            messages.value = [];
            logs.value = [];
            newMessage.value = '';
            messageFilter.value = 'all';

            addLog('已退出登录', 'info', 'system');
        };

        // 监听消息变化，自动滚动
        watch(messages, () => {
            nextTick(() => {
                const messagesContainer = document.querySelector('.messages-list');
                if (messagesContainer) {
                    messagesContainer.scrollTop = messagesContainer.scrollHeight;
                }
            });
        }, { deep: true });

        // 页面加载时初始化protobuf
        onMounted(() => {
            initProtobuf();
        });

        return {
            // 配置和状态
            config,
            roles,
            isLoggedIn,
            currentRole,
            currentUserName,
            selectedRole,
            merchantLoginForm,
            userLoginForm,
            observerForm,
            wsConnected,
            roomId,
            inRoom,
            messages,
            newMessage,
            messageFilter,
            logs,
            quickMessages,
            isProtoLoaded,

            // 计算属性
            filteredMessages,
            canSendMessage,
            canJoinRoom,

            // 方法
            selectRole,
            loginAsMerchant,
            loginAsUser,
            loginAsObserver,
            joinRoom,
            leaveRoom,
            sendMessage,
            sendQuickMessage,
            logout,
            clearLogs,
            formatTime,
            getRoleIcon
        };
    }
});

app.mount('#app');
