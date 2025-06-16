package com.example.ott.service;

import com.example.ott.config.OttProperties;
import com.example.ott.entity.Video;
import com.example.ott.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoFileService {

  private final VideoMapper videoMapper;
  private final OttProperties ottProperties;

  @Transactional
  public Video uploadVideo(MultipartFile file, String title, String description, String uploadUserId) throws IOException {
    // 안전하게 yml 파일에서 값 가져오기
    String uploadPath = ottProperties.getUploadPath();

    // 저장 폴더가 없으면 생성
    File dir = new File(uploadPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    // 원본 파일명 및 확장자 추출
    String originalFilename = file.getOriginalFilename();
    String ext = (originalFilename != null && originalFilename.contains("."))
        ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

    // 파일명은 UUID 기반으로 생성 (중복 방지)
    String newFileName = UUID.randomUUID() + ext;
    String fullPath = uploadPath + File.separator + newFileName;

    // 파일 저장
    file.transferTo(new File(fullPath));

    // Video 엔티티 생성 및 DB 등록
    Video video = Video.builder()
        .title(title)
        .description(description)
        .filePath(fullPath)
        .uploadUserId(uploadUserId)
        .build();

    // VideoMapper를 통해 DB에 저장
    videoMapper.insert(video);

    return video;
  }

  /* videoId로 동영상 파일 객체 반환 */
  public File getVideoFile(Long videoId) {
    Video video = videoMapper.findById(videoId);
    if (video == null || video.getFilePath() == null) return null;
    return new File(video.getFilePath());
  }

}
