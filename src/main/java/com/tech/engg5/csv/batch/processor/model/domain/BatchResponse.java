package com.tech.engg5.csv.batch.processor.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchResponse {

  String summaryId;
  String status;
}
