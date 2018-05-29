package com.wmw.recommender.domain;

import java.util.Random;
import lombok.Data;

@Data
public class Movie {

  private static final Random random = new Random();

  private int movieId;
  private String title;
  private String genres;
  private String urlSuffix = "tt" + random.nextInt(10_000_000);
}
