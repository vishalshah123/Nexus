package com.nexus.api.brain.ai;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/ai")
public class AiController {
 private final AiProviderRegistry registry; public AiController(AiProviderRegistry r){registry=r;}
 public record ChatRequest(@NotBlank String provider,@NotBlank String model,@NotEmpty @Size(max=100) List<AiProvider.ChatMessage> messages,
  String systemPrompt,@DecimalMin("0.0") @DecimalMax("2.0") Double temperature,@Min(1) @Max(32000) Integer maxTokens){}
 @GetMapping("/providers") public Set<String> providers(){return registry.available();}
 @PostMapping("/chat") public AiProvider.AiResponse chat(@Valid @RequestBody ChatRequest r){
  return registry.get(r.provider()).generate(new AiProvider.AiRequest(r.model(),r.systemPrompt(),r.messages(),
   r.temperature()==null?0.7:r.temperature(),r.maxTokens()==null?2048:r.maxTokens()));}
}