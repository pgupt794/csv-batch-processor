package com.tech.engg5.csv.batch.processor.utils;

import com.tech.engg5.csv.batch.processor.constant.FileConstant;
import com.tech.engg5.csv.batch.processor.enums.RecordStatus;
import com.tech.engg5.csv.batch.processor.model.commons.AdditionalParam;
import com.tech.engg5.csv.batch.processor.model.commons.SensitiveId;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchRecord;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BatchFileDeserializer {

  ObjectBuilder objectBuilder;

  public void deserializeBatchFile(InputStream inputStream, BatchSummary summary, FluxSink<BatchRecord> fluxSink) {
    LOG.info("Deserializing batch file.");
    BufferedReader reader = null;

    try {
      BOMInputStream bomInputStream = new BOMInputStream(inputStream);
      ByteOrderMark bom = bomInputStream.getBOM();
      String charsetName = bom == null ? FileConstant.DEFAULT_ENCODING : bom.getCharsetName();

      reader = new BufferedReader(new InputStreamReader(bomInputStream, charsetName));
      String line;
      List<String> headers = Arrays.asList(reader.readLine().trim().split(","));

      while ((line = reader.readLine()) != null) {
        List<String> row = Arrays.asList(line.split(","));
        List<AdditionalParam> additionalParams = new ArrayList<>();

        val record = objectBuilder.buildBatchRecord(summary.getSummaryId(), RecordStatus.PENDING.name());

        if (headers.get(0).equalsIgnoreCase(FileConstant.CUSTOMER_TOKEN)) {
          LOG.info("Batch file contains only customer_token. File-summaryId - [{}]", summary.getSummaryId());
          record.setCustomerToken(row.get(0));
          record.setBookNumber(null);
          record.setCustomerNumber(null);

          for (int i = 1; i < headers.size(); i++) {
            additionalParams.add(AdditionalParam.builder()
              .name(headers.get(i))
              .value(SensitiveId.builder().encryptedValue(row.get(i)).build())
              .build());
          }
          record.setAdditionalParams(additionalParams);
          fluxSink.next(record);
        } else if (headers.get(0).equalsIgnoreCase(FileConstant.BOOK_ID)
            && headers.get(1).equalsIgnoreCase(FileConstant.CUSTOMER_ID)) {
          LOG.info(
              "Batch file contains bookId and customerId. File-summaryId - [{}]",
              summary.getSummaryId());
          record.setCustomerToken(null);
          record.setBookNumber(SensitiveId.builder().encryptedValue(row.get(0)).build());
          record.setCustomerNumber(SensitiveId.builder().encryptedValue(row.get(1)).build());

          for (int i = 2; i < headers.size(); i++) {
            additionalParams.add(AdditionalParam.builder()
              .name(headers.get(i))
              .value(SensitiveId.builder().encryptedValue(row.get(i)).build())
              .build());
          }
          record.setAdditionalParams(additionalParams);
          fluxSink.next(record);
        }
      }
      fluxSink.complete();
    } catch (Exception exc) {
      LOG.error("Exception occurred while deserializing the file.", exc);
    } finally {
      if (ObjectUtils.isNotEmpty(reader)) {
        try {
          reader.close();
        } catch (Exception exc) {
          throw new RuntimeException(exc);
        }
      }
    }
  }
}
