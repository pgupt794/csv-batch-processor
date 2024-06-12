package com.tech.engg5.csv.batch.processor.repository;

import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchSummaryRepository
  extends ReactiveMongoRepository<BatchSummary, String>, BatchSummaryRepositoryCustom {}
