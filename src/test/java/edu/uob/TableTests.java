package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableTests {

    @Test
    public void testSetTableName() throws IOException {
        Table table = new Table();
        table.setTableName("sheds");
        assertEquals(table.getTableName(), "sheds");
    }

    @Test
    public void testWholeTableToString() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        dbServer.handleCommand("CREATE DATABASE vehicles;");
        dbServer.handleCommand("USE vehicles;");
        dbServer.handleCommand("CREATE TABLE planes (brand, price, jet);");
        dbServer.handleCommand("INSERT INTO planes VALUES ('boeing', 10000, TRUE);");
        dbServer.handleCommand("INSERT INTO planes VALUES ('airbus', 9000, FALSE);");
        dbServer.handleCommand("INSERT INTO planes VALUES ('boeing', 8000, FALSE);");
        Database database = databaseManager.getDatabaseObjectFromName("vehicles");
        databaseManager.getDatabaseObjectFromName("vehicles");
        Table table = database.getTableObjectFromDatabaseFromName("planes");
        ArrayList<Integer> listOfRows = table.populateListOfRowsForWholeTable();
        String tableString = table.tableRowsToString(table, listOfRows);
        assertTrue(tableString.contains("'boeing'")); //row 1
        assertTrue(tableString.contains("'airbus'")); //row 2
        assertTrue(tableString.contains("8000")); //row 3
        assertTrue(database.deleteTableObject("planes"));
        assertTrue(databaseManager.deleteDatabaseObject("vehicles"));
        assertTrue(table.deleteTableFile("planes"));
        assertTrue(database.deleteDatabaseDirectory("vehicles"));
    }

    @Test
    public void testWholeTableSelect() throws IOException {
        DBServer dbServer = new DBServer();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        dbServer.handleCommand("CREATE DATABASE vehicles;");
        dbServer.handleCommand("USE vehicles;");
        dbServer.handleCommand("CREATE TABLE planes (brand, price, jet);");
        dbServer.handleCommand("INSERT INTO planes VALUES ('boeing', 10000, TRUE);");
        dbServer.handleCommand("INSERT INTO planes VALUES ('airbus', 9000, FALSE);");
        dbServer.handleCommand("INSERT INTO planes VALUES ('boeing', 8000, FALSE);");
        dbServer.handleCommand("SELECT * FROM planes;");
        Database database = databaseManager.getDatabaseObjectFromName("vehicles");
        databaseManager.getDatabaseObjectFromName("vehicles");
        Table table = database.getTableObjectFromDatabaseFromName("planes");
        assertTrue(database.deleteTableObject("planes"));
        assertTrue(databaseManager.deleteDatabaseObject("vehicles"));
        assertTrue(table.deleteTableFile("planes"));
        assertTrue(database.deleteDatabaseDirectory("vehicles"));
    }
}
