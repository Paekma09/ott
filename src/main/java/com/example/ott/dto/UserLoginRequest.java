package com.example.ott.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {

  @Schema(description = "로그인 ID", example = "user123")
  @NotBlank
  private String username;

  @Schema(description = "비밀번호", example = "pAssword123!")
  @NotBlank
  private String password;
}
