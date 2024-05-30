package com.tech.engg5.csv.batch.processor.model.properties;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDetails {
  String fileName;
  long size;
}
