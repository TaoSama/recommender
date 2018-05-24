package com.wmw.recommender.service;

import com.wmw.recommender.domain.User;
import com.wmw.recommender.mapper.UserMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserService implements UserDetailsService {

  @Autowired
  private UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userMapper.findByUsername(username);
    if (Objects.isNull(user)) {
      throw new UsernameNotFoundException("Username doesn't exists");
    }
    log.info("Username: " + user.getUsername() + ", password:" + user.getPassword());
    return user;
  }
}
