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
@Table(name = "followees", schema = "public")
public class Followees implements Serializable {

    @Serial
    private static final long serialVersionUID = 1123456573434L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "followee_id", insertable=false, updatable=false)
    private String followeeId;

    @Column(name = "user_id", insertable=false, updatable=false)
    private String userId;


    @ManyToOne
    @JoinColumn(name = "followee_id")
    @JsonIgnoreProperties(
            value = {
                    "applications",
                    "contents",
                    "collections",
                    "followers",
                    "followees"
            }
    )
    private User followee;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
