package com.example.ott.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/* JWT 토큰 발급 및 검증 담당 */
@Component
public class JwtTokenProvider {

  // 보안상 application.yml로 분리 가능
  private static final String SECRET_KEY = "MyUltraSecretKeyForJWT-ott-project-0123456789!@#$"; // 32바이트 이상
  private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;  // 24시간

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  /* JWT 토큰 생성 */
  public String generateToken(String username, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + EXPIRATION_TIME);

    return Jwts.builder()
        .subject(username)
        .claim("role", role)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getKey())
        .compact();
  }

  /* JWT 토큰에서 사용자 ID 추출 */
  public String getUsername(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  /* JWT 토큰에서 role(권한) 추출 */
  public String getRole(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("role", String.class);
  }

  /* 토큰 유효성 검증 */
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getKey())
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

}
