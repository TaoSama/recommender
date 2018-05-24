package com.wmw.recommender.domain;

import lombok.Data;

@Data
public class Movie {

  private int movieId;
  private String title;
  private String genres;
}
