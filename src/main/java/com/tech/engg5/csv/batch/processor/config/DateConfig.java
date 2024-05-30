package com.tech.engg5.csv.batch.processor.config;

import java.time.*;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@Configuration
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
public class DateConfig {
  public static final ZoneId EST_ZONE = ZoneId.of("America/New_York");
  public static final ZoneOffset EST_ZONE_OFFSET = ZoneOffset.of("-05:00");

  @Bean
  public Clock systemClock() {
    return Clock.system(EST_ZONE);
  }

  @Bean
  public DateTimeProvider dateTimeProvider(Clock systemClock) {
    return () -> Optional.of(LocalDateTime.ofInstant(Instant.now(systemClock), EST_ZONE));
  }
}
