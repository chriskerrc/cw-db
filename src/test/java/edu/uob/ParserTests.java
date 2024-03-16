package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class ParserTests {

    public ArrayList<String> initialiseArrayList(String input) {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(input);
        return tokens;
    }
    //To do: use initialiseArrayList method across these test methods

    //BoolOperator
    @Test
    public void testParserBoolOperatorOR() {
        ArrayList<String> tokens = initialiseArrayList("OR");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isBoolOperator(tokens));
    }

    @Test
    public void testParserBoolOperatorAND() {
        ArrayList<String> tokens = initialiseArrayList("AND");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isBoolOperator(tokens));
    }

    @Test
    public void testParserBoolOperatorInvalid() {
        ArrayList<String> tokens = initialiseArrayList("BAD");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isBoolOperator(tokens));
    }

    //AlterationType
    @Test
    public void testParserAlterationTypeADD() {
        ArrayList<String> tokens = initialiseArrayList("ADD");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAlterationType(tokens));
    }

    @Test
    public void testParserAlterationTypeDROP() {
        ArrayList<String> tokens = initialiseArrayList("DROP");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAlterationType(tokens));
    }

    @Test
    public void testParserAlterationTypeInvalid() {
        ArrayList<String> tokens = initialiseArrayList("AND");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isAlterationType(tokens));
    }

    //BooleanLiteral
    @Test
    public void testParserBooleanLiteralTRUE() {
        ArrayList<String> tokens = initialiseArrayList("TRUE");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isBooleanLiteral(tokens));
    }

    @Test
    public void testParserBooleanLiteralFALSE() {
        ArrayList<String> tokens = initialiseArrayList("FALSE");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isBooleanLiteral(tokens));
    }

    @Test
    public void testParserBooleanLiteralInvalid() {
        ArrayList<String> tokens = initialiseArrayList("MAYBE");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isBooleanLiteral(tokens));
    }

    //Digit

    @Test
    public void testParserDigit1() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigit(tokens));
    }

    @Test
    public void testParserDigit2() {
        ArrayList<String> tokens = initialiseArrayList("2");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigit(tokens));
    }

    @Test
    public void testParserDigit9() {
        ArrayList<String> tokens = initialiseArrayList("9");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigit(tokens));
    }

    @Test
    public void testParserDigitLetter() {
        ArrayList<String> tokens = initialiseArrayList("a");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigit(tokens));
    }

    @Test
    public void testParserDigitPunctuation() {
        ArrayList<String> tokens = initialiseArrayList("!");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigit(tokens));
    }

    @Test
    public void testParserDigitDoubleDigit() {
        ArrayList<String> tokens = initialiseArrayList("22");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigit(tokens));
    }

    @Test
    public void testParserDigitSequenceMultipleNumbers() {
        ArrayList<String> tokens = initialiseArrayList("12345");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigitSequence(tokens, 0));
    }

    @Test
    public void testParserDigitSequenceSingleNumber() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigitSequence(tokens, 0));
    }

    @Test
    public void testParserDigitSequenceLetter() {
        ArrayList<String> tokens = initialiseArrayList("123a5");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigitSequence(tokens, 0));
    }

    @Test
    public void testParserIntegerLiteralUnsigned() {
        ArrayList<String> tokens = initialiseArrayList("1235");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isIntegerLiteral(tokens));
    }

    @Test
    public void testParserIntegerLiteralPlus() {
        ArrayList<String> tokens = initialiseArrayList("+1235");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isIntegerLiteral(tokens));
    }

    @Test
    public void testParserIntegerLiteralMinus() {
        ArrayList<String> tokens = initialiseArrayList("-1235");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isIntegerLiteral(tokens));
    }

    @Test
    public void testParserIntegerLiteralLetter() {
        ArrayList<String> tokens = initialiseArrayList("-123z");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isIntegerLiteral(tokens));
    }

    //Comparator

    @Test
    public void testParserComparatorEquals() {
        ArrayList<String> tokens = initialiseArrayList("==");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(tokens));
    }

    @Test
    public void testParserComparatorGreaterThan() {
        ArrayList<String> tokens = initialiseArrayList(">");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(tokens));
    }

    @Test
    public void testParserComparatorLessThan() {
        ArrayList<String> tokens = initialiseArrayList("<");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(tokens));
    }

    @Test
    public void testParserComparatorLessThanEquals() {
        ArrayList<String> tokens = initialiseArrayList("<=");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(tokens));
    }

    @Test
    public void testParserComparatorGreaterThanEquals() {
        ArrayList<String> tokens = initialiseArrayList(">=");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(tokens));
    }

    @Test
    public void testParserComparatorNotEquals() {
        ArrayList<String> tokens = initialiseArrayList("!=");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(tokens));
    }

    @Test
    public void testParserComparatorLIKE() {
        ArrayList<String> tokens = initialiseArrayList("LIKE");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(tokens));
    }

    @Test
    public void testParserComparatorInvalid() {
        ArrayList<String> tokens = initialiseArrayList("!");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isComparator(tokens));
    }

    //Uppercase
    @Test
    public void testParserUppercaseA() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUppercase(tokens));
    }

    @Test
    public void testParserUppercaseM() {
        ArrayList<String> tokens = initialiseArrayList("M");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUppercase(tokens));
    }

    @Test
    public void testParserUppercaseZ() {
        ArrayList<String> tokens = initialiseArrayList("Z");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUppercase(tokens));
    }

    @Test
    public void testParserUppercaseLowercaseA() {
        ArrayList<String> tokens = initialiseArrayList("a");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isUppercase(tokens));
    }

    @Test
    public void testParserUppercaseLowercaseT() {
        ArrayList<String> tokens = initialiseArrayList("t");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isUppercase(tokens));
    }

    @Test
    public void testParserUppercasePunctuation() {
        ArrayList<String> tokens = initialiseArrayList("+");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isUppercase(tokens));
    }
    //Lowercase

    @Test
    public void testParserLowercaseA() {
        ArrayList<String> tokens = initialiseArrayList("a");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLowercase(tokens));
    }

    @Test
    public void testParserLowercaseQ() {
        ArrayList<String> tokens = initialiseArrayList("q");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLowercase(tokens));
    }

    @Test
    public void testParserLowercaseY() {
        ArrayList<String> tokens = initialiseArrayList("y");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLowercase(tokens));
    }

    @Test
    public void testParserLowercaseUppercase() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLowercase(tokens));
    }

    @Test
    public void testParserLowercasePunctuation() {
        ArrayList<String> tokens = initialiseArrayList("/");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLowercase(tokens));
    }

    //Letter
    @Test
    public void testParserLetterUppercaseA() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(tokens));
    }

    @Test
    public void testParserLetterUppercaseP() {
        ArrayList<String> tokens = initialiseArrayList("P");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(tokens));
    }

    @Test
    public void testParserLetterLowercaseP() {
        ArrayList<String> tokens = initialiseArrayList("p");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(tokens));
    }

    @Test
    public void testParserLetterLowercaseC() {
        ArrayList<String> tokens = initialiseArrayList("c");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(tokens));
    }

    @Test
    public void testParserLetterPunctuation() {
        ArrayList<String> tokens = initialiseArrayList(".");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLetter(tokens));
    }

    @Test
    public void testParserLetterNumber() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLetter(tokens));
    }

    //Symbol

    @Test
    public void testParserSymbolForwardSlash() {
        ArrayList<String> tokens = initialiseArrayList("/");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(tokens));
    }

    @Test
    public void testParserSymbolOpenSquareBracket() {
        ArrayList<String> tokens = initialiseArrayList("[");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(tokens));
    }

    @Test
    public void testParserSymbolCloseSquareBracket() {
        ArrayList<String> tokens = initialiseArrayList("]");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(tokens));
    }

    @Test
    public void testParserSymbolCaret() {
        ArrayList<String> tokens = initialiseArrayList("^");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(tokens));
    }

    @Test
    public void testParserSymbolAt() {
        ArrayList<String> tokens = initialiseArrayList("@");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(tokens));
    }

    @Test
    public void testParserSymbolTwoSymbols() {
        ArrayList<String> tokens = initialiseArrayList("^/");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isSymbol(tokens));
    }

    @Test
    public void testParserSymbolLetter() {
        ArrayList<String> tokens = initialiseArrayList("a");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isSymbol(tokens));
    }

    @Test
    public void testParserSymbolBar() {
        ArrayList<String> tokens = initialiseArrayList("|");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isSymbol(tokens));
    }

    //Char Literal

    @Test
    public void testParserCharLiteralSpace() {
        ArrayList<String> tokens = initialiseArrayList(" ");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(tokens));
    }

    @Test
    public void testParserCharLiteralLetter() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(tokens));
    }

    @Test
    public void testParserCharLiteralSymbol() {
        ArrayList<String> tokens = initialiseArrayList("/");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(tokens));
    }

    @Test
    public void testParserCharLiteralDigit() {
        ArrayList<String> tokens = initialiseArrayList("7");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(tokens));
    }

    @Test
    public void testParserCharLiteralInvalid() {
        ArrayList<String> tokens = initialiseArrayList("INVALID");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isCharLiteral(tokens));
    }


//Plain Text

    @Test
    public void testParserPlainTextOneDigit() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(tokens));
    }

    @Test
    public void testParserPlainTextOneLetter() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(tokens));
    }

    @Test
    public void testParserPlainTextMixedStartAlpha() {
        ArrayList<String> tokens = initialiseArrayList("a124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(tokens));
    }

    @Test
    public void testParserPlainTextMixedStartDigit() {
        ArrayList<String> tokens = initialiseArrayList("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(tokens));
    }

//Database Name
    @Test
    public void testParserDatabaseNameOneDigit() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(tokens));
    }

    @Test
    public void testParserDatabaseNameOneLetter() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(tokens));
    }

    @Test
    public void testParserDatabaseNameStartAlpha() {
        ArrayList<String> tokens = initialiseArrayList("a124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(tokens));
    }

    @Test
    public void testParserDatabaseNameMixedStartDigit() {
        ArrayList<String> tokens = initialiseArrayList("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(tokens));
    }

    @Test
    public void testParserAttributeNameOneDigit() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAttributeName(tokens));
    }

    @Test
    public void testParserAttributeNameOneLetter() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAttributeName(tokens));
    }

    @Test
    public void testParserAttributeNameStartAlpha() {
        ArrayList<String> tokens = initialiseArrayList("a124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAttributeName(tokens));
    }

    @Test
    public void testParserAttributeNameMixedStartDigit() {
        ArrayList<String> tokens = initialiseArrayList("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAttributeName(tokens));
    }

    @Test
    public void testParserTableNameOneDigit() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isTableName(tokens));
    }

    @Test
    public void testParserTableNameOneLetter() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isTableName(tokens));
    }

    @Test
    public void testParserTableNameStartAlpha() {
        ArrayList<String> tokens = initialiseArrayList("a124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isTableName(tokens));
    }

    @Test
    public void testParserTableNameMixedStartDigit() {
        ArrayList<String> tokens = initialiseArrayList("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isTableName(tokens));
    }
 /*
    @Test
    public void testParserUse() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("USE");
        tokens.add("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUse(tokens));
    }

    @Test
    public void testParserUseInvalidUse() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("US");
        tokens.add("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isUse(tokens));
    }

    @Test
    public void testParserUseInvalidDatabaseName() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("US");
        tokens.add("01#4dfsg1");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isUse(tokens));
    }


    @Test
    public void testParserAttributeListOneAttributeName() {
        ArrayList<String> tokens = initialiseArrayList("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAttributeList(0));
    }

    @Test
    public void testParserAttributeListTwoCommaSeparatedAttributeNames() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("0124dfsg1");
        tokens.add(",");
        tokens.add("124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAttributeList(0));
    }

    @Test
    public void testParserAttributeListTwoAttributeNamesNoComma() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("0124dfsg1");
        tokens.add("124dfsg1");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isAttributeList(0));
    }
*/

    //create a function to convert comma separated tokens in string to tokens

}
