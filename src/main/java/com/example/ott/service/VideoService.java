package com.example.ott.service;

import com.example.ott.dto.VideoDto;
import com.example.ott.entity.Video;
import com.example.ott.mapper.VideoMapper;
import com.example.ott.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

  private final VideoMapper videoMapper;
  private final VideoRepository videoRepository;

  /* 전체 동영상 상세 조회 */
  @Transactional(readOnly = true)
  public List<VideoDto> getAllVideos() {
    List<Video> videoList = videoMapper.findAll();
    return videoList.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  /* 동영상 상세 조회 */
  @Transactional(readOnly = true)
  public VideoDto getVideoDetail(Long id) {
    Video video = videoMapper.findById(id);
    if (video == null) {
      return null;
    }
    return toDto(video);
  }

  /* 특정 유저가 업로드한 동영상 목록 조회 - JPA */
  @Transactional(readOnly = true)
  public List<VideoDto> getVideosByUser(String userId) {
    List<Video> videoList = videoRepository.findByUploadUserIdOrderByCreatedAtDesc(userId);
    return videoList.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  /* 제목으로 동영상 검색 - JPA */
  @Transactional(readOnly = true)
  public List<VideoDto> searchByTitle(String keyword) {
    List<Video> videoList = videoRepository.findByTitleContainingIgnoreCase(keyword);
    return videoList.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  /* Video 엔티티 -> VideoDto 변황 */
  private VideoDto toDto(Video video) {
    return VideoDto.builder()
        .id(video.getId())
        .title(video.getTitle())
        .description(video.getDescription())
        .thumbnailUrl(video.getThumbnailPath())
        .playTime(video.getPlayTime())
        .uploadUserId(video.getUploadUserId())
        .createdAt(video.getCreatedAt() == null ? null : video.getCreatedAt().toString())
        .build();
  }

}
