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

    public boolean doesDirectoryExist(String fileName) {
        String lowercaseDatabaseName = fileName.toLowerCase();
        File fileToOpen = new File(filePath + lowercaseDatabaseName);
        return fileToOpen.exists();
    }
    public boolean createDatabaseDirectory(String databaseName){
        if(doesDirectoryExist(databaseName)){
            return false;
        }
        String databaseNameLowercase = databaseName.toLowerCase();
        File databaseFolder = new File(filePath + databaseNameLowercase);
        return databaseFolder.mkdir();
    }

    public void loadTableToDatabase(Table table){
        tablesInDatabase.add(table);
    }

    public void loadAllTablesInFolderToDatabaseObject(String [] tableNames) throws IOException {
        for (String tableName : tableNames) {
            Table table = new Table();
            table.storeNamedFileToTableObject(tableName);
            table.setTableName(tableName);
            loadTableToDatabase(table);
        }
    }

    public String getDatabaseName(){
        return databaseName;
    }

    public void setDatabaseName(String newDatabaseName){
        databaseName = newDatabaseName;
    }

    public String[] getFilesInDatabaseFolder(String databaseName){
        String lowercaseDatabaseName = databaseName.toLowerCase();
        File directoryToOpen = new File(filePath + lowercaseDatabaseName);
        File[] listFiles = directoryToOpen.listFiles(new FilenameFilter() {
            public boolean accept(File directoryToOpen, String name) {
                return name.toLowerCase().endsWith(".tab");
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

    public boolean tableExistsInDatabase(String tableName){
        for (Table table : tablesInDatabase) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }

    public Table getTableObjectFromDatabaseFromName(String tableName){
        for (Table table : tablesInDatabase) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                return table;
            }
        }
        return null;
    }
}
