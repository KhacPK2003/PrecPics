package com.example.prepics.config;

import com.example.prepics.exception.DuplicateFileException;
import com.example.prepics.exception.FileTypeNotAllowedException;
import com.example.prepics.exception.GlobalRuntimeException;
import com.example.prepics.exception.StorageFullException;
import com.example.prepics.utils.ResponseBodyServer;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<?> handleFileSizeLimitExceeded(FileSizeLimitExceededException ex) {
        ResponseBodyServer bodyServer = ResponseBodyServer.builder()
                .statusCode(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .message("File size exceeds the limit")
                .payload(ex.getMessage()) // Tuỳ chỉnh giới hạn
                .build();
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(bodyServer);
    }

    @ExceptionHandler(FileTypeNotAllowedException.class)
    public ResponseEntity<?> handleFileTypeNotAllowed(FileTypeNotAllowedException ex) {
        ResponseBodyServer bodyServer = ResponseBodyServer.builder()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("File type not allowed")
                .payload("Allowed formats: JPG, PNG, MP4.") // Tuỳ chỉnh định dạng cho phép
                .build();
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(bodyServer);
    }

    @ExceptionHandler(StorageFullException.class)
    public ResponseEntity<?> handleStorageFullException(StorageFullException ex) {
        ResponseBodyServer bodyServer = ResponseBodyServer.builder()
                .statusCode(HttpStatus.INSUFFICIENT_STORAGE.value())
                .message("Storage is full")
                .payload("Please contact support or clear space.")
                .build();
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE).body(bodyServer);
    }

    @ExceptionHandler(DuplicateFileException.class)
    public ResponseEntity<?> handleDuplicateFileException(DuplicateFileException ex) {
        ResponseBodyServer bodyServer = ResponseBodyServer.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message("Fail")
                .payload(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(bodyServer);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        ResponseBodyServer bodyServer = ResponseBodyServer.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .payload(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(bodyServer);
    }

}
