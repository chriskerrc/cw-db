package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTests {

    //run these tests independently for now
    @Test
    public void testCreateDatabaseDirectory() throws IOException {
        Database database = new Database();
        String databaseName = "markbook";
        assertTrue(database.createDatabaseDirectory(databaseName));
        assertTrue(database.deleteDatabaseDirectory(databaseName));
    }

    @Test
    public void testIsDatabaseDirectoryEmpty() throws IOException {
        Database database = new Database();
        String databaseName = "markbook1";
        assertTrue(database.createDatabaseDirectory(databaseName));
        assertTrue(database.isDirectoryEmpty(databaseName));
        assertTrue(database.deleteDatabaseDirectory(databaseName));
    }

    @Test
    public void testDeleteDatabaseDirectory() throws IOException {
        Database database = new Database();
        String databaseName = "markbook2";
        //create folder
        assertTrue(database.createDatabaseDirectory(databaseName));
        //delete folder
        assertTrue(database.deleteDatabaseDirectory(databaseName));

    }

    @Test
    public void testCreateNewDatabaseObjectsAndAddToMetadataList() throws IOException {
        DBServer dbServer = new DBServer();
        //Add first database
        dbServer.handleCommand("CREATE DATABASE databaseForList1;");
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        assertEquals(databaseManager.getDatabaseIndexFromName("databaseForList1"), 0);
        //Add second database
        dbServer.handleCommand("CREATE DATABASE databaseForList2;");
        assertEquals(databaseManager.getDatabaseIndexFromName("databaseForList2"), 1);
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory("databaseForList1"));
        assertTrue(database.deleteDatabaseDirectory("databaseForList2"));
    }

    @Test
    public void testUseDatabaseUpdateDatabaseInUse() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.clearDatabasesList();
        //Add first database
        dbServer.handleCommand("CREATE DATABASE databaseForUse;");
        assertEquals(databaseManager.getDatabaseIndexFromName("databaseForUse"), 0);
        //Use database
        dbServer.handleCommand("USE databaseForUse;");
        assertEquals(databaseManager.getDatabaseInUse(), "databaseForUse");
        //Create new database
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory("databaseForUse"));
    }

    @Test
    public void testLoadFilesInDatabaseFolderIntoArray() throws IOException {
        Database database = new Database();
        String[] filesList = database.getFilesInDatabaseFolder("people");
        if(filesList != null) {
            //not sure why it loads sheds before people...
            assertEquals(filesList[0], "sheds");
            assertEquals(filesList[1], "people");
        }
    }
}
