package com.nexus.api.brain.context;
import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/users/{userId}/context")
public class ContextController {
 private final JdbcTemplate db; public ContextController(JdbcTemplate db){this.db=db;}
 @GetMapping public Map<String,Object> get(@PathVariable UUID userId,@RequestParam(defaultValue="10") int messageLimit,
  @RequestParam(defaultValue="10") int memoryLimit){
  if(messageLimit<1||messageLimit>50||memoryLimit<1||memoryLimit>50) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Limits must be 1..50");
  var memories=db.queryForList("SELECT id,content,category,importance,tags FROM memories WHERE user_id=? ORDER BY importance DESC,updated_at DESC LIMIT ?",userId,memoryLimit);
  var messages=db.queryForList("SELECT m.id,m.role,m.content,m.sequence_number,m.created_at,c.id conversation_id,c.title FROM messages m JOIN conversations c ON c.id=m.conversation_id WHERE c.user_id=? ORDER BY m.created_at DESC LIMIT ?",userId,messageLimit);
  Collections.reverse(messages); return Map.of("userId",userId,"memories",memories,"recentMessages",messages);
 }
}