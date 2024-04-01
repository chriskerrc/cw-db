package edu.uob;
import java.io.*;
import java.lang.reflect.Array;
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

    public boolean doesFileExist(String fileName) {
        File fileToOpen = new File(filePath + fileName + fileExtension);
        return fileToOpen.exists();
    }

    public Table storeNamedFileToTableObject(String fileName) throws IOException{
            File fileToOpen = new File(this.filePath + fileName + this.fileExtension);
            if(fileToOpen.exists()){
                this.setTableName(fileName);
                FileReader reader = new FileReader(fileToOpen);
                BufferedReader buffReader = new BufferedReader(reader);
                this.tableDataStructure = new ArrayList<>();
                String line;
                while ((line = buffReader.readLine()) != null && !line.isEmpty()) {
                    String[] rowArray = line.split("\\t"); //split on tab
                    ArrayList<String> row = fileLineToRow(rowArray);
                    this.tableDataStructure.add(row);
                }
            }
            else{
                throw new IOException("File doesn't exist");
                //create file instead
            }
        return this;
    }

    public ArrayList<String> fileLineToRow(String [] rowArray) {
        ArrayList<String> row = new ArrayList<>();
        int rowLength = rowArray.length;
        int i = 0;
        while(i < rowLength){
            row.add(rowArray[i]);
            i++;
        }
        return row;
    }


    public ArrayList<ArrayList<String>> getTableDataStructure(){
        return this.tableDataStructure;
    }


    public void createTableNoValues(String tableName) throws IOException {
        this.tableDataStructure = this.createTableDataStructureWithNoValues();
        this.writeTableToFile(tableName, false);
    }

    public void createTableWithValues(String tableName, ArrayList<String> values) throws IOException {
        this.tableDataStructure = this.createTableDataStructureWithValues(values);
        this.writeTableToFile(tableName, false);
    }

    public ArrayList<ArrayList<String>> createTableDataStructureWithNoValues(){
        ArrayList<ArrayList<String>> tableDataStructure = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        //add placeholder column heading to otherwise empty table
        row.add("id");
        tableDataStructure.add(row);
        return tableDataStructure;
    }

    public ArrayList<ArrayList<String>> createTableDataStructureWithValues(ArrayList<String> values){
        ArrayList<ArrayList<String>> tableDataStructure = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        //add placeholder column heading to otherwise empty table
        row.add("id");
        row.addAll(values);
        tableDataStructure.add(row);
        return tableDataStructure;
    }

    public Table insertValuesInTable(Table existingTable, ArrayList<String> values){
        ArrayList<ArrayList<String>> tableDataStructure = existingTable.getTableDataStructure();
        ArrayList<String> row = new ArrayList<>();
        //add ID at front of values
        String newRecordID = generateRecordID();
        row.add(0, newRecordID);
        row.addAll(values);
        tableDataStructure.add(row);
        //System.out.println("data structure within insertValuesInTable method " + tableDataStructure);
        //update table's datastructure with new row
        existingTable.tableDataStructure = tableDataStructure;
        return existingTable;
    }

    private String generateRecordID(){
        currentRecordID++;
        return Integer.toString(currentRecordID);
    }

    //the following method isn't finished
    public boolean writeEmptyTableToFile(String tableName) throws IOException {
        if(doesFileExist(tableName)){
            return false;
        }
        File fileToOpen = new File(this.filePath + tableName + this.fileExtension);
        return fileToOpen.createNewFile();
    }

    public String getTableName(){
        return tableName;
    }

    public void setTableName(String newTableName){
        tableName = newTableName;
    }

    public boolean deleteTableFile(String tableName) throws IOException {
       if(doesFileExist(tableName)){
            File tableFile = new File(filePath + tableName + fileExtension);
            return tableFile.delete();
        }
        return false; //or throw error
    }

    public boolean writeTableToFile(String tableName, boolean tableExists) throws IOException { //long method
        String lowercaseTableName = tableName.toLowerCase();
        File tableFile = new File(filePath + lowercaseTableName + fileExtension);
        if(!tableExists) {
            if (!tableFile.createNewFile()) {
                return false;
            }
        }
        FileWriter writer = new FileWriter(tableFile);
        int numberOfRows = this.tableDataStructure.size();
        int currentRow = 0;
        for (ArrayList<String> row : this.tableDataStructure) {
            int numberOfColumns = row.size();
            int currentColumn = 0;
            for (String string : row) {
                writer.write(string);
                currentColumn++;
                if(currentColumn < numberOfColumns) {
                    writer.write("\t");
                }
            }
            currentRow++;
            if(currentRow < numberOfRows) {
                writer.write("\n");
            }
        }
        writer.flush();
        writer.close();
        return true;
    }

    //these two methods seem to add an extra blank line at the end of string: something to do with \n
    //deeply nested method
    public String tableRowsToString (Table selectedTable, ArrayList<Integer> rowsToInclude){
        rowsToInclude.add(0, 0); //always include header row at start of list
        ArrayList<ArrayList<String>> dataStructure = selectedTable.tableDataStructure;
        StringBuilder stringBuilder = new StringBuilder();
        for(int rowIndex : rowsToInclude) {
            if (rowIndex >= 0 && rowIndex < dataStructure.size()) {
                ArrayList<String> row = dataStructure.get(rowIndex);
                int numberOfColumns = row.size();
                int currentColumn = 0;
                for (String token : row) {
                    stringBuilder.append(token);
                    if (currentColumn < numberOfColumns - 1) {
                        stringBuilder.append("\t");
                    }
                    currentColumn++;
                }
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public String valuesInColumnToString (Table selectedTable, ArrayList<Integer> rowsToInclude, int columnIndex){
        rowsToInclude.add(0, 0); //always include header row at start of list
        ArrayList<ArrayList<String>> dataStructure = selectedTable.tableDataStructure;
        StringBuilder stringBuilder = new StringBuilder();
        for(int rowIndex : rowsToInclude) {
            if (rowIndex >= 0 && rowIndex < dataStructure.size()) {
                ArrayList<String> row = dataStructure.get(rowIndex);
                stringBuilder.append(row.get(columnIndex));
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public ArrayList<Integer> populateListOfRowsForWholeTable (){
        ArrayList<ArrayList<String>> dataStructure = this.getTableDataStructure();
        int maximumRow = dataStructure.size();
        ArrayList<Integer> listOfRows = new ArrayList<>();
        int row = 1; //skip 0, because this is always added by tableRowsToString
        while(row < maximumRow){
            listOfRows.add(row);
            row++;
        }
        return listOfRows;
    }

    public int getIndexAttributeName(String attributeName) {
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<String> tableHeader = dataStructure.get(0);
        for(int headerIndex = 0; headerIndex < tableHeader.size(); headerIndex++){
            if(tableHeader.get(headerIndex).equalsIgnoreCase(attributeName)){
                return headerIndex;
            }
        }
        return -1;
    }

    //reduce repeated code across these two methods (hard to do this without triply nested statements but take the hit)
    public ArrayList<Integer> getRowsValueIn(int columnIndex, String conditionValue){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<Integer> rowsToInclude = new ArrayList<>();
        int totalRows = dataStructure.size();
        int row = 1; //skip header row
        while(row < totalRows){
            if(Objects.equals(dataStructure.get(row).get(columnIndex), conditionValue)){
                rowsToInclude.add(row);
            }
            row++;
        }
        return rowsToInclude;
    }

    public ArrayList<Integer> getRowsValueNotIn(ArrayList<Integer> rowsValueIsIn){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<Integer> rowsToInclude = new ArrayList<>();
        int totalRows = dataStructure.size();
        int row = 1; //skip header row
        while(row < totalRows){
            if(!rowsValueIsIn.contains(row)){
                rowsToInclude.add(row);
            }
            row++;
        }
        return rowsToInclude;
    }

    public ArrayList<Integer> getRowsValueGreaterOrLessThan(int columnIndex) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String conditionValue = databaseManager.getConditionValue();
        String conditionComparator = databaseManager.getConditionComparator();
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<Integer> rowsToInclude = new ArrayList<>();
        int totalRows = dataStructure.size();
        int row = 1; //skip header row
        while (row < totalRows) {
            try{ //try catch number format exceptions
            if (Objects.equals(conditionComparator, ">")) {
                if (Integer.parseInt(dataStructure.get(row).get(columnIndex)) > Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(row);
                }
            }
            if (Objects.equals(conditionComparator, "<")) {
                if (Integer.parseInt(dataStructure.get(row).get(columnIndex)) < Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(row);
                }
            }
            if (Objects.equals(conditionComparator, ">=")) {
                if (Integer.parseInt(dataStructure.get(row).get(columnIndex)) >= Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(row);
                }
            }
            if (Objects.equals(conditionComparator, "<=")) {
                if (Integer.parseInt(dataStructure.get(row).get(columnIndex)) <= Integer.parseInt(conditionValue)) {
                    rowsToInclude.add(row);
                }
            }
            } catch (NumberFormatException exception) {
                return new ArrayList<>(); //return empty ArrayList so no data rows included in response
            }
            row++;
        }
        return rowsToInclude;
    }

    public ArrayList<Integer> getRowsValueLike(int columnIndex){
        ArrayList<ArrayList<String>> dataStructure = this.tableDataStructure;
        ArrayList<Integer> rowsToInclude = new ArrayList<>();
        int totalRows = dataStructure.size();
        int row = 1; //skip header row
        while(row < totalRows){
            if(stringContainsCharacter(dataStructure.get(row).get(columnIndex))){
                rowsToInclude.add(row);
            }
            row++;
        }
        return rowsToInclude;
    }

    public boolean stringContainsCharacter(String token) {
    DatabaseManager databaseManager = DatabaseManager.getInstance();
    String conditionValue = databaseManager.getConditionValue();
        if(conditionValue.length() > 2 && conditionValue.startsWith("'") && conditionValue.endsWith("'")) { //magic number
            conditionValue =  conditionValue.substring(1, conditionValue.length() - 1);
        }
        return token.contains(conditionValue);
    }

    public int getNumberColumnsTable(){
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
}
