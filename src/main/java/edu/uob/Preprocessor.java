package edu.uob;
import java.util.Arrays;
import java.util.ArrayList;
public class Preprocessor {

    String[] specialCharacters = {"(",")",",",";",">","<"};
    ArrayList<String> tokens = new ArrayList<String>();

    public Preprocessor(String query) {
        setup(query);
    }
    void setup(String query)
    {
        query = query.trim();
        String[] fragments = query.split("'");
        for (int i=0; i<fragments.length; i++) {
            if (i%2 != 0) tokens.add("'" + fragments[i] + "'");
            else {
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
    }

    String[] tokenise(String input)
    {
        for (String specialCharacter : specialCharacters) {
                input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = input.trim();
        //Ensure composite comparator characters are kept together
        input = input.replace("> =", ">=");
        input = input.replace("< =", "<=");
        input = input.replace("! =", "!=");
        input = input.replace("= =", "==");
        //add a space after >= and <= where they're not already followed by a space
        input = input.replaceAll(">=(?! )", ">= ");
        input = input.replaceAll("<=(?! )", "<= ");
        //add a space before and after == and != where they're not already surrounded by a spaces
        input = input.replaceAll("(?<! )==(?! )", " == ");
        input = input.replaceAll("(?<! )!=(?! )", " != ");
        return input.split(" ");
    }

    public ArrayList<String> getTokens(){
        return tokens;
    }
}


