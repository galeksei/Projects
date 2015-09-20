package ngordnet;

import java.util.Collection;
import java.util.HashMap;

import edu.princeton.cs.introcs.In;

public class NGramMap {
    private HashMap<String, TimeSeries<Integer>> wordsToSeries;
    private HashMap<Integer, YearlyRecord> yearsToRecord;
    private TimeSeries<Long> yearsToCount;
    private TimeSeries<Integer> ts;
    private YearlyRecord yr;
    private In wordsFile;
    private In countsFile;

    /**
     * constructor
     * 
     * @param wordsFileName
     *            file to process words
     * @param countsFileName
     *            file to process year and counts
     */
    public NGramMap(String wordsFileName, String countsFileName) {

        wordsToSeries = new HashMap<String, TimeSeries<Integer>>();
        yearsToRecord = new HashMap<Integer, YearlyRecord>();
        wordsFile = new In(wordsFileName);
        makeRecords();
        yearsToCount = new TimeSeries<Long>();
        countsFile = new In(countsFileName);
        makeYearsToCount();
    }

    /**
     * Creates a yearsToCount map
     */
    private void makeYearsToCount() {
        while (countsFile.hasNextLine()) {
            String[] line = countsFile.readLine().split(",");
            Integer year = Integer.parseInt(line[0]);
            Long count = Long.parseLong(line[1]);
            yearsToCount.put(year, count);
        }
    }

    /**
     * Creates hash map of wordToSeries and yearsToRecord
     */
    private void makeRecords() {
        while (wordsFile.hasNextLine()) {
            String[] line = wordsFile.readLine().split("\t");
            String word = line[0];
            Integer year = Integer.parseInt(line[1]);
            Integer count = Integer.parseInt(line[2]);
            ts = new TimeSeries<Integer>();
            yr = new YearlyRecord();
            makeWordToSeries(word, year, count);
            makeYearsToRecord(word, year, count);

        }
    }

    /**
     * Adds the input to the wordToSeries map
     * 
     * @param word
     *            word to add
     * @param year
     *            year used
     * @param count
     *            how many times the word has been used in that year
     */
    private void makeWordToSeries(String word, Integer year, Integer count) {
        if (!wordsToSeries.containsKey(word)) {
            ts.put(year, count);
            wordsToSeries.put(word, ts);
        }
        wordsToSeries.get(word).put(year, count);
    }

    /**
     * Adds input to the YearsToRecord
     * 
     * @param word
     *            word to add
     * @param year
     *            year used
     * @param count
     *            how many times the word has been used in that year
     */
    private void makeYearsToRecord(String word, Integer year, Integer count) {
        if (!yearsToRecord.containsKey(year)) {
            yr.put(word, count);
            yearsToRecord.put(year, yr);
        }
        yearsToRecord.get(year).put(word, count);
    }

    /**
     * Gives the number of times the word has been used in the given year
     * 
     * @param word
     * @param year
     * @return absolute count of word in a given year
     */
    public int countInYear(String word, int year) {
        YearlyRecord record = yearsToRecord.get(year);
        Collection<String> words = record.words();
        if (record != null && words.contains(word)) {
            return yearsToRecord.get(year).count(word);
        }
        return 0;
    }

    /**
     * returns a yearlyRecord of that year
     * 
     * @param year
     * @return defensive copy of YearlyRecord of year
     */
    public YearlyRecord getRecord(int year) {
        YearlyRecord yearToCopy = yearsToRecord.get(year);
        YearlyRecord tempRecord = new YearlyRecord();
        Collection<String> words = yearToCopy.words();
        for (String word : words) {
            tempRecord.put(word, yearToCopy.count(word));
        }
        return tempRecord;
    }

    /**
     * 
     * @return total number of words recorded in all volumes
     */
    public TimeSeries<Long> totalCountHistory() {
        return yearsToCount;
    }

    /**
     * Provides a defensive copy of the history of WORD.
     * 
     * @param word
     * @return defensive copy of the history of word
     */
    public TimeSeries<Integer> countHistory(String word) {
        return wordsToSeries.get(word);
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR.
     * 
     * @param word
     * @param startYear
     * @param endYear
     * @return history of word between startyear and endyear
     */
    public TimeSeries<Integer> countHistory(String word, int startYear,
            int endYear) {
        return new TimeSeries<Integer>(wordsToSeries.get(word), startYear,
                endYear);
    }

    /**
     * Provides the relative frequency of WORD between STARTYEAR and ENDYEAR.
     * 
     * @param word
     * @param startYear
     * @param endYear
     * @return relative frequency of word between start year and end year
     */
    public TimeSeries<Double> weightHistory(String word, int startYear,
            int endYear) {
        TimeSeries<Integer> temp = new TimeSeries<Integer>(
                wordsToSeries.get(word), startYear, endYear);
        TimeSeries<Long> tempYears = new TimeSeries<Long>(yearsToCount,
                startYear, endYear);
        return temp.dividedBy(tempYears);
    }

    /**
     * Provides the relative frequency of WORD.
     * 
     * @param word
     * @return relative frequency of word
     */
    public TimeSeries<Double> weightHistory(String word) {
        return wordsToSeries.get(word).dividedBy(yearsToCount);
    }

    /**
     * 
     * @param words
     * @return summmed relative frequency of all words
     */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words) {
        TimeSeries<Double> summedWeights = new TimeSeries<Double>();
        for (String x : words) {
            summedWeights = summedWeights.plus(weightHistory(x));
        }
        return summedWeights;
    }

    /**
     * Adds all the weightHistories together
     * 
     * @param words
     *            collection of words being passed in
     * @param startYear
     * @param endYear
     * @return the added weights as a timeSeries
     */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words,
            int startYear, int endYear) {
        TimeSeries<Double> summedWeights = new TimeSeries<Double>();
        for (String x : words) {
            if (wordsToSeries.containsKey(x)) {
                TimeSeries<Double> history = weightHistory(x, startYear,
                        endYear);
                summedWeights = summedWeights.plus(history);
            }
        }
        return summedWeights;
    }

    /**
     * Provides processed history of all words between STARTYEAR and ENDYEAR as
     * processed by YRP.
     * 
     * @param startYear
     * @param endYear
     * @param yrp
     *            processor to use
     * @return TimeSeries of specified
     */
    public TimeSeries<Double> processedHistory(int startYear, int endYear,
            YearlyRecordProcessor yrp) {
        TimeSeries<Double> history = new TimeSeries<Double>(
                processedHistory(yrp), startYear, endYear);
        return history;
    }

    /**
     * Provides processed history of all words ever as processed by YRP.
     * 
     * @param yrp
     *            processor to use
     * @return TimeSeries that is processed through the YRP
     */
    public TimeSeries<Double> processedHistory(YearlyRecordProcessor yrp) {
        TimeSeries<Double> history = new TimeSeries<Double>();
        for (Integer year : yearsToRecord.keySet()) {
            history.put(year, yrp.process(yearsToRecord.get(year)));
        }
        return history;
    }
}
