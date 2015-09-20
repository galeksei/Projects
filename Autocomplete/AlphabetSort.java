import java.util.HashMap;
import java.util.Scanner;

/**
 * Sorts the entries according to the pattern given
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@berkeley.edu
 *
 */
public class AlphabetSort {
    /**
     * main method that runs the program
     * 
     * @param args
     *            arguments passed in
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        if (!in.hasNextLine()) {
            throw new IllegalArgumentException();
        }
        String order = in.nextLine();
        HashMap<Character, Integer> charToInt = new HashMap<Character, Integer>();
        createOrderMap(order, charToInt);
        Trie mainTrie = new Trie(charToInt);
        while (in.hasNextLine()) {
            String word = in.nextLine();
            mainTrie.insert(word);
        }
        mainTrie.printWords();
    }

    /**
     * Creates a map from the given sequence of letters
     * 
     * @param order
     *            a string of letters for alphabet
     * @param charToInt
     *            hashMap to modify
     */
    private static void createOrderMap(String order,
            HashMap<Character, Integer> charToInt) {
        for (int i = 0; i < order.length(); i += 1) {
            Character c = order.charAt(i);
            if (charToInt.containsKey(c)) {
                throw new IllegalArgumentException();
            }
            charToInt.put(c, i);
        }

    }
}
