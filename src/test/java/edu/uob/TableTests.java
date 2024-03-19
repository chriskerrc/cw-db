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




}
