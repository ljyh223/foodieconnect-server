const { fork } = require('child_process');
const path = require('path');

console.log('启动双用户聊天测试...');
console.log('=====================================');

// 启动第一个用户
const user1 = fork(path.join(__dirname, 'flutter-websocket-test.js'), ['--account=1', '--interactive'], {
  silent: true
});

// 启动第二个用户
const user2 = fork(path.join(__dirname, 'flutter-websocket-test.js'), ['--account=2', '--interactive'], {
  silent: true
});

// 处理用户1的输出
user1.stdout.on('data', (data) => {
  console.log(`[用户1] ${data.toString().trim()}`);
});

user1.stderr.on('data', (data) => {
  console.error(`[用户1 错误] ${data.toString().trim()}`);
});

// 处理用户2的输出
user2.stdout.on('data', (data) => {
  console.log(`[用户2] ${data.toString().trim()}`);
});

user2.stderr.on('data', (data) => {
  console.error(`[用户2 错误] ${data.toString().trim()}`);
});

// 等待一段时间让两个用户都连接成功
setTimeout(() => {
  console.log('\n--- 加入聊天室 ---\n');
  
  // 两个用户都先加入聊天室
  user1.stdin.write('join\n');
  user2.stdin.write('join\n');
  
  // 等待加入完成后发送消息
  setTimeout(() => {
    console.log('\n--- 发送测试消息 ---\n');
    
    // 用户1发送消息
    user1.stdin.write('send 你好，我是用户1\n');
    
    // 等待一秒后用户2回复
    setTimeout(() => {
      user2.stdin.write('send 你好用户1，我是用户2\n');
    }, 2000);
    
    // 再等待一秒后用户1再发送一条消息
    setTimeout(() => {
      user1.stdin.write('send 很高兴认识你！\n');
    }, 4000);
    
    // 最后退出
    setTimeout(() => {
      console.log('\n--- 结束测试 ---\n');
      user1.stdin.write('quit\n');
      user2.stdin.write('quit\n');
    }, 6000);
  }, 3000); // 等待3秒让加入操作完成
}, 10000); // 等待10秒让两个用户都连接成功

// 处理进程退出
user1.on('close', (code) => {
  console.log(`用户1进程退出，代码: ${code}`);
});

user2.on('close', (code) => {
  console.log(`用户2进程退出，代码: ${code}`);
});