package com.tech.engg5.csv.batch.processor.utils;

import com.tech.engg5.csv.batch.processor.enums.FileStatus;
import com.tech.engg5.csv.batch.processor.exception.FileDecodeException;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import com.tech.engg5.csv.batch.processor.model.properties.AppProperties;
import com.tech.engg5.csv.batch.processor.service.SourceFileService;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchFileUtility {
  private final AppProperties appProperties;

  @Autowired SourceFileService sourceFileService;

  public Mono<BatchSummary> decodeAndWriteFileToMount(String encodedFileContent, String filename, BatchSummary summary) {
    LOG.info("Decoding the batch file.");
    try {
      byte[] decodeBytes = Base64.getDecoder().decode(encodedFileContent);
      File outputFile = new File(appProperties.getMount().getLocalPath() + "/" + filename);
      FileUtils.writeByteArrayToFile(outputFile, decodeBytes);
      summary.updateFileStatus(FileStatus.FILE_READ_PENDING.name());
      LOG.info("Decoding the batch file completed.");
      return Mono.just(summary);
    } catch (Exception exc) {
      LOG.error("Exception occurred while decoding/writing the file to mount.", exc);
      return Mono.error(new FileDecodeException("Batch file decode failed. Please use "
        + "summaryId - " + summary.getSummaryId() + " for tracking."));
    }
  }

  public Mono<InputStream> getFileAsInputStream(String filename) {
    LOG.info("Returning file input-stream.");
    return Mono.just(sourceFileService.getFileInputStream(filename));
  }
}
