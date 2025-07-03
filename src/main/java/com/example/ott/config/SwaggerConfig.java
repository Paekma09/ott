package com.example.ott.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        // Bearer(JWT) 인증 활성화
        .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
        .components(new Components()
            .addSecuritySchemes("BearerAuth", new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        // 서비스 Info 설정
        .info(new Info()
            .title("OTT API")
            .description("동영상 OTT 서비스 API 문서")
            .version("v1.0.0")
        );
  }

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder()
        .group("user-auth")
        .pathsToMatch("/api/users/**")
        .build();
  }

  @Bean
  public GroupedOpenApi videoApi() {
    return GroupedOpenApi.builder()
        .group("video")
        .pathsToMatch("/api/videos/**")
        .build();
  }

}
