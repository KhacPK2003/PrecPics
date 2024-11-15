package com.example.prepics.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
public class ContentDTO implements Serializable {
//    MultipartFile file;
    Integer type;
    String tags;
    String description;
}
