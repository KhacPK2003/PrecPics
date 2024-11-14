package com.example.prepics.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "collection", schema = "public")
public class Collection implements Serializable {

    @Serial
    private static final long serialVersionUID = 12345L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "user_id", insertable=false, updatable=false)
    private String userId;

    @Column(name = "date_create")
    private BigInteger dateCreate;

    @Column(name = "is_public")
    private boolean isPublic;

    @OneToMany(mappedBy = "collectionId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "content",
                    "collection"
            }
    )
    Set<InCols> inCols;

    @ManyToOne()
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "contents",
                    "collections",
                    "followers",
                    "followees"
            }
    )
    private User user;
}
