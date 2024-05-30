package com.tech.engg5.csv.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.tech.engg5.csv.batch.processor.model.mongo.BatchRecord;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.RecursiveComparisonAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

public class IntegrationTestBase {

  protected static final List<String> DEFAULT_FIELDS_TO_IGNORE_SUMMARY =
    Arrays.asList(BatchSummary.Fields.createdTs, BatchSummary.Fields.lastUpdatedTs);

  protected static final List<String> DEFAULT_FIELDS_TO_IGNORE_RECORD =
    Arrays.asList(BatchRecord.Fields.createdTs, BatchRecord.Fields.lastUpdatedTs);

  protected static final Duration EXPECTING_MESSAGES_TIMEOUT = Duration.ofSeconds(500);

  @Autowired
  protected ReactiveMongoOperations reactiveMongoOperations;

  @SafeVarargs
  protected final <T> void thenExpectSummaryDatabaseEntries(Class<T> type, T... expectedEntries) {
    thenExpectDatabaseEntries(type, DEFAULT_FIELDS_TO_IGNORE_SUMMARY, expectedEntries);
  }

  @SafeVarargs
  protected final <T> void thenExpectRecordDatabaseEntries(Class<T> type, T... expectedEntries) {
    thenExpectDatabaseEntries(type, DEFAULT_FIELDS_TO_IGNORE_RECORD, expectedEntries);
  }

  @SafeVarargs
  protected final <T> void thenExpectDatabaseEntries(Class<T> type, List<String> fieldsToIgnore, T... expectedEntries) {
    await()
      .atMost(EXPECTING_MESSAGES_TIMEOUT)
      .until(() -> reactiveMongoOperations.findAll(type).collectList().block().size() == expectedEntries.length);

    List<T> entriesInCollection = reactiveMongoOperations.findAll(type).collectList().block();
    RecursiveComparisonAssert<?> assertion = assertThat(entriesInCollection)
      .usingRecursiveComparison()
      .ignoringAllOverriddenEquals()
      .ignoringFields(fieldsToIgnore.toArray(String[]::new))
      .ignoringCollectionOrder();

    assertion.isEqualTo(Arrays.asList(expectedEntries));
  }
}
