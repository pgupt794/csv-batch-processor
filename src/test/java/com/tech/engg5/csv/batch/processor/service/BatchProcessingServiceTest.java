package com.tech.engg5.csv.batch.processor.service;

import static com.tech.engg5.csv.batch.processor.config.DateConfig.EST_ZONE;
import static com.tech.engg5.csv.batch.processor.config.DateConfig.EST_ZONE_OFFSET;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.engg5.csv.batch.processor.Fixture;
import com.tech.engg5.csv.batch.processor.IntegrationTestBase;
import com.tech.engg5.csv.batch.processor.model.mongo.BatchSummary;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
public class BatchProcessingServiceTest extends IntegrationTestBase {

  @Autowired ObjectMapper objectMapper;

  @Autowired private BatchProcessingService batchProcessingService;

  @Autowired private ReactiveMongoOperations reactiveMongoOperations;

  @MockBean private Clock systemClock;

  @BeforeEach
  public void beforeEach() {
    val date = LocalDateTime.of(2024, Month.MAY, 30, 0, 0);
    given(systemClock.instant()).willReturn(date.toInstant(EST_ZONE_OFFSET));
    given(systemClock.getZone()).willReturn(EST_ZONE);
    reactiveMongoOperations.remove(BatchSummary.class).all().block();
  }

  public void afterEach() {
    reactiveMongoOperations.remove(BatchSummary.class).all().block();
  }

  @Test
  @SneakyThrows
  @DisplayName("Verify that summary object get saved in database.")
  void shouldSaveSummaryInDB() {
    val file = "summary-context.json";
    val summary = objectMapper.readValue(Fixture.DATABASE.loadFixture(file, Fixture.SubPath.BATCH_SUMMARY),
      BatchSummary.class);

    batchProcessingService.saveBatchSummary(summary).block();
    thenExpectSummaryDatabaseEntries(BatchSummary.class, summary);
  }
}
