# TableTalk 后端开发指南

## 项目概述

TableTalk 是一个基于 Spring Boot 的餐厅服务应用后端系统，提供用户认证、餐厅浏览、评论查看、即时聊天等功能。

## 环境要求

### 开发环境
- **Java**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **IDE**: IntelliJ IDEA / VS Code

### 测试环境
- **Docker**: 20.10+
- **Docker Compose**: 2.0+

### 生产环境
- **Kubernetes**: 1.24+
- **Nginx**: 1.20+

## 快速开始

### 1. 环境准备

#### 安装 Java
```bash
# 使用 SDKMAN 安装 Java
sdk install java 17.0.2-open
sdk use java 17.0.2-open

# 或者下载并配置环境变量
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH
```

#### 安装 MySQL
```bash
# macOS (使用 Homebrew)
brew install mysql
brew services start mysql

# Ubuntu
sudo apt update
sudo apt install mysql-server

# Windows
# 下载 MySQL Installer 并安装
```

#### 安装 Redis
```bash
# macOS (使用 Homebrew)
brew install redis
brew services start redis

# Ubuntu
sudo apt install redis-server
sudo systemctl start redis

# Windows
# 下载 Redis for Windows 并安装
```

### 2. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql

# 或者使用命令行
mysql -u root -p < database/init.sql
```

### 3. 项目结构

```
tabletalk-backend/
├── tabletalk-user-service/          # 用户服务
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tabletalk/user/
│   │   │   │   ├── controller/      # 控制器
│   │   │   │   ├── service/         # 业务逻辑
│   │   │   │   ├── repository/      # 数据访问
│   │   │   │   ├── model/           # 数据模型
│   │   │   │   └── config/          # 配置类
│   │   │   └── resources/
│   │   │       ├── application.yml  # 应用配置
│   │   │       └── db/migration/    # 数据库迁移
│   │   └── test/                    # 测试代码
│   └── pom.xml
├── tabletalk-restaurant-service/    # 餐厅服务
├── tabletalk-chat-service/          # 聊天服务
├── tabletalk-gateway/               # API网关
├── tabletalk-common/                # 公共模块
└── docker-compose.yml               # Docker编排
```

### 4. 配置说明

#### 应用配置文件 (application.yml)
```yaml
server:
  port: 8080
  servlet:
    context-path: /api/v1

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tabletalk?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  
  security:
    oauth2:
      resourceserver:
        jwt:
          secret-key: your-jwt-secret-key

logging:
  level:
    com.tabletalk: DEBUG
    org.springframework.security: INFO

app:
  jwt:
    secret: your-jwt-secret
    expiration: 7200  # 2小时
    refresh-expiration: 604800  # 7天
```

### 5. 构建和运行

#### 使用 Maven 构建
```bash
# 清理并构建项目
mvn clean compile

# 运行测试
mvn test

# 打包
mvn package -DskipTests

# 运行应用
mvn spring-boot:run
```

#### 使用 Docker 运行
```bash
# 构建镜像
docker build -t tabletalk-user-service .

# 运行容器
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/tabletalk \
  -e SPRING_REDIS_HOST=host.docker.internal \
  tabletalk-user-service
```

#### 使用 Docker Compose (推荐)
```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 停止服务
docker-compose down
```

### 6. 开发规范

#### 代码规范
- 遵循 Google Java 代码风格
- 使用 Lombok 减少样板代码
- 使用 SLF4J 进行日志记录
- 使用 JUnit 5 进行单元测试

#### API 开发规范
- 遵循 RESTful API 设计原则
- 使用统一的响应格式
- 实现完整的错误处理
- 添加 API 文档注释

#### 示例代码

##### 控制器示例
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserDTO user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user));
    }
}
```

##### 服务层示例
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("用户不存在: " + id));
        return userMapper.toDTO(user);
    }
    
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserEmailExistsException("邮箱已存在: " + request.getEmail());
        }
        
        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        
        log.info("创建用户成功: {}", savedUser.getEmail());
        return userMapper.toDTO(savedUser);
    }
}
```

##### 数据访问层示例
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findByStatus(@Param("status") UserStatus status);
}
```

### 7. 测试指南

#### 单元测试
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com", "password123", "测试用户");
        
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        
        // When
        UserDTO result = userService.createUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
        verify(userRepository).save(any(User.class));
    }
}
```

#### 集成测试
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldGetUserById() {
        // When
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
            "/api/v1/users/1", ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
    }
}
```

### 8. 部署指南

#### 开发环境部署
```bash
# 使用 Docker Compose
docker-compose -f docker-compose.dev.yml up -d

# 或者直接运行
java -jar target/tabletalk-user-service-1.0.0.jar
```

#### 生产环境部署
```bash
# 使用 Kubernetes
kubectl apply -f k8s/

# 或者使用 Docker Swarm
docker stack deploy -c docker-compose.prod.yml tabletalk
```

### 9. 监控和日志

#### 应用监控
```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 指标查看
curl http://localhost:8080/actuator/metrics

# 应用信息
curl http://localhost:8080/actuator/info
```

#### 日志查看
```bash
# 查看应用日志
tail -f logs/tabletalk.log

# 使用 ELK Stack
# 配置 Logstash 收集日志，Kibana 查看日志
```

### 10. 故障排除

#### 常见问题

1. **数据库连接失败**
   - 检查 MySQL 服务是否启动
   - 验证数据库连接配置
   - 检查网络连接

2. **Redis 连接失败**
   - 检查 Redis 服务是否启动
   - 验证 Redis 配置
   - 检查防火墙设置

3. **JWT 认证失败**
   - 检查 JWT 密钥配置
   - 验证令牌格式
   - 检查令牌过期时间

4. **应用启动失败**
   - 检查端口是否被占用
   - 验证配置文件格式
   - 查看启动日志

#### 调试技巧
```java
// 添加调试日志
@Slf4j
@Service
public class UserService {
    public UserDTO getUserById(Long id) {
        log.debug("获取用户信息: {}", id);
        // ...
    }
}

// 使用断点调试
// 在 IDE 中设置断点进行调试
```

### 11. 性能优化建议

#### 数据库优化
- 合理使用索引
- 避免 N+1 查询问题
- 使用连接池配置

#### 缓存优化
- 合理设置缓存过期时间
- 使用多级缓存策略
- 监控缓存命中率

#### JVM 优化
```bash
# JVM 参数示例
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar your-app.jar
```

## 总结

本指南提供了 TableTalk 后端项目的完整开发流程，从环境准备到部署运维。开发团队可以按照此指南快速开始项目开发工作。

如有问题，请参考相关文档或联系项目负责人。