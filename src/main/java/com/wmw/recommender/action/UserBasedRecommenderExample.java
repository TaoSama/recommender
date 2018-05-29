package com.wmw.recommender.action;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserBasedRecommenderExample {

  private static final int NEAREST_USER_NEIGHBORHOOD = 10;

  private DataSource dataSource;

  private UserBasedRecommender userBasedRecommender;

  @Autowired
  public UserBasedRecommenderExample(@Qualifier("mysqlDataSource") DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @PostConstruct
  public void init() {

    String preferenceTable = "ratings";
    String userIDColumn = "user_id";
    String itemIDColumn = "movie_id";
    String preferenceColumn = "rating";
    String timestampColumn = "timestamp";

    try {
      log.info("Building user based recommender...");
      // 利用ReloadFromJDBCDataModel包裹jdbcDataModel,可以把输入加入内存计算，加快计算速度。
      ReloadFromJDBCDataModel model = new ReloadFromJDBCDataModel(new MySQLJDBCDataModel(
          dataSource,
          preferenceTable,
          userIDColumn,
          itemIDColumn,
          preferenceColumn,
          timestampColumn));

      ScheduledExecutorService scheduledExecutorService
          = Executors.newSingleThreadScheduledExecutor();
      
      Runnable runnable = () -> model.refresh(null);
      scheduledExecutorService.scheduleAtFixedRate(runnable, 5, 5, TimeUnit.SECONDS);

      UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
      UserNeighborhood neighborhood = new NearestNUserNeighborhood(
          NEAREST_USER_NEIGHBORHOOD, similarity, model);

      userBasedRecommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

      log.info("User based recommender was built.");

    } catch (Exception e) {
      log.error("Error when building user base recommender, msg={}", e.toString(), e);
    }
  }

  public List<RecommendedItem> recommend(int userId, int number) {
    try {
      return userBasedRecommender.recommend(userId, number);
    } catch (TasteException e) {
      log.error("Error when recommending for user (userId={}), msg={}", userId, e.toString(), e);
      return Collections.emptyList();
    }
  }
}
