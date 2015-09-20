package ngordnet;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

/**
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */
public class YearlyRecord {

    private boolean cashed;
    private HashMap<String, Integer> wordMap;
    private ArrayList<String> words;
    private ArrayList<Number> counts;
    private HashMap<String, Integer> rankMap;

    /**
     * Default constructor, creates a new YearlyRecord
     */
    public YearlyRecord() {

        cashed = true;
        words = new ArrayList<String>();
        counts = new ArrayList<Number>();
        rankMap = new HashMap<String, Integer>();
        wordMap = new HashMap<String, Integer>();

    }

    /**
     * Copies an input HashMap into the YearlyRecord
     * 
     * @param otherCountMap
     *            map to copy into yearly record
     */
    public YearlyRecord(HashMap<String, Integer> otherCountMap) {
        Set<String> keys = otherCountMap.keySet();
        for (String key : keys) {
            wordMap.put(key, otherCountMap.get(key));
        }
    }

    /**
     * Creates a hashmap of words as keys and rank as values
     * 
     */
    private void createRankMap() {
        int i = wordMap.size();
        for (String word : words) {
            rankMap.put(word, i);
            i -= 1;
        }
    }

    /**
     * Updates all the datastructures
     */
    private void update() {
        createCounts();
        createWords();
        createRankMap();
        cashed = true;
    }

    /**
     * Creates an array of counts
     * 
     * @param givenMap
     *            a map to use to create an array of counts
     */
    private void createCounts() {
        ArrayList<Integer> tempCounts = new ArrayList<Integer>(wordMap.values());
        Collections.sort(tempCounts);
        counts = new ArrayList<Number>(tempCounts);
    }

    /**
     * Creates an arrayList of Words and sorts them according to counts
     * arrayList
     */
    private void createWords() {
        words = new ArrayList<String>(wordMap.keySet());
        Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return wordMap.get(a) - wordMap.get(b);
            }
        });

    }

    /**
     * Shows how many times the specific word has been used
     * 
     * @param word
     *            the words to be counted
     * @return Number of times word appeared in this year
     */
    public int count(String word) {
        if (!cashed) {
            update();
        }
        return wordMap.get(word);
    }

    /**
     * Figure out how many counts there are at a given year
     * 
     * @return a Collections of all counts in ascending order
     */
    public Collection<Number> counts() {
        if (!cashed) {
            update();
        }
        return counts;
    }

    /**
     * 
     * @return all the words in ascending order of count
     */
    public Collection<String> words() {
        if (!cashed) {
            update();
        }
        return words;
    }

    /**
     * Adds an entry to YearlyRecord
     * 
     * @param word
     *            the word to be added
     * @param count
     *            times the word comes up
     */
    public void put(String word, int count) {
        wordMap.put(word, count);
        cashed = false;
    }

    /**
     * Figures out the rank among other words
     * 
     * @param word
     *            : the word to be ranked
     * @return the rank of the word
     */
    public int rank(String word) {
        if (!cashed) {
            update();
        }
        return rankMap.get(word);
    }

    /**
     * 
     * @return the number of words in the recorded year
     */
    public int size() {
        return wordMap.size();
    }

}
