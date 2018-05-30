package com.wmw.recommender.mapper;

import com.wmw.recommender.domain.ImdbLink;
import com.wmw.recommender.domain.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ImdbLinkMapper {

  @Select("SELECT * FROM imdb_links WHERE movie_id = #{movieId}")
  ImdbLink findByMovieId(@Param("movieId") int movieId);
}
