package com.example.prepics.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

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
