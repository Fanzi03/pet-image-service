package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpConfig {
	private final String baseUrl;

	public HttpConfig( 
		@Value("${IMAGE_AI_URL}") String baseUrl
	){
		this.baseUrl = baseUrl;
	}

	@Bean
	public WebClient localModelWebClient (
	){
		return WebClient.builder().baseUrl(baseUrl).codecs(
			configurer -> configurer.defaultCodecs()
			.maxInMemorySize(10 * 1024 * 1024)
		).build();
	}

}
