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

    //is there a simpler/less redundant way of doing this? I'm passing strings between multiple levels of functions?
    public String parseCommand(ArrayList<String> tokens) throws Exception{
        Parser p = new Parser(tokens);
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        try {
            if (Objects.equals(p.isCommand(tokens), "CREATE_DATABASE")) {
                if (databaseManager.interpretCreateDatabase()) {
                    return "CREATE_DATABASE";
                }
            }
            if (Objects.equals(p.isCommand(tokens), "USE")) {
                if (databaseManager.interpretUseDatabase()) {
                    return "USE";
                }
            }
            if (Objects.equals(p.isCommand(tokens), "CREATE_TABLE")) {
                if (databaseManager.interpretCreateTable()) {
                    return "CREATE_TABLE";
                }
            }
            if (Objects.equals(p.isCommand(tokens), "INSERT")) {
                if (databaseManager.interpretInsert()) {
                    return "INSERT";
                }
            }
            if (Objects.equals(p.isCommand(tokens), "SELECT")) {
                if (databaseManager.interpretSelect()) {
                    return "SELECT";
                }
                throw new RuntimeException("No matching command found");
            }
            throw new RuntimeException("Failed to parse");
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }

    //Commands


    public String isCommand(ArrayList<String> tokens) throws Exception {
        String command = "";
        if(Objects.equals(isCommandType(tokens), "INVALID")){
            throw new RuntimeException("Failed to parse");
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
        if(Objects.equals(isCommandType(tokens), "INSERT")){
            command = "INSERT";
        }
        if(Objects.equals(isCommandType(tokens), "SELECT")){
            command = "SELECT";
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
        if(isInsert(tokens)){
            return "INSERT";
        }
        if(isSelect(tokens)){
            return "SELECT";
        }
        return "INVALID";
    }
    private boolean isUse(ArrayList<String> tokens) throws IOException {
        if(currentWordMatches(tokens, "USE")){
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

    //<CreateTable> 	::=  "CREATE " "TABLE " [TableName] | "CREATE " "TABLE " [TableName] "(" <AttributeList> ")"

    //This method is long
    private boolean isCreateTable(ArrayList<String> tokens){
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

    private boolean isCreateDatabase(ArrayList<String> tokens) throws IOException {
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
        databaseManager.setNameTableToInsertInto(getCurrentWordString());
        incrementCurrentWord(tokens);
        if(!currentWordMatches(tokens, "VALUES")){
            return false;
        }
        incrementCurrentWord(tokens);
        return isInsertValueList(tokens);
    }
    //<Select> ::=  "SELECT " <WildAttribList> " FROM " [TableName] | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
    private boolean isSelect(ArrayList<String> tokens){
        //simple select command for now, no "WHERE <Condition>"
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

    private boolean isBoolOperator(ArrayList<String> tokens) {
        if (currentWordMatches(tokens, "AND") || currentWordMatches(tokens, "OR")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Bool Operator");
        }
    }

    private boolean isAlterationType(ArrayList<String> tokens) {
        if (currentWordMatches(tokens, "ADD") || currentWordMatches(tokens, "DROP")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Alteration Type");
        }
    }

    private boolean isBooleanLiteral(ArrayList<String> tokens) {
        return currentWordMatches(tokens, "TRUE") || currentWordMatches(tokens, "FALSE");
    }

    private boolean isDigit(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        char c = tokens.get(currentWord).charAt(0);
        return Character.isDigit(c);
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

    //<Comparator>  	::=  "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "

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
        //preprocessor will strip out spaces either side of "LIKE"
        else if(currentWordMatches(tokens, "LIKE")) {
            return true;
        }
        else {
            throw new RuntimeException("Invalid Comparator");
        }
    }

    private boolean isUppercase(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        char c = tokens.get(currentWord).charAt(0);
        return Character.isUpperCase(c);
    }

    private boolean isLowercase(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        char c = tokens.get(currentWord).charAt(0);
        return Character.isLowerCase(c);
    }

    private boolean isLetter(ArrayList<String> tokens) {
        return isLowercase(tokens) || isUppercase(tokens);
    }

    private boolean isSymbol(ArrayList<String> tokens) {
        if (tokens.get(currentWord).length() != 1) {
            return false;
        }
        return tokens.get(currentWord).matches(".*[!#$%&()*+,-./:;>=<?@\\[\\]^_`{}~].*");
    }

    private boolean isCharLiteral(ArrayList<String> tokens){
        char c = tokens.get(currentWord).charAt(0);
        if(isDigit(tokens) || isLetter(tokens) || isSymbol(tokens) || c == ' '){
            return true;
        }
        throw new RuntimeException("Invalid Char Literal");
    }

    //couldn't think of an easy way to make the isPlainText method recursive

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
                    databaseManager.setValuesForInsertCommand(valueList);
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
        //on return from isAttributeList, current word is too high when there's two valid tokens followed by no closing bracket
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

    // [Value]  ::=  "'" [StringLiteral] "'" | [BooleanLiteral] | [FloatLiteral] | [IntegerLiteral] | "NULL"
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
            //this code assumes only one AttributeName, but won't work if it's a list (grammar allows list)
            //if I got a list from isAttributeList, would I need to tell the program when this method was called as a result of a Select command?
            databaseManager.setSelectAttribute(getCurrentWordString());
            return true;
        }
        if(currentWordMatches(tokens, "*")){
            databaseManager.setSelectAsterisk(true);
            return true;
        }
        return false;
    }

    //<Condition>   	::=  "(" <Condition> <BoolOperator> <Condition> ")" | <Condition> <BoolOperator> <Condition> |
    // "(" [AttributeName] <Comparator> [Value] ")" | [AttributeName] <Comparator> [Value]

    private boolean isCondition(ArrayList<String> tokens){
        //Only works for this simple case for now: [AttributeName] <Comparator> [Value]
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        boolean isBracketedCondition = false;
        if(currentWordMatches(tokens,"(")){
            incrementCurrentWord(tokens);
            isBracketedCondition = true;
        }
        if(!isAttributeName(tokens)){
            return false;
        }
        databaseManager.setConditionAttributeName(getCurrentWordString());
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

    //I don't know if it's OK to break up long lines like this
    private boolean currentWordIsReserved(ArrayList<String> tokens) {
        Set<String> reservedWords = new HashSet<>(Set.of("CREATE", "TABLE", "DATABASE", "INSERT", "INTO", "SELECT",
                "FROM", "TRUE", "FALSE", "NULL", "WHERE", "DROP", "ALTER", "UPDATE", "DELETE", "JOIN", "VALUES",
                "ADD", "AND", "OR", "LIKE"));
        return reservedWords.contains(tokens.get(currentWord).toUpperCase());
    }

}