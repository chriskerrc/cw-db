package edu.uob;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

public class Database {

    String databaseName;

    private String storageFolderPath;
    private String filePath;

    private ArrayList<Table> tablesInDatabase = new ArrayList<>();

    public Database(){
        DBServer dbServer = new DBServer();
        storageFolderPath = dbServer.getStorageFolderPath();
        filePath = storageFolderPath + File.separator;
    }

    private boolean checkFolderExists(String fileName) {
        String lowercaseDatabaseName = fileName.toLowerCase();
        File fileToOpen = new File(filePath + lowercaseDatabaseName);
        return fileToOpen.exists();
    }
    public boolean createDBDirectory(String databaseName){
        if(checkFolderExists(databaseName)){
            return false;
        }
        String lowercaseDatabaseName = databaseName.toLowerCase();
        File databaseFolder = new File(filePath + lowercaseDatabaseName);
        return databaseFolder.mkdir();
    }

    public void loadTableToDatabase(Table newTable){
        tablesInDatabase.add(newTable);
    }

    public void loadTablesToDatabase(String [] tableNames) throws IOException {
        for (String tableName : tableNames) {
            Table newTable = new Table();
            newTable.storeFileToTable(tableName);
            newTable.setTableName(tableName);
            loadTableToDatabase(newTable);
        }
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public void setDatabaseName(String newDatabaseName){
        databaseName = newDatabaseName;
    }

    public String[] getFilesInDBFolder(String databaseName){
        String lowercaseDatabaseName = databaseName.toLowerCase();
        File directoryToOpen = new File(filePath + lowercaseDatabaseName);
        File[] listFiles = directoryToOpen.listFiles(new FilenameFilter() {
            public boolean accept(File directoryToOpen, String fileName) {
                return fileName.toLowerCase().endsWith(".tab");
            }
        });
        if(listFiles == null){
            return null;
        }
        String[] fileNames = new String[listFiles.length];
        for(int i = 0; i < listFiles.length; i++){
            fileNames[i] = listFiles[i].getName();
        }
        for(int i = 0; i <fileNames.length; i++){
            fileNames[i] = fileNames[i].replace(".tab", "");
        }
        return fileNames;
    }

    public boolean tableExistsInDB(String tableName){
        for (Table tableToCheck : tablesInDatabase) {
            if (tableToCheck.getTableName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }

    public Table getTableFromDatabase(String tableName){
        for (Table tableToGet : tablesInDatabase) {
            if (tableToGet.getTableName().equalsIgnoreCase(tableName)) {
                return tableToGet;
            }
        }
        return null;
    }
}
