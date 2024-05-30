package com.tech.engg5.csv.batch.processor.service;

import com.tech.engg5.csv.batch.processor.enums.FileStatus;
import com.tech.engg5.csv.batch.processor.exception.FileDecodeException;
import com.tech.engg5.csv.batch.processor.exception.FileDuplicateException;
import com.tech.engg5.csv.batch.processor.model.domain.BatchRequest;
import com.tech.engg5.csv.batch.processor.model.domain.BatchResponse;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import com.tech.engg5.csv.batch.processor.repository.BatchSummaryRepository;
import com.tech.engg5.csv.batch.processor.utils.BatchFileUtility;
import com.tech.engg5.csv.batch.processor.utils.FileValidator;
import com.tech.engg5.csv.batch.processor.utils.ObjectBuilder;
import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BatchProcessingService {

  BatchSummaryRepository batchSummaryRepository;

  BatchFileUtility batchFileUtility;
  FileValidator fileValidator;
  ObjectBuilder objectBuilder;

  public Mono<BatchResponse> handleBatchFileRequest(BatchRequest request) {
    val summary = objectBuilder.buildBatchFileSummary(request.getFileName(), request.getCreatedBy(),
      request.getTotalRecordCount(), FileStatus.FILE_RECEIVED.name(), request.getFileDate());

    return Mono.just(request)
      .flatMap(req -> fileValidator.duplicateFileValidationCheck(summary))
      .flatMap(this::saveBatchSummary)
      .thenReturn(summary)
      .flatMap(this::triggerAsyncJob)
      .onErrorResume(err -> {
        if (err instanceof FileDuplicateException) {
          this.saveBatchSummary(summary.updateFileStatus(FileStatus.DUPLICATE_FILE_RECEIVED.name())).subscribe();
        } else if (err instanceof FileDecodeException) {
          this.saveBatchSummary(summary.updateFileStatus(FileStatus.FILE_DECODE_FAILED.name())).subscribe();
        }
        LOG.error("Exception occurred while processing batch-file, summaryId - [{}]", summary.getSummaryId());
        return Mono.error(err);
      })
      .map(ctx -> objectBuilder.buildBatchResponse(ctx.getSummaryId(), ctx.getStatus()));
  }

  private Mono<BatchSummary> triggerAsyncJob(BatchSummary summary) {
    LOG.info("Async job to process the file triggered.");
    try {
      CompletableFuture.supplyAsync(() -> this.processBatchFile(summary).thenReturn(summary).subscribe());
    } catch (Exception exc) {
      LOG.error("Exception occurred on job trigger.", exc);
      exc.printStackTrace();
    }
    return Mono.just(summary);
  }

  private Mono<BatchSummary> processBatchFile(BatchSummary summary) {
    LOG.info("Processing batch-file with summaryId - [{}]", summary.getSummaryId());
    return Mono.empty();
  }

  public Mono<BatchSummary> saveBatchSummary(BatchSummary summary) {
    LOG.info("Inside BatchProcessingService.saveBatchSummary method.");
    return batchSummaryRepository.createOrUpdateBatchSummary(summary)
      .onErrorResume(err -> {
        LOG.error("Database exception occurred while saving the summary.");
        return Mono.error(err);
      });
  }
}
