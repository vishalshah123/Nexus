package com.nexus.api.brain.ai;
import org.springframework.stereotype.Service; import java.util.*;
@Service public class AiProviderRegistry {
 private final Map<String,AiProvider> providers;
 public AiProviderRegistry(List<AiProvider> list){Map<String,AiProvider> m=new HashMap<>();list.forEach(p->m.put(p.name(),p));providers=Map.copyOf(m);}
 public AiProvider get(String name){AiProvider p=providers.get(name.toLowerCase(Locale.ROOT));if(p==null)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Unknown provider");return p;}
 public Set<String> available(){return providers.keySet();}
}