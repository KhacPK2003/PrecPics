package com.example.prepics.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "content", schema = "public")
public class Content implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "asset_id")
    private String assetId;

    @Column(name = "date_upload")
    private BigInteger dateUpload;

    @Column(name = "liked")
    private int liked;

    @Column(name = "downloads")
    private int downloads;

    @Column(name = "views")
    private int views;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "data_url")
    private String dataUrl;

    //type true la gallery, false la video
    @Column(name = "type")
    private boolean type;

    @Transient
    private String tags;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "user_id", insertable=false, updatable=false)
    private String userId;

    @OneToMany(mappedBy = "contentId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "content",
                    "collection"
            }
    )
    Set<InCols> inCols;

    @OneToMany(mappedBy = "contentId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "content",
                    "tag"
            }
    )
    Set<GotTags> gotTags;

    @ManyToOne()
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "contents",
                    "collections"
            }
    )
    private User user;
}
