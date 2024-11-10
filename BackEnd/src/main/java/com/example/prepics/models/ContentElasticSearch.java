package com.example.prepics.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "contents")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentElasticSearch {

    Long id;

    String tags;

    boolean type;

    @Override
    public boolean equals(Object o) {
        return o instanceof ContentElasticSearch c &&
                Objects.equals(id, c.id) &&
                Objects.equals(tags, c.tags) &&
                type == c.type;
    }
}
