package com.tech.engg5.csv.batch.processor.model.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.time.LocalDate;
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
@Document(collection = "batch-summary")
@FieldNameConstants
@JsonIgnoreProperties
public class BatchSummary {

  @Id
  String summaryId;
  String fileName;
  String fileCreatedBy;
  Long recordCount;
  Long failedRecordCount;
  String status;
  LocalDate fileDate;
  Instant createdTs;
  Instant lastUpdatedTs;

  public BatchSummary updateFileStatus(String status) {
    this.status = status;
    return this;
  }
}
