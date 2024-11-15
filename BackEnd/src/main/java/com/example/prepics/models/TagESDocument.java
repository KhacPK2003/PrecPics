package com.example.prepics.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = "tags")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagESDocument {
    private String id;
    private String name;

    @Override
    public boolean equals(Object o) {
        return o instanceof TagESDocument c &&
                Objects.equals(id, c.id) &&
                Objects.equals(name, c.name);
    }
}
