package com.wmw.recommender.domain;

import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RecommendedMovie {

  private static final Random random = new Random();

  private int movieId;
  private String title;
  private String genres;
  private String urlSuffix = "tt" + random.nextInt(10_000_000);
  private float averageRating;
  private int ratedUserCount;
}
