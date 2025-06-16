package com.example.ott.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "file_path", nullable = false, length = 512)
  private String filePath;

  @Column(name = "thumbnail_path", length = 512)
  private  String thumbnailPath;

  @Column(name = "play_time")
  private Long playTime;

  @Column(name = "upload_user_id", nullable = false, length = 100)
  private String uploadUserId;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
