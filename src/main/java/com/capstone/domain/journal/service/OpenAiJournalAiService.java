package com.capstone.domain.journal.service;

import com.capstone.domain.journal.dto.response.JournalAiResultDto;
import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiJournalAiService {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.url}")
  private String url;

  @Value("${openai.model}")
  private String model;

  @Qualifier("openAiRestTemplate")
  private final RestTemplate restTemplate;

  public JournalAiResultDto generateAnalysisResult(String journalContent) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(apiKey);
      headers.setContentType(MediaType.APPLICATION_JSON);

      String prompt = """
          너는 사용자의 저널을 분석하는 AI 코치다.
          
          사용자의 저널을 읽고, 아래 JSON 형식으로만 답변하라.
          설명 문장, 코드블록 마크다운, 부가 설명 없이 JSON만 출력하라.
          
          목표:
          - 사용자의 내면 욕구, 방향성, 감정 흐름을 바탕으로 짧은 답장을 만든다
          - 저널 핵심 내용을 요약한다
          - 핵심 키워드 3개를 추출한다
          
          조건:
          - reply는 2~4문장
          - summary는 1~2문장
          - keywords는 정확히 3개
          - keywords.score는 0 이상 1 이하의 소수
          - reply는 공감 + 자기이해/성장 방향 제시가 포함되어야 한다
          - 판단, 진단, 훈계 금지
          - 존댓말 사용
          - 이모지 금지
          
          반드시 아래 형식의 JSON만 출력:
          {
            "reply": "사용자에게 보여줄 답장",
            "summary": "저널 핵심 요약",
            "keywords": [
              {"name": "키워드1", "score": 0.91},
              {"name": "키워드2", "score": 0.84},
              {"name": "키워드3", "score": 0.79}
            ]
          }
          
          저널 내용:
          %s
          """.formatted(journalContent);

      Map<String, Object> body = Map.of(
          "model", model,
          "input", prompt
      );

      HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

      ResponseEntity<Map> responseEntity =
          restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

      Map<String, Object> response = responseEntity.getBody();
      log.debug("OpenAI response body={}", response);

      String rawText = extractOutputText(response);

      if (rawText == null || rawText.isBlank()) {
        log.error("OpenAI text extraction failed. full response={}", response);
        throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
      }

      String cleanedJson = cleanJsonText(rawText);
      JournalAiResultDto result =
          objectMapper.readValue(cleanedJson, JournalAiResultDto.class);

      validateResult(result);
      return result;

    } catch (ResourceAccessException e) {
      log.error("OpenAI timeout or connection error", e);
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (HttpStatusCodeException e) {
      log.error("OpenAI HTTP error status={}, body={}",
          e.getStatusCode(), e.getResponseBodyAsString(), e);
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (JsonProcessingException e) {
      log.error("OpenAI JSON parsing failed", e);
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.error("OpenAI unexpected error", e);
      throw new BusinessException(ErrorStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public String getModel() {
    return model;
  }

  private void validateResult(JournalAiResultDto result) {
    if (result == null) {
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    }

    if (result.reply() == null || result.reply().isBlank()) {
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    }

    if (result.summary() == null || result.summary().isBlank()) {
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    }

    if (result.keywords() == null || result.keywords().size() != 3) {
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    }

    boolean invalidKeyword = result.keywords().stream().anyMatch(keyword ->
        keyword.name() == null || keyword.name().isBlank() || keyword.score() == null
    );

    if (invalidKeyword) {
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    }
  }

  private String cleanJsonText(String rawText) {
    String trimmed = rawText.trim();

    if (trimmed.startsWith("```json")) {
      trimmed = trimmed.substring(7).trim();
    } else if (trimmed.startsWith("```")) {
      trimmed = trimmed.substring(3).trim();
    }

    if (trimmed.endsWith("```")) {
      trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
    }

    return trimmed;
  }

  private String extractOutputText(Map<String, Object> response) {
    if (response == null) {
      return null;
    }

    Object outputObj = response.get("output");
    if (!(outputObj instanceof List<?> outputList) || outputList.isEmpty()) {
      return null;
    }

    for (Object outputItem : outputList) {
      if (!(outputItem instanceof Map<?, ?> outputMap)) {
        continue;
      }

      Object contentObj = outputMap.get("content");
      if (!(contentObj instanceof List<?> contentList) || contentList.isEmpty()) {
        continue;
      }

      for (Object contentItem : contentList) {
        if (!(contentItem instanceof Map<?, ?> contentMap)) {
          continue;
        }

        Object type = contentMap.get("type");
        Object text = contentMap.get("text");

        if ("output_text".equals(type) && text instanceof String textValue && !textValue.isBlank()) {
          return textValue;
        }
      }
    }

    return null;
  }
}