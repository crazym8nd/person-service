package com.bnm.personservice.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

  @Bean
  public AuditorAware<String> auditorProvider() {
    // В будущем здесь можно получать текущего пользователя из SecurityContext
    return () -> Optional.of("system");
  }
} 