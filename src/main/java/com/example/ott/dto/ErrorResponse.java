package com.example.ott.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
  // 오류 코드
  private String error;
  // 오류 메시지
  private String message;

}
