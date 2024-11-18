package edu.uob;
import java.io.*;
import java.util.*;

public class Table {

    private ArrayList<ArrayList<String>> tableDataStructure;
    private String storageFolderPath;
    private String filePath;

    private final String fileExtension = ".tab";

    private String tableName;

    static private int currentRecordID;

    public Table(){
        DBServer dbServer = new DBServer();
        storageFolderPath = dbServer.getStorageFolderPath();
        tableDataStructure = new ArrayList<>();
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String databaseInUse = databaseManager.getDatabaseInUse();
        filePath = storageFolderPath + File.separator + databaseInUse + File.separator;
        currentRecordID = 0;
    }

    public Table storeFileToTable(String fileName) throws IOException{
            File fileToOpen = new File(this.filePath + fileName + this.fileExtension);
            if(!fileToOpen.exists()) {
                throw new IOException("File doesn't exist");
            }
            this.setTableName(fileName);
            FileReader fileReader = new FileReader(fileToOpen);
            BufferedReader buffReader = new BufferedReader(fileReader);
            this.tableDataStructure = new ArrayList<>();
            String line;
            while ((line = buffReader.readLine()) != null && !line.isEmpty()) {
                String[] rowArray = line.split("\\t"); //split on tab
                ArrayList<String> currentRow = fileLineToRow(rowArray);
                this.tableDataStructure.add(currentRow);
            }
        return this;
    }

    public void createTableNoValues(String tableName, Boolean isWrite) throws IOException {
        this.tableDataStructure = this.createDataStructure(false, null);
        if(isWrite){
            this.writeTableToFile(tableName, false);
        }
    }

    public void createTableHasValues(String tableName, ArrayList<String> values) throws IOException {
        this.tableDataStructure = this.createDataStructure(true, values);
        this.writeTableToFile(tableName, false);
    }

    public void alterTableColumn(String columnToAlter, boolean isAdd) throws IOException {
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> headerRow = dataStructure.get(0);
        int newColumnIndex = headerRow.size();
        int totalRows = this.tableDataStructure.size();
        if(isAdd) {
            this.tableDataStructure.get(0).add(newColumnIndex, columnToAlter);
            //add empty values in new column
            for(int row = 1; row < totalRows ; row++) {
                this.tableDataStructure.get(row).add(newColumnIndex, "");
            }
        }
        else{
            int indexColumnRemove = getIndexOfColumn(columnToAlter);
            //iterate through all rows, and remove the nth value
            for(int row = 0; row < totalRows ; row++) {
                this.tableDataStructure.get(row).remove(indexColumnRemove);
            }
        }
        this.writeTableToFile(this.getTableName(), true);
    }

    public void addJoinColumns(ArrayList<String> joinColumns){
        for(String column : joinColumns){
            this.tableDataStructure.get(0).add(column);
        }
    }

    public String getValueAttribute1InJoinTable1(int rowIndex, int columnIndexAttribute){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        return dataStructure.get(rowIndex).get(columnIndexAttribute);
    }

    public int getColumnIndexJoinAttribute(String attribute){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> headerRow = dataStructure.get(0);
        if(!headerRow.contains(attribute)){
            return -1; //panic
        }
        return headerRow.indexOf(attribute);
    }

    public ArrayList<String> getJoinValues(int rowIndex, int attributeColIndex){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> tableRow = dataStructure.get(rowIndex);
        Set<Integer> indicesToExclude = new HashSet<>();
        indicesToExclude.add(0);
        indicesToExclude.add(attributeColIndex);
        return filterExcludedValues(tableRow, indicesToExclude);
    }

    private ArrayList<String> filterExcludedValues(ArrayList<String> originalRow, Set<Integer> excludeList){
        ArrayList<String> filteredRow = new ArrayList<>();
        for(int i = 0; i < originalRow.size(); i++){
            if(!excludeList.contains(i)){
                filteredRow.add(originalRow.get(i));
            }
        }
        return filteredRow;
    }

    public int getRowIndexForJoin(String attr1Value, int attr2ColIndex){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        for(int tableRow = 0; tableRow < dataStructure.size(); tableRow++){
            if(Objects.equals(dataStructure.get(tableRow).get(attr2ColIndex), attr1Value)){
                return tableRow;
            }
        }
        return -1;
    }


    public void insertValuesInTable(Table existingTable, ArrayList<String> tableValues){
        ArrayList<ArrayList<String>> tableDataStructure = this.tableDataStructure;
        ArrayList<String> currentRow = new ArrayList<>();
        String newRecordID = generateRecordID();
        currentRow.add(0, newRecordID);
        currentRow.addAll(tableValues);
        tableDataStructure.add(currentRow);
        existingTable.tableDataStructure = tableDataStructure;
    }

    public int getNumberOfRows(){
        ArrayList<ArrayList<String>> tableDataStructure = this.tableDataStructure;
        return tableDataStructure.size();
    }

    public String getTableName(){
        return tableName;
    }

    public void setTableName(String newTableName){
        tableName = newTableName;
    }

    public boolean writeTableToFile(String tableName, boolean tableExists) throws IOException {
        String lowercaseTableName = tableName.toLowerCase();
        File tableFile = new File(filePath + lowercaseTableName + fileExtension);
        if(!tableExists && !tableFile.createNewFile()) {
            return false;
        }
        FileWriter fileWriter = new FileWriter(tableFile);
        writeTableDataToFile(fileWriter);
        fileWriter.flush();
        fileWriter.close();
        return true;
    }

    public String tableRowsToString (Table selectedTable, ArrayList<Integer> rowsToInclude){
        rowsToInclude.add(0, 0); //always include header row at start of list
        ArrayList<ArrayList<String>> dataStructure = selectedTable.tableDataStructure;
        StringBuilder stringBuilder = new StringBuilder();
        for(int rowIndex : rowsToInclude) {
            if (rowIndex >= 0 && rowIndex < dataStructure.size()) {
                ArrayList<String> currentRow = dataStructure.get(rowIndex);
                int totalColumns = currentRow.size();
                int currentColumn = 0;
                tokensToString(currentRow, stringBuilder, currentColumn, totalColumns);
            }
        }
        return stringBuilder.toString();
    }

    public String columnValuesToString (Table selectedTable, ArrayList<Integer> rowsToInclude, int columnIndex){
        rowsToInclude.add(0, 0); //always include header row at start of list
        ArrayList<ArrayList<String>> dataStructure = selectedTable.tableDataStructure;
        StringBuilder stringBuilder = new StringBuilder();
        for(int rowIndex : rowsToInclude) {
            if (rowIndex >= 0 && rowIndex < dataStructure.size()) {
                ArrayList<String> currentRow = dataStructure.get(rowIndex);
                stringBuilder.append(currentRow.get(columnIndex));
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public ArrayList<Integer> fillListTableRows (){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        int maximumRow = dataStructure.size();
        ArrayList<Integer> listOfRows = new ArrayList<>();
        int currentRow = 1; //skip 0, because this is always added by tableRowsToString
        while(currentRow < maximumRow){
            listOfRows.add(currentRow);
            currentRow++;
        }
        return listOfRows;
    }

    public int getIndexAttribute(String attributeName) {
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> tableHeader = dataStructure.get(0);
        for(int headerIndex = 0; headerIndex < tableHeader.size(); headerIndex++){
            if(tableHeader.get(headerIndex).equalsIgnoreCase(attributeName)){
                return headerIndex;
            }
        }
        return -1;
    }

    public ArrayList<Integer> getRowsValueIn(int columnIndex, String conditionValue, boolean includeRow){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<Integer> rowsToInclude = new ArrayList<>();
        int totalRows = dataStructure.size();
        int currentRow = 1; //skip header row
        while(currentRow < totalRows){
            if(includeRow){
               if(Objects.equals(dataStructure.get(currentRow).get(columnIndex), conditionValue)){
                 rowsToInclude.add(currentRow);
               }
            }
            else{
                if(!Objects.equals(dataStructure.get(currentRow).get(columnIndex), conditionValue)){
                    rowsToInclude.add(currentRow);
                }
            }
            currentRow++;
        }
        return rowsToInclude;
    }

    public ArrayList<Integer> getComparatorRows(int columnIndex) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String conditionValue = databaseManager.getConditionValue();
        String conditionComparator = databaseManager.getConditionComparator();
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<Integer> rowsToInclude = new ArrayList<>();
        int totalRows = dataStructure.size();
        int currentRow = 1; //skip header row
        while (currentRow < totalRows) {
            try{
            if (Objects.equals(conditionComparator, ">")) {
                if (Integer.parseInt(dataStructure.get(currentRow).get(columnIndex)) > Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(currentRow);
                }
            }
            if (Objects.equals(conditionComparator, "<")) {
                if (Integer.parseInt(dataStructure.get(currentRow).get(columnIndex)) < Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(currentRow);
                }
            }
            if (Objects.equals(conditionComparator, ">=")) {
                if (Integer.parseInt(dataStructure.get(currentRow).get(columnIndex)) >= Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(currentRow);
                }
            }
            if (Objects.equals(conditionComparator, "<=")) {
                if (Integer.parseInt(dataStructure.get(currentRow).get(columnIndex)) <= Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(currentRow);
                }
            }
            } catch (NumberFormatException exception) {
                return new ArrayList<>(); //return empty ArrayList so no data rows included in response
            }
            currentRow++;
        }
        return rowsToInclude;
    }

    public ArrayList<Integer> getRowsValueLike(int columnIndex){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<Integer> rowsToInclude = new ArrayList<>();
        int totalRows = dataStructure.size();
        int currentRow = 1; //skip header row
        while(currentRow < totalRows){
            if(stringContainsCharacter(dataStructure.get(currentRow).get(columnIndex))){
                rowsToInclude.add(currentRow);
            }
            currentRow++;
        }
        return rowsToInclude;
    }

    public int getTotalColumns(){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> columnHeaderRow = dataStructure.get(0);
        return columnHeaderRow.size();
    }

    public int getCurrentHighestID(){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        int totalRows = dataStructure.size();
        int highestID = 0;
        for(int i = 1; i < totalRows; i++){ //skip header row
            if(Integer.parseInt(dataStructure.get(i).get(0)) > highestID){
                highestID = Integer.parseInt(dataStructure.get(i).get(0));
            }
        }
        return highestID;
    }

    public void setCurrentRecordID(int newID){
        currentRecordID = newID;
    }

    public boolean columnExistsInTable(String columnName){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> headerRow = dataStructure.get(0);
        for(String row : headerRow){
            if(Objects.equals(row, columnName)){
                return true;
            }
        }
        return false;
    }

    public boolean deleteTableFile(String tableName){
        String lowercaseTableName = tableName.toLowerCase();
        File tableFile = new File(filePath + lowercaseTableName + fileExtension);
        return tableFile.delete();
    }

    public void deleteTableRows (ArrayList<Integer> rowsToDelete) throws IOException {
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        //reverse order to ensure indices stay correct after deletion
        rowsToDelete.sort(Collections.reverseOrder());
        for(Integer tableRow : rowsToDelete){
            if(tableRow >= 0 && tableRow <= dataStructure.size()) {
                dataStructure.remove((int) tableRow);
            }
        }
        this.writeTableToFile(this.getTableName(), true);
    }

    public boolean updateTableRows(ArrayList<Integer> rowsToUpdate, String columnName, String newValue) throws IOException {
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        //get the row index of row to update
        int rowIndex = rowsToUpdate.get(0);
        //get the row we want to update
        ArrayList<String> rowToUpdate = dataStructure.get(rowIndex);
        int colIndexToUpdate = -1;
        //find column index to update
        for(int colIndex = 0; colIndex < rowToUpdate.size(); colIndex++){
            if(columnName.equalsIgnoreCase(dataStructure.get(0).get(colIndex))){
                colIndexToUpdate = colIndex;
            }
        }
        //couldn't find columnName in table
        if(colIndexToUpdate == -1){
            return false;
        }
        //otherwise, replace the value in cell at column found above
        rowToUpdate.set(colIndexToUpdate, newValue);
        //add this row to the table again
        dataStructure.set(rowIndex, rowToUpdate);
        this.writeTableToFile(this.getTableName(), true);
        return true;
    }

    public ArrayList<String> getJoinColumns (String joinAttribute){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> topTableRow = dataStructure.get(0);
        ArrayList<String> columnNamesForJoin = new ArrayList<>();
        for(int i = 1; i < topTableRow.size(); i++){
            String tableColumn = topTableRow.get(i);
            if(!Objects.equals(tableColumn, joinAttribute)){
                columnNamesForJoin.add(tableColumn);
            }
        }
        return columnNamesForJoin;
    }

    //private methods

    private int getIndexOfColumn(String columnName){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> headerRow = dataStructure.get(0);
        for(String row : headerRow){
            if(Objects.equals(row, columnName)){
                return headerRow.indexOf(row);
            }
        }
        return -1;
    }

    private ArrayList<ArrayList<String>> createDataStructure(boolean hasValues, ArrayList<String> tableValues){
        ArrayList<ArrayList<String>> tableDataStructure = new ArrayList<>();
        ArrayList<String> currentRow = new ArrayList<>();
        currentRow.add("id"); //add placeholder column heading to otherwise empty table
        if(hasValues) {
            currentRow.addAll(tableValues);
        }
        tableDataStructure.add(currentRow);
        return tableDataStructure;
    }

    private boolean stringContainsCharacter(String currentToken) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String conditionValue = databaseManager.getConditionValue();
        int minimumLength = 2;
        if(conditionValue.length() > minimumLength && conditionValue.startsWith("'") && conditionValue.endsWith("'")) {
            conditionValue =  conditionValue.substring(1, conditionValue.length() - 1);
        }
        return currentToken.contains(conditionValue);
    }

    private void tokensToString(ArrayList<String> tableRow, StringBuilder strBuilder, int currentColumn, int totalColumns){
        for (String token : tableRow) {
            strBuilder.append(token);
            if (currentColumn < totalColumns - 1) {
                strBuilder.append("\t");
            }
            currentColumn++;
        }
        strBuilder.append("\n");
    }

    private void writeTableDataToFile (FileWriter fileWriter) throws IOException {
        int totalRows = this.tableDataStructure.size();
        int currentRow = 0;
        for (ArrayList<String> tableRow : this.tableDataStructure) {
            int totalColumns = tableRow.size();
            int currentColumn = 0;
            for (String string : tableRow) {
                fileWriter.write(string);
                currentColumn++;
                if(currentColumn < totalColumns) {
                    fileWriter.write("\t");
                }
            }
            currentRow++;
            if(currentRow < totalRows) {
                fileWriter.write("\n");
            }
        }
    }

    private ArrayList<String> fileLineToRow(String [] rowArray) {
        ArrayList<String> currentRow = new ArrayList<>();
        int rowLength = rowArray.length;
        int characterIndex = 0;
        while(characterIndex < rowLength){
            currentRow.add(rowArray[characterIndex]);
            characterIndex++;
        }
        return currentRow;
    }

    private String generateRecordID(){
        currentRecordID++;
        return Integer.toString(currentRecordID);
    }

}
