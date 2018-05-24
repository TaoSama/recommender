# User-based Recommender System
A user-based recommender system, based on Apache Mahout, GroupLens MovieLens dataset.

## Framework used

Spring Boot, Spring Security, Mybatis, Thymeleaf, Bootstrap, ...

# How to install

## Requirements

* **JDK 1.8** or higher.
* Better with IntelliJ IDEA.

## Dependencies

* MariaDB  
  `brew install mariadb` (for Mac users)

## Install

### Get the code

* Clone from repository.  
  `git clone git@github.com:TaoSama/recommender.git`
* Then work aroud in the project root directory.  
  `cd recommender`

### Load processd GroupLens MoviesLens dataset into mysql

* Suppose you have installed **MariaDB**, then execute the following sql.  
  `mysql -u root < ./src/main/resources/import_movielens_dataset.sql`

### Build and run

* Build with maven wrapper.  
  `./mvnw clean package`
* Run with Java.  
  `java -jar target/recommender-0.0.1-SNAPSHOT.jar`
* Open your browser to see the demo.  
  `http://localhost:8080`

# License

[MIT](./LICENSE)

