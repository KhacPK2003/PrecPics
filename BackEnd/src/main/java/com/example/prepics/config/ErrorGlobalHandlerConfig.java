package com.example.prepics.config;

import com.example.prepics.exception.GlobalRuntimeException;
import com.example.prepics.utils.ResponseBodyServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ErrorGlobalHandlerConfig {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResponseBodyServer bodyServer = ResponseBodyServer.builder()
                .statusCode(500)
                .message("fail")
                .payload(ex.getMessage())
                .build();
        return ResponseEntity.status(500).body(bodyServer);
    }

    @ExceptionHandler(GlobalRuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(GlobalRuntimeException ex) {
        ResponseBodyServer bodyServer = ResponseBodyServer.builder()
                .statusCode(ex.getStatusCode())
                .message("fail")
                .payload(ex.getCauseMessage())
                .build();
        return ResponseEntity.status(500).body(bodyServer);
    }
}
