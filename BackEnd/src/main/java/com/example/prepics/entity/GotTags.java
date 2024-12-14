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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id", insertable = false, updatable = false)
  @JsonIgnoreProperties(value = {"applications", "inCols", "gotTags", "user"})
  private Content content;

  @Column(name = "tag_id")
  private Long tagId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", insertable = false, updatable = false)
  @JsonIgnoreProperties(value = {"applications", "gotTags"})
  private Tag tag;
}