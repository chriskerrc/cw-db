package edu.uob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class Parser {
    ArrayList<String> tokenisedList;
    static int currentWord = 0;

    public Parser(ArrayList<String> tokens) {
        this.tokenisedList = tokens;
    }

    public String parseCommand() throws Exception{
        ArrayList<String> tokens = this.tokenisedList;
        Parser p = new Parser(tokens);
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String command = p.isCommand(tokens);
        try {
            switch (command) {
                case "CREATE_DATABASE":
                    if (databaseManager.interpretCreateDatabase()) {
                        return "CREATE_DATABASE";
                    }
                    break;
                case "USE":
                    if (databaseManager.interpretUseDatabase()) {
                        return "USE";
                    }
                    break;
                case "CREATE_TABLE":
                    if (databaseManager.interpretCreateTable()) {
                        return "CREATE_TABLE";
                    }
                    break;
                case "INSERT":
                    if (databaseManager.interpretInsert()) {
                        return "INSERT";
                    }
                    break;
                case "SELECT":
                    if (databaseManager.interpretSelect()) {
                        return "SELECT";
                    }
                    break;
                default:
                    throw new RuntimeException("No matching command found");
            }
            throw new RuntimeException("Failed to parse");
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }

    //Commands

    public String isCommand(ArrayList<String> tokens) throws Exception {
        String command = isCommandType(tokens);
        if(command.equals("INVALID")){
            throw new RuntimeException("Failed to parse");
        }
        if (currentWord >= tokens.size()) {
            throw new Exception("Invalid Command: missing command arguments");
        }
        incrementCurrentWord(tokens);
        if (currentWord >= tokens.size()) {
            throw new Exception("Invalid Command: missing semi-colon or arguments?");
        }
        if (!currentWordMatches(tokens, ";")){
            throw new Exception("Invalid Command: missing semi-colon or arguments?");
        }
        return command;
    }
    private String isCommandType(ArrayList<String> tokens) throws IOException {
        setCurrentWord(0);
        if(isUse(tokens)){
            return "USE";
        }
        setCurrentWord(0);
        if(isCreateTable(tokens)){
            return "CREATE_TABLE";
        }
        setCurrentWord(0);
        if(isCreateDatabase(tokens)){
            return "CREATE_DATABASE";
        }
        if(isInsert(tokens)){
            return "INSERT";
        }
        if(isSelect(tokens)){
            return "SELECT";
        }
        return "INVALID";
    }
    private boolean isUse(ArrayList<String> tokens) throws IOException {
        if(!currentWordMatches(tokens, "USE")) {
            return false;
        }
        incrementCurrentWord(tokens);
        if(isDatabaseName(tokens)){
            String databaseName = getCurrentWordString();
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.setDatabaseInUse(databaseName);
            return true;
        }
        return false;
    }

    private boolean isCreateTable(ArrayList<String> tokens){
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
        int tokensTableNoAttributes = 3;
        if(tokens.size() > tokensTableNoAttributes) {
            incrementCurrentWord(tokens);
            if (!currentWordMatches(tokens, "(")){ //No attribute list
                decrementCurrentWord(tokens);
                databaseManager.setTableHasAttributes(false);
                return true;
            }
            else{ //Expecting attribute list
                databaseManager.setTableHasAttributes(true);
                return isCreateTableAttributeList(tokens);
            }
        }
        return true;
    }

    private boolean isCreateDatabase(ArrayList<String> tokens) throws IOException {
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

    private boolean isInsert(ArrayList<String> tokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(tokens, "INSERT")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, "INTO")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!isTableName(tokens)) {
            return false;
        }
        databaseManager.setNameInsertTable(getCurrentWordString());
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, "VALUES")){
            return false;
        }
        incrementCurrentWord(tokens);
        return isInsertValueList(tokens);
    }

    private boolean isSelect(ArrayList<String> tokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(tokens, "SELECT")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!isWildAttribList(tokens)){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, "FROM")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!isTableName(tokens)){
            return false;
        }
        databaseManager.setTableToSelect(getCurrentWordString());
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, "WHERE")){
            decrementCurrentWord(tokens);
            databaseManager.setHasCondition(false);
            return true;
        }
        databaseManager.setHasCondition(true);
        incrementCurrentWord(tokens);
        return isCondition(tokens);
    }


    //Grammar rule methods

    private boolean isBooleanLiteral(ArrayList<String> tokens) {
        return currentWordMatches(tokens, "TRUE") || currentWordMatches(tokens, "FALSE");
    }

    private boolean isDigitSequence(ArrayList<String> tokens, int startCharacter){
        int tokenLength = tokens.get(currentWord).length();
        for(int i = startCharacter; i < tokenLength; i++){
            if(!Character.isDigit(tokens.get(currentWord).charAt(i))){
                return false;
            }
        }
        return true;
    }

    private boolean isIntegerLiteral(ArrayList<String> tokens){
        if(Character.isDigit(tokens.get(currentWord).charAt(0))){
            if(isDigitSequence(tokens, 0)) {
                return true;
            }
        }
        if(tokens.get(currentWord).charAt(0) == '+' || tokens.get(currentWord).charAt(0) == '-'){
            return isDigitSequence(tokens, 1);
        }
        return false;
    }

    private boolean isComparator(ArrayList<String> tokens) {
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
        else if(currentWordMatches(tokens, "LIKE")) {
            return true;
        }
        else {
            throw new RuntimeException("Invalid Comparator");
        }
    }

    private boolean isPlainText(ArrayList<String> tokens) {
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

    private boolean isDatabaseName(ArrayList<String> tokens) {
        if(currentWordIsReserved(tokens)){
            return false;
        }
        return isPlainText(tokens);
    }

    private boolean isAttributeName(ArrayList<String> tokens) {
        if(currentWordIsReserved(tokens)){
            return false;
        }
        return isPlainText(tokens);
    }

    private boolean isAttributeList(ArrayList<String> tokens) {
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
                   databaseManager.setTableAttributes(attributeNames);
                   return true;
               }
           }
           else{
               return false;
           }
       }
        return false;
    }

    private boolean isValueList(ArrayList<String> tokens) {
        ArrayList<String> valueList = new ArrayList<>();
        while(currentWord < tokens.size()) {
            if (isValue(tokens)) {
                valueList.add(getCurrentWordString());
                incrementCurrentWord(tokens);
                if (currentWord < tokens.size() && currentWordMatches(tokens, ",")) {
                    incrementCurrentWord(tokens);
                }
                else {
                    //Only one Value (no list) so reset currentWord after look ahead
                    decrementCurrentWord(tokens);
                    DatabaseManager databaseManager = DatabaseManager.getInstance();
                    databaseManager.setInsertionValues(valueList);
                    return true;
                }
            }
            else{
                return false;
            }
        }
        return false;
    }

    private boolean isTableName(ArrayList<String> tokens) {
        if(currentWordIsReserved(tokens)){
            return false;
        }
        return isPlainText(tokens);
    }

    private boolean isCreateTableAttributeList(ArrayList<String> tokens){
        if(!currentWordMatches(tokens, "(")){
            return false;
        }
        incrementCurrentWord(tokens);
        if(!isAttributeList(tokens)){
            return false;
        }
        incrementCurrentWord(tokens);
        return currentWordMatches(tokens, ")");
    }

    private boolean isInsertValueList(ArrayList<String> tokens){
    if(!currentWordMatches(tokens, "(")){
        return false;
    }
    incrementCurrentWord(tokens);
    if(!isValueList(tokens)){
        return false;
    }
    incrementCurrentWord(tokens);
    return currentWordMatches(tokens, ")");
}

    private boolean isStringLiteral(ArrayList<String> tokens) {
        String currentWordString = getCurrentWordString();
        String currentWordNoQuotes = removeSingleQuotesFromString(currentWordString);
        if(currentWordNoQuotes == null){
            return false;
        }
        if(currentWordNoQuotes.isEmpty()){
            return true;
        }
        if(currentWordNoQuotes.length() == 1 && Character.isAlphabetic(currentWordNoQuotes.charAt(0))){
            return true;
        }
        return currentWordNoQuotes.matches("[a-zA-Z]+");
    }

    private boolean isFloatLiteral(ArrayList<String> tokens){
        return getCurrentWordString().matches("([-+])?\\d+\\.\\d+");
    }

    private boolean isValue(ArrayList<String> tokens){
        if(isBooleanLiteral(tokens)){
            return true;
        }
        if(isFloatLiteral(tokens)){
            return true;
        }
        if(isIntegerLiteral(tokens)){
            return true;
        }
        if(currentWordMatches(tokens, "NULL")){
            return true;
        }
        return isStringLiteral(tokens);
    }

    private boolean isWildAttribList(ArrayList<String> tokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(isAttributeList(tokens)){
            databaseManager.setSelectAsterisk(false);
            //this assumes only one AttributeName
            databaseManager.setSelectAttribute(getCurrentWordString());
            return true;
        }
        if(currentWordMatches(tokens, "*")){
            databaseManager.setSelectAsterisk(true);
            return true;
        }
        return false;
    }

    private boolean isCondition(ArrayList<String> tokens){
        //Only works for the simple case: [AttributeName] <Comparator> [Value]
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        boolean isBracketedCondition = false;
        if(currentWordMatches(tokens,"(")){
            incrementCurrentWord(tokens);
            isBracketedCondition = true;
        }
        if(!isAttributeName(tokens)){
            return false;
        }
        databaseManager.setConditionAttribute(getCurrentWordString());
        incrementCurrentWord(tokens);
        if(!isComparator(tokens)){
            return false;
        }
        databaseManager.setConditionComparator(getCurrentWordString());
        incrementCurrentWord(tokens);
        if(!isValue(tokens)){
            return false;
        }
        databaseManager.setConditionValue(getCurrentWordString());
        if(!isBracketedCondition) {
            return true;
        }
        incrementCurrentWord(tokens);
        return currentWordMatches(tokens, ")");
    }

    //Helper methods

    private String tokenToString(int tokenIndex) {
        return tokenisedList.get(tokenIndex);
    }

    private void setCurrentWord(int wordIndex){
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

    private int getCurrentWord(){
        return currentWord;
    }

    private String getCurrentWordString(){
        int currentWordIndex = getCurrentWord();
        return tokenToString(currentWordIndex);
    }

    private String removeSingleQuotesFromString(String input){
        if(input.isEmpty()){
            return "";
        }
        if(input.length() > 2 && input.startsWith("'") && input.endsWith("'")) { //magic number
            return input.substring(1, input.length() - 1);
        }
        else{
            return null;
        }
    }

    private boolean currentWordMatches(ArrayList<String> tokens, String input){
        return tokens.get(currentWord).equalsIgnoreCase(input);
    }

    private boolean currentWordIsReserved(ArrayList<String> tokens) {
        Set<String> reservedWords = new HashSet<>(Set.of("CREATE", "TABLE", "DATABASE", "INSERT", "INTO", "SELECT",
                "FROM", "TRUE", "FALSE", "NULL", "WHERE", "DROP", "ALTER", "UPDATE", "DELETE", "JOIN", "VALUES",
                "ADD", "AND", "OR", "LIKE"));
        return reservedWords.contains(tokens.get(currentWord).toUpperCase());
    }

}