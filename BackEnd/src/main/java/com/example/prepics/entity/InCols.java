package com.example.prepics.entity;


import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inCols", schema = "public")
public class InCols implements Serializable {

    @Serial
    private static final long serialVersionUID = 12L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "collection_id", insertable=false, updatable=false)
    private String collectionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "inCols",
                    "user"
            }
    )
    private Collection collection;
}
