const axios = require('axios');
const fs = require('fs');
const path = require('path');

// 配置
const BASE_URL = 'http://localhost:8080/api/v1';
const MERCHANT_LOGIN_URL = `${BASE_URL}/merchant/auth/login`;
const MERCHANT_UPLOAD_URL = `${BASE_URL}/merchant/upload/image`;
const MERCHANT_BATCH_UPLOAD_URL = `${BASE_URL}/merchant/upload/images`;

// 测试商户登录信息
const MERCHANT_CREDENTIALS = {
    username: 'admin_chuanweixuan',  // 实际的商户用户名
    password: 'jj123456'              // 实际的商户密码
};

// 测试文件上传
async function testMerchantFileUpload() {
    let merchantToken;
    
    try {
        console.log('=== 商户端文件上传测试 ===\n');
        
        // 步骤1: 商户登录获取JWT token
        console.log('1. 商户登录...');
        const loginResponse = await axios.post(MERCHANT_LOGIN_URL, MERCHANT_CREDENTIALS);
        
        if (loginResponse.data.success && loginResponse.data.data.token) {
            merchantToken = loginResponse.data.data.token;
            console.log('✅ 商户登录成功');
            console.log(`Token: ${merchantToken.substring(0, 50)}...\n`);
        } else {
            throw new Error('商户登录失败: ' + JSON.stringify(loginResponse.data));
        }
        
        // 步骤2: 测试单文件上传
        console.log('2. 测试单文件上传...');
        
        // 读取真实图片文件
        const image1Path = path.join(__dirname, 'test_image', '1.jpg');
        const image1Buffer = fs.readFileSync(image1Path);
        
        const formData1 = new FormData();
        const blob1 = new Blob([image1Buffer], { type: 'image/jpeg' });
        formData1.append('file', blob1, '1.jpg');
        
        const uploadResponse = await axios.post(MERCHANT_UPLOAD_URL, formData1.getBuffer(), {
            headers: {
                'Authorization': `Bearer ${merchantToken}`,
                'Content-Type': `multipart/form-data; boundary=${formData1.boundary}`
            }
        });
        
        if (uploadResponse.data.success) {
            console.log('✅ 单文件上传成功');
            console.log('上传结果:', JSON.stringify(uploadResponse.data.data, null, 2));
        } else {
            throw new Error('单文件上传失败: ' + JSON.stringify(uploadResponse.data));
        }
        
        // 步骤3: 测试批量文件上传
        console.log('\n3. 测试批量文件上传...');
        
        // 读取两张真实图片
        const image2Path = path.join(__dirname, 'test_image', '2.jpg');
        const image2Buffer = fs.readFileSync(image2Path);
        
        const formData2 = new FormData();
        const blob2_1 = new Blob([image1Buffer], { type: 'image/jpeg' });
        const blob2_2 = new Blob([image2Buffer], { type: 'image/jpeg' });
        formData2.append('files', blob2_1, '1.jpg');
        formData2.append('files', blob2_2, '2.jpg');
        
        const batchUploadResponse = await axios.post(MERCHANT_BATCH_UPLOAD_URL, formData2.getBuffer(), {
            headers: {
                'Authorization': `Bearer ${merchantToken}`,
                'Content-Type': `multipart/form-data; boundary=${formData2.boundary}`
            }
        });
        
        if (batchUploadResponse.data.success) {
            console.log('✅ 批量文件上传成功');
            console.log('上传结果:', JSON.stringify(batchUploadResponse.data.data, null, 2));
        } else {
            throw new Error('批量文件上传失败: ' + JSON.stringify(batchUploadResponse.data));
        }
        
        // 步骤4: 测试无token上传（应该失败）
        console.log('\n4. 测试无token上传（应该失败）...');
        try {
            const unauthorizedResponse = await axios.post(MERCHANT_UPLOAD_URL, formData1.getBuffer(), {
                headers: {
                    'Content-Type': `multipart/form-data; boundary=${formData1.boundary}`
                }
            });
            console.log('❌ 无token上传应该失败但却成功了');
        } catch (error) {
            if (error.response && error.response.status === 401) {
                console.log('✅ 无token上传正确返回401错误');
            } else {
                console.log('⚠️ 无token上传返回了其他错误:', error.message);
            }
        }
        
        // 步骤5: 测试用户token上传（应该失败）
        console.log('\n5. 测试用户token上传（应该失败）...');
        try {
            // 先获取用户token
            const userLoginResponse = await axios.post(`${BASE_URL}/auth/login`, {
                email: 'user@example.com',  // 请替换为实际用户邮箱
                password: 'password123'      // 请替换为实际用户密码
            });
            
            if (userLoginResponse.data.success && userLoginResponse.data.data.token) {
                const userToken = userLoginResponse.data.data.token;
                
                try {
                    const userTokenUploadResponse = await axios.post(MERCHANT_UPLOAD_URL, formData1.getBuffer(), {
                        headers: {
                            'Authorization': `Bearer ${userToken}`,
                            'Content-Type': `multipart/form-data; boundary=${formData1.boundary}`
                        }
                    });
                    console.log('❌ 用户token上传商户接口应该失败但却成功了');
                } catch (uploadError) {
                    if (uploadError.response && uploadError.response.status === 401) {
                        console.log('✅ 用户token上传商户接口正确返回401错误');
                    } else {
                        console.log('⚠️ 用户token上传商户接口返回了其他错误:', uploadError.message);
                    }
                }
            } else {
                console.log('⚠️ 无法获取用户token，跳过此测试');
            }
        } catch (userError) {
            console.log('⚠️ 用户登录失败，跳过此测试:', userError.message);
        }
        
        console.log('\n=== 测试完成 ===');
        
    } catch (error) {
        console.error('❌ 测试失败:', error.message);
        if (error.response) {
            console.error('响应数据:', JSON.stringify(error.response.data, null, 2));
            console.error('响应状态:', error.response.status);
        }
    }
}

// 简单的FormData实现（Node.js环境）
class FormData {
    constructor() {
        this.boundary = '----WebKitFormBoundary' + Math.random().toString(36).substr(2, 16);
        this.data = [];
    }
    
    append(name, value, filename) {
        let header = `--${this.boundary}\r\n`;
        if (filename) {
            header += `Content-Disposition: form-data; name="${name}"; filename="${filename}"\r\n`;
            header += `Content-Type: ${value.type || 'application/octet-stream'}\r\n\r\n`;
        } else {
            header += `Content-Disposition: form-data; name="${name}"\r\n\r\n`;
        }
        this.data.push(Buffer.from(header, 'utf8'));
        this.data.push(Buffer.isBuffer(value) ? value : Buffer.from(String(value), 'utf8'));
        this.data.push(Buffer.from('\r\n', 'utf8'));
    }
    
    getLength() {
        const footer = `\r\n--${this.boundary}--\r\n`;
        return this.data.reduce((sum, buf) => sum + buf.length, 0) + Buffer.byteLength(footer, 'utf8');
    }
    
    getBuffer() {
        const footer = `\r\n--${this.boundary}--\r\n`;
        return Buffer.concat([...this.data, Buffer.from(footer, 'utf8')]);
    }
}

// 运行测试
if (require.main === module) {
    testMerchantFileUpload();
}

module.exports = { testMerchantFileUpload };