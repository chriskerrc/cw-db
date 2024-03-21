package edu.uob;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;

public class Table {

    private ArrayList<ArrayList<String>> tableDataStructure;
    private String storageFolderPath;
    private String filePath;
    private final String fileExtension = ".tab";

    private String tableName;

    public Table(){
        DBServer dbServer = new DBServer();
        storageFolderPath = dbServer.getStorageFolderPath();
        tableDataStructure = new ArrayList<>();
        //hardcoding inUseFolder to "people" for now. Need to get it instead
        filePath = storageFolderPath + File.separator + "people" + File.separator;
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

    //copied and pasted code across these two methods
    public void storeFileToDataStructure(String fileName) throws IOException{
        if(doesFileExist(fileName)) {
            File fileToOpen = new File(filePath + fileName);
            FileReader reader = new FileReader(fileToOpen);
            BufferedReader buffReader = new BufferedReader(reader);
            tableDataStructure = new ArrayList<>();
            String line;
            while ((line = buffReader.readLine()) != null && !line.isEmpty()) {
                String[] rowArray = line.split("\\t"); //split on tab
                ArrayList<String> row = fileLineToRow(rowArray);
                tableDataStructure.add(row);
            }
            System.out.println(tableDataStructure);
        }
        else{
            throw new IOException("File doesn't exist");
            //create file instead
        }
    }

    public Table storeNamedFileToTableObject(String fileName) throws IOException{
        if(doesFileExist(fileName)) {
            Table table = new Table();
            File fileToOpen = new File(filePath + fileName + fileExtension);
            FileReader reader = new FileReader(fileToOpen);
            BufferedReader buffReader = new BufferedReader(reader);
            tableDataStructure = new ArrayList<>();
            String line;
            while ((line = buffReader.readLine()) != null && !line.isEmpty()) {
                String[] rowArray = line.split("\\t"); //split on tab
                ArrayList<String> row = fileLineToRow(rowArray);
                tableDataStructure.add(row);
            }
            return table;
        }
        else{
            throw new IOException("File doesn't exist");
            //create file instead
        }
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

    public void createTableNoValues(String tableName){
        //create table datastructure instance
        //add ID column
        //create file tableName.tab
        //write data to file

    }

    public ArrayList<ArrayList<String>> createTableDataStructure(){
        tableDataStructure = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        //add placeholder column heading to otherwise empty table
        row.add("id");
        tableDataStructure.add(row);
        return tableDataStructure;
    }

    //the following method isn't finished
    public boolean writeTableToFile(String tableName, ArrayList<ArrayList<String>> table) throws IOException {
        if(!doesFileExist(tableName)){
            return false;
        }
        File fileToOpen = new File(filePath + tableName + fileExtension);
        if(!fileToOpen.createNewFile()){
            return false;
        }
        return true;
    }

    public String getTableName(){
        return tableName;
    }

    public void setTableName(String newTableName){
        tableName = newTableName;
    }

    //writeTableToFile
    //check value is updated
    //write a method to increment all ages by 1 (need to convert from string to int and back again)
}
