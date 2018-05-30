package com.wmw.recommender.controller;

import com.wmw.recommender.action.UserBasedRecommenderExample;
import com.wmw.recommender.domain.Movie;
import com.wmw.recommender.domain.Rating;
import com.wmw.recommender.domain.RecommendedMovie;
import com.wmw.recommender.domain.User;
import com.wmw.recommender.mapper.MovieMapper;
import com.wmw.recommender.mapper.RatingMapper;
import com.wmw.recommender.mapper.UserMapper;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
public class RecommenderController {

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
  public RecommenderController(UserMapper userMapper,
                               MovieMapper movieMapper,
                               RatingMapper ratingMapper,
                               UserBasedRecommenderExample userBasedRecommender) {
    this.userMapper = userMapper;
    this.movieMapper = movieMapper;
    this.ratingMapper = ratingMapper;
    this.userBasedRecommender = userBasedRecommender;
  }

  @RequestMapping("/")
  public String index(ModelMap map, Authentication auth) {
    log.info("Authenticated auth: {}", auth);
    if (Objects.nonNull(auth)) {
      int userId = userMapper.findByUsername(auth.getName()).getUserId();
      map.put("userId", userId);
      log.info("Username: {}, userId: {}, rated movies count: {}, sample: {}",
          auth.getName(),
          userId,
          ratingMapper.findByUserId(userId).size(),
          ratingMapper.findByUserId(userId).stream().limit(1).collect(Collectors.toList()));
    }

    List<RecommendedMovie> recommendedMovies = recommendedMoviesReference.get();
    log.info("Recommended in welcome page, movies={}", recommendedMovies);
    map.put("movies", recommendedMovies);
    return "index";
  }

  private List<RecommendedMovie> getUserRatedMovies(int userId, int limit) {
    List<Rating> ratings = ratingMapper.findByUserId(userId);
    return ratings.stream()
        .sorted(Comparator.comparing(Rating::getTimestamp).reversed())
        .map(rating -> {
          Movie movie = movieMapper.findByMovieId(rating.getMovieId());
          RecommendedMovie recommendedMovie = new RecommendedMovie();
          recommendedMovie.setMovieId(movie.getMovieId());
          recommendedMovie.setAverageRating(rating.getRating());
          recommendedMovie.setUrlSuffix(movie.getUrlSuffix());
          recommendedMovie.setGenres(movie.getGenres());
          recommendedMovie.setTitle(movie.getTitle());
          return recommendedMovie;
        })
        .limit(limit)
        .collect(Collectors.toList());
  }

  @GetMapping("/profile/{userId}")
  public String profileGet(@PathVariable int userId, Rating rating, ModelMap map) {
    List<RecommendedMovie> recommendedMovies = recommendedMoviesReference.get();
    log.info("Recommended in profile page, movies={}", recommendedMovies);
    List<RecommendedMovie> userBasedRecommendedMovies =
        userBasedRecommender.recommend(userId, 6)
            .stream()
            .map(recommendedItem -> {
              RecommendedMovie recommendedMovie =
                  ratingMapper.findByMovieId((int) recommendedItem.getItemID());
              Movie movie = movieMapper.findByMovieId((int) recommendedItem.getItemID());
              recommendedMovie.setUrlSuffix(movie.getUrlSuffix());
              recommendedMovie.setGenres(movie.getGenres());
              recommendedMovie.setTitle(movie.getTitle());
              return recommendedMovie;
            })
            .collect(Collectors.toList());
    userBasedRecommendedMovies.addAll(recommendedMovies);
    userBasedRecommendedMovies = userBasedRecommendedMovies.subList(0, 6);
    log.info("Recommended in profile page, movies={}", userBasedRecommendedMovies);

    map.put("umovies", getUserRatedMovies(userId, 6));
    map.put("movies", userBasedRecommendedMovies);
    map.put("username", userMapper.findByUserId(userId).getUsername());
    return "profile";
  }

  @GetMapping("profile/{userId}/{movieId}")
  public String rateMovieGet(
      @PathVariable int userId,
      @PathVariable int movieId,
      Rating rating,
      ModelMap map) {
    RecommendedMovie recommendedMovie = ratingMapper.findByMovieId(movieId);
    Movie movie = movieMapper.findByMovieId(movieId);
    recommendedMovie.setUrlSuffix(movie.getUrlSuffix());
    recommendedMovie.setGenres(movie.getGenres());
    recommendedMovie.setTitle(movie.getTitle());
    map.put("movie", recommendedMovie);
    map.put("username", userMapper.findByUserId(userId).getUsername());
    map.put("success", "not");
    return "rate";
  }

  @PostMapping("profile/{userId}/{movieId}")
  public String rateMoviePost(@PathVariable int userId,
                              @PathVariable int movieId,
                              Rating rating,
                            ModelMap map) {
    RecommendedMovie recommendedMovie = ratingMapper.findByMovieId(movieId);
    Movie movie = movieMapper.findByMovieId(movieId);
    recommendedMovie.setUrlSuffix(movie.getUrlSuffix());
    recommendedMovie.setGenres(movie.getGenres());
    recommendedMovie.setTitle(movie.getTitle());
    map.put("movie", recommendedMovie);
    map.put("username", userMapper.findByUserId(userId).getUsername());

    rating.setTimestamp(System.currentTimeMillis() / 1000);
    log.info("User rated a movie, rating={}", rating);
    try {
      ratingMapper.addRating(rating.getUserId(),
          rating.getMovieId(),
          rating.getRating(),
          rating.getTimestamp());
      map.put("success", "true");
    } catch (Exception e) {
      log.error("Can't rate movie, rating={}", rating, e);
      map.put("success", "false");
    }
    return "rate";
  }

  @GetMapping("/profile/{userId}/showall")
  public String showAll(@PathVariable int userId, ModelMap map) {
    map.put("umovies", getUserRatedMovies(userId, Integer.MAX_VALUE));
    map.put("username", userMapper.findByUserId(userId).getUsername());
    return "showall";
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
    if (Objects.nonNull(userMapper.findByUsername(user.getUsername()))) {
      bindingResult.rejectValue(
          "username", "username.existent", "Existent username");
      return "signup";
    }
    try {
      userMapper.addUser(
          user.getUsername(),
          user.getPassword(),
          user.getGender(),
          user.getAge(),
          user.getOccupation(),
          user.getZipCode());
    } catch (Exception e) {
      log.error("Can't add user, user={}", user, e);
      bindingResult.rejectValue(
          "username", "username.illegal", "Illegal username");
      return "signup";
    }
    log.info("User added, user={}", userMapper.findByUsername(user.getUsername()));
    return "redirect:/signin";
  }

  @RequestMapping("/signin")
  public String signin() {
    return "signin";
  }
}
