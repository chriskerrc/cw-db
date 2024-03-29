package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DBTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
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
    public void testSelectAsteriskWhereAttributeEqualsValue() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 50, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name == 'Simon';");
        System.out.println(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertTrue(response.contains("'Simon'"));
        assertTrue(response.contains("65"));
        assertFalse(response.contains("'Chris'"));
        assertFalse(response.contains("50"));
        //make this after each?
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectAsteriskWhereAttributeDoesNotEqualValue() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Finn', 70, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name != 'Simon';");
        System.out.println(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertTrue(response.contains("'Chris'"));
        assertTrue(response.contains("38"));
        assertTrue(response.contains("'Finn'"));
        assertTrue(response.contains("70"));
        assertFalse(response.contains("'Simon'"));
        assertFalse(response.contains("65"));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));    }

    @Test
    public void testSelectAsteriskNoCondition() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 50, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertTrue(response.contains("'Simon'"));
        assertTrue(response.contains("65"));
        assertTrue(response.contains("'Chris'"));
        assertTrue(response.contains("50"));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));

    }

    @Test
    public void testQueryIDDoesNotEqualMultipleResults() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 50, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Finn', 70, TRUE);");
        String response = sendCommandToServer("SELECT id FROM marks WHERE name != 'Chris';");
        System.out.println(response);
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the space character
        String[] tokens = singleLine.split(" ");
        String lastToken = tokens[tokens.length-1];
        String penultimateToken = tokens[tokens.length-2];
        assertEquals(3, Integer.parseInt(lastToken));
        assertEquals(2, Integer.parseInt(penultimateToken));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testQueryNonexistentDatabase() throws IOException {
        String randomName = generateRandomName();
        String response = sendCommandToServer("USE " + randomName + ";");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testQueryNonexistentTable() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        assertTrue(response.contains("[ERROR]"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testQueryNonexistentColumn() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 38, FALSE);");
        String response = sendCommandToServer("SELECT colour FROM marks WHERE name == 'Chris';");
        assertTrue(response.contains("[ERROR]"));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testQueryCreateDatabaseNameAlreadyExists() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[ERROR]"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testQueryCreateTableNameAlreadyExists() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        String response = sendCommandToServer("CREATE TABLE marks;");
        assertTrue(response.contains("[ERROR]"));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectAsteriskQueryReturnsValuesInOrderStored() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testQueryCreateTableDuplicateAttributes() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testQueryCreateInsertColumnMismatch() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }


    @Test
    public void testQueryKeywordsCaseInsensitive() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    //these tests return error Invalid command
    @Test
    public void testDatabaseNameIsNotReserved() throws IOException {
        String response = sendCommandToServer("CREATE DATABASE INSERT;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testTableNameIsNotReserved() throws IOException {
        String randomName = generateRandomName();
        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("use " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("CREATE TABLE true (name, mark, pass);");
        assertTrue(response.contains("[ERROR]"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testAttributeNameIsNotReserved() throws IOException {
        String randomName = generateRandomName();
        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("use " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        response = sendCommandToServer("CREATE TABLE marks (name, mark, null);");
        assertTrue(response.contains("[ERROR]"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testDatabaseNamesAreCaseInsensitive() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE cars;");
        //try to use database of same name but different case
        String response = sendCommandToServer("USE CaRs;");
        assertTrue(response.contains("[OK]"));
        //try to create database with the same name but different case
        response = sendCommandToServer("CREATE DATABASE CARS;");
        assertTrue(response.contains("[ERROR]"));
        //make this after each?
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory("cars"));
    }

    @Test
    public void testTableNamesAreCaseInsensitive() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE markbook;");
        sendCommandToServer("USE markbook;");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        //try to create table with same name but different case
        String response = sendCommandToServer("CREATE TABLE MaRkS;");
        assertTrue(response.contains("[ERROR]"));
        //insert into same table name, but with different case
        response = sendCommandToServer("INSERT INTO mArKs VALUES ('chris', 50, TRUE);");
        assertTrue(response.contains("[OK]"));
        //select same table name, but with different case
        response = sendCommandToServer("SELECT * FROM MARKS;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("50"));
        //make this after each?
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory("markbook"));
    }

    //this test fails: seems to be reading directory name as uppercase (not sure why)
    @Test
    public void testDatabaseNameSavedToFileAsLowercase() throws IOException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //create table with uppercase name
        sendCommandToServer("CREATE DATABASE UPPERCASE;");
        Database database = databaseManager.getDatabaseObjectFromName("UPPERCASE");
        assertTrue(database.databaseDirectoryIsSavedAsLowercase("UPPERCASE"));
        assertTrue(database.deleteDatabaseDirectory("uppercase"));
    }

    //to do: this test is unfinished
    @Test
    public void testTableNameSavedToFileAsLowercase() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        //create table with uppercase name
        sendCommandToServer("CREATE TABLE MARKS (name, mark, pass);");

        //method to check that file is lowercase: to do

        //make this after each?
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    //to do: this test fails
    @Test
    public void testColumnNamesAreCaseInsensitiveForQuerying() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        //create table with camelCase attribute names
        sendCommandToServer("CREATE TABLE marks (studentName, studentMark, studentPasses);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 60, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 34, FALSE);");
        //select with all lowercase column name
        String response = sendCommandToServer("SELECT studentname FROM marks WHERE id == 1;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        //select with all uppercase column name
        response = sendCommandToServer("SELECT STUDENTMARK FROM marks WHERE id == 2;");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("34"));
        //make this after each?
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    //response = sendCommandToServer("SELECT STUDENTMARK FROM marks WHERE name == 'Bob';");
    //when name isn't a column in the table, error = index -1 out of bounds for length 4. Should say
    //attribute not in table

    @Test
    public void testColumnNameCaseIsPreservedWhenStored() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        //create table with camelCase attribute names
        sendCommandToServer("CREATE TABLE marks (studentName, studentMark, studentPasses);");
        sendCommandToServer("INSERT INTO marks VALUES ('chris', 60, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('bob', 34, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"));
        //check that case is preserved
        assertTrue(response.contains("studentName"));
        assertTrue(response.contains("studentMark"));
        assertTrue(response.contains("studentPasses"));
        //make this after each?
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectAttributeComparators() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectAsteriskComparators() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectAsteriskLike() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectAsteriskLikeMultiChararacter() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectAttributeLike() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectWithoutSemicolon() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    //This test fails
    @Test
    public void testSelectTableNotExist() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM crew WHERE name LIKE 'i'");
        System.out.println(response);
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("table")); //message refers to missing semi-colon
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    //this test fails
    @Test
    public void testSelectAttributeNotInTable() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT height FROM marks WHERE name LIKE 'i'");
        System.out.println(response);
        assertTrue(response.contains("[ERROR]"));
        assertTrue(response.contains("attribute")); //message refers to missing semi-colon
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    //should not ERROR, just return column header row with no data
    @Test
    public void testSelectValidQueryNoMatches() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'z';");
        System.out.println(response);
        assertTrue(response.contains("[OK]")); //just return column header row with no data
        assertFalse(response.contains("'Chris'"));
        assertFalse(response.contains("'Bob'"));
        assertFalse(response.contains("'Fred'"));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    //this test fails: java.lang.numberformatexception for input string in getRowsValueGreaterOrLessThan
    @Test
    public void testSelectCompareDifferentDataTypesTextInteger() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name>60;");
        //return blank results
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("id"));
        assertTrue(response.contains("name"));
        assertFalse(response.contains("'Chris'"));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    // SELECT * FROM marks WHERE (pass == FALSE)

    @Test
    public void testSelectBracketedConditionAsterisk() throws IOException {
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
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testSelectBracketedConditionAttribute() throws IOException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 61, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 40, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Fred', 30, FALSE);");
        String response = sendCommandToServer("SELECT name FROM marks WHERE (mark>60);");
        assertTrue(response.contains("[OK]"));
        assertTrue(response.contains("'Chris'"));
        assertFalse(response.contains("61"));
        assertFalse(response.contains("TRUE"));
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }



}
