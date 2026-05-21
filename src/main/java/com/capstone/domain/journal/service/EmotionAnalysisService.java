package com.capstone.domain.journal.service;

import com.capstone.domain.journal.dto.response.JournalKeywordItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {

  private final RestTemplate restTemplate;

  @Value("${emotion.api.url}")
  private String url;

  public List<JournalKeywordItemDto> analyzeEmotion(String content) {

    // 요청 바디
    Map<String, String> request = Map.of("text", content);

    // Map으로 받음
    ResponseEntity<Map> response =
        restTemplate.postForEntity(url, request, Map.class);

    Map body = response.getBody();

    if (body == null) {
      throw new RuntimeException("Emotion API response is null");
    }

    // emotions를 꺼냄
    List<Map<String, Object>> predictions =
        (List<Map<String, Object>>) body.get("predictions");

    if (predictions == null) {
      throw new RuntimeException("predictions field is null");
    }

    // DTO 변환
    List<JournalKeywordItemDto> result = new ArrayList<>();

    for (Map<String, Object> item : predictions) {

      String keyword = (String) item.get("label");
      Double score = ((Number) item.get("score")).doubleValue();

      result.add(
          JournalKeywordItemDto.builder()
              .keyword(keyword)
              .score(BigDecimal.valueOf(score))
              .build()
      );
    }

    return result;
  }
}