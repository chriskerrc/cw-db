package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableTests {

    //I don't know if I can leave the example people.tab file in the databases folder...
    //Better to create a file then delete it after testing to tidy up

   /*
   These tests assume the existence of a people.tab file in the root databases folder

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
*/
    //temporary method
    @Test
    public void testPrintStorageFolderPath(){
        Table table = new Table();
        table.printStorageFolderPath();
    }

    //the next three tests don't work because they assumed that there was a file called people.tab in the root of the databases folder
    /*
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
    */
    @Test
    public void testSetTableName() throws IOException {
        Table table = new Table();
        table.setTableName("sheds");
        assertEquals(table.getTableName(), "sheds");
    }

    @Test
    public void testStoreNamedFileToTableObject() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("people");
        databaseManager.addDatabaseToList(peopleDatabase);
        dbServer.handleCommand("USE people;");
        Table tableSheds = new Table();
        tableSheds = tableSheds.storeNamedFileToTableObject("sheds");
        assertEquals(tableSheds.getTableCellValueFromDataStructure(1, 1), "Dorchester");
        assertEquals(tableSheds.getTableCellValueFromDataStructure(2, 2), "1200");
    }

    @Test
    public void testGetTableNameAfterLoadFileToTableObject() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("people");
        databaseManager.addDatabaseToList(peopleDatabase);
        dbServer.handleCommand("USE people;");
        Table tableSheds = new Table();
        tableSheds = tableSheds.storeNamedFileToTableObject("sheds");
        assertEquals("sheds", tableSheds.getTableName());
    }

    @Test
    public void testCreateEmptyTableFile() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("people");
        databaseManager.addDatabaseToList(peopleDatabase);
        dbServer.handleCommand("USE people;");
        Table table = new Table();
        assertTrue(table.writeEmptyTableToFile("apple"));
        assertTrue(table.deleteTableFile("apple"));
    }

    @Test
    public void testWriteTableToFile() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("people");
        databaseManager.addDatabaseToList(peopleDatabase);
        dbServer.handleCommand("USE people;");
        Table table = new Table();
        table = table.storeNamedFileToTableObject("sheds");
        assertTrue(table.writeTableToFile("shedsCopy"));
        assertTrue(table.deleteTableFile("shedsCopy"));
    }

    @Test
    public void testCreateTableNoValuesAndWriteToFile() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        //manually create and add people database object to metadata list
        //when I have ability to create tables in databases, do that here instead from commands
        Database peopleDatabase = new Database();
        peopleDatabase.setDatabaseName("people");
        databaseManager.addDatabaseToList(peopleDatabase);
        dbServer.handleCommand("USE people;");
        Table table = new Table();
        table.createTableNoValues("noValues");
        assertTrue(table.deleteTableFile("noValues"));
    }

    //add a test to read in file, update value, output new file

    //add test for create table with no columns (see notebook for outline)

    //add test for create table with columns

}
