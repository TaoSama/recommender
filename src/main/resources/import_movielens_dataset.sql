
CREATE DATABASE recommender;

USE recommender;

-- UserID::Gender::Age::Occupation::Zip-code
CREATE TABLE users (
    user_id INT NOT NULL AUTO_INCREMENT,
    gender CHAR(1),
    age INT,
    occupation INT,
    zip_code VARCHAR(20),
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id),
    INDEX (user_id)
);

LOAD DATA LOCAL INFILE '/absolute-directory/recommender/ml-1m/users.dat'
INTO TABLE users
FIELDS TERMINATED BY '::'
LINES TERMINATED BY '\n';

--------------------------------------------------------------

-- MovieID::Title::Genres
CREATE TABLE movies (
    movie_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    genres VARCHAR(100) NOT NULL,
    PRIMARY KEY (movie_id),
    INDEX (movie_id)
);

LOAD DATA LOCAL INFILE '/absolute-directory/recommender/ml-1m/movies.dat'
INTO TABLE movies
FIELDS TERMINATED BY '::'
LINES TERMINATED BY '\n';

--------------------------------------------------------------

-- UserID::MovieID::Rating::Timestamp
CREATE TABLE ratings (
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    rating FLOAT NOT NULL,
    timestamp LONG NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

LOAD DATA LOCAL INFILE '/absolute-directory/recommender/ml-1m/ratings.dat'
INTO TABLE ratings
FIELDS TERMINATED BY '::'
LINES TERMINATED BY '\n';

--------------------------------------------------------------

-- MovieID::UrlSuffix
CREATE TABLE imdb_links (
    movie_id INT NOT NULL,
    url_suffix VARCHAR(100) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

LOAD DATA LOCAL INFILE '/absolute-directory/recommender/ml-1m/imdb_links.dat'
INTO TABLE imdb_links
FIELDS TERMINATED BY '::'
LINES TERMINATED BY '\n';
