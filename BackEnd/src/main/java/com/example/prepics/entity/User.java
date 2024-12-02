package com.example.prepics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user", schema = "public")
@Builder
@ToString
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 123456L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(unique = true, nullable = false, name = "user_name")
    private String userName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "inCols",
                    "user"
            }
    )
    private Set<Collection> collections;

    @OneToMany(mappedBy = "userId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "inCols",
                    "gotTags",
//                    "user"
            }
    )
    private Set<Content> contents;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "user"
            }
    )
    private Set<Followees> followees;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "user"
            }
    )
    private Set<Followers> followers;
}


