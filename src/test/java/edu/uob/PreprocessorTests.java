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
        String processed = preprocessor.tokens.toString();
        String expected = "[INSERT, INTO, people, VALUES, (, 'Simon Lock', ,, 35, ,, 'simon@bristol.ac.uk', ,, 1.8, ), ;]";
        assertEquals(processed, expected);
    }
}
