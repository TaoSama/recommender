package com.wmw.recommender.controller;

import com.wmw.recommender.action.UserBasedRecommenderExample;
import com.wmw.recommender.domain.Movie;
import com.wmw.recommender.domain.User;
import com.wmw.recommender.mapper.MovieMapper;
import com.wmw.recommender.mapper.RatingMapper;
import com.wmw.recommender.mapper.UserMapper;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
public class HelloWorldController {

  private UserMapper userMapper;
  private MovieMapper movieMapper;
  private RatingMapper ratingMapper;

  private UserBasedRecommenderExample userBasedRecommender;

  private static final Random random = new Random();

  @NotNull
  @Value("${grouplens.userSize}")
  private int userSize;

  @NotNull
  @Value("${grouplens.movieSize}")
  private int movieSize;

  @NotNull
  @Value("${grouplens.occupations")
  private List<String> occupations;

  @Autowired
  public HelloWorldController(UserMapper userMapper,
                              MovieMapper movieMapper,
                              RatingMapper ratingMapper,
                              UserBasedRecommenderExample userBasedRecommender) {
    this.userMapper = userMapper;
    this.movieMapper = movieMapper;
    this.ratingMapper = ratingMapper;
    this.userBasedRecommender = userBasedRecommender;
  }

  @RequestMapping("/")
  public String index(ModelMap map, Principal principal) {
    log.info("Authenticated principal: {}", principal);
    if (Objects.nonNull(principal)) {
      int userId = userMapper.findByUsername(principal.getName()).getUserId();
      log.info("Username: {}, userId: {}, rated movies count: {}, sample: {}",
          principal.getName(),
          userId,
          ratingMapper.findByUserId(userId).size(),
          ratingMapper.findByUserId(userId).stream().limit(1).collect(Collectors.toList()));
    }
    List<Movie> movies = IntStream.range(0, 6)
        .mapToObj(index -> {
          while (true) {
            Movie movie = movieMapper.findByMovieId(random.nextInt(movieSize) + 1);
            if (Objects.nonNull(movie)) {
              return movie;
            }
          }
        })
        .collect(Collectors.toList());
    log.info("Recommended in welcome page, movies={}", movies);
    map.put("movies", movies);
    return "index";
  }

  @GetMapping("/signup")
  public String signUp(User user) {
    return "signup";
  }

  @PostMapping("/signup")
  public String signUp(@Valid User user, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "signup";
    }
    if (!user.getPassword().equals("12345")) {
      log.info("test ok");
    }
    return "redirect:/";
  }

  @RequestMapping("/signin")
  public String signin() {
    return "signin";
  }
}
