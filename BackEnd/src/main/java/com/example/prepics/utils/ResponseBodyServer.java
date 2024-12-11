package com.example.prepics.utils;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResponseBodyServer implements Serializable {

  @Serial
  private static final long serialVersionUID = 2834365693598045398L;

  private Integer statusCode;

  private String message;

  private Object payload;
}