package com.example.service;


import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ImageService {
	WebClient webClient;

	public Mono<String> generateImageAsync(String prompt) {
		Map<String, Object> body = Map.of("prompt", prompt);

		return webClient.post()
		.uri("/generate")
		.bodyValue(body)
		.retrieve()
		.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}) 		.			   map(map -> {
			Object url = map.get("url");
			return url == null ? null : url.toString();
		});
	}
}
