package com.wmw.recommender.mapper;

import com.wmw.recommender.domain.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MovieMapper {

  @Select("select * from movies where movie_id = #{movieId}")
  Movie findByMovieId(@Param("movieId") int movieId);
}
