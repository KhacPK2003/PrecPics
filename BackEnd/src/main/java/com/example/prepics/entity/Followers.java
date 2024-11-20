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

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "followers", schema = "public")
public class Followers implements Serializable {

    @Serial
    private static final long serialVersionUID = 112345657L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "follower_id")
    private String followerId;

    @Column(name = "user_id")
    private String userId;


    @ManyToOne
    @JoinColumn(name = "follower_id", insertable=false, updatable=false)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "contents",
                    "collections",
                    "followers",
                    "followees"
            }
    )
    private User follower;

    @OneToOne
    @JoinColumn(name = "user_id", insertable=false, updatable=false)
    @JsonIgnore
    private User user;
}
