package com.nexus.api.brain.tasks;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/v1/users/{userId}/tasks")
public class BackgroundTaskController {
 private final JdbcTemplate db;public BackgroundTaskController(JdbcTemplate db){this.db=db;}
 public record TaskRequest(@NotBlank @Size(max=80) String taskType,Map<String,Object> payload){}
 @PostMapping @ResponseStatus(org.springframework.http.HttpStatus.ACCEPTED)
 public Map<String,Object> enqueue(@PathVariable UUID userId,@Valid @RequestBody TaskRequest r){
  if(!Set.of("CONTEXT_SNAPSHOT","MEMORY_REINDEX","CONVERSATION_SUMMARY").contains(r.taskType()))
   throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Unsupported task type");
  UUID id=UUID.randomUUID();String json=new tools.jackson.databind.ObjectMapper().valueToTree(r.payload()==null?Map.of():r.payload()).toString();
  db.update("INSERT INTO background_tasks(id,user_id,task_type,payload) VALUES(?,?,?,?::jsonb)",id,userId,r.taskType(),json);
  return Map.of("id",id,"taskType",r.taskType(),"status","QUEUED");
 }
 @GetMapping public List<Map<String,Object>> list(@PathVariable UUID userId,@RequestParam(defaultValue="20") int limit){
  if(limit<1||limit>100)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"limit must be 1..100");
  return db.queryForList("SELECT id,task_type,status,payload,result,error_message,created_at,started_at,completed_at FROM background_tasks WHERE user_id=? ORDER BY created_at DESC LIMIT ?",userId,limit);}
 @GetMapping("/{taskId}") public Map<String,Object> get(@PathVariable UUID userId,@PathVariable UUID taskId){
  var rows=db.queryForList("SELECT id,task_type,status,payload,result,error_message,created_at,started_at,completed_at FROM background_tasks WHERE user_id=? AND id=?",userId,taskId);
  if(rows.isEmpty())throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,"Task not found");return rows.get(0);}
 @PostMapping("/{taskId}/cancel") public Map<String,Object> cancel(@PathVariable UUID userId,@PathVariable UUID taskId){
  int n=db.update("UPDATE background_tasks SET status='CANCELLED',completed_at=now() WHERE user_id=? AND id=? AND status='QUEUED'",userId,taskId);
  if(n==0)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Task is not queued or does not exist");
  return Map.of("id",taskId,"status","CANCELLED");}
}