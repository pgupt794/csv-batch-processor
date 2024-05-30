package com.tech.engg5.csv.batch.processor.repository;

import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BatchSummaryRepositoryCustom {

  Mono<Boolean> doesSummaryFileRecordExist(String fileName);

  Mono<BatchSummary> createOrUpdateBatchSummary(BatchSummary batchSummary);
}
