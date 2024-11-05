
A project from the module Object Oriented Programming with Java, part of my MSc course 2023-2024. 

A database server written from scratch. The database persists when the server goes down. Data is stored in tab-separated files ("tables") within folders ("databases"). The server  can interpret SQL-like commands, for example:

- CREATE DATABASE markbook;
- USE markbook;
- CREATE TABLE marks (name, mark, pass); 
- INSERT INTO marks VALUES ('Chris', 70, TRUE);
- SELECT * FROM marks WHERE name == 'Chris';
- ALTER TABLE marks ADD age;
- DROP TABLE marks;
- DROP DATABASE markbook; 
