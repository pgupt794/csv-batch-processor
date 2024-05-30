package com.tech.engg5.csv.batch.processor.model.commons;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdditionalParam {
  String name;
  SensitiveId value;
}
