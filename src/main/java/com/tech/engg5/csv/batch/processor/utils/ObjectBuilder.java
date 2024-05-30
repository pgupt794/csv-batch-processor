package com.tech.engg5.csv.batch.processor.utils;

import com.tech.engg5.csv.batch.processor.model.domain.BatchResponse;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchRecord;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ObjectBuilder {

  UuidProvider uuidProvider;

  public BatchSummary buildBatchFileSummary(String filename, String user, Long recordCount, String status,
    LocalDate fileDate) {

    LOG.info("Initializing file-summary with [{}] status.", status);
    return BatchSummary.builder()
      .summaryId(uuidProvider.uuid())
      .fileName(filename)
      .fileCreatedBy(user)
      .recordCount(recordCount)
      .failedRecordCount(0L)
      .status(status)
      .fileDate(fileDate)
      .build();
  }

  public BatchRecord buildBatchRecord(String summaryId, String status) {
    LOG.info("Initializing file-record with status - [{}].", status);
    return BatchRecord.builder()
      .recordCorrelationId(uuidProvider.uuid())
      .summaryId(summaryId)
      .status(status)
      .build();
  }

  public BatchResponse buildBatchResponse(String summaryId, String status) {
    return BatchResponse.builder().summaryId(summaryId).status(status).build();
  }
}
