package ngordnet;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;

public class NGramMapTest {

    @Test
    public void testCountBasic() {
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");

        System.out.println(ngm.countInYear("quantity", 1736)); // should print
                                                               // 139
        YearlyRecord yr = ngm.getRecord(1736);
        System.out.println(yr.count("quantity")); // should print 139

        TimeSeries<Integer> countHistory = ngm.countHistory("quantity");
        System.out.println(countHistory.get(1736)); // should print 139

        TimeSeries<Long> totalCountHistory = ngm.totalCountHistory();
        System.out.println(totalCountHistory.get(1736)); // should print 8049773

        TimeSeries<Double> weightHistory = ngm.weightHistory("quantity");
        System.out.println(weightHistory.get(1736)); // should print roughly
                                                     // 1.7267E-5

        System.out.println((double) countHistory.get(1736)
                / (double) totalCountHistory.get(1736));

        ArrayList<String> words = new ArrayList<String>();
        words.add("quantity");
        words.add("quality");
        ngm.summedWeightHistory(words).size();
        TimeSeries<Double> sum = ngm.summedWeightHistory(words);
        System.out.println(sum.get(1736)); // should print roughly 3.875E-5
    }

    @Test
    public void testCountInYear() {
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");

        assertEquals(139, ngm.countInYear("quantity", 1736));
    }

    @Test
    public void testYear() {
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");
        YearlyRecord yr = ngm.getRecord(1736);
        assertEquals(139, yr.count("quantity"));

    }

    @Test
    public void testCollisions() {
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");
        TimeSeries<Integer> countHistory = ngm.countHistory("quantity");
        assertEquals((Integer) 139, countHistory.get(1736));
    }

    @Test
    public void testDefensiveTS() {
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");
        TimeSeries<Integer> countHistory = ngm.countHistory("quantity");
        ngm = new NGramMap("./ngrams/very_short.csv",
                "./ngrams/total_counts.csv");
        assertEquals((Integer) 139, countHistory.get(1736));
    }

    @Test
    public void testDefensiveYR() {
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");
        YearlyRecord recordHistory = ngm.getRecord(1736);
        ngm = new NGramMap("./ngrams/very_short.csv",
                "./ngrams/total_counts.csv");
        assertEquals((Integer) 139, (Integer) recordHistory.count("quantity"));
    }
    @Test
    public void processedHistoryTest(){
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");
        WordLengthProccessor yrp = new WordLengthProccessor();
        System.out.println(yrp.process(ngm.getRecord(1736)));
    }
    @Test
    public void cointInTest(){
        NGramMap ngm = new NGramMap("./ngrams/words_that_start_with_q.csv",
                "./ngrams/total_counts.csv");
        assertEquals(0, ngm.countInYear("blah", 1993));
    }
    
    /** Runs tests. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(NGramMapTest.class);

    }

}
