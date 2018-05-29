package com.wmw.recommender.domain;

import lombok.Data;

@Data
public class RecommendedMovie {
  private int movieId;
  private String title;
  private String genres;
  private String urlSuffix;
  private double averageRating;
  private int ratedUserCount;
}
