package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.*;

//Singleton class to manage databases as single source of truth and contain interpreter methods
public class DatabaseManager {

	static private DatabaseManager instance = null;

	static private final ArrayList<Database> databasesList = new ArrayList<>();

	static private String databaseInUse;

	static private String databaseToCreate;

	static private boolean tableHasAttributes;

	static private String tableToCreate;

	static private String insertionTable;

	static private ArrayList<String> tableAttributes;

	static private ArrayList<String> insertionValues;

	static private boolean hasAsterisk;

	static private boolean hasCondition;

	static private boolean isAddAlter;

	static private String selectResponse;

	static private String joinResponse;

	static private String tableToSelect;

	static private String tableToAlter;

	static private String columnToAlter;

	static private String conditionAttribute;

	static private String conditionComparator;
	static private String conditionValue;

	static private String selectAttribute;

	static private String tableToDrop;

	static private String databaseToDrop;

	static private String tableToDeleteFrom;

	static private String columnToUpdate;

	static private String newUpdatedValue;

	static private String tableToUpdate;

	static private String joinTableName1;

	static private String joinTableName2;

	static private String joinAttribute1;

	static private String joinAttribute2;

	private DatabaseManager() {
	}

	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}

	//Getters and setters

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

	public void setTableToAlter(String tableName){
		tableToAlter = tableName;
	}

	public void setTableToDeleteFrom(String tableName){
		tableToDeleteFrom = tableName;
	}

	public void setColumnToAlter(String tableName){
		columnToAlter = tableName;
	}

	public void setAddAlter(boolean isAdd){
		isAddAlter = isAdd;
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

	public String getJoinResponse(){
		return joinResponse;
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

	public String getConditionValue() {
		return conditionValue;
	}

	public void setTableToDrop(String tableName){
		tableToDrop = tableName;
	}

	public void setDatabaseToDrop(String databaseName){
		databaseToDrop = databaseName;
	}

	public String getConditionComparator() {
		return conditionComparator;
	}

	public void setColumnToUpdate(String columnName) {
		columnToUpdate = columnName;
	}

	public void setUpdateNewValue(String newValue) {
		newUpdatedValue = newValue;
	}

	public void setTableToUpdate(String tableName) { tableToUpdate = tableName; }

	public void setJoinTables(String tableName1, String tableName2) {
		joinTableName1 = tableName1;
		joinTableName2 = tableName2;
	}

	public void setJoinAttributes(String attribute1, String attribute2){
		joinAttribute1 = attribute1;
		joinAttribute2 = attribute2;
	}

	//Interpreter methods
	public boolean interpretCreateDatabase() {
		if(checkDBExistsInMemory(databaseToCreate)) {
			throw new RuntimeException("Trying to create a database that already exists?");
		}
		Database newDatabase = createNewDatabase();
		addDatabaseToList(newDatabase);
		return newDatabase.createDBDirectory(databaseToCreate);
	}

	public boolean interpretUseDatabase() throws IOException {
		//check if database is in memory
		if(checkDatabaseInUse("Placeholder")){
			return true;
		}
		//if it's not, check if it's on file and try to load it
		Database loadedDatabase = new Database();
		String[] filesList = loadedDatabase.getFilesInDBFolder(databaseInUse);
		if(filesList == null){
			throw new RuntimeException("Failed to load database");
		}
		loadedDatabase.loadTablesToDatabase(filesList);
		loadedDatabase.setDatabaseName(databaseInUse);
		addDatabaseToList(loadedDatabase);
		return true;
	}

	public boolean interpretCreateTable() throws IOException{
		checkDatabaseInUse("Database doesn't exist? Try creating it");
		Database currentDatabase = getDatabase(databaseInUse);
		assert currentDatabase != null;
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
		assert currentDatabase != null;
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
		assert currentDatabase != null;
		if (!currentDatabase.tableExistsInDB(tableToSelect)) {
			throw new RuntimeException("Selected table doesn't exist");
		}
		Table selectedTable = currentDatabase.getTableFromDatabase(tableToSelect);
		if (hasAsterisk && !hasCondition) {
			setWholeTableResponse(selectedTable, false);
			return true;
		}
		if (hasAsterisk) {
			selectStarCondition(selectedTable);
			return true;
		}
		if (hasCondition) {
			return selectNoStarCondition(selectedTable);
		}
		return false;
	}

	public boolean interpretAlter() throws IOException {
		Database currentDatabase = getDatabase(databaseInUse);
		assert currentDatabase != null;
		if(!currentDatabase.tableExistsInDB(tableToAlter)){
			return false;
		}
		Table alteredTable = currentDatabase.getTableFromDatabase(tableToAlter);
		if(isAddAlter){
			//check table doesn't already have a column of name specified in command
			if(alteredTable.columnExistsInTable(columnToAlter)){
				return false;
			}
			//add column
			alteredTable.alterTableColumn(columnToAlter, true);
		}
		else{
			//prevent removal of id column
			if(columnToAlter.equalsIgnoreCase("id")){
				return false;
			}
			//check table has column of name specified in command
			if(!alteredTable.columnExistsInTable(columnToAlter)){
				return false;
			}
			//remove column
			alteredTable.alterTableColumn(columnToAlter, false);
		}
		return true;
	}

	public boolean interpretDropTable() throws IOException {
		Database currentDatabase = getDatabase(databaseInUse);
		assert currentDatabase != null;
		if(!currentDatabase.tableExistsInDB(tableToDrop)){
			return false;
		}
		//delete table file
		Table droppableTable = currentDatabase.getTableFromDatabase(tableToDrop);
		if(!droppableTable.deleteTableFile(tableToDrop)){
			return false;
		}
		//delete table from memory
		currentDatabase.dropTableFromDatabase(tableToDrop);
		return true;
	}

	//assumes we can only drop a database in use
    public boolean interpretDropDatabase() throws IOException {
        Database currentDatabase = getDatabase(databaseInUse);
        assert currentDatabase != null;
        //check that database exists in memory
        if(!this.checkDBExistsInMemory(databaseInUse)){
            return false;
        }
		//check that databaseToDrop matches databaseInUse
		if(!Objects.equals(databaseToDrop, databaseInUse)){
			return false;
		}
        //delete database directory including all files within it
        deleteDatabaseDirectory();
        //delete database from memory
        deleteDatabaseFromMemory(databaseInUse);
        return true;
    }

	public boolean interpretDelete() throws IOException {
		Table activeTable = getTableToModify(tableToDeleteFrom);
		//get the rows that apply to condition
		ArrayList<Integer> rowsUnderCondition = getRowsIncludeCondition(activeTable);
		//delete those rows
		activeTable.deleteTableRows(rowsUnderCondition);
		return true;
	}

	public boolean interpretUpdate() throws IOException {
		Table activeTable = getTableToModify(tableToUpdate);
		//get the rows that apply to condition
		ArrayList<Integer> rowsUnderCondition = getRowsIncludeCondition(activeTable);
		//panic if there's more than one row to update (not implementing multiple rows for now)
		if (rowsUnderCondition.size() != 1){
			return false;
		}
		return activeTable.updateTableRows(rowsUnderCondition, columnToUpdate, newUpdatedValue);
	}

	public boolean interpretJoin() throws IOException {
		return createJoinTable();
	}

	//Private methods

	private Table getTableToModify (String modifiedTableName){
		checkDatabaseInUse("Database doesn't exist or not in USE?");
		Database currentDatabase = getDatabase(databaseInUse);
		assert currentDatabase != null;
		if (!currentDatabase.tableExistsInDB(modifiedTableName)) {
			throw new RuntimeException("Table that you're trying to modify doesn't exist");
		}
        return currentDatabase.getTableFromDatabase(modifiedTableName);
	}

	private ArrayList<Integer> getRowsIncludeCondition(Table table) {
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

	private void setWholeTableResponse(Table outputTable, boolean isJoin) {
		ArrayList<Integer> rowList = outputTable.fillListTableRows();
		if(!isJoin) {
			selectResponse = outputTable.tableRowsToString(outputTable, rowList);
			return;
		}
		joinResponse = outputTable.tableRowsToString(outputTable, rowList);
	}

	private void selectStarCondition(Table selectedTable) {
		ArrayList<Integer> rowList = getRowsIncludeCondition(selectedTable);
		selectResponse = selectedTable.tableRowsToString(selectedTable, rowList);
	}

	private boolean selectNoStarCondition(Table selectedTable) {
		//this code assumes only one attribute name to search for, but the grammar allows for a list
		int columnIndex = selectedTable.getIndexAttribute(selectAttribute);
		if(columnIndex == -1){
			throw new RuntimeException("Attribute not in table");
		}
		ArrayList<Integer> rowList = getRowsIncludeCondition(selectedTable);
		selectResponse = selectedTable.columnValuesToString(selectedTable, rowList, columnIndex);
		return true;
	}

	private boolean checkRepeatAttributes(ArrayList<String> attributeList){
		attributeList.replaceAll(String::toLowerCase);
		Set<String> attributeSet = new HashSet<>(attributeList);
		//if the Set is smaller than the ArrayList, there are duplicates in the ArrayList
		return attributeSet.size() < attributeList.size();
	}

	private void setTableIDFromFile(Table tableToInsetInto) throws IOException {
		Table tableFromFile = new Table();
		tableFromFile = tableFromFile.storeFileToTable(insertionTable);
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
			newTable.createTableNoValues(tableToCreate, true);
		}
		else{
			ArrayList<String> valuesList = tableAttributes;
			ArrayList<String> valuesOriginalCase = new ArrayList<>(valuesList);
			if(checkRepeatAttributes(valuesList)){
				throw new RuntimeException("Column headers are duplicated?");
			}
			newTable.createTableHasValues(tableToCreate, valuesOriginalCase);
		}
		return newTable;
	}

	private boolean createJoinTable() throws IOException {
		Table joinTable = new Table();
		//create table in memory with id
		joinTable.createTableNoValues("joinTable", false);
		Database currentDatabase = getDatabase(databaseInUse);
		if(currentDatabase == null){
			return false;
		}
		if(!this.checkDBExistsInMemory(databaseInUse)){
			return false;
		}
		Table firstJoinTable = currentDatabase.getTableFromDatabase(joinTableName1);
		Table secondJoinTable = currentDatabase.getTableFromDatabase(joinTableName2);
		if(firstJoinTable == null || secondJoinTable == null){
			return false;
		}
		ArrayList<String> joinColumns = buildJoinColumns(firstJoinTable, secondJoinTable);
		if(joinColumns.isEmpty()){
			return false;
		}
		joinTable.addJoinColumns(joinColumns);
		int sizeTable1 = firstJoinTable.getNumberOfRows();
		if(sizeTable1 == 0){
			return false;
		}
		//for every row in joinTable1
		for(int rowIndex = 1; rowIndex < sizeTable1; rowIndex++) {
			ArrayList<String> joinRow = buildJoinRow(firstJoinTable, secondJoinTable, rowIndex);
			if(joinRow == null){
				return false;
			}
			joinTable.insertValuesInTable(joinTable, joinRow);
		}
		setWholeTableResponse(joinTable, true);
		return true;
	}

	private ArrayList<String> buildJoinColumns(Table firstJoinTable, Table secondJoinTable){
		ArrayList<String> tableOneColumnsForJoin = firstJoinTable.getJoinColumns(joinAttribute1);
		ArrayList<String> tableTwoColumnsForJoin = secondJoinTable.getJoinColumns(joinAttribute2);
		ArrayList<String> joinColumns = new ArrayList<>();
		for(String tableColumn : tableOneColumnsForJoin){
            joinColumns.add(joinTableName1 + "." + tableColumn);
		}
		for(String tableColumn : tableTwoColumnsForJoin){
			joinColumns.add(joinTableName2 + "." + tableColumn);
		}
		return joinColumns;
	}

	private ArrayList<String> buildJoinRow(Table firstJoinTable, Table secondJoinTable, int rowIndex){
		int colIndexAttr1 = firstJoinTable.getColumnIndexJoinAttribute(joinAttribute1);
		String valueAttr1 = firstJoinTable.getValueAttribute1InJoinTable1(rowIndex, colIndexAttr1);
		ArrayList<String> joinValuesRow = firstJoinTable.getJoinValues(rowIndex, colIndexAttr1);
		int colIndexAttr2 = secondJoinTable.getColumnIndexJoinAttribute(joinAttribute2);
		int rowIndexTable2 = secondJoinTable.getRowIndexForJoin(valueAttr1, colIndexAttr2);
		if(rowIndexTable2 == -1) {
			return null;
		}
		ArrayList<String> joinValuesTable2 = secondJoinTable.getJoinValues(rowIndexTable2, colIndexAttr2);
		joinValuesRow.addAll(joinValuesTable2);
		return joinValuesRow;
	}

	private boolean checkDatabaseInUse(String exceptionMessage){
		return checkDBExistsInMemory(databaseInUse);
	}

	private boolean checkDBExistsInMemory(String databaseName){
		for (Database database : databasesList) {
			if (database.getDatabaseName().equalsIgnoreCase(databaseName)) {
				return true;
			}
		}
		return false;
	}

	private void checkTotalColumns(Table tableToInsertInto){
		int numberOfColumns = tableToInsertInto.getTotalColumns();
		if(numberOfColumns != insertionValues.size() + 1) { //add 1 to account for id column
			throw new RuntimeException("Attempting to insert wrong number of values?");
		}
	}

	private void addDatabaseToList(Database newDatabase){
		databasesList.add(newDatabase);
		databasesList.indexOf(newDatabase);
	}

	private Database getDatabase(String databaseName){
		for (Database databaseInList : databasesList) {
			if (databaseInList.getDatabaseName().equalsIgnoreCase(databaseName)) {
				return databaseInList;
			}
		}
		return null;
	}

    private void deleteDatabaseFromMemory(String databaseName){
        databasesList.removeIf(database -> Objects.equals(database.databaseName, databaseName));
    }

    private void deleteDatabaseDirectory(){
        Database currentDatabase = getDatabase(databaseInUse);
        assert currentDatabase != null;
        String filePath = currentDatabase.getFilePath();
        //check database directory exists
        if(!currentDatabase.checkFolderExists(databaseInUse)){
            return;
        }
        //delete table files in database directory
        String lowercaseDatabaseName = databaseInUse.toLowerCase();
        File directoryToDelete = new File(filePath + lowercaseDatabaseName);
        for(File file: Objects.requireNonNull(directoryToDelete.listFiles())){
            if(!file.delete()){
                return;
            }
        }
        //delete database directory
		directoryToDelete.delete();
	}



}