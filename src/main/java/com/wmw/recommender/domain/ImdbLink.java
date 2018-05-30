package com.wmw.recommender.domain;

import lombok.Data;

@Data
public class ImdbLink {

  private int movieId;
  private String urlSuffix;
}
