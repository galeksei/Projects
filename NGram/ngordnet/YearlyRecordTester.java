package ngordnet;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assert.*;

public class YearlyRecordTester {
    @Test
    public void testWords() {
        YearlyRecord yr = new YearlyRecord();
        yr.put("quayside", 95);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);

        Collection<String> words = yr.words();
        assertEquals(true, words.contains("quayside"));
        assertEquals(true, words.contains("surrogate"));
        assertEquals(true, words.contains("merchantman"));
    }

    @Test
    public void testRank() {
        YearlyRecord yr = new YearlyRecord();
        yr.put("quayside", 95);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);
        assertEquals(1, yr.rank("surrogate"));
        assertEquals(3, yr.rank("quayside"));
        yr.put("melt", 100);
        assertEquals(3, yr.rank("melt"));
        assertEquals(1, yr.rank("surrogate"));
        assertEquals(2, yr.rank("merchantman"));
        assertEquals(4, yr.rank("quayside"));
    }

    @Test
    public void testCollisions() {
        YearlyRecord yr = new YearlyRecord();
        yr.put("quayside", 95);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);
        yr.put("test", 340);
        Collection<String> words = yr.words();
        assertTrue(words.contains("test"));
        assertTrue(words.contains("surrogate"));
    }
    
    @Test
    public void testrank() {
        YearlyRecord yr = new YearlyRecord();
        yr.put("quayside", 95);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);
        yr.put("surrogate", 1);
        assertEquals(3, yr.rank("surrogate"));
        
    }
    
    @Test
    public void testCountReplacement(){
        YearlyRecord yr = new YearlyRecord();
        yr.put("quayside", 95);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);
        yr.put("surrogate", 1);
        assertEquals(1, yr.count("surrogate"));
    }
    
    @Test
    public void testSize(){
        YearlyRecord yr = new YearlyRecord();
        yr.put("quayside", 95);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);
        yr.put("surrogate", 1);
        assertEquals(3, yr.size());
    }
    /** Runs tests. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(YearlyRecordTester.class);

    }

}
