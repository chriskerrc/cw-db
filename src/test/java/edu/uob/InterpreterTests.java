package edu.uob;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    @AfterEach
    public void cleanUp() {
        String storageFolderPath = server.getStorageFolderPath();
        cleanDatabasesFolder(storageFolderPath);
    }
    
    private void cleanDatabasesFolder(String path) {
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            cleanDatabaseSubfolders(folder);
        }
    }

    private void cleanDatabaseSubfolders(File folder) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                cleanDatabaseSubfolders(file);
            }
            file.delete();
        }
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    private String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testValidCreateDatabaseCommand() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        String response = dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
    }

    @Test
    public void testTryCreateDatabaseInvalidName() {
        DBServer dbServer = new DBServer();
        String response = dbServer.handleCommand("CREATE DATABASE *&^ ;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testValidUseCommand() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        String response = dbServer.handleCommand("USE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
    }

    @Test
    public void testLowercaseCommandKeywords(){
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        String response = dbServer.handleCommand("create database " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = dbServer.handleCommand("use " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = dbServer.handleCommand("create table marks (name, mark, pass);");
        assertTrue(response.contains("[OK]"));
        response = dbServer.handleCommand("insert into marks values ('Chris', 60, TRUE);");
        assertTrue(response.contains("[OK]"));
        response = dbServer.handleCommand("select * from marks;");
        assertTrue(response.contains("[OK]"));
    }

    @Test
    public void testTryCreateDatabaseWithSameNameExisting() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        String response = dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("database"));
        assertTrue(response.contains("exists"));
    }

    @Test
    public void testCreateDatabaseMissingSemicolon() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        String response = dbServer.handleCommand("CREATE DATABASE " + randomName);
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("semi-colon"));
    }

    @Test
    public void testCreateValidTable() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        dbServer.handleCommand("USE " + randomName + ";");
        String response = dbServer.handleCommand("CREATE TABLE marks;");
        assertTrue(response.contains("[OK]"));
    }

    @Test
    public void testTryCreateTableInvalidName() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        dbServer.handleCommand("USE " + randomName + ";");
        String response = dbServer.handleCommand("CREATE TABLE &^%;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testTryCreateTableSameNameExisting() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        dbServer.handleCommand("USE " + randomName + ";");
        dbServer.handleCommand("CREATE TABLE marks;");
        String response = dbServer.handleCommand("CREATE TABLE marks;");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("table"));
        assertTrue(response.contains("exists"));
    }

    @Test
    public void testTryCreateTableSameNameExistingDifferentCase() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        dbServer.handleCommand("USE " + randomName + ";");
        dbServer.handleCommand("CREATE TABLE marks;");
        String response = dbServer.handleCommand("CREATE TABLE MARKS;");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("table"));
        assertTrue(response.contains("exists"));
    }

    @Test
    public void testCreateTableNoAttributesHasID() {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        dbServer.handleCommand("USE " + randomName + ";");
        dbServer.handleCommand("CREATE TABLE marks;");
        String response = dbServer.handleCommand("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("id"));
    }

    @Test
    public void testSelectAsteriskWhereAttributeEqualsValue() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 50, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name == 'Simon';");
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertTrue(response.contains("'Simon'"));
        assertTrue(response.contains("65"));
        assertFalse(response.contains("'Chris'"));
        assertFalse(response.contains("50"));
    }

    @Test
    public void testSelectAsteriskWhereAttributeDoesNotEqualValue() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Finn', 70, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name != 'Simon';");
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertTrue(response.contains("'Chris'"));
        assertTrue(response.contains("38"));
        assertTrue(response.contains("'Finn'"));
        assertTrue(response.contains("70"));
        assertFalse(response.contains("'Simon'"));
        assertFalse(response.contains("65"));
        }


    @Test
    public void testSelectAsteriskNoCondition() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 50, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertTrue(response.contains("'Simon'"));
        assertTrue(response.contains("65"));
        assertTrue(response.contains("'Chris'"));
        assertTrue(response.contains("50"));
    }

    @Test
    public void testQueryIDDoesNotEqualMultipleResults() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 50, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Finn', 70, TRUE);");
        String response = sendCommandToServer("SELECT id FROM marks WHERE name != 'Chris';");
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the space character
        String[] tokens = singleLine.split(" ");
        String lastToken = tokens[tokens.length-1];
        String penultimateToken = tokens[tokens.length-2];
        assertEquals(3, Integer.parseInt(lastToken));
        assertEquals(2, Integer.parseInt(penultimateToken));
    }

    @Test
    public void testQueryNonexistentDatabase() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("USE " + randomName + ";");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testQueryNonexistentTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testQueryNonexistentColumn() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        String response = sendCommandToServer("SELECT colour FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("Attribute"));
    }

    @Test
    public void testSelectAsteriskQueryReturnsValuesInOrderStored() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 50, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Finn', 70, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the tab character
        String[] tokens = singleLine.split("\t"); //changed this to split on tabs (example test split on spaces)
        String lastToken = tokens[tokens.length-1];
        String penultimateToken = tokens[tokens.length-2];
        String secondColumnHeader = tokens[2];
        assertEquals("TRUE", lastToken);
        assertEquals("70", penultimateToken);
        assertEquals("mark", secondColumnHeader);
    }

    @Test
    public void testQueryCreateTableDuplicateAttributes() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        //duplicate attribute with same case
        String response = sendCommandToServer("CREATE TABLE marks (name, mark, pass, mark);");
        assertTrue(response.contains("[ERROR]"));
        //duplicate attribute lowercase/uppercase
        response = sendCommandToServer("CREATE TABLE marks (name, pass, PASS, mark);");
        assertTrue(response.contains("[ERROR]"));
        //duplicate attribute mixed case
        response = sendCommandToServer("CREATE TABLE marks (name, Pass, pAsS, mark);");
        assertTrue(response.contains("[ERROR]"));
        response = sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        assertTrue(response.contains("[OK]"));
    }

    @Test
    public void testQueryCreateInsertColumnMismatch() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        //Try to insert too few values
        String response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', FALSE);");
        assertTrue(response.contains("[ERROR]"));
        //Try to insert too many values
        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 50, FALSE, 60);");
        assertTrue(response.contains("[ERROR]"));
        //Try to insert correct number of values
        response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 50, FALSE);");
        assertTrue(response.contains("[OK]"));
    }


    @Test
    public void testQueryKeywordsCaseInsensitive() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("create database " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("use " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("create table marks (name, mark, pass);");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("insert into marks values ('Chris', 38, FALSE);");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("select id from marks where name == 'Chris';");
        assertTrue(response.contains("[OK]"));
    }

    @Test
    public void testDatabaseNameIsNotReserved() {
        String response = sendCommandToServer("CREATE DATABASE INSERT;");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("parse"));
    }

    @Test
    public void testTableNameIsNotReserved() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("use " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("CREATE TABLE true (name, mark, pass);");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("parse"));
    }

    @Test
    public void testAttributeNameIsNotReserved() {
        String randomName = generateRandomName();
        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("use " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("CREATE TABLE marks (name, mark, null);");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("parse"));
    }

    @Test
    public void testDatabaseNamesAreCaseInsensitive() {
        sendCommandToServer("CREATE DATABASE cars;");
        //try to use database of same name but different case
        String response = sendCommandToServer("USE CaRs;");
        assertTrue(response.contains("[OK]"));
        //try to create database with the same name but different case
        response = sendCommandToServer("CREATE DATABASE CARS;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testColumnNamesAreCaseInsensitiveForQuerying() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        //create table with camelCase attribute names
        sendCommandToServer("CREATE TABLE test (studentName, studentMark, studentPasses);");
        sendCommandToServer("INSERT INTO test VALUES ('Chris', 60, TRUE);");
        sendCommandToServer("INSERT INTO test VALUES ('Bob', 34, FALSE);");
        //select with all lowercase column name
        String response = sendCommandToServer("SELECT studentname FROM test WHERE id == 1;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        //select with all uppercase column name
        response = sendCommandToServer("SELECT STUDENTMARK FROM test WHERE id == 2;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("34"));
    }

    @Test
    public void testColumnNameCaseIsPreservedWhenStored() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        //create table with camelCase attribute names
        sendCommandToServer("CREATE TABLE marks (studentName, studentMark, studentPasses);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        //check that case is preserved
        assertTrue(response.contains("studentName"));
        assertTrue(response.contains("studentMark"));
        assertTrue(response.contains("studentPasses"));
    }

    @Test
    public void testSelectAttributeComparators() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT name FROM marks WHERE mark>60;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        assertFalse(response.contains("'Fred'"));
        assertFalse(response.contains("'Bob'"));
        response = sendCommandToServer("SELECT name FROM marks WHERE mark<60;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("'Chris'"));
        assertTrue(response.contains("'Fred'"));
        assertTrue(response.contains("'Bob'"));
        response = sendCommandToServer("SELECT name FROM marks WHERE mark>=40;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        assertFalse(response.contains("'Fred'"));
        assertTrue(response.contains("'Bob'"));
        response = sendCommandToServer("SELECT name FROM marks WHERE mark<=40;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("'Chris'"));
        assertTrue(response.contains("'Fred'"));
        assertTrue(response.contains("'Bob'"));
    }

    @Test
    public void testSelectAsteriskComparators() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE mark>60;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        assertTrue(response.contains("61"));
        assertTrue(response.contains("TRUE"));
        assertTrue(response.contains("1"));
        assertFalse(response.contains("'Fred'"));
        assertFalse(response.contains("'Bob'"));
        response = sendCommandToServer("SELECT * FROM marks WHERE mark<60;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("'Chris'"));
        assertTrue(response.contains("'Fred'"));
        assertTrue(response.contains("'Bob'"));
        assertTrue(response.contains("30"));
        assertTrue(response.contains("40"));
        assertTrue(response.contains("FALSE"));
        assertTrue(response.contains("2"));
        assertTrue(response.contains("3"));
        response = sendCommandToServer("SELECT * FROM marks WHERE mark>=40;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        assertFalse(response.contains("'Fred'"));
        assertTrue(response.contains("'Bob'"));
        assertTrue(response.contains("61"));
        assertTrue(response.contains("TRUE"));
        assertTrue(response.contains("1"));
        assertTrue(response.contains("40"));
        assertTrue(response.contains("FALSE"));
        response = sendCommandToServer("SELECT * FROM marks WHERE mark<=40;");
        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("'Chris'"));
        assertTrue(response.contains("'Fred'"));
        assertTrue(response.contains("'Bob'"));
        assertTrue(response.contains("30"));
        assertTrue(response.contains("40"));
        assertTrue(response.contains("FALSE"));
        assertTrue(response.contains("2"));
        assertTrue(response.contains("3"));
    }

    @Test
    public void testSelectAsteriskLike() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'i';");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("61"));
        assertTrue(response.contains("TRUE"));
        assertTrue(response.contains("'Chris'"));
        response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'o';");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("40"));
        assertTrue(response.contains("FALSE"));
        assertTrue(response.contains("'Bob'"));
    }

    @Test
    public void testSelectAsteriskLikeMultiCharacter()  {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Ann', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Anna', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'Ann';");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("40"));
        assertTrue(response.contains("30"));
        assertTrue(response.contains("FALSE"));
        assertTrue(response.contains("'Ann'"));
        assertTrue(response.contains("'Anna'"));
        assertFalse(response.contains("'Chris'"));
        assertFalse(response.contains("61"));
        assertFalse(response.contains("TRUE"));
    }

    @Test
    public void testSelectAttributeLike() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT mark FROM marks WHERE name LIKE 'i';");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("61"));
        assertFalse(response.contains("TRUE"));
        assertFalse(response.contains("'Chris'"));
        response = sendCommandToServer("SELECT mark FROM marks WHERE name LIKE 'o';");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("40"));
        assertFalse(response.contains("FALSE"));
        assertFalse(response.contains("'Bob'"));
    }

    @Test
    public void testSelectWithoutSemicolon() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'i'");
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("semi-colon"));
    }

    @Test
    public void testSelectTableNotExist() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM crew WHERE name LIKE 'i'");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testSelectAttributeNotInTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT height FROM marks WHERE name LIKE 'i'");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testSelectValidQueryNoMatchesReturnsColumnsNoDataRows() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'z';");
        assertTrue(response.contains("[OK]")); //just return column header row with no data
        assertFalse(response.contains("'Chris'"));
        assertFalse(response.contains("'Bob'"));
        assertFalse(response.contains("'Fred'"));
    }

    @Test
    public void testSelectCompareDifferentDataTypesTextInteger() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE (name>60);");
        //return blank results
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertFalse(response.contains("'Chris'"));
    }

    @Test
    public void testSelectBracketedConditionAsterisk() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE (mark>60);");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        assertTrue(response.contains("61"));
        assertTrue(response.contains("TRUE"));
    }

    @Test
    public void testSelectBracketedConditionAttribute() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT name FROM marks WHERE mark>60;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        assertFalse(response.contains("61"));
        assertFalse(response.contains("TRUE"));
    }

    @Test
    public void testQueriesWithWhitespace() {
        String randomName = generateRandomName();
        sendCommandToServer("    CREATE      DATABASE " + randomName + ";");
        sendCommandToServer("USE     " + randomName + ";");
        sendCommandToServer("CREATE     TABLE     marks   (  name, mark, pass);");
        sendCommandToServer("INSERT    INTO   marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT   INTO    marks VALUES ('Bob', 40,   FALSE);");
        sendCommandToServer("INSERT     INTO marks   VALUES (  'Fred',   30, FALSE);");
        //no space either side of ==
        String response = sendCommandToServer("SELECT id FROM marks WHERE name=='Chris';");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("1"));
        assertFalse(response.contains("61"));
        assertFalse(response.contains("TRUE"));
    }

    @Test
    public void testRowIDsRemainUniqueAfterServerRestart() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 80, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("1"));
        assertTrue(response.contains("2"));
        assertFalse(response.contains("3"));
        // Create a new server object
        server = new DBServer();
        response = sendCommandToServer("USE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 49, FALSE);");
        response = sendCommandToServer("SELECT * FROM marks;");
        //check that id of last row is 3, not 1
        assertTrue(response.contains("3"));
    }

    @Test
    public void testAlterTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        //add column age
        String response = sendCommandToServer("ALTER TABLE marks ADD age;");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("age"));
        //cannot add column name (existing column name)
        response = sendCommandToServer("ALTER TABLE marks ADD name;");
        assertTrue(response.contains("[ERROR]")); //to do: fix failing test
        //drop column pass
        response = sendCommandToServer("ALTER TABLE marks DROP pass;");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        assertFalse(response.contains("pass"));
        //cannot drop column id
        response = sendCommandToServer("ALTER TABLE marks DROP id;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testDropTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("name"));
        //try to drop table
        response = sendCommandToServer("DROP table marks;");
        assertTrue(response.contains("[OK]"));
        //check table is gone
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[ERROR]"));
        //create a table of the same name as previous
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("name"));
        //delete the 2nd table
        response = sendCommandToServer("DROP table marks;");
        assertTrue(response.contains("[OK]"));
        //check 2nd table is gone
        response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testDropDatabase() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("USE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        //try to delete an empty database
        response = sendCommandToServer("DROP database " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        //check you can't use deleted database
        response = sendCommandToServer("USE " + randomName + ";");
        assertTrue(response.contains("[ERROR]"));
        //create a new database
        sendCommandToServer("CREATE DATABASE marine;");
        response = sendCommandToServer("USE marine;");
        assertTrue(response.contains("[OK]"));
        //add some tables to the database
        sendCommandToServer("CREATE TABLE whales;");
        sendCommandToServer("CREATE TABLE fish;");
        sendCommandToServer("CREATE TABLE seas;");
        //try to delete database with tables in it
        response = sendCommandToServer("DROP database marine;");
        assertTrue(response.contains("[OK]"));
        //check you can't use deleted database
        response = sendCommandToServer("USE marine;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testDeleteFromTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Julia', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Finn', 70, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Hannah', 80, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Ahmed', 90, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");

        //delete a single row with == operator
        String response = sendCommandToServer("DELETE FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check that Chris's row is gone
        assertFalse(response.contains("Chris"));
        assertFalse(response.contains("61"));
        //check that Bob's row is still there
        assertTrue(response.contains("Bob"));

        //delete two non-consecutive rows with < operator
        response = sendCommandToServer("DELETE FROM marks WHERE mark<50;");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check that Bob and Fred rows are gone
        assertFalse(response.contains("Bob"));
        assertFalse(response.contains("Fred")); //Bob is deleted but Fred is not deleted...
        //check Julia is still there
        assertTrue(response.contains("Julia"));

        //delete row with > operator
        response = sendCommandToServer("DELETE FROM marks WHERE mark>85;");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check that Ahmed row is gone
        assertFalse(response.contains("Ahmed"));
        //check Julia is still there
        assertTrue(response.contains("Julia"));

        //Check that deleted row IDs aren't reused when adding new rows
        sendCommandToServer("INSERT INTO marks VALUES ('Jan', 33, FALSE);");
        response = sendCommandToServer("SELECT * FROM marks;");
        //check that IDs 1 and 2 aren't there
        assertFalse(response.contains("1"));
        assertFalse(response.contains("2"));

        //delete where != (with spaces either side of comparator)
        response = sendCommandToServer("DELETE FROM marks WHERE pass != TRUE;");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check Jan is gone
        assertFalse(response.contains("Jan"));

        //delete where >= (with spaces either side of comparator)
        response = sendCommandToServer("DELETE FROM marks WHERE mark >= 80;");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check Hannah is gone
        assertFalse(response.contains("Hannah"));

        //delete where <= (with spaces either side of comparator)
        response = sendCommandToServer("DELETE FROM marks WHERE mark <= 55;");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check Julia is gone
        assertFalse(response.contains("Julia"));

        sendCommandToServer("INSERT INTO marks VALUES ('Ian', 34, FALSE);");

        //delete where name LIKE
        response = sendCommandToServer("DELETE FROM marks WHERE name LIKE 'F';");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check Finn is gone
        assertFalse(response.contains("Finn"));
    }

    @Test
    public void testUpdateTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 80, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Julia', 55, TRUE);");

        //update a single row with == operator on string
        String response = sendCommandToServer("UPDATE marks SET mark = 38 WHERE name == 'Chris';");
        assertTrue(response.contains("OK"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check that new mark is there
        assertTrue(response.contains("38"));
        //check that old mark is gone
        assertFalse(response.contains("61"));
        //update another single row with == operator on number
        response = sendCommandToServer("UPDATE marks SET pass = FALSE WHERE mark == 80;");
        assertTrue(response.contains("OK"));
        response = sendCommandToServer("SELECT * FROM marks;");
        //check that new pass status is there
        assertTrue(response.contains("FALSE"));
        //fail to update two rows, because I haven't implemented this
        response = sendCommandToServer("UPDATE marks SET pass = FALSE , mark = 0  WHERE mark > 20;");
        assertTrue(response.contains("[ERROR]"));
        //fail to update column that isn't in table
        response = sendCommandToServer("UPDATE marks SET unicorn = FALSE WHERE name == 'Chris';");
        assertTrue(response.contains("[ERROR]"));
    }
}
