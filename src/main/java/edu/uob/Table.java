package edu.uob;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

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

    /*

    The first (0th) column in each table must contain a unique numerical identifier or 'primary key' (which should always be called id).
    The id value of each row will NOT be provided by the user, but rather they should be automatically generated by the server.
    It is up to you how you do this, however you should ensure that each id is unique (within the table where is resides).

    For example, the user provides a Create command:
    CREATE TABLE marks (name, mark, pass);
    Then the user inserts data:
    INSERT INTO marks VALUES ('Simon', 65, TRUE);
    Need to generate unique IDs for this row/record. It is not provided by the user

    database and table names should be case-insensitive (I think the parser already does this?)

    read table into data structure: arraylist of arraylists...

    create file writing method
    in writing method, ensure that name of table is lowercase

    create method to add ID for each row (unique per table)

    column names are case-insensitive for querying, but are preserved as written by user when writing out to file

    Note that if you encounter a tab file with invalid formatting when reading in data from the filesystem, your file
    parsing method should throw an IOException. You should however ensure that this exception is subsequently caught
    by another part of your server - remember: don't let your server crash !

    Do I need to be using Try around code that reads or writes to file

     */

    public void printStorageFolderPath(){
        System.out.println(this.storageFolderPath);
    }


    public boolean doesFileExist(String fileName) {
        File fileToOpen = new File(filePath + fileName + fileExtension);
        return fileToOpen.exists();
    }

    public void readFileToConsole(String fileName) throws IOException {
        if(doesFileExist(fileName)) {
            File fileToOpen = new File(filePath + fileName);
            FileReader reader = new FileReader(fileToOpen);
            BufferedReader buffReader = new BufferedReader(reader);
            String line;
            while ((line = buffReader.readLine()) != null) {
                System.out.println(line);
            }
            buffReader.close();
        }
        else{
            throw new IOException("File doesn't exist");
            //create file instead
        }
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
                throw new IOException("File doesn't exist purple raspberry");
                //create file instead
            }
        //System.out.println(this.filePath + fileName + this.fileExtension);

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

    //need somewhere to store current table names
    public void incrementAgeInExampleTableInDataStructure(){

    }

    public ArrayList<ArrayList<String>> getTableDataStructure(){
        //System.out.println(this.table);
        return this.tableDataStructure;
    }

    public void setTableDataStructure(ArrayList<ArrayList<String>> dataStructure){
        //
    }

    public String getTableCellValueFromDataStructure(int row, int column) {
        //check it's in bounds
        ArrayList<ArrayList<String>> tableDataStructure = getTableDataStructure();
        return tableDataStructure.get(row).get(column);
    }

    public void setTableCellValueInDataStructure(int row, int column, String input){
        //check it's in bounds
        ArrayList<ArrayList<String>> tableDataStructure = getTableDataStructure();
        tableDataStructure.get(row).set(column, input);
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
        System.out.println("data structure within insertValuesInTable method " + tableDataStructure);
        //update table's datastructure with new row
        existingTable.tableDataStructure = tableDataStructure;
        return existingTable;
    }

    //the IDs generated will only be unique while the server is up.
    // instead of doing this, create method to read in highest current ID from table on disk
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
        File tableFile = new File(filePath + tableName + fileExtension);
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

    public String wholeTableToString (Table selectedTable){
        //this methods incorrectly adds a tab at the end of each line
        ArrayList<ArrayList<String>> dataStructure = selectedTable.tableDataStructure;
        StringBuilder stringBuilder = new StringBuilder();
        for (ArrayList<String> row : dataStructure) {
            for(String token : row){
                stringBuilder.append(token);
                stringBuilder.append("\t");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
