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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

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
          
          반드시 JSON만 출력하라.
          
          {
            "reply": "사용자에게 보여줄 답장",
            "summary": "저널 핵심 요약"
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

      String rawText = extractOutputText(response);

      if (rawText == null || rawText.isBlank()) {
        throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
      }

      String cleanedJson = cleanJsonText(rawText);

      JournalAiResultDto result =
          objectMapper.readValue(cleanedJson, JournalAiResultDto.class);

      validateResult(result);

      return result;

    } catch (ResourceAccessException | HttpStatusCodeException e) {
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (JsonProcessingException e) {
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (Exception e) {
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
    if (response == null) return null;

    Object outputObj = response.get("output");
    if (!(outputObj instanceof java.util.List<?> outputList) || outputList.isEmpty()) return null;

    for (Object outputItem : outputList) {
      if (!(outputItem instanceof Map<?, ?> outputMap)) continue;

      Object contentObj = outputMap.get("content");
      if (!(contentObj instanceof java.util.List<?> contentList) || contentList.isEmpty()) continue;

      for (Object contentItem : contentList) {
        if (!(contentItem instanceof Map<?, ?> contentMap)) continue;

        Object type = contentMap.get("type");
        Object text = contentMap.get("text");

        if ("output_text".equals(type) && text instanceof String t && !t.isBlank()) {
          return t;
        }
      }
    }
    return null;
  }
}