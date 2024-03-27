package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    static private boolean hasAsterisk;

    static private boolean hasCondition;

    static private String selectResponse;

    static private String tableToSelect;

    static private String conditionAttributeName;

    static private String conditionComparator;
    static private String conditionValue;

    static private String selectAttribute; //in "SELECT id FROM tableName WHERE ..." this is the "id"

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

    public void setSelectAttribute(String attribute){
        selectAttribute = attribute;
    }

    public String getDatabaseToCreate(){
        return databaseToCreate;
    }

    public void setTableToSelect(String tableName){
        tableToSelect = tableName;
    }

    public String getTableToSelect(){
        return tableToSelect;
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

    public void setSelectAsterisk(boolean value){
        hasAsterisk = value;
    }

    public void setHasCondition(boolean value){
        hasCondition = value;
    }


    public boolean getSelectAsterisk(){
        return hasAsterisk;
    }

    public void setNameTableToInsertInto(String tableName){
        tableToInsertInto = tableName;
    }

    public String getSelectResponse(){
        return selectResponse;
    }

    public void setSelectResponse(String responseInput){
        selectResponse = responseInput;
    }

    public void clearDatabasesList(){
        databasesList.clear();
    }

    public void setConditionAttributeName(String newAttributeName){
        conditionAttributeName = newAttributeName;
    }

    public void setConditionValue(String newConditionValue){
        conditionValue = newConditionValue;
    }

    public void setConditionComparator(String newConditionComparator){
        conditionComparator = newConditionComparator;
    }

    public boolean deleteDatabaseObject(String tableName) throws IOException {
        Iterator<Database> iterator = databasesList.iterator();
        while(iterator.hasNext()){
            Database database = iterator.next();
            if (Objects.equals(database.getDatabaseName(), tableName)) {
                iterator.remove();
                return true;
            }
        }
        return false;
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
            Database database = getDatabaseObjectFromName(databaseInUse);
            if (database.tableExistsInDatabase(tableToInsertInto)) {
                Table table = database.getTableObjectFromDatabaseFromName(tableToInsertInto);
                table.insertValuesInTable(table, valuesForInsertCommand);
                database.loadTableToDatabase(table);
                return table.writeTableToFile(tableToInsertInto, true);
            }
        }
        return false;
    }

    public boolean interpretSelect() throws IOException{
        if(databaseObjectAlreadyExists(databaseInUse)) {
            Database database = getDatabaseObjectFromName(databaseInUse);
            if (database.tableExistsInDatabase(tableToSelect)) {
                Table selectedTableObject = database.getTableObjectFromDatabaseFromName(tableToSelect);
                if (hasAsterisk && !hasCondition) {
                    ArrayList<Integer> listOfRows = selectedTableObject.populateListOfRowsForWholeTable();
                    selectResponse = selectedTableObject.tableRowsToString(selectedTableObject, listOfRows);
                    return true;
                }
                if (hasAsterisk && hasCondition) {
                    ArrayList<Integer> listOfRows = interpretSelectAsteriskCondition(selectedTableObject);
                    selectResponse = selectedTableObject.tableRowsToString(selectedTableObject, listOfRows);
                    return true;
                }
                if(!hasAsterisk && hasCondition) {
                    //this code assumes only one attribute name to search for, but the grammar allows for a list
                    int columnIndex = selectedTableObject.getIndexAttributeName(selectAttribute);
                    ArrayList<Integer> listOfRows = interpretSelectAsteriskCondition(selectedTableObject);
                    selectResponse = selectedTableObject.valuesInColumnToString(selectedTableObject, listOfRows, columnIndex);
                    return true;
                }
                //account for case where no asterisk and yes condition e.g. "SELECT id FROM marks WHERE pass == FALSE;"
            }
        }
        return false;
    }

    //"SELECT * FROM marks WHERE name == 'Simon';"
    //display all rows from table marks where AttributeName Comparator Value
    private ArrayList<Integer> interpretSelectAsteriskCondition(Table table) {
        ArrayList<Integer> rowsToIncludeInSelectResponse = new ArrayList<>();
        //for now just doing == case (need logic to switch between these cases, based on conditionComparator)
        int columnIndex = table.getIndexAttributeName(conditionAttributeName);
        rowsToIncludeInSelectResponse = table.getRowsValueIsIn(columnIndex, conditionValue);
        return rowsToIncludeInSelectResponse;
    }

    private ArrayList<Integer> interpretSelectAttributeListCondition(Table table) {
        ArrayList<Integer> rowsToIncludeInSelectResponse = new ArrayList<>();
        //for now just doing == case (need logic to switch between these cases, based on conditionComparator)
        int columnIndex = table.getIndexAttributeName(conditionAttributeName);
        rowsToIncludeInSelectResponse = table.getRowsValueIsIn(columnIndex, conditionValue);
        return rowsToIncludeInSelectResponse;
    }






    //when running tests, consider zeroing out the attributes in this class after every scenario

}