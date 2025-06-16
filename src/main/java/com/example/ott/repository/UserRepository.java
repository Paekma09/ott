package com.example.ott.repository;

import com.example.ott.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /* ID(Username)로 회원 조회 */
  Optional<User> findByUsername(String username);

  /* 이름(별명)으로 검색 - 중복 가능 */
  Optional<User> findByDisplayName(String displayName);

  /* 권한(ROLE)별 회원 목록 */
  List<User> findByRole(String role);
}
