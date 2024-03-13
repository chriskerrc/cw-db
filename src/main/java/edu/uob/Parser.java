package edu.uob;
import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    ArrayList<String> tokenisedList;
   public Parser(String query) { //maybe pass parser the arraylist of tokens, not a string?
       Preprocessor preprocessor = new Preprocessor(query);
       tokenisedList = preprocessor.getTokens();
   }

    public String tokenToString(int tokenIndex) {
        return tokenisedList.get(tokenIndex);
    }

    //I need to catch these runtime exceptions somewhere so they're not passed onto the user
    //Could I use for each to avoid repeated line "String currentToken ..."
   public boolean isBoolOperator(int tokenIndex) {
       String currentToken = tokenToString(tokenIndex);
       if(Objects.equals(currentToken, "AND") || Objects.equals(currentToken, "OR")) {
           return true;
       }
       else {
           throw new RuntimeException("Invalid Bool Operator");
       }
   }

   public boolean isAlterationType(int tokenIndex) {
       String currentToken = tokenToString(tokenIndex);
       if(Objects.equals(currentToken, "ADD") || Objects.equals(currentToken, "DROP")) {
           return true;
       }
       else{
           throw new RuntimeException("Invalid Alteration Type");
       }
   }

   public boolean isBooleanLiteral(int tokenIndex) {
       String currentToken = tokenToString(tokenIndex);
       if(Objects.equals(currentToken, "TRUE") || Objects.equals(currentToken, "FALSE")) {
           return true;
       }
       else{
           throw new RuntimeException("Invalid Boolean Literal");
       }
   }

    public boolean isDigit(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if(currentToken.length() != 1){
            throw new RuntimeException("Invalid Digit");
        }
        char c = currentToken.charAt(0);
        if(!Character.isDigit(c)){
            return true;
        }
        else{
            throw new RuntimeException("Invalid Digit");
        }
    }

    //TO DO: make this long line shorter
    public boolean isComparator(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if(Objects.equals(currentToken, "==") || Objects.equals(currentToken, ">") || Objects.equals(currentToken, "<") || Objects.equals(currentToken, "<=") || Objects.equals(currentToken, ">=") || Objects.equals(currentToken, "!=") || Objects.equals(currentToken, "LIKE")) {
            return true;
        }
        else{
            throw new RuntimeException("Invalid Comparator");
        }
    }

    public boolean isUppercase(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if(currentToken.length() != 1){
            throw new RuntimeException("Invalid Uppercase Letter");
        }
        char c = currentToken.charAt(0);
        if(Character.isUpperCase(c)){
            return true;
        }
        else{
            throw new RuntimeException("Invalid Uppercase Letter");
        }
    }

    public boolean isLowercase(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if(currentToken.length() != 1){
            throw new RuntimeException("Invalid Lowercase Letter A");
        }
        char c = currentToken.charAt(0);
        if(Character.isLowerCase(c)){
            return true;
        }
        else{
            throw new RuntimeException("Invalid Lowercase Letter B");
        }
    }
/*
    public boolean isLetter(int tokenIndex) {
        //need to separate this from upper and lowercase checks because they throw exceptions
        else{
            throw new RuntimeException("Invalid Letter");
        }
    }

 */

    //how can I handle ORs between different parts of the grammar when one part will throw an exception
    //only throw exception in isLetter?

}
