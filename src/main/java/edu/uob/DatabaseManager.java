package edu.uob;

import java.io.IOException;
import java.util.*;

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

    public void addDatabaseToList(Database database){
        databasesList.add(database);
        databasesList.indexOf(database);
    }

    public Database getDatabaseObjectFromName(String databaseName){
        for (Database database : databasesList) {
            if (database.getDatabaseName().equalsIgnoreCase(databaseName)) {
                return database;
            }
        }
        return null;
    }

    public boolean databaseObjectAlreadyExists(String databaseName){
        for (Database database : databasesList) {
            if (database.getDatabaseName().equalsIgnoreCase(databaseName)) {
                return true;
            }
        }
        return false;
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

    public void setTableToSelect(String tableName){
        tableToSelect = tableName;
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

    public void setNameTableToCreate(String newTableName){
        tableToCreate = newTableName;
    }

    public void setSelectAsterisk(boolean value){
        hasAsterisk = value;
    }

    public void setHasCondition(boolean value){
        hasCondition = value;
    }

    public void setNameTableToInsertInto(String tableName){
        tableToInsertInto = tableName;
    }

    public String getSelectResponse(){
        return selectResponse;
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

    //Interpreter methods
    public boolean interpretCreateDatabase() {
        if(databaseObjectAlreadyExists(databaseToCreate)) {
            throw new RuntimeException("Trying to create a database that already exists?");
        }
        Database database = createNewDatabase();
        addDatabaseToList(database);
        return database.createDatabaseDirectory(databaseToCreate);
    }

    public boolean interpretUseDatabase() throws IOException {
        checkDatabaseInUse("Database doesn't exist or not in USE?");
        setDatabaseInUse(databaseInUse);
        Database database = getDatabaseObjectFromName(databaseInUse);
        String[] filesList = database.getFilesInDatabaseFolder(databaseInUse);
        if(filesList == null){
            throw new RuntimeException("Failed to load database");
        }
        database.loadAllTablesInFolderToDatabaseObject(filesList);
        return true;
    }

    public boolean interpretCreateTable() throws IOException{
        checkDatabaseInUse("Database doesn't exist? Try creating it");
        Database database = getDatabaseObjectFromName(databaseInUse);
        if(database.tableExistsInDatabase(tableToCreate)){
            throw new RuntimeException("Trying to create a table that already exists?");
        }
        Table newTable = createNewTable();
        database.loadTableToDatabase(newTable);
        return true;
    }

    public boolean interpretInsert() throws IOException{
        checkDatabaseInUse("Database doesn't exist or not in USE?");
        Database database = getDatabaseObjectFromName(databaseInUse);
        if (!database.tableExistsInDatabase(tableToInsertInto)) {
            throw new RuntimeException("Trying to insert values into a table that doesn't exist?");
        }
        Table table = database.getTableObjectFromDatabaseFromName(tableToInsertInto);
        if(table == null){
            throw new RuntimeException("Table doesn't exist?");
        }
        updateHighestIDFromTableOnFIle(table);
        checkNumberOfColumns(table);
        table.insertValuesInTable(table, valuesForInsertCommand);
        database.loadTableToDatabase(table);
        return table.writeTableToFile(tableToInsertInto, true);
    }

    public boolean interpretSelect() {
        checkDatabaseInUse("Database doesn't exist or not in USE?");
        Database database = getDatabaseObjectFromName(databaseInUse);
        if (!database.tableExistsInDatabase(tableToSelect)) {
            throw new RuntimeException("Selected table doesn't exist");
        }
        Table selectedTableObject = database.getTableObjectFromDatabaseFromName(tableToSelect);
        if (hasAsterisk && !hasCondition) {
            handleSelectCommandAsteriskNoCondition(selectedTableObject);
            return true;
        }
        if (hasAsterisk) {
            handleSelectCommandAsteriskCondition(selectedTableObject);
            return true;
        }
        if(hasCondition) {
            return handleSelectCommandNoAsteriskCondition(selectedTableObject);
        }
        return false;
    }

    private ArrayList<Integer> interpretSelectCondition(Table table) {
        ArrayList<Integer> rowsToIncludeInSelectResponse;
        int columnIndex = table.getIndexAttributeName(conditionAttributeName);
        rowsToIncludeInSelectResponse = switch (conditionComparator) {
            case "==" -> table.getRowsValueIn(columnIndex, conditionValue, true);
            case "!=" -> table.getRowsValueIn(columnIndex, conditionValue, false);
            case ">", "<", ">=", "<=" -> table.getRowsValueGreaterOrLessThan(columnIndex);
            case "LIKE" -> table.getRowsValueLike(columnIndex);
            default -> throw new IllegalStateException("Unexpected comparator in condition: " + conditionComparator);
        };
        return rowsToIncludeInSelectResponse;
    }

    //wrapper and helper methods
    private void handleSelectCommandAsteriskNoCondition(Table selectedTableObject) {
        ArrayList<Integer> listOfRows = selectedTableObject.populateListOfRowsForWholeTable();
        selectResponse = selectedTableObject.tableRowsToString(selectedTableObject, listOfRows);
    }

    private void handleSelectCommandAsteriskCondition(Table selectedTableObject) {
        ArrayList<Integer> listOfRows = interpretSelectCondition(selectedTableObject);
        selectResponse = selectedTableObject.tableRowsToString(selectedTableObject, listOfRows);
    }

    private boolean handleSelectCommandNoAsteriskCondition(Table selectedTableObject) {
        //this code assumes only one attribute name to search for, but the grammar allows for a list
        int columnIndex = selectedTableObject.getIndexAttributeName(selectAttribute);
        if(columnIndex == -1){
            throw new RuntimeException("Attribute not in table");
        }
        ArrayList<Integer> listOfRows = interpretSelectCondition(selectedTableObject);
        selectResponse = selectedTableObject.valuesInColumnToString(selectedTableObject, listOfRows, columnIndex);
        return true;
    }

    private boolean attributesDuplicated(ArrayList<String> attributeList){
        attributeList.replaceAll(String::toLowerCase);
        Set<String> set = new HashSet<>(attributeList);
        //if the Set is smaller than the ArrayList, there are duplicates in the ArrayList
        return set.size() < attributeList.size();
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public String getConditionComparator() {
        return conditionComparator;
    }

    private void updateHighestIDFromTableOnFIle(Table tableToInsetInto) throws IOException {
        Table tableFromFile = new Table();
        tableFromFile = tableFromFile.storeNamedFileToTableObject(tableToInsertInto);
        int highestCurrentID = tableFromFile.getCurrentHighestID();
        tableToInsetInto.setCurrentRecordID(highestCurrentID);
    }

    private Database createNewDatabase(){
        Database database = new Database();
        database.setDatabaseName(databaseToCreate);
        return database;
    }

    private Table createNewTable() throws IOException {
        Table newTable = new Table();
        newTable.setTableName(tableToCreate);
        if(!isAttributeListForCreateTable){
            newTable.createTableNoValues(tableToCreate); //this method also writes the table to file:
        }
        else{
            ArrayList<String> valuesList = attributeNamesForCreateTable;
            ArrayList<String> valuesPreserveCase = new ArrayList<>(valuesList);
            //preserve case of values before attributesDuplicated method called
            if(attributesDuplicated(valuesList)){
                throw new RuntimeException("Column headers are duplicated?");
            }
            newTable.createTableWithValues(tableToCreate, valuesPreserveCase);
        }
        return newTable;
    }

    private void checkDatabaseInUse(String exceptionMessage){
        if(!databaseObjectAlreadyExists(databaseInUse)) {
            throw new RuntimeException(exceptionMessage);
        }
    }

    private void checkNumberOfColumns(Table tableToInsertInto){
        int numberOfColumns = tableToInsertInto.getNumberColumnsTable();
        if(numberOfColumns != valuesForInsertCommand.size() + 1) { //add 1 to account for id column
            throw new RuntimeException("Attempting to insert wrong number of values?");
        }
    }

}