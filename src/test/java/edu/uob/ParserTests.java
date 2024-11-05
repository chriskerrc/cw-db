package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
public class ParserTests {

    private String sendCommandToParser(String command) throws Exception {
        Preprocessor preprocessor = new Preprocessor(command);
        ArrayList<String> tokens = preprocessor.getTokens();
        Parser p = new Parser(tokens);
        return p.isCommand(tokens);
    }

    @Test
    public void testParseCreateDatabase() throws Exception {
        String response = sendCommandToParser("CREATE DATABASE test;");
        assertTrue(response.contains("CREATE_DATABASE"));
        //lowercase
        response = sendCommandToParser("create database test;");
        assertTrue(response.contains("CREATE_DATABASE"));
        //number in database name
        response = sendCommandToParser("create database te7t;");
        assertTrue(response.contains("CREATE_DATABASE"));
        //capitals in database name
        response = sendCommandToParser("create database TEST;");
        assertTrue(response.contains("CREATE_DATABASE"));
        //database name single number
        response = sendCommandToParser("create database 8;");
        assertTrue(response.contains("CREATE_DATABASE"));
        //database name multiple numbers
        response = sendCommandToParser("create database 878;");
        assertTrue(response.contains("CREATE_DATABASE"));
        //database name single letter
        response = sendCommandToParser("create database z;");
        assertTrue(response.contains("CREATE_DATABASE"));
        //missing semi-colon
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE DATABASE test");
        });
        //misspelled keywords
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREAT DATABASE test;");
        });
        //database name is keyword
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE DATABASE create;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE DATABASE drop;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE DATABAS test;");
        });
        //invalid database name punctuation
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE DATABASE t@st;");
        });
    }

    @Test
    public void testParseCreateTableNoAttributeNames() throws Exception {
        //no attribute names
        String response = sendCommandToParser("CREATE TABLE test;");
        assertTrue(response.contains("CREATE_TABLE"));
        //no attribute names, missing semi-colon
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE test");
        });
        //lowercase commands
        response = sendCommandToParser("create table test;");
        assertTrue(response.contains("CREATE_TABLE"));
        //number in table name
        response = sendCommandToParser("create table t3st;");
        assertTrue(response.contains("CREATE_TABLE"));
        //capitals in table name
        response = sendCommandToParser("create table TEST;");
        assertTrue(response.contains("CREATE_TABLE"));
        //table name single letter
        response = sendCommandToParser("create table A;");
        assertTrue(response.contains("CREATE_TABLE"));
        //table name single number
        response = sendCommandToParser("create table 7;");
        assertTrue(response.contains("CREATE_TABLE"));
    }

    @Test
    public void testParseCreateTableAttributeNames() throws Exception {
        //attribute names
        String response = sendCommandToParser("CREATE TABLE test (name, mark, pass);");
        assertTrue(response.contains("CREATE_TABLE"));
        //attribute names, missing semi-colon
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE test (name, mark, pass)");
        });
        //punctuation in attribute name
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE test (nam@, mark, pass);");
        });
        //misspelled commands
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREAT TABLE test (name, mark, pass);");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABL test (name, mark, pass);");
        });
        //missing brackets
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE test name, mark, pass);");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE test (name, mark, pass;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE test name, mark, pass;");
        });
        //table name is keyword
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE null (name, mark, pass);");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("CREATE TABLE update (name, mark, pass);");
        });
        //lowercase commands
        response = sendCommandToParser("create table test (name, mark, pass);");
        assertTrue(response.contains("CREATE_TABLE"));
    }

    @Test
    public void testParseUseDatabase() throws Exception {
        String response = sendCommandToParser("USE test;");
        assertTrue(response.contains("USE"));
        //lowercase command
        response = sendCommandToParser("use test;");
        assertTrue(response.contains("USE"));
        //database name single letter
        response = sendCommandToParser("use a;");
        assertTrue(response.contains("USE"));
        //database name single number
        response = sendCommandToParser("use 9;");
        assertTrue(response.contains("USE"));
        //capitals in database name
        response = sendCommandToParser("USE TEST;");
        assertTrue(response.contains("USE"));
        //number in database name
        response = sendCommandToParser("USE t35t;");
        assertTrue(response.contains("USE"));
        //missing semi-colon
        assertThrows(Exception.class, () -> {
            sendCommandToParser("USE test");
        });
        //misspelled command
        assertThrows(Exception.class, () -> {
            sendCommandToParser("US test;");
        });
        //database name is keyword
        assertThrows(Exception.class, () -> {
            sendCommandToParser("USE delete;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("USE and;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("USE select;");
        });
    }

    @Test
    public void testParseInsert() throws Exception {
        String response = sendCommandToParser("INSERT INTO test VALUES ('Chris', 34, TRUE);");
        assertTrue(response.contains("INSERT"));
        //lowercase command
        response = sendCommandToParser("insert into test values ('Chris', 34, TRUE);");
        assertTrue(response.contains("INSERT"));
        //one value in list
        response = sendCommandToParser("INSERT INTO test VALUES ('Chris');");
        assertTrue(response.contains("INSERT"));
        //capitals table name
        response = sendCommandToParser("INSERT INTO TEST VALUES ('Chris');");
        assertTrue(response.contains("INSERT"));
        //table name one letter
        response = sendCommandToParser("INSERT INTO a VALUES ('Chris');");
        assertTrue(response.contains("INSERT"));
        //table name one number
        response = sendCommandToParser("INSERT INTO a VALUES ('Chris');");
        assertTrue(response.contains("INSERT"));
        //reserved table name
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO select VALUES ('Chris');");
        });
        //misspelled keywords
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSET INTO table VALUES ('Chris');");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT ITO table VALUES ('Chris');");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO table VLUES ('Chris');");
        });
        //invalid table name: punctuation
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO * VALUES ('Chris');");
        });
        //missing brackets
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES 'Chris', 34, TRUE);");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES ('Chris', 34, TRUE;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES 'Chris', 34, TRUE;");
        });
    }

    @Test
    public void testParseInsertValidValues() throws Exception {
        //commas in value list
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES ('Chris' 34 TRUE;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES ('Chris', 34 TRUE;");
        });
        //missing single quotes string literal
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES ('Chris;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES (Chris;");
        });
        //bool: misspelled
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES (TRU);");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES (FALS);");
        });
        //float value
        String response = sendCommandToParser("INSERT INTO test VALUES (3.1415);");
        assertTrue(response.contains("INSERT"));
        response = sendCommandToParser("INSERT INTO test VALUES (+3.1415);");
        assertTrue(response.contains("INSERT"));
        response = sendCommandToParser("INSERT INTO test VALUES (-3.1415);");
        assertTrue(response.contains("INSERT"));
        //float: multiple decimal points
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES (3.14.15);");
        });
        //integer literal
        response = sendCommandToParser("INSERT INTO test VALUES (31415);");
        assertTrue(response.contains("INSERT"));
        response = sendCommandToParser("INSERT INTO test VALUES (3);");
        assertTrue(response.contains("INSERT"));
        response = sendCommandToParser("INSERT INTO test VALUES (+31415);");
        assertTrue(response.contains("INSERT"));
        response = sendCommandToParser("INSERT INTO test VALUES (-31415);");
        assertTrue(response.contains("INSERT"));
        //NULL
        response = sendCommandToParser("INSERT INTO test VALUES (NULL);");
        assertTrue(response.contains("INSERT"));
        assertThrows(Exception.class, () -> {
            sendCommandToParser("INSERT INTO test VALUES (NUL);");
        });
    }

    //"SELECT * FROM " [TableName]
    @Test
    public void testParseSelectAsteriskNoCondition() throws Exception {
        String response = sendCommandToParser("SELECT * FROM test;");
        assertTrue(response.contains("SELECT"));
        //lowercase keywords
        response = sendCommandToParser("select * from test;");
        assertTrue(response.contains("SELECT"));
        //uppercase table name
        response = sendCommandToParser("SELECT * FROM TEST;");
        assertTrue(response.contains("SELECT"));
        //number in table name
        response = sendCommandToParser("SELECT * FROM te5t;");
        assertTrue(response.contains("SELECT"));
        //missing semi-colon
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test");
        });
        //misspelled keywords
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELEC * FRO test;");
        });
        //invalid table name
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM te&t;");
        });
        //table name is reserved word
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM create;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM select;");
        });
    }

    @Test
    public void testParseSelectAttributeNoCondition() throws Exception {
        String response = sendCommandToParser("SELECT name FROM test;");
        assertTrue(response.contains("SELECT"));
        //lowercase keywords
        response = sendCommandToParser("select name from test;");
        assertTrue(response.contains("SELECT"));
        //uppercase table name
        response = sendCommandToParser("SELECT name FROM TEST;");
        assertTrue(response.contains("SELECT"));
        //number in table name
        response = sendCommandToParser("SELECT name FROM te5t;");
        assertTrue(response.contains("SELECT"));
        //missing semi-colon
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT name FROM test");
        });
        //camelCase attributeName
        response = sendCommandToParser("SELECT studentName FROM te5t;");
        assertTrue(response.contains("SELECT"));
        //attribute name is reserved keyword
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT select FROM test;");
        });
        //attribute name is invalid (punctuation)
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT name* FROM test;");
        });
        //misspelled keywords
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELEC name FRO test;");
        });
        //invalid table name
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT name FROM te&t;");
        });
        //table name is reserved word
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT name FROM create;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT name FROM select;");
        });
    }

    @Test
    public void testParseSelectAsteriskCondition() throws Exception {
        String response = sendCommandToParser("SELECT * FROM test WHERE id>5;");
        assertTrue(response.contains("SELECT"));
        //lowercase WHERE
        response = sendCommandToParser("SELECT * FROM test where id>5;");
        assertTrue(response.contains("SELECT"));
        //misspelled WHERE
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test WHER Name <= 5;");
        });
        //comparators without spaces
        response = sendCommandToParser("SELECT * FROM test where id<5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id==5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id!=5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id>=5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id<=5;");
        assertTrue(response.contains("SELECT"));
        //comparators with spaces
        response = sendCommandToParser("SELECT * FROM test where id < 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id == 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id != 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id >= 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id <= 5;");
        assertTrue(response.contains("SELECT"));
        //LIKE
        response = sendCommandToParser("SELECT * FROM test where id LIKE 'i';");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where id LIKE 'ion';");
        assertTrue(response.contains("SELECT"));
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test where Name LIKE i;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test where Name LIKE 'i;");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test where Name LIKE i';");
        });
        //value is string literal
        response = sendCommandToParser("SELECT * FROM test where id <= 'Chris';");
        assertTrue(response.contains("SELECT"));
        //invalid AttributeName
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test where N@me <= 5;");
        });
    }

    @Test
    public void testParseSelectAsteriskConditionBrackets() throws Exception {
        String response = sendCommandToParser("SELECT * FROM test WHERE (id>5);");
        assertTrue(response.contains("SELECT"));
        //lowercase WHERE
        response = sendCommandToParser("SELECT * FROM test where (id>5);");
        assertTrue(response.contains("SELECT"));
        //comparators without spaces
        response = sendCommandToParser("SELECT * FROM test where (id<5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id==5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id!=5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id>=5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id<=5);");
        assertTrue(response.contains("SELECT"));
        //comparators with spaces
        response = sendCommandToParser("SELECT * FROM test where (id < 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id == 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id != 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id >= 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id <= 5);");
        assertTrue(response.contains("SELECT"));
        //LIKE
        response = sendCommandToParser("SELECT * FROM test where (id LIKE 'i');");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT * FROM test where (id LIKE 'ion');");
        assertTrue(response.contains("SELECT"));
        //value is string literal
        response = sendCommandToParser("SELECT * FROM test where (id <= 'Chris');");
        assertTrue(response.contains("SELECT"));
        //invalid AttributeName
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test where (N@me <= 5);");
        });
        //one bracket
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test where Name <= 5);");
        });
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT * FROM test where (Name <= 5;");
        });
    }

    @Test
    public void testParseSelectAttributeCondition() throws Exception {
        String response = sendCommandToParser("SELECT name FROM test WHERE id>5;");
        assertTrue(response.contains("SELECT"));
        //lowercase WHERE
        response = sendCommandToParser("SELECT name FROM test where id>5;");
        assertTrue(response.contains("SELECT"));
        //comparators without spaces
        response = sendCommandToParser("SELECT name FROM test where id<5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id==5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id!=5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id>=5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id<=5;");
        assertTrue(response.contains("SELECT"));
        //comparators with spaces
        response = sendCommandToParser("SELECT name FROM test where id < 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id == 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id != 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id >= 5;");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id <= 5;");
        assertTrue(response.contains("SELECT"));
        //LIKE
        response = sendCommandToParser("SELECT name FROM test where id LIKE 'i';");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where id LIKE 'ion';");
        assertTrue(response.contains("SELECT"));
        //value is string literal
        response = sendCommandToParser("SELECT name FROM test where id <= 'Chris';");
        assertTrue(response.contains("SELECT"));
        //invalid AttributeName in condition
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT name FROM test where N@me <= 5;");
        });
        //invalid AttributeName after SELECT
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT n@me FROM test where Name <= 5;");
        });
        //camelCase AttributeName after SELECT
        response = sendCommandToParser("SELECT studentName FROM test where id <= 'Chris';");
        assertTrue(response.contains("SELECT"));
    }

    @Test
        public void testParseSelectAttributeConditionBrackets() throws Exception {
        String response = sendCommandToParser("SELECT name FROM test WHERE (id>5);");
        assertTrue(response.contains("SELECT"));
        //lowercase WHERE
        response = sendCommandToParser("SELECT name FROM test where (id>5);");
        assertTrue(response.contains("SELECT"));
        //comparators without spaces
        response = sendCommandToParser("SELECT name FROM test where (id<5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id==5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id!=5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id>=5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id<=5);");
        assertTrue(response.contains("SELECT"));
        //comparators with spaces
        response = sendCommandToParser("SELECT name FROM test where (id < 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id == 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id != 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id >= 5);");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id <= 5);");
        assertTrue(response.contains("SELECT"));
        //LIKE
        response = sendCommandToParser("SELECT name FROM test where (id LIKE 'i');");
        assertTrue(response.contains("SELECT"));
        response = sendCommandToParser("SELECT name FROM test where (id LIKE 'ion');");
        assertTrue(response.contains("SELECT"));
        //value is string literal
        response = sendCommandToParser("SELECT name FROM test where (id <= 'Chris');");
        assertTrue(response.contains("SELECT"));
        //invalid AttributeName in condition
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT name FROM test where (N@me <= 5);");
        });
        //invalid AttributeName after SELECT
        assertThrows(Exception.class, () -> {
            sendCommandToParser("SELECT n@me FROM test where (Name <= 5);");
        });
        //camelCase AttributeName after SELECT
        response = sendCommandToParser("SELECT studentName FROM test where (id <= 'Chris');");
        assertTrue(response.contains("SELECT"));
    }

    @Test
    public void testAlterTable() throws Exception {
        //valid add command
        String response = sendCommandToParser("ALTER table marks add building;");
        assertTrue(response.contains("ALTER"));
        //valid drop command
        response = sendCommandToParser("ALTER table marks drop building;");
        assertTrue(response.contains("ALTER"));
        //no table given
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALTER marks add building;");
        });
        //no column given
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALTER table marks add;");
        });
        //misspelled ALTER
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALER table marks add building;");
        });
        //misspelled ADD
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALTER table marks ad building;");
        });
        //misspelled DROP
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALTER table marks drp building;");
        });
        //misspelled table
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALTER tabe marks drop building;");
        });
        //invalid table
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALTER table m@rks drop building;");
        });
        //invalid column
        assertThrows(Exception.class, () -> {
            sendCommandToParser("ALTER table marks drop bui|ding;");
        });
    }

    @Test
    public void testDropTable() throws Exception {
        //valid drop table command
        String response = sendCommandToParser("DROP table marks;");
        assertTrue(response.contains("DROP_TABLE"));
        //misspelled drop
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DRO table marks;");
        });
        //no table object
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DROP table;");
        });
        //no table or database keyword
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DROP marks;");
        });
    }

    @Test
    public void testDropDatabase() throws Exception {
        //valid drop database command
        String response = sendCommandToParser("DROP database markbook;");
        assertTrue(response.contains("DROP_DATABASE"));
        //no database object
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DROP database;");
        });
        //misspelled drop
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DRO database markbook;");
        });
    }

    @Test
    public void testDelete() throws Exception {
        //greater than condition, no spaces
        String response = sendCommandToParser("DELETE FROM test WHERE (id>5);");
        assertTrue(response.contains("DELETE"));
        //lowercase WHERE
        response = sendCommandToParser("DELETE FROM test where (id>5);");
        assertTrue(response.contains("DELETE"));
        //comparators without spaces
        response = sendCommandToParser("DELETE FROM test where (id<5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id==5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id!=5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id>=5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id<=5);");
        assertTrue(response.contains("DELETE"));
        //comparators with spaces
        response = sendCommandToParser("DELETE FROM test where (id < 5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id == 5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id != 5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id >= 5);");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id <= 5);");
        assertTrue(response.contains("DELETE"));
        //LIKE
        response = sendCommandToParser("DELETE FROM test where (id LIKE 'i');");
        assertTrue(response.contains("DELETE"));
        response = sendCommandToParser("DELETE FROM test where (id LIKE 'ion');");
        assertTrue(response.contains("DELETE"));
        //value is string literal
        response = sendCommandToParser("DELETE FROM test where (id <= 'Chris');");
        assertTrue(response.contains("DELETE"));
        //invalid AttributeName in condition
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DELETE FROM test where (N@me <= 5);");
        });
        //missing FROM
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DELETE test where (Name <= 5);");
        });
        //misspelled DELETE
        assertThrows(Exception.class, () -> {
            sendCommandToParser("DELET FROM test where (Name <= 5);");
        });
        //camelCase table name
        response = sendCommandToParser("DELETE FROM testTest where (id <= 'Chris');");
        assertTrue(response.contains("DELETE"));
    }
}
