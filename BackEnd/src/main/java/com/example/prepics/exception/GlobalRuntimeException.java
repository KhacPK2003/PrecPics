package com.example.prepics.exception;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class GlobalRuntimeException extends RuntimeException{

    private Integer statusCode;

    private String causeMessage;

}
