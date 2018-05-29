package com.wmw.recommender.mapper;

import com.wmw.recommender.domain.Rating;
import com.wmw.recommender.domain.RecommendedMovie;
import com.wmw.recommender.domain.User;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RatingMapper {

  @Select("SELECT * FROM ratings WHERE user_id = #{userId}")
  List<Rating> findByUserId(@Param("userId") int userId);

  @Select("SELECT MAX(movie_id) movieId, AVG(rating) averageRating, COUNT(user_id) ratedUserCount "
      + "FROM ratings WHERE movie_id = #{movieId}")
  RecommendedMovie findByMovieId(@Param("movieId") int movieId);

  @Insert("INSERT INTO ratings (user_id, movie_id, rating, timestamp) "
      + "VALUES (#{userId},"
      + "#{movieId},"
      + "#{rating},"
      + "#{timestamp})")
  void addRating(@Param("userId") int userId,
               @Param("movieId") int movieId,
               @Param("rating") float rating,
               @Param("timestamp") long timestamp);
}
