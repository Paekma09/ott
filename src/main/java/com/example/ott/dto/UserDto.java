package com.example.ott.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  @Schema(description = "회원 고유번호", example = "1")
  private Long id;

  @Schema(description = "회원 로그인 ID", example = "user123")
  private String username;

  @Schema(description = "별명/이름", example = "장국영")
  private String displayName;

  @Schema(description = "권한", example = "USER")
  private String role;

  @Schema(description = "가입일(ISO8601)", example = "2025-06-16T10:00:00")
  private String createdAt;
}
