package com.nexus.api.brain.memory;
import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController("brainMemoryController") @RequestMapping("/api/v1/users/{userId}/memories") @Validated
public class MemoryController {
 private static final tools.jackson.databind.ObjectMapper JSON = tools.jackson.databind.json.JsonMapper.builder().build();
 private final JdbcTemplate db; public MemoryController(JdbcTemplate db){this.db=db;}
 public record Request(@NotBlank @Size(max=12000) String content,@Size(max=100) String category,
  @Size(max=100) String source,@Min(1) @Max(5) Integer importance,List<String> tags){}
 public record Memory(UUID id,String content,String category,String source,int importance,List<String> tags,
  java.time.OffsetDateTime createdAt,java.time.OffsetDateTime updatedAt){}
 @PostMapping public Memory create(@PathVariable UUID userId,@Valid @RequestBody Request r){
  UUID id=UUID.randomUUID(); db.update("INSERT INTO memories(id,user_id,content,category,source,importance,tags) VALUES(?,?,?,COALESCE(?, 'GENERAL'),?,?,?::jsonb)",
   id,userId,r.content(),r.category(),r.source(),r.importance()==null?3:r.importance(),json(r.tags())); return get(userId,id);}
 @GetMapping public List<Memory> list(@PathVariable UUID userId,@RequestParam(defaultValue="50") @Min(1) @Max(100) int limit,
  @RequestParam(required=false) String category,@RequestParam(required=false) String q){
  String term=q==null?null:"%"+q.trim()+"%";
  return db.query("SELECT * FROM memories WHERE user_id=? AND (? IS NULL OR category=?) AND (? IS NULL OR content ILIKE ?) ORDER BY importance DESC,updated_at DESC LIMIT ?",
   (rs,n)->map(rs),userId,category,category,term,term,limit);}
 @GetMapping("/{id}") public Memory get(@PathVariable UUID userId,@PathVariable UUID id){
  List<Memory> rows=db.query("SELECT * FROM memories WHERE user_id=? AND id=?",(rs,n)->map(rs),userId,id);
  if(rows.isEmpty()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,"Memory not found"); return rows.get(0);}
 @PutMapping("/{id}") public Memory update(@PathVariable UUID userId,@PathVariable UUID id,@Valid @RequestBody Request r){
  int n=db.update("UPDATE memories SET content=?,category=COALESCE(?, 'GENERAL'),source=?,importance=?,tags=?::jsonb,updated_at=now() WHERE user_id=? AND id=?",
   r.content(),r.category(),r.source(),r.importance()==null?3:r.importance(),json(r.tags()),userId,id);
  if(n==0) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,"Memory not found"); return get(userId,id);}
 @DeleteMapping("/{id}") @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
 public void delete(@PathVariable UUID userId,@PathVariable UUID id){if(db.update("DELETE FROM memories WHERE user_id=? AND id=?",userId,id)==0)
  throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,"Memory not found");}
 private static Memory map(java.sql.ResultSet rs)throws java.sql.SQLException{
  List<String> tags=JSON.readValue(rs.getString("tags"),new tools.jackson.core.type.TypeReference<List<String>>(){});
  return new Memory(rs.getObject("id",UUID.class),rs.getString("content"),rs.getString("category"),rs.getString("source"),rs.getInt("importance"),tags,
   rs.getObject("created_at",java.time.OffsetDateTime.class),rs.getObject("updated_at",java.time.OffsetDateTime.class));}
 private static String json(List<String> tags){if(tags==null)return "[]";if(tags.size()>30||tags.stream().anyMatch(t->t==null||t.length()>40))
  throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Invalid tags");
  return JSON.valueToTree(tags).toString();}
}