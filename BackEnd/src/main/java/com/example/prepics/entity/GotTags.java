package com.example.prepics.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "gottags", schema = "public")
public class GotTags implements Serializable {

    @Serial
    private static final long serialVersionUID = 123L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content_id", insertable=false, updatable=false)
    private String contentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "inCols",
                    "gotTags",
                    "user"
            }
    )
    private Content content;

    @Column(name = "tag_id", insertable=false, updatable=false)
    private String tagId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "gotTags",
            }
    )
    private Tag tag;
}
