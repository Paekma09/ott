package com.example.ott.controller;

import com.example.ott.dto.UserDto;
import com.example.ott.dto.UserLoginRequest;
import com.example.ott.dto.UserSignupRequest;
import com.example.ott.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원가입/로그인/JWT 인증 API")
public class UserController {

  private final UserService userService;

  /* 회원 가입 */
  @PostMapping("/signup")
  @Operation(summary = "회원가입", description = "신규 회원을 등록합니다.")
  public ResponseEntity<UserDto> signup(@Valid @RequestBody UserSignupRequest userSignupRequest) {
    return ResponseEntity.ok(userService.signup(userSignupRequest));
  }

  /* 로그인 */
  @PostMapping("/login")
  @Operation(summary = "로그인", description = "로그인 성공시 JWT 토큰을 반환합니다.")
  public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
    String jwt = userService.login(userLoginRequest);

    return ResponseEntity.ok().body(jwt);
  }

  /* 내 정보 조히 */
  @GetMapping("/my-info")
  @Operation(summary = "내 정보 조회", description = "JWT 인증 후 내 정보를 조회합니다.")
  public ResponseEntity<UserDto> getMyInfo(@RequestHeader("Authorization") String authHeader) {
    // JWT 토큰에서 사용자 이름 추출
    String token = authHeader.replace("Bearer ", "");
    String username = userService.getUsernameFromToken(token);

    return ResponseEntity.ok(userService.getUserInfo(username));
  }

}
