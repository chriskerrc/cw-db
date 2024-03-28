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
        if(!doesDirectoryExist(databaseName)){
            String databaseNameLowercase = databaseName.toLowerCase();
            File databaseFolder = new File(filePath + databaseNameLowercase);
            return databaseFolder.mkdir();
        }
        return false; //or throw error
    }

    public boolean deleteDatabaseDirectory(String databaseName) throws IOException {
        String lowercaseDatabaseName = databaseName.toLowerCase();
        if(isDirectoryEmpty(lowercaseDatabaseName)){
            File databaseFolder = new File(filePath + lowercaseDatabaseName);
            return databaseFolder.delete();

        }
        return false; //or throw error
    }


    public boolean deleteTableObject(String tableName) throws IOException {
        Iterator<Table> iterator = tablesInDatabase.iterator();
        while(iterator.hasNext()){
            Table table = iterator.next();
            if (Objects.equals(table.getTableName(), tableName)) {
                iterator.remove();
                return true;
            }
        }
        return false;
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

    public boolean databaseDirectoryIsSavedAsLowercase(String databaseName) throws IOException {
        Path directoryPath = Path.of(filePath, databaseName);
        if(doesDirectoryExist(databaseName)){
            String directoryName = directoryPath.getFileName().toString();
            System.out.println("directory Name " + directoryName);
            String directoryNameToLowercase = directoryName.toLowerCase();
            System.out.println("directory Name to lowercase " + directoryNameToLowercase);
            return directoryName.equals(directoryNameToLowercase);
        }
        return false;
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
        if(listFiles != null) {
            String[] fileNames = new String[listFiles.length];
            for(int i = 0; i < listFiles.length; i++){
                    fileNames[i] = listFiles[i].getName();
            }
            for(int i = 0; i <fileNames.length; i++){
                    fileNames[i] = fileNames[i].replace(".tab", "");
            }
            return fileNames;
        }
        return null;
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
    //call addTableToList when creating table
    //method: have a setter method addTableToActiveDatabase that can be called from Table class to add the table to list of current tables, or remove it when deleted:
            //a list storing a reference to the table object
            //need to call this on create table

}
