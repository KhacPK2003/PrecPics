package com.example.prepics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsDTO {

  private int totalContents;
  private int totalFollowers;
  private int totalFollowing;
  private int totalLikes;
}
