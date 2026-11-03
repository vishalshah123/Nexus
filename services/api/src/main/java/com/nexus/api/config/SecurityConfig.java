package com.nexus.api.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean SecurityFilterChain nexusSecurity(HttpSecurity http,
   @Value("${nexus.security.enabled:false}") boolean securityEnabled)throws Exception {
  http.csrf(csrf->csrf.disable());
  if(securityEnabled) {
   http.authorizeHttpRequests(auth->auth.requestMatchers("/actuator/health","/actuator/info").permitAll().anyRequest().authenticated())
       .oauth2ResourceServer(oauth->oauth.jwt(Customizer.withDefaults()));
  } else {
   http.authorizeHttpRequests(auth->auth.anyRequest().permitAll());
  }
  return http.build();
 }

  @Bean
  @ConditionalOnProperty(name="nexus.security.enabled", havingValue="true")
  JwtDecoder nexusJwtDecoder(@Value("${nexus.jwt.issuer-uri}") String issuerUri) {
   return JwtDecoders.fromIssuerLocation(issuerUri);
  }
}