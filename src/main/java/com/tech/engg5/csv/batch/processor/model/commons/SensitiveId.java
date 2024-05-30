package com.tech.engg5.csv.batch.processor.model.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SensitiveId {
  String encryptedValue;
  String hashedValue;
}
