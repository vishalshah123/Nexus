package com.nexus.api.config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.web.SecurityFilterChain;
@Configuration @ConditionalOnProperty(name="nexus.security.enabled",havingValue="true")
public class SecurityConfig {
 @Bean SecurityFilterChain nexusSecurity(HttpSecurity http)throws Exception{return http.csrf(csrf->csrf.disable())
  .authorizeHttpRequests(auth->auth.requestMatchers("/actuator/health","/actuator/info").permitAll().anyRequest().authenticated())
  .oauth2ResourceServer(oauth->oauth.jwt(Customizer.withDefaults())).build();}
}