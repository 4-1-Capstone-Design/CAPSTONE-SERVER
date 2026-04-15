package com.capstone.domain.journal.service;

import com.capstone.global.error.BusinessException;
import com.capstone.global.error.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiReplyService {

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.url}")
  private String url;

  @Value("${openai.model}")
  private String model;

  @Qualifier("openAiRestTemplate")
  private final RestTemplate restTemplate;

  public String generateReply(String journalContent) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(apiKey);
      headers.setContentType(MediaType.APPLICATION_JSON);

      String prompt = """
          너는 사용자의 글을 바탕으로, 그 사람의 내면에 있는 생각과 욕구를 자연스럽게 끌어내도록 돕는 코치다.

          사용자의 저널을 읽고, 단순한 위로가 아니라 사용자가 스스로를 더 깊이 이해하고 앞으로 나아갈 수 있도록 돕는 한국어 답장을 작성해라.

          목표:
          - 사용자가 자신의 감정 뒤에 있는 진짜 이유나 원하는 것을 스스로 떠올리게 만든다
          - 부담 없이 생각을 이어가며 자기이해와 성장을 돕는다

          조건:
          - 총 2~4문장
          - 첫 문장: 사용자의 감정이나 상황을 조심스럽게 공감
          - 이후 문장:
            - 감정 뒤에 있는 이유, 욕구, 패턴을 떠올릴 수 있도록 유도하는 질문 1개 포함
            - 또는 새로운 시각이나 작은 방향 제시
          - 질문은 부담스럽지 않고 자연스럽게, “왜?” 대신 “혹시 ~일 수도 있을까요?” 형태로 작성
          - 판단, 진단, 훈계 금지
          - 감정을 단정하지 말고 가능성 형태로 표현
          - 존댓말 사용
          - 이모지 금지
          - 답변만 출력

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

      String replyText = extractOutputText(response);

      if (replyText == null || replyText.isBlank()) {
        log.error("OpenAI text extraction failed. full response={}", response);
        throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
      }

      return replyText.trim();

    } catch (ResourceAccessException e) {
      log.error("OpenAI timeout or connection error", e);
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (HttpStatusCodeException e) {
      log.error("OpenAI HTTP error status={}, body={}",
          e.getStatusCode(), e.getResponseBodyAsString(), e);
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

  private String extractOutputText(Map<String, Object> response) {
    if (response == null) {
      return null;
    }

    Object outputObj = response.get("output");
    if (!(outputObj instanceof List<?> outputList) || outputList.isEmpty()) {
      return null;
    }

    for (Object outputItem : outputList) {
      if (!(outputItem instanceof Map<?, ?> outputMap)) continue;

      Object contentObj = outputMap.get("content");
      if (!(contentObj instanceof List<?> contentList) || contentList.isEmpty()) continue;

      for (Object contentItem : contentList) {
        if (!(contentItem instanceof Map<?, ?> contentMap)) continue;

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