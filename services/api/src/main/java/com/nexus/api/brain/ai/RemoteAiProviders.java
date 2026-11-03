package com.nexus.api.brain.ai;
import com.fasterxml.jackson.databind.JsonNode; import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType; import org.springframework.stereotype.Component; import org.springframework.web.client.RestClient; import java.util.*;
@Component class OpenAiProvider implements AiProvider {
 private final RestClient http; private final String key;
 OpenAiProvider(RestClient.Builder b,@Value("${NEXUS_OPENAI_API_KEY:}") String k,@Value("${NEXUS_OPENAI_BASE_URL:https://api.openai.com/v1}") String url){key=k;http=b.baseUrl(url).build();}
 public String name(){return "openai";}
 public AiResponse generate(AiRequest r){if(key.isBlank())throw new IllegalStateException("NEXUS_OPENAI_API_KEY is missing");
  var msgs=new ArrayList<Map<String,String>>();if(r.systemPrompt()!=null&&!r.systemPrompt().isBlank())msgs.add(Map.of("role","system","content",r.systemPrompt()));
  r.messages().forEach(m->msgs.add(Map.of("role",m.role(),"content",m.content())));
  JsonNode out=http.post().uri("/chat/completions").contentType(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+key)
   .body(Map.of("model",r.model(),"messages",msgs,"temperature",r.temperature(),"max_tokens",r.maxTokens())).retrieve().body(JsonNode.class);
  return new AiResponse(name(),r.model(),out.path("choices").path(0).path("message").path("content").asText());}
}
@Component class GeminiProvider implements AiProvider {
 private final RestClient http;private final String key;
 GeminiProvider(RestClient.Builder b,@Value("${NEXUS_GEMINI_API_KEY:}") String k,@Value("${NEXUS_GEMINI_BASE_URL:https://generativelanguage.googleapis.com/v1beta}") String url){key=k;http=b.baseUrl(url).build();}
 public String name(){return "gemini";}
 public AiResponse generate(AiRequest r){if(key.isBlank())throw new IllegalStateException("NEXUS_GEMINI_API_KEY is missing");
  var contents=r.messages().stream().map(m->Map.of("role",m.role().equals("assistant")?"model":"user","parts",List.of(Map.of("text",m.content())))).toList();
  var body=new HashMap<String,Object>();body.put("contents",contents);body.put("generationConfig",Map.of("temperature",r.temperature(),"maxOutputTokens",r.maxTokens()));
  if(r.systemPrompt()!=null&&!r.systemPrompt().isBlank())body.put("systemInstruction",Map.of("parts",List.of(Map.of("text",r.systemPrompt()))));
  JsonNode out=http.post().uri("/models/{model}:generateContent?key={key}",r.model(),key).body(body).retrieve().body(JsonNode.class);
  return new AiResponse(name(),r.model(),out.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText());}
}
@Component class ClaudeProvider implements AiProvider {
 private final RestClient http;private final String key;
 ClaudeProvider(RestClient.Builder b,@Value("${NEXUS_ANTHROPIC_API_KEY:}") String k,@Value("${NEXUS_ANTHROPIC_BASE_URL:https://api.anthropic.com/v1}") String url){key=k;http=b.baseUrl(url).build();}
 public String name(){return "claude";}
 public AiResponse generate(AiRequest r){if(key.isBlank())throw new IllegalStateException("NEXUS_ANTHROPIC_API_KEY is missing");
  var msgs=r.messages().stream().filter(m->!m.role().equals("system")).map(m->Map.of("role",m.role().equals("assistant")?"assistant":"user","content",m.content())).toList();
  var body=new HashMap<String,Object>();body.put("model",r.model());body.put("messages",msgs);body.put("max_tokens",r.maxTokens());body.put("temperature",r.temperature());
  if(r.systemPrompt()!=null&&!r.systemPrompt().isBlank())body.put("system",r.systemPrompt());
  JsonNode out=http.post().uri("/messages").contentType(MediaType.APPLICATION_JSON).header("x-api-key",key).header("anthropic-version","2023-06-01").body(body).retrieve().body(JsonNode.class);
  return new AiResponse(name(),r.model(),out.path("content").path(0).path("text").asText());}
}