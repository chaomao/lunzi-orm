DROP DATABASE IF EXISTS orm;
CREATE DATABASE orm;
USE orm;

CREATE TABLE author(
  id  int(11) NOT NULL auto_increment PRIMARY KEY,
  name VARCHAR(255)
);
