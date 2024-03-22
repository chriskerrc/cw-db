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
    public void testUseDatabaseUpdateDatabaseInUse() throws IOException {
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
        assertTrue(database.tableExistsInDatabase("sheds"));
        assertTrue(database.tableExistsInDatabase("people"));
    }



    @Test
    public void testLoadTableToDatabase() throws IOException {
        //for now current database is hardcoded to "people", will need to do "USE people;" command in future
        Table tableSheds = new Table();
        //Load table from file
        tableSheds = tableSheds.storeNamedFileToTableObject("sheds");
        assertEquals("sheds", tableSheds.getTableName());
        assertEquals(tableSheds.getTableCellValueFromDataStructure(1, 1), "Dorchester");
        assertEquals(tableSheds.getTableCellValueFromDataStructure(2, 2), "1200");
        Database databasePeople = new Database();
        //load table to database
        databasePeople.loadTableToDatabase(tableSheds);
        //check that table of this name exists in database
        assertTrue(databasePeople.tableExistsInDatabase("sheds"));
        //get table from database and check it has expected values
        Table tableShedsFromDatabase = databasePeople.getTableObjectFromDatabaseFromName("sheds");
        assertEquals(tableShedsFromDatabase.getTableCellValueFromDataStructure(1, 1), "Dorchester");
    }

    @Test
    public void testUseDatabaseLoadDatabase() throws IOException {
        //for now, this assumes that the people folder exists. In the future, create a database and populate with tables people.tab and sheds.tab within this test via commands
        DBServer dbServer = new DBServer();
        DatabaseMetadata databaseMetadata = dbServer.getDatabaseMetadata();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("people");
        databaseMetadata.addDatabaseToList(peopleDatabase);
        dbServer.handleCommand("USE people;");
        assertEquals(databaseMetadata.getDatabaseInUse(), "people");
        Database databasePeople = new Database();
        databasePeople = databasePeople.getDatabaseObjectFromName(databaseMetadata.getDatabaseInUse());
        assertTrue(databasePeople.tableExistsInDatabase("sheds"));
        assertTrue(databasePeople.tableExistsInDatabase("people"));
        Table tableSheds = databasePeople.getTableObjectFromDatabaseFromName("sheds");
        Table tablePeople = databasePeople.getTableObjectFromDatabaseFromName("people");
        System.out.println(tableSheds);
        //assertEquals(tableSheds.getTableCellValueFromDataStructure(1, 1), "Dorchester");
        //assertEquals(tablePeople.getTableCellValueFromDataStructure(3, 1), "Chris");
    }






}
