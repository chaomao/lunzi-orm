DROP DATABASE IF EXISTS orm;
CREATE DATABASE orm;
USE orm;

CREATE TABLE author(
  id  int(11) NOT NULL auto_increment PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE person(
  id  int(11) NOT NULL auto_increment PRIMARY KEY,
  age int,
  name VARCHAR(255),
  sex varchar(255)
);
