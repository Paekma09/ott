package com.example.ott.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ott")
@Getter @Setter
public class OttProperties {

  /* 동영상 파일 저장 경로 (application.yml의 ott.upload-path) */
  private String uploadPath;
}
