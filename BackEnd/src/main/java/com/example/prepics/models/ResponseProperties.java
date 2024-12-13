package com.example.prepics.models;

import com.example.prepics.utils.ResponseBodyServer;
import org.springframework.http.ResponseEntity;

public class ResponseProperties {

  public static ResponseEntity<?> createResponse(int status, String message, Object payload) {
    ResponseBodyServer response = ResponseBodyServer.builder()
        .statusCode(status)
        .message(message)
        .payload(payload)
        .build();
    return ResponseEntity.status(status).body(response);
  }
}