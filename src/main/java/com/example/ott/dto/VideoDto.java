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
public class VideoDto {

  @Schema(description = "동영상 고유번호(PK)", example = "1")
  private Long id;

  @Schema(description = "동영상 제목", example = "영웅본색")
  @NotBlank(message = "동영상 제목은 필수 입력 사항입니다.")
  private String title;

  @Schema(description = "동영상 설명", example = "홍콩 느와르의 대표작")
  private String description;

  @Schema(description = "썸네일 이미지 URL", example = "/uploads/thumb/sample.jpg")
  private String thumbnailUrl;

  @Schema(description = "동영상 재생 시간(초)", example = "5400")
  private Long playTime;

  @Schema(description = "업로드한 회원 ID", example = "user123")
  private String uploadUserId;

  @Schema(description = "업로드 일시(ISO8601)", example = "2025-06-16T10:00:00")
  private String createdAt;
}
