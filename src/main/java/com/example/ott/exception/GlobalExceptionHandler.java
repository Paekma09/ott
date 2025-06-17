package com.example.ott.exception;

import com.example.ott.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /* IllegalArgumentException 등 커스텀 에러 */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
  }

  /* 검증 실패(@Valid) */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    String msg = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
    return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", msg));
  }

  /* 기타 예상치 못한 에러 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    return ResponseEntity.internalServerError().body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
  }

}
