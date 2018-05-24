package com.wmw.recommender.domain;

import lombok.Data;

@Data
public class Rating {

  private int userId;
  private int movieId;
  private float rating;
  private long timestamp;
}
