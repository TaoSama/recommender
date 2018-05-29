package com.wmw.recommender.mapper;

import com.wmw.recommender.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.security.core.parameters.P;

@Mapper
public interface UserMapper {

  @Select("SELECT * FROM users WHERE user_id = #{userId}")
  User findByUserId(@Param("userId") int userId);

  @Select("SELECT * FROM users WHERE username = #{username}")
  User findByUsername(@Param("username") String username);

  @Insert("INSERT INTO users (username, password, gender, age, occupation, zip_code)"
       + "VALUES (#{username}, "
       + "#{password},"
       + "#{gender},"
       + "#{age},"
       + "#{occupation},"
       + "#{zipCode})")
  void addUser(@Param("username") String username,
               @Param("password") String password,
               @Param("gender") char gender,
               @Param("age") int age,
               @Param("occupation") int occupation,
               @Param("zipCode") String zipCode);

}
