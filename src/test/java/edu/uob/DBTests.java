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




}
