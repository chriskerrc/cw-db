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
        //

    }

    //select


}
