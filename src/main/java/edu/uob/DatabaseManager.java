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

    static private String tableToInsertInto;

    static private ArrayList<String> attributeNamesForCreateTable;

    static private ArrayList<String> valuesForInsertCommand;

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

    public void setValuesForInsertCommand(ArrayList<String> valuesList){
        valuesForInsertCommand = valuesList;
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

    public String getNameTableToInsertInto(){
        return tableToInsertInto;
    }

    public void setNameTableToInsertInto(String tableName){
        tableToInsertInto = tableName;
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

    //after server restart, Use database returns error: suggests it's not loading files
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
                return false;
            }
            Table newTable = new Table();
            newTable.setTableName(tableToCreate);
            if(!isAttributeListForCreateTable){
                //this method also writes the table to file:
                newTable.createTableNoValues(tableToCreate);
            }
            else{
                newTable.createTableWithValues(tableToCreate, values);
            }
            database.loadTableToDatabase(newTable);
            return true;

            //newTable.writeTableToFile(tableToCreate);
        }
        return false;
    }

    public boolean interpretInsert() throws IOException{
        if(databaseObjectAlreadyExists(databaseInUse)) {
            System.out.println("database exists");
            Database database = getDatabaseObjectFromName(databaseInUse);
            if (database.tableExistsInDatabase(tableToInsertInto)) {
                System.out.println("table exists in database");
                Table table = database.getTableObjectFromDatabaseFromName(tableToInsertInto);
                Table updatedTable = table.insertValuesInTable(table, valuesForInsertCommand);
                //String value = updatedTable.getTableCellValueFromDataStructure(1,1);
                //System.out.println("value at 1,1 " + value);
                database.loadTableToDatabase(updatedTable);
                return updatedTable.writeTableToFile(tableToInsertInto, true);
            }
        }
        return false;
    }

    //when running tests, consider zeroing out the attributes in this class after every scenario

}