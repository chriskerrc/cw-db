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
        String query = " OR ";
        Parser parser = new Parser(query);
        assertTrue(parser.isBoolOperator(0));
    }

    @Test
    public void testParserBoolOperatorAND() {
        String query = " AND ";
        Parser parser = new Parser(query);
        assertTrue(parser.isBoolOperator(0));
    }

    @Test
    public void testParserBoolOperatorInvalid() {
        String query = " BAD ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isBoolOperator(0));
    }

    //AlterationType
    @Test
    public void testParserAlterationTypeADD() {
        String query = " ADD ";
        Parser parser = new Parser(query);
        assertTrue(parser.isAlterationType(0));
    }

    @Test
    public void testParserAlterationTypeDROP() {
        String query = " DROP ";
        Parser parser = new Parser(query);
        assertTrue(parser.isAlterationType(0));
    }

    @Test
    public void testParserAlterationTypeInvalid() {
        String query = " AND ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isAlterationType(0));
    }

    //BooleanLiteral
    @Test
    public void testParserBooleanLiteralTRUE() {
        String query = " TRUE ";
        Parser parser = new Parser(query);
        assertTrue(parser.isBooleanLiteral(0));
    }

    @Test
    public void testParserBooleanLiteralFALSE() {
        String query = " FALSE ";
        Parser parser = new Parser(query);
        assertTrue(parser.isBooleanLiteral(0));
    }

    @Test
    public void testParserBooleanLiteralInvalid() {
        String query = " MAYBE ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isBooleanLiteral(0));
    }

    //Digit

    @Test
    public void testParserDigit1() {
        String query = " 1 ";
        Parser parser = new Parser(query);
        assertTrue(parser.isDigit(0));
    }

    @Test
    public void testParserDigit2() {
        String query = " 2 ";
        Parser parser = new Parser(query);
        assertTrue(parser.isDigit(0));
    }

    @Test
    public void testParserDigit9() {
        String query = " 9 ";
        Parser parser = new Parser(query);
        assertTrue(parser.isDigit(0));
    }

    @Test
    public void testParserDigitLetter() {
        String query = " a ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isDigit(0));
    }

    @Test
    public void testParserDigitPunctuation() {
        String query = " ! ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isDigit(0));
    }

    @Test
    public void testParserDigitDoubleDigit() {
        String query = " 22 ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isDigit(0));
    }

    //Comparator

    @Test
    public void testParserComparatorEquals() {
        String query = " == ";
        Parser parser = new Parser(query);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorGreaterThan() {
        String query = " > ";
        Parser parser = new Parser(query);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorLessThan() {
        String query = " < ";
        Parser parser = new Parser(query);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorLessThanEquals() {
        String query = " <= ";
        Parser parser = new Parser(query);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorGreaterThanEquals() {
        String query = " >= ";
        Parser parser = new Parser(query);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorNotEquals() {
        String query = " != ";
        Parser parser = new Parser(query);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorLIKE() {
        String query = " LIKE ";
        Parser parser = new Parser(query);
        assertTrue(parser.isComparator(0));
    }

    @Test
    public void testParserComparatorInvalid() {
        String query = " ! ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isComparator(0));
    }

    //Uppercase
    @Test
    public void testParserUppercaseA() {
        String query = " A ";
        Parser parser = new Parser(query);
        assertTrue(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseM() {
        String query = " M ";
        Parser parser = new Parser(query);
        assertTrue(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseZ() {
        String query = " Z ";
        Parser parser = new Parser(query);
        assertTrue(parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseLowercaseA() {
        String query = " a ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isUppercase(0));
    }

    @Test
    public void testParserUppercaseLowercaseT() {
        String query = " t ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isUppercase(0));
    }

    @Test
    public void testParserUppercasePunctuation() {
        String query = " + ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isUppercase(0));
    }

    //Lowercase

    @Test
    public void testParserLowercaseA() {
        String query = " a ";
        Parser parser = new Parser(query);
        assertTrue(parser.isLowercase(0));
    }

    @Test
    public void testParserLowercaseQ() {
        String query = " q ";
        Parser parser = new Parser(query);
        assertTrue(parser.isLowercase(0));
    }

    @Test
    public void testParserLowercaseY() {
        String query = " y ";
        Parser parser = new Parser(query);
        assertTrue(parser.isLowercase(0));
    }

    @Test
    public void testParserLowercaseUppercase() {
        String query = " A ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isLowercase(0));
    }

    @Test
    public void testParserLowercasePunctuation() {
        String query = " / ";
        Parser parser = new Parser(query);
        assertThrows(RuntimeException.class, ()-> parser.isLowercase(0));
    }

    //Letter

    @Test
    public void testParserLetterUppercase() {
        String query = " A ";
        Parser parser = new Parser(query);
        assertTrue(parser.isLetter(0));
    }




}
