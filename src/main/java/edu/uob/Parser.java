package edu.uob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class Parser {
    ArrayList<String> tokenisedList;
    static int currentWord = 0;


    public Parser(ArrayList<String> tokens) {
        this.tokenisedList = tokens;
    }

    //maybe use error tags for error messages? And throw errors from every small method not just from the top
    //ensure that arbitrary additional whitespace is handled

    //I need to catch these runtime exceptions somewhere so they're not passed onto the user
    //Could I use for each to avoid repeated line "String currentToken ..."

    //Replace Objects.equals ... with currentWordMatches method


    //need to update return value of parseCommand method for select, because Select needs to
    //return more than just binary [OK] or [ERROR]
    public boolean parseCommand(ArrayList<String> tokens){
        Parser p = new Parser(tokens);
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        try {
            if(Objects.equals(p.isCommand(tokens), "CREATE_DATABASE")){
                return databaseManager.interpretCreateDatabase();
            }
            if(Objects.equals(p.isCommand(tokens), "USE")){
                return databaseManager.interpretUseDatabase();
            }
            if(Objects.equals(p.isCommand(tokens), "CREATE_TABLE")){
                return databaseManager.interpretCreateTable();
            }
            if(Objects.equals(p.isCommand(tokens), "INVALID")){
                return false;
            }
        } catch (RuntimeException | IOException exception){
            System.err.println("Error: " + exception.getMessage()); //not sure if this is right thing to do
            return false;
        }
        return false;
    }

    //Commands


    public String isCommand(ArrayList<String> tokens) throws IOException {
        String command = "";
        if(Objects.equals(isCommandType(tokens), "INVALID")){
            throw new RuntimeException("Invalid Command");
        }
        if(Objects.equals(isCommandType(tokens), "USE")){
            command = "USE";
        }
        if(Objects.equals(isCommandType(tokens), "CREATE_DATABASE")){
            command = "CREATE_DATABASE";
        }
        if(Objects.equals(isCommandType(tokens), "CREATE_TABLE")){
            command = "CREATE_TABLE";
        }
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, ";")){
            throw new RuntimeException("Invalid Command: missing semi-colon?");
        }
        return command;
    }
    public String isCommandType(ArrayList<String> tokens) throws IOException {
        setCurrentWord(0); //absence of this line broke parsing of USE 
        if(isUse(tokens)){
            return "USE";
        }
        setCurrentWord(0); //could replace with reset current word
        if(isCreateTable(tokens)){
            return "CREATE_TABLE";
        }
        setCurrentWord(0);
        if(isCreateDatabase(tokens)){
            return "CREATE_DATABASE";
        }
        //add other commands
        return "INVALID";
    }
    public boolean isUse(ArrayList<String> tokens) throws IOException {
        if(Objects.equals(tokens.get(currentWord), "USE")){
            incrementCurrentWord(tokens);
            if(isDatabaseName(tokens)){
                String databaseName = getCurrentWordString();
                DatabaseManager databaseManager = DatabaseManager.getInstance();
                databaseManager.setDatabaseInUse(databaseName);
                return true;
            }
        }
        return false;
    }


    //Grammar rule methods

    public boolean isBoolOperator(ArrayList<String> tokens) {
        if (Objects.equals(tokens.get(currentWord), "AND") || Objects.equals(tokens.get(currentWord), "OR")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Bool Operator");
        }
    }

    public boolean isAlterationType(ArrayList<String> tokens) {
        if (Objects.equals(tokens.get(currentWord), "ADD") || Objects.equals(tokens.get(currentWord), "DROP")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Alteration Type");
        }
    }

    public boolean isBooleanLiteral(ArrayList<String> tokens) {
        if (Objects.equals(tokens.get(currentWord), "TRUE") || Objects.equals(tokens.get(currentWord), "FALSE")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Boolean Literal");
        }
    }

    public boolean isDigit(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        char c = tokens.get(currentWord).charAt(0);
        return Character.isDigit(c);
    }

    public boolean isDigitSequence(ArrayList<String> tokens, int startCharacter){
        int tokenLength = tokens.get(currentWord).length();
        for(int i = startCharacter; i < tokenLength; i++){
            if(!Character.isDigit(tokens.get(currentWord).charAt(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isIntegerLiteral(ArrayList<String> tokens){
        if(Character.isDigit(tokens.get(currentWord).charAt(0))){
            if(isDigitSequence(tokens, 0)) {
                return true;
            }
        }
        if(tokens.get(currentWord).charAt(0) == '+' || tokens.get(currentWord).charAt(0) == '-'){
            if(isDigitSequence(tokens, 1)){
                return true;
            }
        }
        throw new RuntimeException("Invalid Integer Literal");
    }

    //<Comparator>  	::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "

    public boolean isComparator(ArrayList<String> tokens) {
        if (currentWordMatches(tokens, "==")) {
            return true;
        }
        else if(currentWordMatches(tokens, ">")) {
            return true;
            }
        else if(currentWordMatches(tokens, "<")) {
            return true;
        }
        else if(currentWordMatches(tokens, ">=")) {
            return true;
        }
        else if(currentWordMatches(tokens, "<=")) {
            return true;
        }
        else if(currentWordMatches(tokens, "!=")) {
            return true;
        }
        //preprocessor will strip out spaces either side of "LIKE"
        else if(currentWordMatches(tokens, "LIKE")) {
            return true;
        }
        else {
            throw new RuntimeException("Invalid Comparator");
        }
    }

    public boolean isUppercase(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        char c = tokens.get(currentWord).charAt(0);
        return Character.isUpperCase(c);
    }

    public boolean isLowercase(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        char c = tokens.get(currentWord).charAt(0);
        return Character.isLowerCase(c);
    }

    public boolean isLetter(ArrayList<String> tokens) {
        return isLowercase(tokens) || isUppercase(tokens);
    }

    public boolean isSymbol(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        return tokens.get(currentWord).matches(".*[!#$%&()*+,-./:;>=<?@\\[\\]^_`{}~].*");
    }

    public boolean isCharLiteral(ArrayList<String> tokens){
        char c = tokens.get(currentWord).charAt(0);
        if(isDigit(tokens) || isLetter(tokens) || isSymbol(tokens) || c == ' '){
            return true;
        }
        throw new RuntimeException("Invalid Char Literal");
    }

    //couldn't think of an easy way to make the isPlainText method recursive

    public boolean isPlainText(ArrayList<String> tokens) {
        int tokenLength = tokens.get(currentWord).length();
        String currentToken = tokens.get(currentWord);
        for (int i = 0; i < tokenLength; i++) {
            char c = currentToken.charAt(i);
            if (!Character.isDigit(c) && !Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDatabaseName(ArrayList<String> tokens) {
        return isPlainText(tokens);
    }

    public boolean isAttributeName(ArrayList<String> tokens) {
        return isPlainText(tokens);
    }

    public boolean isAttributeList(ArrayList<String> tokens) {
        ArrayList<String> attributeNames = new ArrayList<>();
       while(currentWord < tokens.size()) {
           if (isAttributeName(tokens)) {
               attributeNames.add(getCurrentWordString());
               incrementCurrentWord(tokens);
               if (currentWord < tokens.size() && currentWordMatches(tokens, ",")) {
                   incrementCurrentWord(tokens);
               }
               else {
                   //Only one Attribute Name (no list) so reset currentWord after look ahead
                   decrementCurrentWord(tokens);
                   DatabaseManager databaseManager = DatabaseManager.getInstance();
                   databaseManager.setAttributeNamesForCreateTable(attributeNames);
                   return true;
               }
           }
           else{
               return false;
           }
       }
        return false;
    }

    public boolean isTableName(ArrayList<String> tokens) {
        return isPlainText(tokens);
    }

    //<CreateTable> 	::=  "CREATE " "TABLE " [TableName] | "CREATE " "TABLE " [TableName] "(" <AttributeList> ")"

    //This method is long
    public boolean isCreateTable(ArrayList<String> tokens){
        //I'm assuming space after CREATE and TABLE will be stripped out by preprocessor?
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(tokens, "CREATE")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, "TABLE")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!isTableName(tokens) && !currentWordMatches(tokens, "(")) {
            return false;
        }
        databaseManager.setNameTableToCreate(getCurrentWordString());
        if(tokens.size() > 3) { //magic number
            incrementCurrentWord(tokens);
            //No attribute list
            if (!currentWordMatches(tokens, "(")){
                decrementCurrentWord(tokens);
                databaseManager.setIsAttributeListForCreateTable(false);
                return true;
            }
            //Expecting attribute list
            else{
                databaseManager.setIsAttributeListForCreateTable(true);
                return isCreateTableAttributeList(tokens);
            }
        }
        else{
            return true;
        }
    }

    public boolean isCreateTableAttributeList(ArrayList<String> tokens){
        if(!currentWordMatches(tokens, "(")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!isAttributeList(tokens)){
            return false;
        }
        incrementCurrentWord(tokens);
        //on return from isAttributeList, current word is too high when there's two valid tokens followed by no closing bracket
        if(!currentWordMatches(tokens, ")")){
            return false;
        }
        return true;
    }

    public boolean isCreateDatabase(ArrayList<String> tokens) throws IOException {
        //I'm assuming space after CREATE and DATABASE will be stripped out by preprocessor?
        if(!currentWordMatches(tokens, "CREATE")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, "DATABASE")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(isDatabaseName(tokens)){
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.setDatabaseToCreate(getCurrentWordString());
            return true;
        }
        return false;
    }

    //Helper methods

    public String tokenToString(int tokenIndex) {
        return tokenisedList.get(tokenIndex);
    }

    public void setCurrentWord(int wordIndex){
        currentWord = wordIndex;
    }

    private void incrementCurrentWord(ArrayList<String> tokens){
        if(currentWord < tokens.size()) {
            currentWord++;
            return;
        }
        throw new RuntimeException("Reached last token");
    }

    private void decrementCurrentWord(ArrayList<String> tokens){
        if(currentWord > 0) {
            currentWord--;
            return;
        }
        throw new RuntimeException("Reached 0th token");
    }

    public int getCurrentWord(){
        return currentWord;
    }

    public String getCurrentWordString(){
        int currentWordIndex = getCurrentWord();
        return tokenToString(currentWordIndex);
    }

    private boolean currentWordMatches(ArrayList<String> tokens, String input){
        return Objects.equals(tokens.get(currentWord), input);
    }





}