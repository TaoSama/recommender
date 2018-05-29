package com.wmw.recommender.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendedMovie {
  private int movieId;
  private String title;
  private String genres;
  private String urlSuffix;
  private float averageRating;
  private int ratedUserCount;
}
