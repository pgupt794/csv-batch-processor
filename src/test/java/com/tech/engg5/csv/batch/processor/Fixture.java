package com.tech.engg5.csv.batch.processor;

import static wiremock.org.apache.commons.lang3.StringUtils.join;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Fixture {
  CONTROLLER_REQUEST("controller/requests"),
  CONTROLLER_RESPONSE("controller/response"),
  DATABASE("database");

  String path;

  @SneakyThrows
  public String loadFixture(String filename, SubPath... subPaths) {
    String fixturePath = "fixtures/" + this.path + '/' + join(subPaths, '/') + '/' + filename;
    try (InputStream inputStream = new ClassPathResource(fixturePath).getInputStream()) {
      return new String(IOUtils.toByteArray(inputStream), StandardCharsets.UTF_8);
    }
  }

  @RequiredArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  public enum SubPath {
    BATCH_RECORDS("record-context"),
    BATCH_SUMMARY("summary-context"),
    RAW("raw-context"),
    TRANSFORMED("transformed-context");

    String subPath;

    @Override
    public String toString() {
      return subPath;
    }
  }
}
