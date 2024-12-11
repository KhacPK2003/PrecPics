package com.example.prepics.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DuplicateFileException extends RuntimeException {

  private Integer statusCode;

  private String causeMessage;

  public DuplicateFileException() {
    super();
  }

  public DuplicateFileException(Integer statusCode, String causeMessage) {
    super(causeMessage);
    this.statusCode = statusCode;
    this.causeMessage = causeMessage;
  }
}