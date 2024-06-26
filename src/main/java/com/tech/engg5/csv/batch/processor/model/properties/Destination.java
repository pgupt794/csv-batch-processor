package com.tech.engg5.csv.batch.processor.model.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Destination {
  String target;
  String localPath;
}
