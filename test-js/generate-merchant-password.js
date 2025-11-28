const bcrypt = require('bcryptjs');

// 明文密码
const plainPassword = 'jj123456';

// 生成BCrypt加密的密码
bcrypt.hash(plainPassword, 10, (err, hash) => {
    if (err) {
        console.error('密码加密失败:', err);
        return;
    }
    
    console.log('明文密码:', plainPassword);
    console.log('BCrypt加密后的密码:', hash);
    
    // 验证密码
    bcrypt.compare(plainPassword, hash, (err, result) => {
        if (err) {
            console.error('密码验证失败:', err);
            return;
        }
        console.log('密码验证结果:', result);
    });
});