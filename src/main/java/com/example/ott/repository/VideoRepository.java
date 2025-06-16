package com.example.ott.repository;

import com.example.ott.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

  /* 업로더 ID 별로 동영상 목록 조회 */
  List<Video> findByUploadUserIdOrderByCreatedAtDesc(String uploadUserId);

  /* 특정 제목(부분 일치) 검색 */
  List<Video> findByTitleContainingIgnoreCase(String keyword);
}
