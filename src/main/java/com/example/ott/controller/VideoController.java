package com.example.ott.controller;

import com.example.ott.dto.VideoDto;
import com.example.ott.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Tag(name = "동영상", description = "동영상 관련 API")
public class VideoController {

  private final VideoService videoService;

  /* 동영상 전체 목록 조회 API */
  @GetMapping
  @Operation(summary = "동영상 목록 조회", description = "등록된 모든 동영상을 최신순으로 조회합니다.")
  public ResponseEntity<List<VideoDto>> getVideoList() {
    List<VideoDto> videoList = videoService.getAllVideos();
    return ResponseEntity.ok(videoList);
  }

  /* 동영상 상세 조회 API */
  @GetMapping("/{id}")
  @Operation(summary = "동영상 상세 조회", description = "동영상 ID로 특정 동영상을 조회합니다.")
  public ResponseEntity<VideoDto> getVideoDetail(@PathVariable Long id) {
    VideoDto video = videoService.getVideoDetail(id);
    if (video == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(video);
  }
}
