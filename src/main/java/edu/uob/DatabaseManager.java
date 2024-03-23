package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

//Singleton class to manage databases as single source of truth and contain interpreter methods
public class DatabaseManager {

    private static DatabaseManager instance = null;

    static private ArrayList<Database> databasesList = new ArrayList<>();

    static private String databaseInUse;

    static private String databaseToCreate;

    static private boolean isAttributeListForCreateTable;

    static private String tableToCreate;

    static private ArrayList<String> attributeNamesForCreateTable;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    //private int activeDatabaseIndex;

    public int addDatabaseToList(Database database){
        databasesList.add(database);
        return databasesList.indexOf(database);
    }
/*
//I don't think it's necessary to store "active database" index, should be enough to search by name
    public Database getActiveDatabase(int activeDatabaseIndex){
        return databasesList.get(activeDatabaseIndex);
    }
*/
    public int generateNewDatabaseIndex(){
        return databasesList.size(); //check that this provides the correct index (avoid off by one error)
    }

    public Database getDatabaseObjectFromName(String databaseName){
        for (Database database : databasesList) {
            if (Objects.equals(database.getDatabaseName(), databaseName)) {
                return database;
            }
        }
        return null;
    }

    public int getDatabaseIndexFromName(String databaseName){
        for (int index = 0; index < databasesList.size(); index++) {
            if (Objects.equals(databasesList.get(index).getDatabaseName(), databaseName)) {
                return index;
            }
        }
        return -1;
    }

    public boolean databaseObjectAlreadyExists(String databaseName){
        for (Database database : databasesList) {
            if (Objects.equals(database.getDatabaseName(), databaseName)) {
                return true;
            }
        }
        return false;
    }


    public ArrayList<Database> getDatabasesList(){
        return databasesList;
    }

    public void setDatabaseInUse(String databaseName){
        databaseInUse = databaseName;
    }

    public String getDatabaseInUse(){
        return databaseInUse;
    }

    public void setDatabaseToCreate(String databaseName){
        databaseToCreate = databaseName;
    }

    public String getDatabaseToCreate(){
        return databaseToCreate;
    }

    public boolean getIsAttributeListForCreateTable(){
        return isAttributeListForCreateTable;
    }

    public ArrayList<String> getAttributeNamesForCreateTable(){
        return attributeNamesForCreateTable;
    }

    public void setAttributeNamesForCreateTable(ArrayList<String> attributeList){
        attributeNamesForCreateTable = attributeList;
    }

    public void setIsAttributeListForCreateTable(boolean isAttributeList){
        isAttributeListForCreateTable = isAttributeList;
    }

    public String getNameTableToCreate(){
        return tableToCreate;
    }

    public void setNameTableToCreate(String newTableName){
        tableToCreate = newTableName;
    }

    public void clearDatabasesList(){
        databasesList.clear();
    }

    //Interpreter methods
    public boolean interpretCreateDatabase() throws IOException {
        Database database = new Database();
        addDatabaseToList(database);
        database.setDatabaseName(databaseToCreate);
        return database.createDatabaseDirectory(databaseToCreate);
    }

    public boolean interpretUseDatabase() throws IOException {
        if(databaseObjectAlreadyExists(databaseInUse)){
            setDatabaseInUse(databaseInUse);
            Database database = getDatabaseObjectFromName(databaseInUse);
            String[] filesList = database.getFilesInDatabaseFolder(databaseInUse);
            if(filesList != null) {
                database.loadAllTablesInFolderToDatabaseObject(filesList);
            }
            return true;
        }
        else{
            return false;
        }
    }

    public boolean interpretCreateTable() throws IOException{
        if(databaseObjectAlreadyExists(databaseInUse)){
            Database database = getDatabaseObjectFromName(databaseInUse);
            ArrayList<String> values = attributeNamesForCreateTable;
            if(database.tableExistsInDatabase(tableToCreate)){
                //this seems bugged: it's possible to create two tables with same name
                return false;
            }
            Table newTable = new Table();
            newTable.setTableName(tableToCreate);
            if(!isAttributeListForCreateTable){
                //this method also writes the table to file:
                newTable.createTableNoValues(tableToCreate);
                database.loadTableToDatabase(newTable);
                return true;
            }
            else{
                newTable.createTableWithValues(tableToCreate, values);
                database.loadTableToDatabase(newTable);
                return true;
            }

            //newTable.writeTableToFile(tableToCreate);
        }
        return false;
    }

}