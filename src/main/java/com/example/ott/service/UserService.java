package com.example.ott.service;

import com.example.ott.dto.UserDto;
import com.example.ott.dto.UserLoginRequest;
import com.example.ott.dto.UserSignupRequest;
import com.example.ott.entity.User;
import com.example.ott.repository.UserRepository;
import com.example.ott.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  /* 회원 가입 */
  @Transactional
  public UserDto signup(UserSignupRequest userSignupRequest) {
    if (userRepository.findByUsername(userSignupRequest.getUsername()).isPresent()) {
      throw new IllegalArgumentException("이미 사용중인 아이디 입니다.");
    }

    User user = User.builder()
        .username(userSignupRequest.getUsername())
        .password(passwordEncoder.encode(userSignupRequest.getPassword()))
        .displayName(userSignupRequest.getDisplayName())
        .role("USER")
        .build();

    User saved = userRepository.save(user);
    return toDto(saved);
  }

  /* 로그인 - 성공 시 JWT 반환 */
  public String login(UserLoginRequest userLoginRequest) {
    User user = userRepository.findByUsername(userLoginRequest.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 입니다."));

    if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    return jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
  }

  /* 사용자 정보 조회 */
  public UserDto getUserInfo(String username) {
    return userRepository.findByUsername(username)
        .map(this::toDto)
        .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
  }

  /* JwtToken 으로 username 가져오기 */
  public String getUsernameFromToken(String token) {
    return jwtTokenProvider.getUsername(token);
  }

  /* User 엔티티 -> UserDto 변황 */
  private UserDto toDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .displayName(user.getDisplayName())
        .role(user.getRole())
        .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
        .build();
  }

}
