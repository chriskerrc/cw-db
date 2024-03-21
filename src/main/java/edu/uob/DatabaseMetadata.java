package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class DatabaseMetadata {

    static private ArrayList<Database> databasesList = new ArrayList<>();

    static private String databaseInUse;

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
}



