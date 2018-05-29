package com.wmw.recommender.mapper;

import com.wmw.recommender.domain.Movie;
import com.wmw.recommender.domain.RecommendedMovie;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MovieMapper {

  @Select("SELECT * FROM movies WHERE movie_id = #{movieId}")
  Movie findByMovieId(@Param("movieId") int movieId);

  @Select("SELECT movie_id, average_rating, rated_user_count FROM "
      + "(SELECT movie_id, COUNT(user_id) rated_user_count, AVG(rating) average_rating "
      + "FROM ratings GROUP BY movie_id) a "
      + "WHERE rated_user_count > 100 ORDER BY average_rating DESC LIMIT 100")
  List<RecommendedMovie> top100UserBasedRecommenderMovies();
}
