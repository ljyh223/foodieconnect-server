package com.ljyh.foodieconnect.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档配置
 */
@Configuration
public class SwaggerConfig {

    /**
     * 用户端 API 分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户端-API")
                .pathsToMatch("/user/**", "/auth/**", "/restaurants/**", "/reviews/**",
                        "/favorites/**", "/follows/**", "/recommendations/**", "/upload/**")
                .build();
    }

    /**
     * 商家端 API 分组
     */
    @Bean
    public GroupedOpenApi merchantApi() {
        return GroupedOpenApi.builder()
                .group("商家端-API")
                .pathsToMatch("/merchant/**", "/merchant-auth/**")
                .build();
    }

    /**
     * 全局 OpenAPI 配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Foodie Connect API 文档")
                        .description("Foodie Connect 餐厅服务应用后端API接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Foodie Connect 开发团队")
                                .email("dev@foodieconnect.com")
                                .url("https://foodieconnect.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}