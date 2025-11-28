const WebSocket = require('ws');
const axios = require('axios');
const path = require('path');
const protobuf = require('protobufjs');

const config = {
  baseUrl: 'http://localhost:8080',
  wsUrl: 'ws://localhost:8080/ws/chat-bin',
  restaurantId: 1,
  verificationCode: '888888',
};

async function loadProto() {
  const protoPath = path.join(__dirname, '../src/main/proto/chat.proto');
  const root = await protobuf.load(protoPath);
  const ns = 'com.ljyh.tabletalk.protobuf';
  return {
    WebSocketMessage: root.lookupType(`${ns}.WebSocketMessage`),
    SendMessageRequest: root.lookupType(`${ns}.SendMessageRequest`),
    JoinRoomRequest: root.lookupType(`${ns}.JoinRoomRequest`),
    LeaveRoomRequest: root.lookupType(`${ns}.LeaveRoomRequest`),
    ChatResponse: root.lookupType(`${ns}.ChatResponse`),
    ChatMessage: root.lookupType(`${ns}.ChatMessage`),
  };
}

async function login(email, password) {
  const resp = await axios.post(`${config.baseUrl}/auth/login`, { email, password });
  if (!resp.data.success) throw new Error(`登录失败: ${resp.data.message}`);
  return resp.data.data.token;
}

async function verifyChatRoom(jwtToken) {
  const resp = await axios.get(
    `${config.baseUrl}/chat-rooms/verify?restaurantId=${config.restaurantId}&verificationCode=${config.verificationCode}`,
    { headers: { Authorization: `Bearer ${jwtToken}` } }
  );
  if (!resp.data.success) throw new Error(`验证码验证失败: ${resp.data.message}`);
  return { tempToken: resp.data.data.tempToken, roomId: resp.data.data.chatRoom.id };
}

function wrapMessage(types, typeStr, payloadBytes) {
  const msg = types.WebSocketMessage.create({ type: typeStr, payload: payloadBytes });
  return types.WebSocketMessage.encode(msg).finish();
}

async function main() {
  try {
    const types = await loadProto();

    const email = 'alex@gmail.com';
    const password = 'jj123456';
    const jwtToken = await login(email, password);
    const { tempToken, roomId } = await verifyChatRoom(jwtToken);

    const ws = new WebSocket(config.wsUrl, {
      headers: { Authorization: `Bearer ${tempToken}` },
    });

    ws.on('open', () => {
      console.log('WebSocket连接成功');
      const joinReq = types.JoinRoomRequest.create({ roomId });
      const joinBytes = types.JoinRoomRequest.encode(joinReq).finish();
      const wsBytes = wrapMessage(types, 'JOIN_ROOM', joinBytes);
      ws.send(wsBytes);
      console.log(`已发送加入房间请求: roomId=${roomId}`);

      setTimeout(() => {
        const sendReq = types.SendMessageRequest.create({ roomId, content: '大家好，这是纯WS+protobuf测试！' });
        const sendBytes = types.SendMessageRequest.encode(sendReq).finish();
        const wsSend = wrapMessage(types, 'SEND_MESSAGE', sendBytes);
        ws.send(wsSend);
        console.log('已发送聊天消息');
      }, 1000);
    });

    ws.on('message', (data) => {
      try {
        const resp = types.ChatResponse.decode(new Uint8Array(data));
        if (!resp.success) {
          console.error('收到错误响应:', resp.errorMessage);
          return;
        }
        if (resp.message) {
          const m = resp.message;
          console.log(`收到聊天室消息: [${m.roomId}] ${m.senderName}: ${m.content}`);
        } else if (resp.joinResponse) {
          console.log(`加入房间成功: roomId=${resp.joinResponse.roomId}`);
        } else if (resp.leaveResponse) {
          console.log(`离开房间成功: roomId=${resp.leaveResponse.roomId}`);
        } else {
          console.log('收到未知成功响应');
        }
      } catch (e) {
        console.error('解析响应失败:', e.message);
      }
    });

    ws.on('error', (err) => {
      console.error('WebSocket错误:', err.message);
    });

    ws.on('close', () => {
      console.log('WebSocket连接关闭');
    });

    // 自动在5秒后发送离开
    setTimeout(() => {
      if (ws.readyState === WebSocket.OPEN) {
        const leaveReq = types.LeaveRoomRequest.create({ roomId });
        const leaveBytes = types.LeaveRoomRequest.encode(leaveReq).finish();
        const wsLeave = wrapMessage(types, 'LEAVE_ROOM', leaveBytes);
        ws.send(wsLeave);
        console.log('已发送离开房间请求');
        setTimeout(() => ws.close(), 1000);
      }
    }, 5000);
  } catch (e) {
    console.error('测试执行失败:', e.message);
    process.exit(1);
  }
}

main();
