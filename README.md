
A project from the module Object Oriented Programming with Java, part of my MSc course 2023-2024. 

A database server written from scratch. The database persists when the server goes down. Data is stored in tab-separated files ("tables") within folders ("databases"). The server  can interpret SQL-like commands, for example:

```sql
CREATE DATABASE markbook;
[OK]

USE markbook;
[OK]
```

`CREATE TABLE marks (name, mark, pass);`
[OK]

`INSERT INTO marks VALUES ('Simon', 65, TRUE);`
[OK]

`INSERT INTO marks VALUES ('Sion', 55, TRUE);`

`INSERT INTO marks VALUES ('Rob', 35, FALSE);`

`INSERT INTO marks VALUES ('Chris', 70, TRUE);

`CREATE TABLE coursework (task, submission);`

`INSERT INTO coursework VALUES ('OXO', 3);`

`INSERT INTO coursework VALUES ('DB', 1);`

`INSERT INTO coursework VALUES ('OXO', 4);`

`INSERT INTO coursework VALUES ('STAG', 2);`

`SELECT * FROM marks WHERE name == 'Chris';`
[OK]
4 Chris 70 TRUE

`JOIN coursework AND marks ON submission AND id;`
[OK]
id coursework.task marks.name marks.mark marks.pass
1 OXO Rob 35 FALSE
2 DB Simon 65 TRUE
3 OXO Chris 20 FALSE
4 STAG Sion 55 TRUE

`ALTER TABLE marks ADD age;`
[OK]

`DELETE FROM marks WHERE name != 'Bob';`
[OK]

`UPDATE marks SET mark = 75 WHERE name == 'Chris';`
[OK]

`DROP TABLE marks;`
[OK]

`DROP DATABASE markbook;`
[OK]
 
For the specification of the query language, see [BNF.txt](https://github.com/chriskerrc/cw-db/blob/main/BNF.txt) and for examples of a sequence of commands, see [example-transcript.pdf](https://github.com/chriskerrc/cw-db/blob/main/example-transcript.pdf).
