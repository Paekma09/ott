package com.example.ott.controller;

import com.example.ott.entity.Video;
import com.example.ott.service.VideoFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/videos/file")
@RequiredArgsConstructor
@Tag(name = "동영상파일", description = "동영상 파일 업로드/다운로드(스트리밍) API")
public class VideoFileController {

  private final VideoFileService videoFileService;

  /* 동영상 파일 업로드 API */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "동영상 파일 업로드", description = "동영상 파일과 메타데이터를 업로드하고 DB에 등록합니다.")
  public ResponseEntity<?> uploadVideo(
      @RequestParam("file") MultipartFile file,
      @RequestParam("title") String title,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam("userId") String userId
  ) throws IOException {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("파일을 첨부해주세요.");
    }
    Video saved = videoFileService.uploadVideo(file, title, description, userId);
    return ResponseEntity.ok(saved.getId());
  }

  /* 동영상 파일 스트리밍(HTTP Range 지원) API */
  @GetMapping("/stream/{id}")
  @Operation(summary = "동영상 스트리밍", description = "HTTP Range를 지원하는 동영상 스트리밍 API 입니다.")
  public ResponseEntity<Resource> streamVideo(@PathVariable Long id, HttpServletRequest request) throws IOException {
    // videoId로 파일 경로 조회
    File videoFile = videoFileService.getVideoFile(id);
    // 파일이 존재하지 않으면 404 Not Found 응답
    if (videoFile == null || !videoFile.exists()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Range 헤더 파싱: "bytes=start-end" 형태
    String range = request.getHeader("Range");
    long fileLength = videoFile.length();

    // 바이트 범위 기본값 (전체 파일)
    long start = 0;
    long end = fileLength - 1;

    // Range 헤더가 있다면, start/end 추출하여 부분 스트리밍 세팅
    if (range != null && range.startsWith("bytes=")) {
      String[] parts = range.replace("bytes=", "").split("-");
      try {
        start = Long.parseLong(parts[0]); // 시작 바이트
        if (parts.length > 1 && !parts[1].isEmpty()) {
          end = Long.parseLong(parts[1]); // 끝 바이트
        }
      } catch (NumberFormatException e) {
        // 잘못된 Range 헤더: 전체 스트리밍으로 fallback
        start = 0;
        end = fileLength - 1;
      }
    }

    // 스트림 준비: 시작위치만큼 skip
    long contentLength = end - start + 1;
    InputStream inputStream = new BufferedInputStream(new FileInputStream(videoFile));
    inputStream.skip(start);

    // 응답 헤더 세팅
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaTypeFactory.getMediaType(videoFile.getName())
        .orElse(MediaType.APPLICATION_OCTET_STREAM));
    headers.setContentLength(contentLength);  // 전송 바이트 수
    headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");  // Range 지원 선언

    // Partial Content(206) vs 전체(200) 구분
    if (range != null) {
      // Range 요청이 있을 때: Partial Content(206), Content-Range 명시
      headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength));
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)  // 206
          .headers(headers)
          .body(new InputStreamResource(inputStream));
    } else {
      // 전체 파일 스트리밍 (200)
      return ResponseEntity.ok()
          .headers(headers)
          .body(new InputStreamResource(inputStream));
    }
  }

}
