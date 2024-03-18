package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableTests {

    //I don't know if I can leave the example people.tab file in the databases folder...
    //Better to create a file then delete it after testing to tidy up
    @Test
    public void testFileExists() {
        Table table = new Table();
        table.doesFileExist();
        assertTrue(table.doesFileExist());
    }

    @Test
    public void testFileRead() throws IOException {
        Table table = new Table();
        table.readFileToConsole();
    }

    @Test
    public void testStoreTableToDataStructure() throws IOException {
        Table table = new Table();
        table.storeFileToDataStructure();
    }

}
