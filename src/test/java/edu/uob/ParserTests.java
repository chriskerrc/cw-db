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
        assertTrue(parser.isBoolOperator(0));
    }

    @Test
    public void testParserBoolOperatorAND() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("AND");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isBoolOperator(0));
    }

    @Test
    public void testParserBoolOperatorInvalid() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("BAD");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isBoolOperator(0));
    }

    //AlterationType
    @Test
    public void testParserAlterationTypeADD() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("ADD");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAlterationType(0));
    }

    @Test
    public void testParserAlterationTypeDROP() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("DROP");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isAlterationType(0));
    }

    @Test
    public void testParserAlterationTypeInvalid() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("AND");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isAlterationType(0));
    }

    //BooleanLiteral
    @Test
    public void testParserBooleanLiteralTRUE() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("TRUE");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isBooleanLiteral(0));
    }

    @Test
    public void testParserBooleanLiteralFALSE() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("FALSE");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isBooleanLiteral(0));
    }

    @Test
    public void testParserBooleanLiteralInvalid() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("MAYBE");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isBooleanLiteral(0));
    }

    //Digit

    @Test
    public void testParserDigit1() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigit(0));
    }

    @Test
    public void testParserDigit2() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("2");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigit(0));
    }

    @Test
    public void testParserDigit9() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("9");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigit(0));
    }

    @Test
    public void testParserDigitLetter() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("a");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigit(0));
    }

    @Test
    public void testParserDigitPunctuation() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("!");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigit(0));
    }

    @Test
    public void testParserDigitDoubleDigit() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("22");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigit(0));
    }

    //Comparator

    @Test
    public void testParserComparatorEquals() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("==");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorGreaterThan() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(">");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorLessThan() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("<");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorLessThanEquals() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("<=");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorGreaterThanEquals() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(">=");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorNotEquals() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("!=");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorLIKE() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("LIKE");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorInvalid() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("!");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isComparator(0));
    }

    //Uppercase
    @Test
    public void testParserUppercaseA() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseM() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("M");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseZ() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("Z");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseLowercaseA() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("a");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseLowercaseT() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("t");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercasePunctuation() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("+");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isUppercase(0));
    }

    //Lowercase

    @Test
    public void testParserLowercaseA() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("a");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLowercase(0));
    }

    @Test
    public void testParserLowercaseQ() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("q");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLowercase(0));
    }

    @Test
    public void testParserLowercaseY() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("y");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLowercase(0));
    }

    @Test
    public void testParserLowercaseUppercase() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("A");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLowercase(0));
    }

    @Test
    public void testParserLowercasePunctuation() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("/");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLowercase(0));
    }

    //Letter
    @Test
    public void testParserLetterUppercaseA() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(0));
    }

    @Test
    public void testParserLetterUppercaseO() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("O");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(0));
    }

    @Test
    public void testParserLetterLowercaseP() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("p");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(0));
    }

    @Test
    public void testParserLetterLowercaseC() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("c");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isLetter(0));
    }

    @Test
    public void testParserLetterPunctuation() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(".");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLetter(0));
    }

    @Test
    public void testParserLetterNumber() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("0");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isLetter(0));
    }

    //Symbol

    @Test
    public void testParserSymbolForwardSlash() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("/");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(0));
    }

    @Test
    public void testParserSymbolOpenSquareBracket() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("[");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(0));
    }

    @Test
    public void testParserSymbolCloseSquareBracket() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("]");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(0));
    }

    @Test
    public void testParserSymbolCaret() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("^");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(0));
    }

    @Test
    public void testParserSymbolAt() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("@");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isSymbol(0));
    }

    @Test
    public void testParserSymbolTwoSymbols() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("^/");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isSymbol(0));
    }

    @Test
    public void testParserSymbolLetter() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("a");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isSymbol(0));
    }

    @Test
    public void testParserSymbolBar() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("|");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isSymbol(0));
    }

    //Char Literal

    @Test
    public void testParserCharLiteralSpace() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(" ");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(0));
    }

    @Test
    public void testParserCharLiteralLetter() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(0));
    }

    @Test
    public void testParserCharLiteralSymbol() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("/");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(0));
    }

    @Test
    public void testParserCharLiteralDigit() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("7");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isCharLiteral(0));
    }

    @Test
    public void testParserCharLiteralInvalid() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("INVALID");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isCharLiteral(0));
    }

    @Test
    public void testParserDigitSequenceMultipleNumbers() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("12345");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigitSequence(0, 0));
    }

    @Test
    public void testParserDigitSequenceSingleNumber() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDigitSequence(0, 0));
    }

    @Test
    public void testParserDigitSequenceLetter() {
        ArrayList<String> tokens = initialiseArrayList("123a5");
        Parser parser = new Parser(tokens);
        assertFalse(parser.isDigitSequence(0, 0));
    }

    @Test
    public void testParserIntegerLiteralUnsigned() {
        ArrayList<String> tokens = initialiseArrayList("1235");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isIntegerLiteral(0));
    }

    @Test
    public void testParserIntegerLiteralPlus() {
        ArrayList<String> tokens = initialiseArrayList("+1235");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isIntegerLiteral(0));
    }

    @Test
    public void testParserIntegerLiteralMinus() {
        ArrayList<String> tokens = initialiseArrayList("-1235");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isIntegerLiteral(0));
    }

    @Test
    public void testParserIntegerLiteralLetter() {
        ArrayList<String> tokens = initialiseArrayList("-123z");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isIntegerLiteral(0));
    }

    @Test
    public void testParserPlainTextOneDigit() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(0));
    }

    @Test
    public void testParserPlainTextOneLetter() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(0));
    }

    @Test
    public void testParserPlainTextMixedStartAlpha() {
        ArrayList<String> tokens = initialiseArrayList("a124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(0));
    }

    @Test
    public void testParserPlainTextMixedStartDigit() {
        ArrayList<String> tokens = initialiseArrayList("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isPlainText(0));
    }

    @Test
    public void testParserDatabaseNameOneDigit() {
        ArrayList<String> tokens = initialiseArrayList("1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(0));
    }

    @Test
    public void testParserDatabaseNameOneLetter() {
        ArrayList<String> tokens = initialiseArrayList("A");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(0));
    }

    @Test
    public void testParserDatabaseNameStartAlpha() {
        ArrayList<String> tokens = initialiseArrayList("a124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(0));
    }

    @Test
    public void testParserDatabaseNameMixedStartDigit() {
        ArrayList<String> tokens = initialiseArrayList("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isDatabaseName(0));
    }

    @Test
    public void testParserUse() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("USE");
        tokens.add("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertTrue(parser.isUse(0));
    }

    @Test
    public void testParserUseInvalidUse() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("US");
        tokens.add("0124dfsg1");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isUse(0));
    }

    @Test
    public void testParserUseInvalidDatabaseName() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("US");
        tokens.add("01#4dfsg1");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isUse(0));
    }

    //create a function to convert comma separated tokens in string to tokens


}
