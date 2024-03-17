package edu.uob;
import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    ArrayList<String> tokenisedList;
    static int currentWord = 0;

    public Parser(ArrayList<String> tokens) {
        this.tokenisedList = tokens;
    }

    public String tokenToString(int tokenIndex) {
        return tokenisedList.get(tokenIndex);
    }

    public void setCurrentWord(int wordIndex){
        currentWord = wordIndex;
    }

    private void incrementCurrentWord(){
        currentWord++;
    }

    public int getCurrentWord(){
        return currentWord;
    }

    //ensure that arbitrary additional whitespace is handled

    //I need to catch these runtime exceptions somewhere so they're not passed onto the user
    //Could I use for each to avoid repeated line "String currentToken ..."
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
        if (Objects.equals(tokens.get(currentWord), "==")) {
            return true;
        }
        else if(Objects.equals(tokens.get(currentWord), ">")) {
            return true;
            }
        else if(Objects.equals(tokens.get(currentWord), "<")) {
            return true;
        }
        else if(Objects.equals(tokens.get(currentWord), ">=")) {
            return true;
        }
        else if(Objects.equals(tokens.get(currentWord), "<=")) {
            return true;
        }
        else if(Objects.equals(tokens.get(currentWord), "!=")) {
            return true;
        }
        //preprocessor will strip out spaces either side of "LIKE"
        else if(Objects.equals(tokens.get(currentWord), "LIKE")) {
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
        if(isPlainText(tokens)){
            return true;
        }
        throw new RuntimeException("Invalid Database Name");
    }

    public boolean isAttributeName(ArrayList<String> tokens) {
        return isPlainText(tokens);
    }

    public boolean isAttributeList(ArrayList<String> tokens) {
       while(currentWord < tokens.size()) {
           if (isAttributeName(tokens)) {
               incrementCurrentWord();
               if (currentWord < tokens.size() && Objects.equals(tokens.get(currentWord), ",")) {
                   incrementCurrentWord();
               }
               else {
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
        if(isPlainText(tokens)){
            return true;
        }
        throw new RuntimeException("Invalid Table Name");
    }

    public boolean isUse(ArrayList<String> tokens){
        if(Objects.equals(tokens.get(currentWord), "USE")){
            setCurrentWord(1);
            if(isDatabaseName(tokens)){
                return true;
            }
        }
        throw new RuntimeException("Invalid Use command");
    }





}