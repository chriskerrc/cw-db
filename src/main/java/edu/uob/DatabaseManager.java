package edu.uob;

import java.io.IOException;
import java.util.*;

//Singleton class to manage databases as single source of truth and contain interpreter methods
public class DatabaseManager {

    private static DatabaseManager instance = null;

    static private ArrayList<Database> databasesList = new ArrayList<>();

    static private String databaseInUse;

    static private String databaseToCreate;

    static private boolean tableHasAttributes;

    static private String tableToCreate;

    static private String insertionTable;

    static private ArrayList<String> tableAttributes;

    static private ArrayList<String> insertionValues;

    static private boolean hasAsterisk;

    static private boolean hasCondition;

    static private String selectResponse;

    static private String tableToSelect;

    static private String conditionAttribute;

    static private String conditionComparator;
    static private String conditionValue;

    static private String selectAttribute;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void addDatabaseToList(Database newDatabase){
        databasesList.add(newDatabase);
        databasesList.indexOf(newDatabase);
    }

    public Database getDatabase(String databaseName){
        for (Database databaseInList : databasesList) {
            if (databaseInList.getDatabaseName().equalsIgnoreCase(databaseName)) {
                return databaseInList;
            }
        }
        return null;
    }

    public boolean checkDBExists(String databaseName){
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

    public void setTableAttributes(ArrayList<String> attributeList){
        tableAttributes = attributeList;
    }

    public void setInsertionValues(ArrayList<String> valuesList){
        insertionValues = valuesList;
    }

    public void setTableHasAttributes(boolean attributesExist){
        tableHasAttributes = attributesExist;
    }

    public void setNameTableToCreate(String newTableName){
        tableToCreate = newTableName;
    }

    public void setSelectAsterisk(boolean asteriskExists){
        hasAsterisk = asteriskExists;
    }

    public void setHasCondition(boolean conditionExists){
        hasCondition = conditionExists;
    }

    public void setNameInsertTable(String tableName){
        insertionTable = tableName;
    }

    public String getSelectResponse(){
        return selectResponse;
    }

    public void setConditionAttribute(String newAttributeName){
        conditionAttribute = newAttributeName;
    }

    public void setConditionValue(String newConditionValue){
        conditionValue = newConditionValue;
    }

    public void setConditionComparator(String newConditionComparator){
        conditionComparator = newConditionComparator;
    }

    //Interpreter methods
    public boolean interpretCreateDatabase() {
        if(checkDBExists(databaseToCreate)) {
            throw new RuntimeException("Trying to create a database that already exists?");
        }
        Database newDatabase = createNewDatabase();
        addDatabaseToList(newDatabase);
        return newDatabase.createDBDirectory(databaseToCreate);
    }

    public boolean interpretUseDatabase() throws IOException {
        checkDatabaseInUse("Database doesn't exist or not in USE?");
        setDatabaseInUse(databaseInUse);
        Database currentDatabase = getDatabase(databaseInUse);
        String[] filesList = currentDatabase.getFilesInDBFolder(databaseInUse);
        if(filesList == null){
            throw new RuntimeException("Failed to load database");
        }
        currentDatabase.loadTablesToDatabase(filesList);
        return true;
    }

    public boolean interpretCreateTable() throws IOException{
        checkDatabaseInUse("Database doesn't exist? Try creating it");
        Database currentDatabase = getDatabase(databaseInUse);
        if(currentDatabase.tableExistsInDB(tableToCreate)){
            throw new RuntimeException("Trying to create a table that already exists?");
        }
        Table newTable = createNewTable();
        currentDatabase.loadTableToDatabase(newTable);
        return true;
    }

    public boolean interpretInsert() throws IOException{
        checkDatabaseInUse("Database doesn't exist or not in USE?");
        Database currentDatabase = getDatabase(databaseInUse);
        if (!currentDatabase.tableExistsInDB(insertionTable)) {
            throw new RuntimeException("Trying to insert values into a table that doesn't exist?");
        }
        Table currentTable = currentDatabase.getTableFromDatabase(insertionTable);
        if(currentTable == null){
            throw new RuntimeException("Table doesn't exist?");
        }
        setTableIDFromFile(currentTable);
        checkTotalColumns(currentTable);
        currentTable.insertValuesInTable(currentTable, insertionValues);
        currentDatabase.loadTableToDatabase(currentTable);
        return currentTable.writeTableToFile(insertionTable, true);
    }

    public boolean interpretSelect() {
        checkDatabaseInUse("Database doesn't exist or not in USE?");
        Database currentDatabase = getDatabase(databaseInUse);
        if (!currentDatabase.tableExistsInDB(tableToSelect)) {
            throw new RuntimeException("Selected table doesn't exist");
        }
        Table selectedTable = currentDatabase.getTableFromDatabase(tableToSelect);
        if (hasAsterisk && !hasCondition) {
            selectStarNoCondition(selectedTable);
            return true;
        }
        if (hasAsterisk) {
            selectStarCondition(selectedTable);
            return true;
        }
        if(hasCondition) {
            return selectNoStarCondition(selectedTable);
        }
        return false;
    }

    private ArrayList<Integer> interpretSelectCondition(Table table) {
        ArrayList<Integer> rowsToInclude;
        int columnIndex = table.getIndexAttribute(conditionAttribute);
        rowsToInclude = switch (conditionComparator) {
            case "==" -> table.getRowsValueIn(columnIndex, conditionValue, true);
            case "!=" -> table.getRowsValueIn(columnIndex, conditionValue, false);
            case ">", "<", ">=", "<=" -> table.getComparatorRows(columnIndex);
            case "LIKE" -> table.getRowsValueLike(columnIndex);
            default -> throw new IllegalStateException("Unexpected condition comparator: " + conditionComparator);
        };
        return rowsToInclude;
    }

    //wrapper and helper methods
    private void selectStarNoCondition(Table selectedTable) {
        ArrayList<Integer> rowList = selectedTable.fillListTableRows();
        selectResponse = selectedTable.tableRowsToString(selectedTable, rowList);
    }

    private void selectStarCondition(Table selectedTable) {
        ArrayList<Integer> rowList = interpretSelectCondition(selectedTable);
        selectResponse = selectedTable.tableRowsToString(selectedTable, rowList);
    }

    private boolean selectNoStarCondition(Table selectedTable) {
        //this code assumes only one attribute name to search for, but the grammar allows for a list
        int columnIndex = selectedTable.getIndexAttribute(selectAttribute);
        if(columnIndex == -1){
            throw new RuntimeException("Attribute not in table");
        }
        ArrayList<Integer> rowList = interpretSelectCondition(selectedTable);
        selectResponse = selectedTable.columnValuesToString(selectedTable, rowList, columnIndex);
        return true;
    }

    private boolean checkRepeatAttributes(ArrayList<String> attributeList){
        attributeList.replaceAll(String::toLowerCase);
        Set<String> attributeSet = new HashSet<>(attributeList);
        //if the Set is smaller than the ArrayList, there are duplicates in the ArrayList
        return attributeSet.size() < attributeList.size();
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public String getConditionComparator() {
        return conditionComparator;
    }

    private void setTableIDFromFile(Table tableToInsetInto) throws IOException {
        Table tableFromFile = new Table();
        tableFromFile = tableFromFile.storeNamedFileToTableObject(insertionTable);
        int highestCurrentID = tableFromFile.getCurrentHighestID();
        tableToInsetInto.setCurrentRecordID(highestCurrentID);
    }

    private Database createNewDatabase(){
        Database newDatabase = new Database();
        newDatabase.setDatabaseName(databaseToCreate);
        return newDatabase;
    }

    private Table createNewTable() throws IOException {
        Table newTable = new Table();
        newTable.setTableName(tableToCreate);
        if(!tableHasAttributes){
            newTable.createTableNoValues(tableToCreate);
        }
        else{
            ArrayList<String> valuesList = tableAttributes;
            ArrayList<String> valuesOriginalCase = new ArrayList<>(valuesList);
            if(checkRepeatAttributes(valuesList)){
                throw new RuntimeException("Column headers are duplicated?");
            }
            newTable.createTableWithValues(tableToCreate, valuesOriginalCase);
        }
        return newTable;
    }

    private void checkDatabaseInUse(String exceptionMessage){
        if(!checkDBExists(databaseInUse)) {
            throw new RuntimeException(exceptionMessage);
        }
    }

    private void checkTotalColumns(Table tableToInsertInto){
        int numberOfColumns = tableToInsertInto.getSumColumnsTable();
        if(numberOfColumns != insertionValues.size() + 1) { //add 1 to account for id column
            throw new RuntimeException("Attempting to insert wrong number of values?");
        }
    }

}