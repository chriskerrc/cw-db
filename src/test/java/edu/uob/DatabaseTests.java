package edu.uob;

import org.junit.jupiter.api.Test;

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





}
