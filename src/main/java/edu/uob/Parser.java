package edu.uob;
import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    ArrayList<String> tokenisedList;

    public Parser(ArrayList<String> tokens) {
        this.tokenisedList = tokens;
    }

    public String tokenToString(int tokenIndex) {
        return tokenisedList.get(tokenIndex);
    }

    //ensure that arbitrary additional whitespace is handled

    //I need to catch these runtime exceptions somewhere so they're not passed onto the user
    //Could I use for each to avoid repeated line "String currentToken ..."
    public boolean isBoolOperator(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (Objects.equals(currentToken, "AND") || Objects.equals(currentToken, "OR")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Bool Operator");
        }
    }

    public boolean isAlterationType(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (Objects.equals(currentToken, "ADD") || Objects.equals(currentToken, "DROP")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Alteration Type");
        }
    }

    public boolean isBooleanLiteral(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (Objects.equals(currentToken, "TRUE") || Objects.equals(currentToken, "FALSE")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Boolean Literal");
        }
    }

    public boolean isDigit(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (currentToken.length() != 1) {
            return false;
        }
        char c = currentToken.charAt(0);
        return Character.isDigit(c);
    }

    public boolean isDigitSequence(int tokenIndex, int startCharacterIndex){
        String currentToken = tokenToString(tokenIndex);
        int stringLength = currentToken.length();
        for(int i = startCharacterIndex; i < stringLength; i++){
            if(!Character.isDigit(currentToken.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isIntegerLiteral(int tokenIndex){
        String currentToken = tokenToString(tokenIndex);
        if(Character.isDigit(currentToken.charAt(0))){
            if(isDigitSequence(tokenIndex, 0)) {
                return true;
            }
        }
        if(currentToken.charAt(0) == '+' || currentToken.charAt(0) == '-'){
            if(isDigitSequence(tokenIndex, 1)){
                return true;
            }
        }
        throw new RuntimeException("Invalid Integer Literal");
    }
    /*
    public boolean isFloatLiteral(int tokenIndex){

    }
     */

    //TO DO: make this long line shorter
    public boolean isComparator(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (Objects.equals(currentToken, "==") || Objects.equals(currentToken, ">") || Objects.equals(currentToken, "<") || Objects.equals(currentToken, "<=") || Objects.equals(currentToken, ">=") || Objects.equals(currentToken, "!=") || Objects.equals(currentToken, "LIKE")) {
            return true;
        } else {
            throw new RuntimeException("Invalid Comparator");
        }
    }

    public boolean isUppercase(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (currentToken.length() != 1) {
            return false;
        }
        char c = currentToken.charAt(0);
        return Character.isUpperCase(c);
    }

    public boolean isLowercase(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (currentToken.length() != 1) {
            return false;
        }
        char c = currentToken.charAt(0);
        return Character.isLowerCase(c);
    }

    public boolean isLetter(int tokenIndex) {
        return isLowercase(tokenIndex) || isUppercase(tokenIndex);
    }

    public boolean isSymbol(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        if (currentToken.length() != 1) {
            return false;
        }
        return currentToken.matches(".*[!#$%&()*+,-./:;>=<?@\\[\\]^_`{}~].*");
    }

    public boolean isCharLiteral(int tokenIndex){
        String currentToken = tokenToString(tokenIndex);
        char c = currentToken.charAt(0);
        if(isDigit(tokenIndex) || isLetter(tokenIndex) || isSymbol(tokenIndex) || c == ' '){
            return true;
        }
        throw new RuntimeException("Invalid Char Literal");
    }

    //couldn't think of an easy way to make this method recursive
    public boolean isPlainText(int tokenIndex) {
        String currentToken = tokenToString(tokenIndex);
        int stringLength = currentToken.length();
        for (int i = 0; i < stringLength; i++) {
            if (!Character.isDigit(currentToken.charAt(i)) && !Character.isAlphabetic(currentToken.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean isDatabaseName(int tokenIndex) {
        if(isPlainText(tokenIndex)){
            return true;
        }
        throw new RuntimeException("Invalid Database Name");
    }

    public boolean isUse(int tokenIndex){
        String currentToken = tokenToString(tokenIndex);
        if(Objects.equals(currentToken, "USE")){
            tokenIndex++;
            if(isDatabaseName(tokenIndex)){
                return true;
            }
        }

        throw new RuntimeException("Invalid Use command");
    }

    //TO DO NEXT

    //Create...




}