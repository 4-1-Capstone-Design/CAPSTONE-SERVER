package com.capstone.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  @Qualifier("openAiRestTemplate")
  public RestTemplate openAiRestTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);   // 연결 타임아웃 (5sec)
    factory.setReadTimeout(30000);     // 응답 대기 (30sec)

    return new RestTemplate(factory);
  }
}