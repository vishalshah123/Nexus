package com.nexus.api.brain.ai;
import java.util.List;
public interface AiProvider {
 String name(); AiResponse generate(AiRequest request);
 record AiRequest(String model,String systemPrompt,List<ChatMessage> messages,double temperature,Integer maxTokens){}
 record ChatMessage(String role,String content){}
 record AiResponse(String provider,String model,String content){}
}