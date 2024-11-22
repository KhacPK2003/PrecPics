package com.example.prepics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment", schema = "public")
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 11234134523452345L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "content")
    private String contentValue;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "content_id")
    private String contentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "user_id")
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "contents",
                    "collections",
                    "followers",
                    "followees",
            }
    )
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "content_id")
    @JsonIgnore()
    private Content content;
}
