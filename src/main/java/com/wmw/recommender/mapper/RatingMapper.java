package com.wmw.recommender.mapper;

import com.wmw.recommender.domain.Rating;
import com.wmw.recommender.domain.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RatingMapper {

  @Select("SELECT * FROM ratings WHERE user_id = #{userId}")
  List<Rating> findByUserId(@Param("userId") int userId);
}
