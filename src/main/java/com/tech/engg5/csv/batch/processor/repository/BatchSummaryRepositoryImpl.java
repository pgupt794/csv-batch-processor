package com.tech.engg5.csv.batch.processor.repository;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class BatchSummaryRepositoryImpl implements BatchSummaryRepositoryCustom {

  private final ReactiveMongoTemplate mongoTemplate;

  @Override
  public Mono<Boolean> doesSummaryFileRecordExist(String fileName) {
    Query booleanQuery = query(where(BatchSummary.Fields.fileName).is(fileName));
    return this.mongoTemplate.exists(booleanQuery, BatchSummary.class);
  }

  @Override
  public Mono<BatchSummary> createOrUpdateBatchSummary(BatchSummary batchSummary) {
    Query createOrUpdateQuery =
      query(where(BatchSummary.Fields.summaryId).is(batchSummary.getSummaryId()));
    Update updateDef = new Update()
      .setOnInsert(BatchSummary.Fields.summaryId, batchSummary.getSummaryId())
      .setOnInsert(BatchSummary.Fields.fileName, batchSummary.getFileName())
      .setOnInsert(BatchSummary.Fields.fileCreatedBy, batchSummary.getFileCreatedBy())
      .setOnInsert(BatchSummary.Fields.fileDate, batchSummary.getFileDate())
      .setOnInsert(BatchSummary.Fields.recordCount, batchSummary.getRecordCount())
      .set(BatchSummary.Fields.failedRecordCount, batchSummary.getFailedRecordCount())
      .setOnInsert(BatchSummary.Fields.createdTs, Instant.now())
      .set(BatchSummary.Fields.status, batchSummary.getStatus())
      .set(BatchSummary.Fields.lastUpdatedTs, Instant.now());

    return this.mongoTemplate.findAndModify(createOrUpdateQuery, updateDef, options().upsert(true).returnNew(true),
      BatchSummary.class)
      .doOnSuccess(res -> LOG.info("BatchSummary - [{}] saved successfully.", batchSummary.getSummaryId()));
  }
}
