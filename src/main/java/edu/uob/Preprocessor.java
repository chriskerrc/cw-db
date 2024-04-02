package edu.uob;
import java.util.Arrays;
import java.util.ArrayList;
public class Preprocessor {

    String[] specialCharacters = {"(",")",",",";",">","<"};
    ArrayList<String> commandTokens = new ArrayList<String>();

    int modulusConstant = 2;

    public Preprocessor(String databaseQuery) {
        setupTokeniser(databaseQuery);
    }
    void setupTokeniser(String databaseQuery)
    {
        databaseQuery = databaseQuery.trim();
        String[] commandFragments = databaseQuery.split("'");
        for (int fragmentIndex=0; fragmentIndex<commandFragments.length; fragmentIndex++) {
            if (fragmentIndex % modulusConstant != 0) commandTokens.add("'" + commandFragments[fragmentIndex] + "'");
            else {
                String[] nextBatchOfTokens = tokeniseQuery(commandFragments[fragmentIndex]);
                commandTokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
    }

    String[] tokeniseQuery(String inputString)
    {
        for (String specialCharacter : specialCharacters) {
            inputString = inputString.replace(specialCharacter, " " + specialCharacter + " ");
        }
        while (inputString.contains("  ")) inputString = inputString.replaceAll("  ", " ");
        inputString = inputString.trim();
        //Ensure composite comparator characters are kept together
        inputString = inputString.replace("> =", ">=");
        inputString = inputString.replace("< =", "<=");
        inputString = inputString.replace("! =", "!=");
        inputString = inputString.replace("= =", "==");
        //add a space after >= and <= where they're not already followed by a space
        inputString = inputString.replaceAll(">=(?! )", ">= ");
        inputString = inputString.replaceAll("<=(?! )", "<= ");
        //add a space before and after == and != where they're not already surrounded by a spaces
        inputString = inputString.replaceAll("(?<! )==(?! )", " == ");
        inputString = inputString.replaceAll("(?<! )!=(?! )", " != ");
        return inputString.split(" ");
    }

    public ArrayList<String> getTokens(){
        return commandTokens;
    }
}


