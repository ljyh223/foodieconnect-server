# Foodie Connect

一个使用Flutter 开发的餐厅互动app，目前正在开发中，尽情期待


### UI设计稿

![](/UI.png)

## 测试指南

### 测试环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 运行测试

#### 运行所有测试

```bash
mvn test
```

#### 运行特定测试类

```bash
mvn test -Dtest=AuthControllerTest
```

#### 运行特定测试方法

```bash
mvn test -Dtest=AuthControllerTest#testLoginSuccess
```

#### 运行多个测试类

```bash
mvn test -Dtest=AuthControllerTest,ChatRoomControllerTest
```

#### 运行特定包下的所有测试

```bash
mvn test -Dtest="com.ljyh.foodieconnect.controller.*Test"
```

### 测试结构

项目的测试代码位于 `src/test/java` 目录下，按照功能模块组织：

```
src/test/java/com/ljyh/tabletalk/
├── controller/          # 控制器层测试
│   ├── AuthControllerTest.java          # 用户认证测试
│   ├── ChatRoomControllerTest.java      # 聊天室测试
│   ├── FavoriteFoodControllerTest.java  # 喜好食物测试
│   ├── FollowControllerTest.java        # 用户关注测试
│   ├── RecommendationControllerTest.java  # 餐厅推荐测试
│   ├── RestaurantControllerTest.java    # 餐厅管理测试
│   ├── ReviewControllerTest.java        # 评论管理测试
│   ├── UserControllerTest.java          # 用户管理测试
│   └── UserRecommendationControllerTest # 用户推荐测试
├── recommendation/      # 推荐算法测试
│   ├── CollaborativeFilteringAlgorithmTest.java  # 协同过滤算法测试
│   ├── HybridRecommendationStrategyTest.java     # 混合推荐策略测试
│   └── SocialRecommendationAlgorithmTest.java    # 社交推荐算法测试
├── service/             # 服务层测试
│   └── UserRecommendationServiceTest.java        # 用户推荐服务测试
├── websocket/           # WebSocket测试
│   └── ProtobufMessageTest.java                 # Protobuf消息测试
└── TabletalkApplicationTests.java  # 应用集成测试
```

### 测试类型

1. **单元测试**：测试单个组件或方法的功能
2. **集成测试**：测试多个组件之间的交互
3. **API测试**：测试REST API端点的功能
4. **算法测试**：测试推荐算法的准确性和性能
5. **WebSocket测试**：测试实时通信功能

### 测试框架

- **Spring Boot Test**：提供Spring应用的测试支持
- **JUnit 5**：测试框架
- **Mockito**：模拟框架，用于模拟依赖对象
- **MockMvc**：用于测试Spring MVC控制器
- **Spring Security Test**：用于测试安全相关功能


### 常见问题

#### 测试失败时如何查看详细日志

```bash
mvn test -Dtest=AuthControllerTest -X
```

#### 如何跳过特定测试

在测试方法上添加 `@Disabled` 注解：

```java
@Disabled("暂时跳过此测试")
@Test
void testSomeFeature() {
    // 测试代码
}
```

#### 如何运行测试并生成覆盖率报告

项目未配置覆盖率报告插件，如需生成覆盖率报告，可以在pom.xml中添加Jacoco插件配置：

```xml
<build>
    <plugins>
        <!-- 添加Jacoco插件 -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

然后运行：

```bash
mvn test jacoco:report
```

覆盖率报告将生成在 `target/site/jacoco` 目录下。

## 开发说明

### 启动应用

```bash
mvn spring-boot:run
```

### 构建应用

```bash
mvn clean package
```

### 运行构建后的应用

```bash
java -jar target/tabletalk-0.0.1-SNAPSHOT.jar
```

## API文档

应用启动后，可以通过以下地址访问API文档：

```
http://localhost:8080/swagger-ui.html
```

## 项目结构

```
src/main/java/com/ljyh/tabletalk/
├── config/              # 配置类
├── controller/          # 控制器层
├── dto/                 # 数据传输对象
├── entity/              # 实体类
├── exception/           # 异常处理
├── mapper/              # 数据访问层
├── recommendation/      # 推荐算法
├── service/             # 服务层
├── utils/               # 工具类
├── websocket/           # WebSocket相关
└── TabletalkApplication.java  # 应用入口
```

## 技术栈

- **后端框架**: Spring Boot 3.5.6
- **数据库**: MySQL 8.0 + MyBatis Plus
- **缓存**: Redis
- **安全**: Spring Security + JWT
- **实时通信**: WebSocket + Protobuf
- **API文档**: Swagger/OpenAPI
- **构建工具**: Maven
- **开发语言**: Java 17

## 功能模块

1. **用户管理**：注册、登录、个人信息管理
2. **餐厅管理**：餐厅信息、菜单、推荐菜品
3. **评论系统**：用户评论、评分
4. **推荐系统**：基于算法的餐厅和用户推荐
5. **社交功能**：关注用户、共同兴趣
6. **实时通信**：餐厅聊天室、通知
7. **喜好管理**：用户喜好食物管理



## 许可证

MIT