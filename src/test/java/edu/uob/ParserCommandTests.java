package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserCommandTests {

    private String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    //previously, these tests targeted individual parser commands, but I've refactored them to make them
    //more realistic responses to DB Commands

    //add deletion of random database folders these tests create

    @Test
    public void testValidCreateDatabaseCommand() throws IOException {
        String randomName = generateRandomName();
        //System.out.println("this is random name " + randomName);
        DBServer dbServer = new DBServer();
        String response = dbServer.handleCommand("CREATE DATABASE " + randomName + ";");
        assertTrue(response.contains("[OK]"));
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory(randomName));
    }

    @Test
    public void testValidUseCommand() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("Name");
        databaseManager.addDatabaseToList(peopleDatabase);
        String response = dbServer.handleCommand("USE Name;");
        assertTrue(response.contains("[OK]"));
    }

    //This test triggers error
    @Test
    public void testInvalidUseDatabaseCommand() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("Name");
        databaseManager.addDatabaseToList(peopleDatabase);
        String response = dbServer.handleCommand("USE DATABASE Name");
        assertTrue(response.contains("[ERROR]"));
    }


    //IMPORTANT: THIS TEST TRIGGERS INDEX OUT OF BOUNDS ERROR
    @Test
    public void testCreateDatabaseMissingSemicolon() throws IOException {
        String randomName = generateRandomName();
        DBServer dbServer = new DBServer();
        String response = dbServer.handleCommand("CREATE DATABASE " + randomName);
        assertTrue(response.contains("[ERROR]"));
    }

}
