package com.tech.engg5.csv.batch.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class CsvBatchProcessorApplication {
  public static void main(String[] args) {
    ReactorDebugAgent.init();
    System.setProperty("APP_ID", "1000152");
    System.setProperty("APP_NAME", "csv-batch-processor");
    SpringApplication application = new SpringApplication(CsvBatchProcessorApplication.class);
    application.run(args);
  }
}
