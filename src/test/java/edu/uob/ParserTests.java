package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class ParserTests {

    //BoolOperator
    @Test
    public void testParserBoolOperatorOR() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("OR");
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
        assertThrows(RuntimeException.class, ()-> parser.isDigit(0));
    }

    @Test
    public void testParserDigitPunctuation() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("!");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isDigit(0));
    }

    @Test
    public void testParserDigitDoubleDigit() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("22");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isDigit(0));
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
        assertThrows(RuntimeException.class, ()-> parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseLowercaseT() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("t");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isUppercase(0));
    }

    @Test
    public void testParserUppercasePunctuation() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("+");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isUppercase(0));
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
        assertThrows(RuntimeException.class, ()-> parser.isLowercase(0));
    }

    @Test
    public void testParserLowercasePunctuation() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("/");
        Parser parser = new Parser(tokens);
        assertThrows(RuntimeException.class, ()-> parser.isLowercase(0));
    }

    //Letter
/*
    @Test
    public void testParserLetterUppercase() {
        String query = " A ";
        Parser parser = new Parser(query);
        assertTrue(parser.isLetter(0));
    }

*/


}
