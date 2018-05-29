package com.wmw.recommender.controller;

import com.wmw.recommender.action.UserBasedRecommenderExample;
import com.wmw.recommender.domain.Movie;
import com.wmw.recommender.domain.RecommendedMovie;
import com.wmw.recommender.domain.User;
import com.wmw.recommender.mapper.MovieMapper;
import com.wmw.recommender.mapper.RatingMapper;
import com.wmw.recommender.mapper.UserMapper;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
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

  private AtomicReference<List<RecommendedMovie>> recommendedMoviesReference
      = new AtomicReference<>(Collections.emptyList());

  private void updateTop100RecommendedMovies() {
    List<RecommendedMovie> top100RecommendedMovies = movieMapper.top100UserBasedRecommenderMovies();
    List<RecommendedMovie> recommendedMovies = IntStream.range(0, 6)
        .mapToObj(index -> {
          RecommendedMovie recommendedMovie =
              top100RecommendedMovies.get(random.nextInt(top100RecommendedMovies.size()));
          Movie movie = movieMapper.findByMovieId(recommendedMovie.getMovieId());
          recommendedMovie.setTitle(movie.getTitle());
          recommendedMovie.setGenres(movie.getGenres());
          recommendedMovie.setUrlSuffix(movie.getUrlSuffix());
          return recommendedMovie;
        })
        .collect(Collectors.toList());
    recommendedMoviesReference.set(recommendedMovies);
    log.info("Recommended movies updated, movies={}", recommendedMovies);
  }

  @PostConstruct
  public void init() {
    ScheduledExecutorService scheduledExecutorService
        = Executors.newSingleThreadScheduledExecutor();
    Runnable runnable = this::updateTop100RecommendedMovies;
    runnable.run();
    scheduledExecutorService.scheduleAtFixedRate(runnable, 5, 5, TimeUnit.SECONDS);
  }

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

    List<RecommendedMovie> recommendedMovies = recommendedMoviesReference.get();
    log.info("Recommended in welcome page, movies={}", recommendedMovies);
    map.put("movies", recommendedMovies);
    return "index";
  }

  @GetMapping("/signup")
  public String signUp(User user) {
    return "signup";
  }

  @PostMapping("/signup")
  public String signUp(@Valid User user, BindingResult bindingResult) {
    log.info("Sign up, user={}", user);
    if (bindingResult.hasErrors()) {
      return "signup";
    }
    if (!user.getConfirmPassword().equals(user.getPassword())) {
      bindingResult.rejectValue(
          "confirmPassword", "confirmPassword.mismatch", "Mismatched password");
      return "signup";
    }
    userMapper.addUser(
        user.getUsername(),
        user.getPassword(),
        user.getGender(),
        user.getAge(),
        user.getOccupation(),
        user.getZipCode());
    log.info("User added, user={}", userMapper.findByUsername(user.getUsername()));
    return "redirect:/";
  }

  @RequestMapping("/signin")
  public String signin() {
    return "signin";
  }
}
