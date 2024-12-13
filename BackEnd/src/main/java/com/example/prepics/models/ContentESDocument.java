package com.example.prepics.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = "contents")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentESDocument {

  String id;

  String tags;

  boolean type;

  @Override
  public boolean equals(Object o) {
    return o instanceof ContentESDocument c &&
        Objects.equals(id, c.id) &&
        Objects.equals(tags, c.tags) &&
        type == c.type;
  }
}