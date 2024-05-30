package com.tech.engg5.csv.batch.processor.utils;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidProvider {

  public String uuid() {
    return UUID.randomUUID().toString();
  }
}
