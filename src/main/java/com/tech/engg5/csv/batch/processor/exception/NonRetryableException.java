package com.tech.engg5.csv.batch.processor.exception;

public class NonRetryableException extends RuntimeException {
  public NonRetryableException(String message) {
    super(message);
  }

  public NonRetryableException(String message, Throwable cause) {
    super(message, cause);
  }
}
