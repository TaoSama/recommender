# User-based Recommender System
A user-based recommender system, based on Apache Mahout, GroupLens MovieLens dataset.

## Framework used

Apache Mahout, Spring Boot, Spring Security, Mybatis, Thymeleaf, Bootstrap, ...

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
* Then work around in root directory of the project.  
  `cd recommender`

### Load processd GroupLens MoviesLens dataset into mysql

* Suppose you have installed **MariaDB**.
* Modify the `absolute-directory` to fit your computer in `./src/main/resources/import_movielens_dataset.sql`
* Then execute the sql.   
  `mysql -u root < ./src/main/resources/import_movielens_dataset.sql`

### Build and run

* Build with maven wrapper.  
  `./mvnw clean package`
* Run with Java.  
  `java -jar ./target/recommender-0.0.1-SNAPSHOT.jar`
* Open your browser to see the demo.  
  `http://localhost:8080`

# License

[MIT](./LICENSE)

