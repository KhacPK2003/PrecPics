package com.example.prepics.dto;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class ContentDTO implements Serializable {

  Integer type;
  String tags;
  String description;
}
