package com.example.prepics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

  @Column(name = "description")
  private String description;

  @Column(name = "date_upload")
  private BigInteger dateUpload;

  @Column(name = "liked")
  private int likeds;

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

  @Column(name = "data_byte")
  private String dataByte;

  //type true la gallery, false la video
  @Column(name = "type")
  private boolean type;

  @Transient
  private String tags;

  @Transient
  private boolean isLiked;

  @Column(name = "is_public")
  private boolean isPublic;

  @Column(name = "user_id")
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

  @OneToMany(mappedBy = "contentId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonIgnoreProperties(
      value = {
          "applications",
//          "user"
      }
  )
  Set<Comment> comments;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  @JsonIgnoreProperties(
      value = {
          "applications",
          "contents",
          "collections",
          "user",
          "followers",
          "followees"
      }
  )
  private User user;
}
