package com.wmw.recommender.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("database")
@Data
public class DatabaseConfiguration {

  private String serverName;
  private String databaseName;
  private String username;
  private String password;
  private String driverClassName;
}
