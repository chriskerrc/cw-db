package edu.uob;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class Database {

    //Important: ensure I have a .mvn/wrapper folder in my github repo: maybe it will regenerate if I run mvn from command line?

    //Add constructor for this class
    String databaseName;

    private String storageFolderPath;
    private String filePath;

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

    public boolean interpretCreateDatabase(String databaseName) throws IOException {
        return createDatabaseDirectory(databaseName);
    }

}
