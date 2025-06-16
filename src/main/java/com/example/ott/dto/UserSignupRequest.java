package com.example.ott.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequest {

  @Schema(description = "로그인 ID", example = "user123")
  @NotBlank(message = "ID는 필수 입력 사항입니다.")
  private String username;

  @Schema(description = "비밀번호(8~30자)", example = "pAssword123!")
  @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
  @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해주세요.")
  private String password;

  @Schema(description = "별명", example = "장국영")
  @NotBlank(message = "별명은 필수 입력 사항입니다.")
  private String displayName;
}
