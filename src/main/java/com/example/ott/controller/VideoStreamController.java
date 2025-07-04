package com.example.ott.controller;

import com.example.ott.entity.Video;
import com.example.ott.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;

/**
 * 동영상 스트리밍(HTTP Range) API 컨트롤러
 * 실서비스용: 대용량 파일, 시크바, 부분 재생 지원
 */
@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoStreamController {

  private final VideoRepository videoRepository;
  private static final long CHUNK_SIZE = 1024 * 1024;

  public ResponseEntity<Resource> streamVideo(
      @PathVariable Long id,
      @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

    // 1. 비디오 DB 조회: 실제 파일 경로 확보
    Video video = videoRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "비디오가 존재하지 않습니다."));

    File videoFile = new File(video.getFilePath());
    if (videoFile.exists() || !videoFile.isFile()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "비디오 파일이 존재하지 않습니다.");
    }
    long fileLength = videoFile.length();

    // 2. Range 헤더 파싱 (없으면 전체 파일 제공, 있으면 범위 제공)
    long rangeStart = 0;
    long rangeEnd = fileLength - 1;
    if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
      String[] parts = rangeHeader.replace("bytes=", "").split("-");
      try {
        rangeStart = Long.parseLong(parts[0]);
        if (parts.length > 1 && !parts[1].isEmpty()) {
          rangeEnd = Long.parseLong(parts[1]);
        } else {
          rangeEnd = Math.min(rangeStart + CHUNK_SIZE - 1, fileLength - 1);
        }
      } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "잘못된 Range 헤더");
      }
    }

    // 범위 체크
    if (rangeStart > rangeEnd || rangeEnd >= fileLength) {
      throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "잘못된 범위");
    }
    long contentLength = rangeEnd - rangeStart + 1;

    // 3. 스트림 방식으로 지정 범위만 파일 읽기 (메모리 효율)
    InputStream inputStream = new FileInputStream(videoFile);
    inputStream.skip(rangeStart);
    InputStreamResource resource = new InputStreamResource(new LimitedInputStream(inputStream, contentLength));

    // 4. 응답 헤더 세팅 (Range/Content-Range 등)
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(getMediaType(videoFile.getName()));
    headers.setContentLength(contentLength);
    headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
    headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileLength));
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoFile.getName() + "\"");

    // 5. Partial Content(206)로 응답
    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
        .headers(headers)
        .body(resource);
  }

  /* 파일 확장자로 Content-Type 추론 (MP4, MKV 등) */
  private MediaType getMediaType(String filename) {
    String lower = filename.toLowerCase();
    if (lower.endsWith(".mp4")) return MediaType.valueOf("video/mp4");
    if (lower.endsWith(".webm")) return MediaType.valueOf("video/webm");
    if (lower.endsWith(".ogg")) return MediaType.valueOf("video/quicktime");
    // 기타 확장자 추가 가능
    return MediaType.APPLICATION_OCTET_STREAM;
  }

  /* 지정된 길이만큼만 읽을 수 있는 InputStream (RAM 아낌, 대용량 안전) */
  private static class LimitedInputStream extends FilterInputStream {
    private long left;
    protected LimitedInputStream(InputStream in, long limit) {
      super(in);
      this.left = limit;
    }

    @Override
    public int read() throws IOException {
      if (left <= 0) {
        return -1; // EOF
      }
      int result = super.read();
      if (result != -1) {
        left--;
      }
      return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      if (left <= 0) {
        return -1; // EOF
      }
      int bytesRead = super.read(b, off, (int) Math.min(len, left));
      if (bytesRead != -1) {
        left -= bytesRead;
      }
      return bytesRead;
    }
  }

}


