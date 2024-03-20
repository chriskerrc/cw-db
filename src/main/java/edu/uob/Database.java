package edu.uob;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class Database {

    String databaseName;

    private String storageFolderPath;
    private String filePath;

    static private ArrayList<Database> databasesList;

    private int activeDatabaseIndex;

    private int databaseIndex;

    public Database(){
        DBServer dbServer = new DBServer();
        storageFolderPath = dbServer.getStorageFolderPath();
        filePath = storageFolderPath + File.separator;
        Table[] tablesInDatabase;
    }

    public boolean doesDirectoryExist(String fileName) {
        File fileToOpen = new File(filePath + databaseName);
        return fileToOpen.exists();
    }
    public boolean createDatabaseDirectory(String databaseName){
        if(!doesDirectoryExist(databaseName)){
            File databaseFolder = new File(filePath + databaseName);
            return databaseFolder.mkdir();
        }
        return false; //or throw error
    }

    public boolean deleteDatabaseDirectory(String databaseName) throws IOException {
        if(isDirectoryEmpty(databaseName)){
            File databaseFolder = new File(filePath + databaseName);
            return databaseFolder.delete();

        }
        return false; //or throw error
    }

    public boolean isDirectoryEmpty(String databaseName) throws IOException {
        Path directoryPath = Path.of(filePath, databaseName);
        if(doesDirectoryExist(databaseName)){
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(directoryPath)){
                return !directory.iterator().hasNext();
            }//catch exception
        }
        return true;
    }

    public boolean interpretCreateDatabase(String databaseName) throws IOException {
        return createDatabaseDirectory(databaseName);
    }

    //when add database to list, store its index in the database object
    public int addDatabaseToList(Database database){
        databasesList.add(database);
        return databasesList.indexOf(database);
    }

    public void setDatabaseIndex(Database database, int databaseIndex){
        database.databaseIndex = databaseIndex;
    }

    public int getDatabaseIndex(Database database){
        return database.databaseIndex;
    }

    public Database getActiveDatabase(int activeDatabaseIndex){
        return databasesList.get(activeDatabaseIndex);
    }

    public int generateNewDatabaseIndex(){
        return databasesList.size(); //check that this provides the correct index (avoid off by one error)
    }

    public void addTableListCurrentTables(Table table){

    }

    //method: have a setter method addTableToActiveDatabase that can be called from Table class to add the table to list of current tables, or remove it when deleted:
            //a list storing a reference to the table object
            //need to call this on create table
    //inside table class, have a table name attribute with getter and setter method
    //method: load tables into database - put this method in table class

}
