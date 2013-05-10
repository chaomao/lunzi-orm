DROP DATABASE IF EXISTS orm;
CREATE DATABASE orm;
USE orm;

CREATE TABLE author (
  id   INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE owner (
  id   INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE richowner (
  id   INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE house (
  id       INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  size     INT(11),
  owner_id INT(11)
);

CREATE TABLE room (
  id       INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  size     INT(11),
  house_id INT(11)
);

CREATE TABLE person (
  id               INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  age              INT,
  name             VARCHAR(255),
  gender           VARCHAR(255),
  telephoneNumbers VARCHAR(255)
);
