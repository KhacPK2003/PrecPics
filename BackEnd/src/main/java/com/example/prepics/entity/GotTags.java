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
@Table(name = "gottags", schema = "public")
public class GotTags implements Serializable {

    @Serial
    private static final long serialVersionUID = 123L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content_id")
    private String contentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "content_id", insertable=false, updatable=false)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "inCols",
                    "gotTags",
                    "user"
            }
    )
    private Content content;

    @Column(name = "tag_id")
    private Long tagId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id", insertable=false, updatable=false)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "gotTags",
            }
    )
    private Tag tag;
}
