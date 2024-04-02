package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class PreprocessorTests {

    @Test
    public void testProcessorInsertQuery() {
        String query = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[INSERT, INTO, people, VALUES, (, 'Simon Lock', ,, 35, ,, 'simon@bristol.ac.uk', ,, 1.8, ), ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectComparatorEquals() {
        String query = "  SELECT  *  FROM people   WHERE name == 'Simon'; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, *, FROM, people, WHERE, name, ==, 'Simon', ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectComparatorNotEquals() {
        String query = "  SELECT  *  FROM people   WHERE name != 'Simon'; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, *, FROM, people, WHERE, name, !=, 'Simon', ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectComparatorGreaterThanOrEqualTo() {
        String query = "  SELECT  *  FROM people   WHERE age >= 40; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, *, FROM, people, WHERE, age, >=, 40, ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectGreaterThanNoSpaces() {
        String query = "  SELECT name FROM marks WHERE mark>40; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, name, FROM, marks, WHERE, mark, >, 40, ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectLessThanNoSpaces() {
        String query = "  SELECT name FROM marks WHERE mark<40; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, name, FROM, marks, WHERE, mark, <, 40, ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectLessThanEqualToNoSpaces() {
        String query = "  SELECT name FROM marks WHERE mark<=40; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, name, FROM, marks, WHERE, mark, <=, 40, ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectGreaterThanEqualToNoSpaces() {
        String query = "  SELECT name FROM marks WHERE mark>=40; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, name, FROM, marks, WHERE, mark, >=, 40, ;]";
        assertEquals(expected, processed);
    }

    @Test
    public void testProcessorSelectEqualsNoSpaces() {
        String query = "  SELECT name FROM marks WHERE mark==40; ";
        Preprocessor preprocessor = new Preprocessor(query);
        String processed = preprocessor.commandTokens.toString();
        String expected = "[SELECT, name, FROM, marks, WHERE, mark, ==, 40, ;]";
        assertEquals(expected, processed);
    }
}
