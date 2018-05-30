package com.wmw.recommender.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Rating {

  private int userId;
  private int movieId;
  private float rating;
  private long timestamp;
}
