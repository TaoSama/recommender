package com.wmw.recommender.mapper;

import com.wmw.recommender.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

  @Select("select * from users where user_id = #{userId}")
  User findByUserId(@Param("userId") int userId);

  @Select("select * from users where username = #{username}")
  User findByUsername(@Param("username") String username);
}
