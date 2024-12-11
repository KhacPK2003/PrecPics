package com.example.prepics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

  @Column(name = "user_id")
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
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
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
