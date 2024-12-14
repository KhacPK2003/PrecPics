package com.example.prepics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

  @Column(name = "content_id")
  private String contentId;

  // Thay đổi FetchType thành LAZY để tối ưu hiệu suất
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id", insertable = false, updatable = false)
  @JsonIgnoreProperties(
      value = {
          "applications",
          "inCols",
          "gotTags",
          "user"
      }
  )
  private Content content;

  @Column(name = "collection_id")
  private Long collectionId;

  // Thay đổi FetchType thành LAZY để tối ưu hiệu suất
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collection_id", insertable = false, updatable = false)
  @JsonIgnoreProperties(
      value = {
          "applications",
          "inCols",
          "user"
      }
  )
  private Collection collection;
}