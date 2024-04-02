package edu.uob;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

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

    public void createTableNoValues(String tableName) throws IOException {
        this.tableDataStructure = this.createDataStructure(false, null);
        this.writeTableToFile(tableName, false);
    }

    public void createTableHasValues(String tableName, ArrayList<String> values) throws IOException {
        this.tableDataStructure = this.createDataStructure(true, values);
        this.writeTableToFile(tableName, false);
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
    public void insertValuesInTable(Table existingTable, ArrayList<String> tableValues){
        ArrayList<ArrayList<String>> tableDataStructure = this.tableDataStructure;
        ArrayList<String> currentRow = new ArrayList<>();
        String newRecordID = generateRecordID();
        currentRow.add(0, newRecordID);
        currentRow.addAll(tableValues);
        tableDataStructure.add(currentRow);
        existingTable.tableDataStructure = tableDataStructure;
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

    //private methods

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
