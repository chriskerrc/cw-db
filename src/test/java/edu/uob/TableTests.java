package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableTests {

    //I don't know if I can leave the example people.tab file in the databases folder...
    //Better to create a file then delete it after testing to tidy up
    @Test
    public void testFileExists() {
        Table table = new Table();
        String fileName = "people.tab";
        assertTrue(table.doesFileExist(fileName));
    }

    @Test
    public void testFileRead() throws IOException {
        Table table = new Table();
        String fileName = "people.tab";
        table.readFileToConsole(fileName);
    }

    @Test
    public void testStoreTableToDataStructure() throws IOException {
        Table table = new Table();
        String fileName = "people.tab";
        table.storeFileToDataStructure(fileName);
    }

    //temporary method
    @Test
    public void testPrintStorageFolderPath(){
        Table table = new Table();
        table.printStorageFolderPath();
    }

    //the next three tests don't work because they assumed that there was a file called people.tab in the root of the databases folder
    @Test
    public void testGetTableDataStructure() throws IOException {
        Table table = new Table();
        String fileName = "people.tab";
        //read in file to data structure
        table.storeFileToDataStructure(fileName);
        ArrayList<ArrayList<String>> tableDataStructure = table.getTableDataStructure();
    }

    @Test
    public void testGetTableCellValueTopLeftFromDataStructure() throws IOException {
        Table table = new Table();
        String fileName = "people.tab";
        table.storeFileToDataStructure(fileName);
        String value = table.getTableCellValueFromDataStructure(0,0);
        assertEquals(value, "id");
    }

    @Test
    public void testSetTableCellValueNameRow1InDataStructure() throws IOException {
        Table table = new Table();
        String fileName = "people.tab";
        table.storeFileToDataStructure(fileName);
        String value = table.getTableCellValueFromDataStructure(1,1);
        assertEquals(value, "Bob");
        table.setTableCellValueInDataStructure(1, 1, "Christian");
        value = table.getTableCellValueFromDataStructure(1,1);
        assertEquals(value, "Christian");
    }

    @Test
    public void testFileExistsInDatabaseFolder() throws IOException {
        Table table = new Table();
        assertTrue(table.doesFileExist("sheds"));
    }

    @Test
    public void testSetTableName() throws IOException {
        Table table = new Table();
        table.setTableName("sheds");
        assertEquals(table.getTableName(), "sheds");
    }

    @Test
    public void testStoreNamedFileToTableObject() throws IOException {
        //for now current database is hardcoded to "people", will need to do "USE people;" command in future
        Table tableSheds = new Table();
        tableSheds = tableSheds.storeNamedFileToTableObject("sheds");
        assertEquals(tableSheds.getTableCellValueFromDataStructure(1, 1), "Dorchester");
        assertEquals(tableSheds.getTableCellValueFromDataStructure(2, 2), "1200");
    }

    @Test
    public void testGetTableNameAfterLoadFileToTableObject() throws IOException {
        //for now current database is hardcoded to "people", will need to do "USE people;" command in future
        Table tableSheds = new Table();
        tableSheds = tableSheds.storeNamedFileToTableObject("sheds");
        assertEquals("sheds", tableSheds.getTableName());
    }


    //loadTableToDatabase
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
}
