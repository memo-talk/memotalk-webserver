package com.memotalk.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("Memo Talk")
                .description("Memo Talk API 명세서");

        // HTTPS 서버 주소 추가
        Server server = new Server();
        server.setUrl("https://memotalk.shop");

        return new OpenAPI()
                .info(info)
                .addServersItem(server); // 서버 정보를 OpenAPI 설정에 추가
    }
}