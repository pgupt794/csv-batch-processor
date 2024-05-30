package com.tech.engg5.csv.batch.processor.utils;

import com.tech.engg5.csv.batch.processor.exception.FileDuplicateException;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import com.tech.engg5.csv.batch.processor.repository.BatchSummaryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileValidator {

  BatchSummaryRepository batchSummaryRepository;

  public Mono<BatchSummary> duplicateFileValidationCheck(BatchSummary summary) {
    val fileName = summary.getFileName();
    return batchSummaryRepository.doesSummaryFileRecordExist(fileName)
      .flatMap(fileExists -> {
        if (fileExists) {
          LOG.error("Duplicate file received - [{}]", fileName);
          return Mono.error(new FileDuplicateException("Batch file is duplicate. Please use "
            + "summaryId - " + summary.getSummaryId() + " for tracking."));
        }
        LOG.info("Valid file received - [{}]", fileName);
        return Mono.just(summary);
      });
  }
}
