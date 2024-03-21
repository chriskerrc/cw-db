package edu.uob;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

public class Database extends DatabaseMetadata {

    String databaseName;

    private String storageFolderPath;
    private String filePath;

    private ArrayList<Table> tablesInDatabase = new ArrayList<>();

    private int databaseIndex;

    public Database(){
        DBServer dbServer = new DBServer();
        storageFolderPath = dbServer.getStorageFolderPath();
        filePath = storageFolderPath + File.separator;

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

    public void setDatabaseIndex(Database database, int databaseIndex){
        database.databaseIndex = databaseIndex;
    }

    public int getDatabaseIndex(Database database){
        return database.databaseIndex;
    }

    public void loadTableToDatabase(Table table){
        tablesInDatabase.add(table);
    }

    public void loadAllTablesInFolderToDatabaseObject(String [] tableNames) throws IOException {
        for (String tableName : tableNames) {
            Table table = new Table();
            Table loadedTable = table.storeNamedFileToTableObject(tableName);
            loadedTable.setTableName(tableName);
            loadTableToDatabase(loadedTable);
        }
    }
/*
    public Table getTableFromDatabase(String tableName){
        //
        return table;
    }
*/
    public void removeTableFromListCurrentTables(String tableName){
        Iterator<Table> iterator = tablesInDatabase.iterator();
        while(iterator.hasNext()){
            Table table = iterator.next();
            if(Objects.equals(table.getTableName(), tableName)){
                iterator.remove();
                break;
            }
        }
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public void setDatabaseName(String newDatabaseName){
        databaseName = newDatabaseName;
    }

    public String[] getFilesInDatabaseFolder(String databaseName){
        File directoryToOpen = new File(filePath + databaseName);
        File[] listFiles = directoryToOpen.listFiles();
        String[] fileNames = new String[listFiles.length]; //fix the yellow squiggly lines
        if(listFiles != null){
            for(int i = 0; i < listFiles.length; i++){
                fileNames[i] = listFiles[i].getName();
            }
            for(int i = 0; i <fileNames.length; i++){
                fileNames[i] = fileNames[i].replace(".tab", "");
            }
        }
        return fileNames;
    }


    public ArrayList<Table> getTablesInDatabase(){
        return tablesInDatabase;
    }

    public boolean getTableObjectInDatabaseFromName(String tableName){
        for (Table table : tablesInDatabase) {
            if (Objects.equals(table.getTableName(), tableName)) {
                return true;
            }
        }
        return false;
    }
    //call addTableToList when creating table
    //method: have a setter method addTableToActiveDatabase that can be called from Table class to add the table to list of current tables, or remove it when deleted:
            //a list storing a reference to the table object
            //need to call this on create table

}
