package com.tech.engg5.csv.batch.processor.model.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tech.engg5.csv.batch.processor.model.commons.AdditionalParam;
import com.tech.engg5.csv.batch.processor.model.commons.SensitiveId;
import java.time.Instant;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "batch-record")
@FieldNameConstants
@JsonIgnoreProperties
public class BatchRecord {

  @Id
  String recordCorrelationId;
  String summaryId;
  String customerToken;
  SensitiveId bookNumber;
  SensitiveId customerNumber;
  List<AdditionalParam> additionalParams;
  String status;
  Instant createdTs;
  Instant lastUpdatedTs;
}
