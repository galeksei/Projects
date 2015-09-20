import java.util.HashMap;
import java.util.LinkedList;

/**
 * Implements autocomplete on prefixes for a given dictionary of terms and
 * weights.
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 */
public class Autocomplete {
    HashMap<String, Double> wordToWeight;
    AutoTrie trie;

    /**
     * Initializes required data structures from parallel arrays.
     * 
     * @param terms
     *            Array of terms.
     * @param weights
     *            Array of weights.
     */
    public Autocomplete(String[] terms, double[] weights) {
        if (terms.length != weights.length) {
            throw new IllegalArgumentException();
        }
        wordToWeight = new HashMap<String, Double>();
        trie = new AutoTrie();
        for (int i = 0; i < weights.length; i += 1) {
            if (wordToWeight.containsKey(terms[i]) || weights[i] < 0) {
                throw new IllegalArgumentException();
            }
            wordToWeight.put(terms[i], weights[i]);
            trie.insert(terms[i], weights[i]);
        }
        trie.setMap(wordToWeight);
    }

    /**
     * Find the weight of a given term. If it is not in the dictionary, return
     * 0.0
     * 
     * @param term
     *            the string in question
     * 
     * @return weight of the string
     */
    public double weightOf(String term) {
        return wordToWeight.get(term);
    }

    /**
     * Return the top match for given prefix, or null if there is no matching
     * term.
     * 
     * @param prefix
     *            Input prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        if (prefix == null) {
            throw new NullPointerException();
        }
        String topWord = trie.getTop(prefix);
        return topWord;
    }

    /**
     * Returns the top k matching terms (in descending order of weight) as an
     * iterable. If there are less than k matches, return all the matching
     * terms.
     * 
     * @param prefix
     *            string of the prefix to match
     * 
     * @param k
     *            integer
     * 
     * @return Iterable of matches
     */
    public Iterable<String> topMatches(String prefix, int k) {
        if (k < 0) {
            throw new IllegalArgumentException();
        }
        Iterable<String> topWords = trie.topWords(prefix, k);
        return topWords;
    }

    /**
     * Returns the highest weighted matches within k edit distance of the word.
     * If the word is in the dictionary, then return an empty list.
     * 
     * @param word
     *            The word to spell-check
     * @param dist
     *            Maximum edit distance to search
     * @param k
     *            Number of results to return
     * @return Iterable in descending weight order of the matches
     */
    public Iterable<String> spellCheck(String word, int dist, int k) {
        LinkedList<String> results = new LinkedList<String>();
        /* YOUR CODE HERE; LEAVE BLANK IF NOT PURSUING BONUS */
        return results;
    }

    /**
     * Test client. Reads the data from the file, then repeatedly reads
     * autocomplete queries from standard input and prints out the top k
     * matching terms.
     * 
     * @param args
     *            takes the name of an input file and an integer k as
     *            command-line arguments
     */
    public static void main(String[] args) {
        // initialize autocomplete data structure
        In in = new In(args[0]);
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble(); // read the next weight
            in.readChar(); // scan past the tab
            terms[i] = in.readLine(); // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);

        // process queries from standard input
        int k = Integer.parseInt(args[1]);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            for (String term : autocomplete.topMatches(prefix, k)) {
                StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
            }
        }
    }
}
