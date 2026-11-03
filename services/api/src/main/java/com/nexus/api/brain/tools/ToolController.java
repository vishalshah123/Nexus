package com.nexus.api.brain.tools;
import jakarta.validation.Valid;import jakarta.validation.constraints.NotBlank;import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;import java.time.Instant;import java.util.*;
@RestController @RequestMapping("/api/v1/tools")
public class ToolController {
 private final JdbcTemplate db;public ToolController(JdbcTemplate db){this.db=db;}
 public record ExecuteRequest(@NotBlank String toolName,Map<String,Object> arguments){}
 @GetMapping public List<Map<String,Object>> list(){return List.of(
  Map.of("name","nexus.time","description","Returns current UTC time","risk","READ_ONLY","approvalRequired",false),
  Map.of("name","nexus.echo","description","Returns supplied text unchanged","risk","READ_ONLY","approvalRequired",false));}
 @PostMapping("/execute") public Map<String,Object> execute(@RequestHeader("X-Nexus-User-Id") UUID userId,@Valid @RequestBody ExecuteRequest r){
  Map<String,Object> args=r.arguments()==null?Map.of():r.arguments();Object result;
  if(r.toolName().equals("nexus.time"))result=Instant.now().toString();
  else if(r.toolName().equals("nexus.echo")){Object text=args.get("text");if(!(text instanceof String s)||s.length()>4000)
   throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"text must be a string up to 4000 chars");result=s;}
  else throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,"Tool is not allowlisted");
  String json=new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(args).toString();
  db.update("INSERT INTO nexus_tool_audit(id,user_id,tool_name,arguments,status) VALUES(?,?,?,?::jsonb,'SUCCEEDED')",UUID.randomUUID(),userId,r.toolName(),json);
  return Map.of("tool",r.toolName(),"result",result);
 }
}