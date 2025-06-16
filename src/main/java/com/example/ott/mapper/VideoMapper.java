package com.example.ott.mapper;

import com.example.ott.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper {
  /* 전체 동영상 목록(최신순) */
  List<Video> findAll();

  /* 동영상 상세 정보 */
  Video findById(@Param("id") Long id);

  /* 동영상 신규 등록 */
  void insert(Video video);

  /* 동영싱 삭제 */
  int delete(@Param("id") Long id);
}
