package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HttpConfig {
	@Value("${IMAGE_AI_URL")
	private final String baseUrl;

	@Bean
	public WebClient localModelWebClient (
	){
		return WebClient.builder().baseUrl(baseUrl).build();
	}

}
