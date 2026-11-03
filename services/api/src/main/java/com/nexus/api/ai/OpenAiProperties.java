package com.nexus.api.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexus.ai.openai")
public record OpenAiProperties(String baseUrl, String apiKey, String model, int timeoutSeconds) {}
