package com.wmw.recommender.domain;

import java.util.Random;
import lombok.Data;

@Data
public class Movie {

  private int movieId;
  private String title;
  private String genres;
}
