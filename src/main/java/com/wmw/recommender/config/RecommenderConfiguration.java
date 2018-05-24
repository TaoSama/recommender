package com.wmw.recommender.config;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
@MapperScan("com.wmw.recommender.mapper")
public class RecommenderConfiguration {

  @Autowired
  @Bean("mysqlDataSource")
  public DataSource dataSource(DatabaseConfiguration databaseConfiguration) {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setServerName(databaseConfiguration.getServerName());
    dataSource.setDatabaseName(databaseConfiguration.getDatabaseName());
    dataSource.setUser(databaseConfiguration.getUsername());
    dataSource.setPassword(databaseConfiguration.getPassword());
    return dataSource;
  }

  @Bean
  public DataSourceTransactionManager transactionManager(
      @Qualifier("mysqlDataSource") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Autowired
  @Bean
  public SqlSessionFactory sqlSessionFactory(
      @Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    SqlSessionFactory factory = sessionFactory.getObject();
    factory.getConfiguration().setMapUnderscoreToCamelCase(true);
    return factory;
  }
}
