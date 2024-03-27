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
        // Check that the very last token is a number (which should be the ID of the entry)
        String lastToken = tokens[tokens.length-1];
        try {
            Integer.parseInt(lastToken);
        } catch (NumberFormatException nfe) {
            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Simon';` should have been an integer ID, but was " + lastToken);
        }
        Table table = new Table();
        assertTrue(table.deleteTableFile("marks"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }


}
