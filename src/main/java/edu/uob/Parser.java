package edu.uob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Parser {
    ArrayList<String> tokenisedList;
    static int currentWord = 0;

    public Parser(ArrayList<String> commandTokens) {
        this.tokenisedList = commandTokens;
    }

    public String parseCommand() throws Exception{
        ArrayList<String> commandTokens = this.tokenisedList;
        Parser commandParser = new Parser(commandTokens);
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        String dbCommand = commandParser.isCommand(commandTokens);
        try {
            switch (dbCommand) {
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
                case "ALTER":
                    if (databaseManager.interpretAlter()) {
                        return "ALTER";
                    }
                    break;
                case "DROP_TABLE":
                    if (databaseManager.interpretDropTable()) {
                        return "DROP_TABLE";
                    }
                    break;
                case "DROP_DATABASE":
                    if (databaseManager.interpretDropDatabase()) {
                        return "DROP_DATABASE";
                    }
                    break;
                case "DELETE":
                    if (databaseManager.interpretDelete()) {
                        return "DELETE";
                    }
                    break;
                case "UPDATE":
                    if (databaseManager.interpretUpdate()) {
                        return "UPDATE";
                    }
                    break;
                case "JOIN":
                    if (databaseManager.interpretJoin()) {
                        return "JOIN";
                    }
                    break;
                default:
                    throw new RuntimeException("No matching command found");
            }
            throw new RuntimeException("Failed to parse");
        } catch (Exception parserException) {
            throw new Exception(parserException.getMessage());
        }
    }

    //Commands

    public String isCommand(ArrayList<String> commandTokens) throws Exception {
        String dbCommand = isCommandType(commandTokens);
        if(dbCommand.equals("INVALID")){
            throw new RuntimeException("Failed to parse");
        }
        if (currentWord >= commandTokens.size()) {
            throw new Exception("Invalid Command: missing command arguments");
        }
        incrementCurrentWord(commandTokens);
        if (currentWord >= commandTokens.size()) {
            throw new Exception("Invalid Command: missing semi-colon or arguments?");
        }
        if (!currentWordMatches(commandTokens, ";")){
            throw new Exception("Invalid Command: missing semi-colon or arguments?");
        }
        return dbCommand;
    }
    private String isCommandType(ArrayList<String> commandTokens) throws IOException {
        resetCurrentWord();
        if(isUse(commandTokens)){
            return "USE";
        }
        resetCurrentWord();
        if(isCreateTable(commandTokens)){
            return "CREATE_TABLE";
        }
        resetCurrentWord();
        if(isCreateDatabase(commandTokens)){
            return "CREATE_DATABASE";
        }
        if(isInsert(commandTokens)){
            return "INSERT";
        }
        if(isSelect(commandTokens)){
            return "SELECT";
        }
        if(isAlter(commandTokens)){
            return "ALTER";
        }
        if(isDropDatabase(commandTokens)){
            return "DROP_DATABASE";
        }
        resetCurrentWord();
        if(isDropTable(commandTokens)){
            return "DROP_TABLE";
        }
        if(isDelete(commandTokens)){
            return "DELETE";
        }
        if(isUpdate(commandTokens)){
            return "UPDATE";
        }
        if(isJoin(commandTokens)){
            return "JOIN";
        }
        return "INVALID";
    }
    private boolean isUse(ArrayList<String> commandTokens) throws IOException {
        if(!currentWordMatches(commandTokens, "USE")) {
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(isDatabaseName(commandTokens)){
            String databaseName = getCurrentWordString();
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.setDatabaseInUse(databaseName);
            return true;
        }
        return false;
    }

    private boolean isCreateTable(ArrayList<String> commandTokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(commandTokens, "CREATE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "TABLE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens) && !currentWordMatches(commandTokens, "(")) {
            return false;
        }
        databaseManager.setNameTableToCreate(getCurrentWordString());
        int tokensNoAttributes = 3;
        if(commandTokens.size() > tokensNoAttributes) {
            incrementCurrentWord(commandTokens);
            if (!currentWordMatches(commandTokens, "(")){ //No attribute list
                decrementCurrentWord();
                databaseManager.setTableHasAttributes(false);
                return true;
            }
            else{ //Expecting attribute list
                databaseManager.setTableHasAttributes(true);
                return isCreateTableAttributeList(commandTokens);
            }
        }
        return true;
    }

    private boolean isCreateDatabase(ArrayList<String> commandTokens) {
        if(!currentWordMatches(commandTokens, "CREATE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "DATABASE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(isDatabaseName(commandTokens)){
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.setDatabaseToCreate(getCurrentWordString());
            return true;
        }
        return false;
    }

    private boolean isInsert(ArrayList<String> commandTokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(commandTokens, "INSERT")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "INTO")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)) {
            return false;
        }
        databaseManager.setNameInsertTable(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "VALUES")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        return isInsertValueList(commandTokens);
    }

    private boolean isSelect(ArrayList<String> commandTokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(commandTokens, "SELECT")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isWildAttribList(commandTokens)){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "FROM")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)){
            return false;
        }
        databaseManager.setTableToSelect(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "WHERE")){
            decrementCurrentWord();
            databaseManager.setHasCondition(false);
            return true;
        }
        databaseManager.setHasCondition(true);
        incrementCurrentWord(commandTokens);
        return isCondition(commandTokens);
    }

    private boolean isAlter(ArrayList<String> commandTokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(commandTokens, "ALTER")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "TABLE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)) {
            return false;
        }
        databaseManager.setTableToAlter(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "ADD") && !currentWordMatches(commandTokens, "DROP")){
            return false;
        }
        databaseManager.setAddAlter(currentWordMatches(commandTokens, "ADD"));
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)){
            return false;
        }
        databaseManager.setColumnToAlter(getCurrentWordString());
        return true;
    }

    private boolean isDropTable(ArrayList<String> commandTokens){
        if(!currentWordMatches(commandTokens, "DROP")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "TABLE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(isUnreservedPlaintext(commandTokens)){
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.setTableToDrop(getCurrentWordString());
            return true;
        }
        return false;
    }

    private boolean isDropDatabase(ArrayList<String> commandTokens){
        if(!currentWordMatches(commandTokens, "DROP")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "DATABASE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(isUnreservedPlaintext(commandTokens)){
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.setDatabaseToDrop(getCurrentWordString());
            return true;
        }
        return false;
    }

    private boolean isDelete(ArrayList<String> commandTokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!currentWordMatches(commandTokens, "DELETE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "FROM")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)){
            return false;
        }
        databaseManager.setTableToDeleteFrom(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "WHERE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        return isCondition(commandTokens);
    }

    //Interpreting isUpdate currently only works for a single name-value pair
    private boolean isUpdate(ArrayList<String> commandTokens){
        if(!currentWordMatches(commandTokens, "UPDATE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)){
            return false;
        }
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.setTableToUpdate(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "SET")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isNameValueList(commandTokens)){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "WHERE")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        return isCondition(commandTokens);
    }

    private boolean isJoin(ArrayList<String> commandTokens){
        if(!currentWordMatches(commandTokens, "JOIN")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "AND")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isUnreservedPlaintext(commandTokens)){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "ON")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isAttributeName(commandTokens)){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "AND")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        return isAttributeName(commandTokens);
    }

    //Grammar rule methods

    private boolean isBooleanLiteral(ArrayList<String> commandTokens) {
        return currentWordMatches(commandTokens, "TRUE") || currentWordMatches(commandTokens, "FALSE");
    }

    private boolean isDigitSequence(ArrayList<String> commandTokens, int startCharacter){
        int tokenLength = commandTokens.get(currentWord).length();
        for(int characterIndex = startCharacter; characterIndex < tokenLength; characterIndex++){
            if(!Character.isDigit(commandTokens.get(currentWord).charAt(characterIndex))){
                return false;
            }
        }
        return true;
    }

    private boolean isIntegerLiteral(ArrayList<String> commandTokens){
        if(Character.isDigit(commandTokens.get(currentWord).charAt(0))){
            if(isDigitSequence(commandTokens, 0)) {
                return true;
            }
        }
        if(commandTokens.get(currentWord).charAt(0) == '+' || commandTokens.get(currentWord).charAt(0) == '-'){
            return isDigitSequence(commandTokens, 1);
        }
        return false;
    }

    private boolean isComparator(ArrayList<String> commandTokens) {
        if (currentWordMatches(commandTokens, "==")) {
            return true;
        }
        else if(currentWordMatches(commandTokens, ">")) {
            return true;
            }
        else if(currentWordMatches(commandTokens, "<")) {
            return true;
        }
        else if(currentWordMatches(commandTokens, ">=")) {
            return true;
        }
        else if(currentWordMatches(commandTokens, "<=")) {
            return true;
        }
        else if(currentWordMatches(commandTokens, "!=")) {
            return true;
        }
        else if(currentWordMatches(commandTokens, "LIKE")) {
            return true;
        }
        else {
            throw new RuntimeException("Invalid Comparator");
        }
    }

    private boolean isPlainText(ArrayList<String> commandTokens) {
        int tokenLength = commandTokens.get(currentWord).length();
        String currentToken = commandTokens.get(currentWord);
        for (int characterIndex = 0; characterIndex < tokenLength; characterIndex++) {
            char tokenCharacter = currentToken.charAt(characterIndex);
            if (!Character.isDigit(tokenCharacter) && !Character.isAlphabetic(tokenCharacter)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDatabaseName(ArrayList<String> commandTokens) {
        if(checkWordReserved(commandTokens)){
            return false;
        }
        return isPlainText(commandTokens);
    }

    private boolean isAttributeName(ArrayList<String> commandTokens) {
        if(checkWordReserved(commandTokens)){
            return false;
        }
        return isPlainText(commandTokens);
    }

    private boolean isAttributeList(ArrayList<String> commandTokens) {
        ArrayList<String> attributeNames = new ArrayList<>();
        while(currentWord < commandTokens.size()) {
           if (isAttributeName(commandTokens)) {
               attributeNames.add(getCurrentWordString());
               incrementCurrentWord(commandTokens);
               if (currentWord < commandTokens.size() && currentWordMatches(commandTokens, ",")) {
                   incrementCurrentWord(commandTokens);
               }
               else {
                   //Only one Attribute Name (no list) so reset currentWord after look ahead
                   decrementCurrentWord();
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

    private boolean isValueList(ArrayList<String> commandTokens) {
        ArrayList<String> valueList = new ArrayList<>();
        while(currentWord < commandTokens.size()) {
            if (isValue(commandTokens)) {
                valueList.add(getCurrentWordString());
                incrementCurrentWord(commandTokens);
                if (currentWord < commandTokens.size() && currentWordMatches(commandTokens, ",")) {
                    incrementCurrentWord(commandTokens);
                }
                else {
                    //Only one Value (no list) so reset currentWord after look ahead
                    decrementCurrentWord();
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

    private boolean isUnreservedPlaintext(ArrayList<String> commandTokens) {
        if(checkWordReserved(commandTokens)){
            return false;
        }
        return isPlainText(commandTokens);
    }

    private boolean isCreateTableAttributeList(ArrayList<String> commandTokens){
        if(!currentWordMatches(commandTokens, "(")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isAttributeList(commandTokens)){
            return false;
        }
        incrementCurrentWord(commandTokens);
        return currentWordMatches(commandTokens, ")");
    }

    private boolean isInsertValueList(ArrayList<String> commandTokens){
    if(!currentWordMatches(commandTokens, "(")){
        return false;
    }
    incrementCurrentWord(commandTokens);
    if(!isValueList(commandTokens)){
        return false;
    }
    incrementCurrentWord(commandTokens);
    return currentWordMatches(commandTokens, ")");
}

    private boolean isStringLiteral() {
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

    private boolean isFloatLiteral(){
        return getCurrentWordString().matches("([-+])?\\d+\\.\\d+");
    }

    private boolean isValue(ArrayList<String> commandTokens){
        if(isBooleanLiteral(commandTokens)){
            return true;
        }
        if(isFloatLiteral()){
            return true;
        }
        if(isIntegerLiteral(commandTokens)){
            return true;
        }
        if(currentWordMatches(commandTokens, "NULL")){
            return true;
        }
        return isStringLiteral();
    }

    private boolean isWildAttribList(ArrayList<String> commandTokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(isAttributeList(commandTokens)){
            databaseManager.setSelectAsterisk(false);
            //this assumes only one AttributeName
            databaseManager.setSelectAttribute(getCurrentWordString());
            return true;
        }
        if(currentWordMatches(commandTokens, "*")){
            databaseManager.setSelectAsterisk(true);
            return true;
        }
        return false;
    }

    private boolean isCondition(ArrayList<String> commandTokens){
        //Only works for the simple case: [AttributeName] <Comparator> [Value]
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        boolean isBracketedCondition = false;
        if(currentWordMatches(commandTokens,"(")){
            incrementCurrentWord(commandTokens);
            isBracketedCondition = true;
        }
        if(!isAttributeName(commandTokens)){
            return false;
        }
        databaseManager.setConditionAttribute(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!isComparator(commandTokens)){
            return false;
        }
        databaseManager.setConditionComparator(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!isValue(commandTokens)){
            return false;
        }
        databaseManager.setConditionValue(getCurrentWordString());
        if(!isBracketedCondition) {
            return true;
        }
        incrementCurrentWord(commandTokens);
        return currentWordMatches(commandTokens, ")");
    }

    //<NameValuePair>   ::=  [AttributeName] "=" [Value]
    private boolean isNameValuePair(ArrayList<String> commandTokens){
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        if(!isAttributeName(commandTokens)){
            return false;
        }
        databaseManager.setColumnToUpdate(getCurrentWordString());
        incrementCurrentWord(commandTokens);
        if(!currentWordMatches(commandTokens, "=")){
            return false;
        }
        incrementCurrentWord(commandTokens);
        if(!isValue(commandTokens)){
            return false;
        }
        databaseManager.setUpdateNewValue(getCurrentWordString());
        return true;
    }

    private boolean isNameValueList(ArrayList<String> commandTokens){
        while(true){
            if(!isNameValuePair(commandTokens)){
                return false;
            }
            incrementCurrentWord(commandTokens);
            if(!currentWordMatches(commandTokens, ",")){
                //we assume we're at the end of the list
                //reset current word so that word after the NVP can be checked by the next method
                decrementCurrentWord();
                return true;
            }
            //if current word is a comma
            incrementCurrentWord(commandTokens);
        }
    }

    //Helper methods

    private String tokenToString(int tokenIndex) {
        return tokenisedList.get(tokenIndex);
    }

    private void resetCurrentWord(){
        currentWord = 0;
    }

    private void incrementCurrentWord(ArrayList<String> commandTokens){
        if(currentWord < commandTokens.size()) {
            currentWord++;
            return;
        }
        throw new RuntimeException("Reached last token");
    }

    private void decrementCurrentWord(){
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

    private String removeSingleQuotesFromString(String inputString){
        if(inputString.isEmpty()){
            return "";
        }
        int minimumLength = 2;
        if(inputString.length() > minimumLength && inputString.startsWith("'") && inputString.endsWith("'")) {
            return inputString.substring(1, inputString.length() - 1);
        }
        else{
            return null;
        }
    }

    private boolean currentWordMatches(ArrayList<String> commandTokens, String inputString){
        return commandTokens.get(currentWord).equalsIgnoreCase(inputString);
    }

    private boolean checkWordReserved(ArrayList<String> commandTokens) {
        Set<String> reservedWords = new HashSet<>(Set.of("CREATE", "TABLE", "DATABASE", "INSERT", "INTO", "SELECT",
                "FROM", "TRUE", "FALSE", "NULL", "WHERE", "DROP", "ALTER", "UPDATE", "DELETE", "JOIN", "VALUES",
                "ADD", "AND", "OR", "LIKE"));
        return reservedWords.contains(commandTokens.get(currentWord).toUpperCase());
    }

}