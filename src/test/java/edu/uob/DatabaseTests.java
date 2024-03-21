package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTests {

    //run these tests independently for now
    @Test
    public void testCreateDatabaseDirectory() throws IOException {
        Database database = new Database();
        String databaseName = "markbook";
        assertTrue(database.createDatabaseDirectory(databaseName));
    }

    @Test
    public void testIsDatabaseDirectoryEmpty() throws IOException {
        Database database = new Database();
        String databaseName = "markbook1";
        assertTrue(database.createDatabaseDirectory(databaseName));
        assertTrue(database.isDirectoryEmpty(databaseName));
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
        DatabaseMetadata databaseMetadata = dbServer.getDatabaseMetadata();
        assertEquals(databaseMetadata.getDatabaseIndexFromName("databaseForList1"), 0);
        //Add second database
        dbServer.handleCommand("CREATE DATABASE databaseForList2;");
        databaseMetadata = dbServer.getDatabaseMetadata();
        assertEquals(databaseMetadata.getDatabaseIndexFromName("databaseForList2"), 1);
        Database database = new Database();
        assertTrue(database.deleteDatabaseDirectory("databaseForList1"));
        assertTrue(database.deleteDatabaseDirectory("databaseForList2"));
    }

    @Test
    public void testUseDatabase() throws IOException {
        DBServer dbServer = new DBServer();
        //Add first database
        dbServer.handleCommand("CREATE DATABASE databaseForUse;");
        DatabaseMetadata databaseMetadata = dbServer.getDatabaseMetadata();
        assertEquals(databaseMetadata.getDatabaseIndexFromName("databaseForUse"), 0);
        //Use database
        dbServer.handleCommand("USE databaseForUse;");
        databaseMetadata = dbServer.getDatabaseMetadata();
        assertEquals(databaseMetadata.getDatabaseInUse(), "databaseForUse");
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


    //the following test fails because the file reading code expects the file to be in the root, not in the people folder
    @Test
    public void testLoadAllFilesInDatabaseFolderIntoDatabaseObject() throws IOException {
        Database database = new Database();
        String[] filesList = database.getFilesInDatabaseFolder("people");
        if(filesList != null) {
            assertEquals(filesList[0], "sheds");
            assertEquals(filesList[1], "people");
        }
        assert filesList != null;
        database.loadAllTablesInFolderToDatabaseObject(filesList);
        assertTrue(database.getTableObjectInDatabaseFromName("sheds"));
        assertTrue(database.getTableObjectInDatabaseFromName("people"));

    }







}
