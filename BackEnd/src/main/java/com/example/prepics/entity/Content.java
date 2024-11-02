package com.example.prepics.entity;

import com.example.prepics.views.ContentView;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.firebase.database.core.view.View;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private Long id;

    @Column(name = "location")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private String location;

    @Column(name = "date_upload")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private BigInteger dateUpload;

    @Column(name = "liked")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private int liked;

    @Column(name = "downloads")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private int downloads;

    @Column(name = "views")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private int views;

    @Column(name = "wide")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private int wide;

    @Column(name = "height")
    @JsonView(value = {ContentView.Video.class})
    private int height;

    @Column(name = "image_data")
    @JsonView(value = {ContentView.Gallery.class})
    private byte[] imageData;

    @Column(name = "video_data")
    @JsonView(value = {ContentView.Video.class})
    private String videoData;

    //type true la gallery, false la video
    @Column(name = "type")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private boolean type;

    @Column(name = "is_public")
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private boolean isPublic;

    @Column(name = "user_id", insertable=false, updatable=false)
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    private String userId;

    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    @OneToMany(mappedBy = "contentId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "content",
                    "collection"
            }
    )
    Set<InCols> inCols;

    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
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
    @JsonView(value = {ContentView.Gallery.class, ContentView.Video.class})
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "contents",
                    "collections"
            }
    )
    private User user;
}
