import java.util.HashMap;

/**
 * Prefix-Trie. Supports linear time find() and insert(). Should support
 * determining whether a word is a full word in the Trie or a prefix.
 * Implementation taken from the Alg 4th edition, Sedgewick and Wayne
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@bekrley.edu
 */
public class Trie {
    /**
     * trie Node class
     * 
     * 
     */
    public class TNode implements Comparable<TNode> {
        private boolean end;
        private boolean start;
        private char letter;
        private TNode left;
        private TNode right;
        private TNode middle;
        private int weight;

        /**
         * TNode constructor
         * 
         * @param letter
         *            letter within the node
         * @param weight
         *            how the letter stacks up agains others
         */
        public TNode(char letter, int weight) {
            this.letter = letter;
            this.weight = weight;
        }

        /**
         * Custum compare method for two TNodes
         * 
         * @param other
         *            the other TNode
         * @return the value of the difference between the two nodes
         */
        @Override
        public int compareTo(TNode other) {
            return weight - other.weight;
        }
    }

    HashMap<Character, Integer> weights;
    TNode root;

    /**
     * constructor
     */
    public Trie() {
        root = null;
        weights = new HashMap<Character, Integer>();
    }

    /**
     * constructor with custom weights
     * 
     * @param weights
     *            a map with value of the characters
     */
    public Trie(HashMap<Character, Integer> weights) {
        root = null;
        this.weights = new HashMap<Character, Integer>(weights);
    }

    /**
     * Finds whether the string is within the trie
     * 
     * @param s
     *            the given string
     * @param isFullWord
     *            indicates whether it needs to be a full word or not
     * @return a boolean value whether the string is within the trie or not
     */
    public boolean find(String s, boolean isFullWord) {
        if (s == null || s.trim().length() == 0) {
            throw new IllegalArgumentException("key must not be null");
        }
        TNode node = find(root, s, 0);
        if (node == null) {
            return false;
        }
        if (isFullWord) {
            return isFullWord == node.end;
        }
        return true;

    }

    /**
     * Finds the node that matches the word, if any
     * 
     * @param node
     *            the node to look up char in
     * @param s
     *            string input
     * @param index
     *            indext of the character in s
     * @return The very last node
     */
    private TNode find(TNode node, String s, int index) {
        if (node == null) {
            return null;
        }
        char c = s.charAt(index);
        TNode temp = null;
        if (weights.containsKey(c)) {
            temp = new TNode(c, weights.get(c));
        } else if (weights.size() == 0) {
            temp = new TNode(c, c);
        }
        if (node.compareTo(temp) > 0) {
            return find(node.left, s, index);
        } else if (node.compareTo(temp) < 0) {
            return find(node.right, s, index);
        } else if (index < s.length() - 1) {
            return find(node.middle, s, index + 1);
        } else {
            return node;
        }
    }

    /**
     * Puts each character in a tree at the appropriate location
     * 
     * @param node
     *            the node in question
     * @param key
     *            the string we are trying to add
     * @param index
     *            the index to look up the character
     * @return return a new node with updated character set
     */
    private TNode put(TNode node, String key, int index) {
        char c = key.charAt(index);
        TNode temp = null;
        if (weights.containsKey(c)) {
            temp = new TNode(c, weights.get(c));
        } else if (weights.size() == 0) {
            temp = new TNode(c, c);
        }
        if (index == 0) {
            temp.start = true;
        }
        if (node == null) {
            node = temp;
            node.letter = c;
        }
        if (node.compareTo(temp) > 0) {
            node.left = put(node.left, key, index);
        } else if (node.compareTo(temp) < 0) {
            node.right = put(node.right, key, index);
        } else if (index < key.length() - 1) {
            node.middle = put(node.middle, key, index + 1);
        } else {
            node.end = true;
        }
        return node;
    }

    /**
     * Inserts the string into a trie
     * 
     * @param s
     *            the string to be passed
     */
    public void insert(String s) {
        if (s == null || s.trim().length() == 0) {
            throw new IllegalArgumentException("String cannot be null or empty");
        }
        for (int i = 0; i < s.length(); i += 1) {
            if (!(weights.size() == 0) && !weights.containsKey(s.charAt(i))) {
                return;
            }
        }
        TNode temp = put(root, s, 0);
        if (temp != null) {
            root = put(root, s, 0);
        }
    }

    /**
     * Prints out the words from largest to smallest
     */
    public void printWords() {
        String word = "";
        printWords(root, word);

    }

    /**
     * Helper method to print out the words from smallest to largest
     * 
     * @param node
     *            to check
     * @param word
     *            the words that i would add onto
     */
    private void printWords(TNode node, String word) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        if (node.left == null && node.right == null && node.middle == null
                && node.end) {
            word += String.valueOf(node.letter);
            System.out.println(word);
            return;
        }
        if (node.left != null) {
            printWords(node.left, word);
        }
        if (node.start) {
            word = String.valueOf(node.letter);
        } else if (!node.start) {
            word += String.valueOf(node.letter);
        }
        if (node.end) {
            System.out.println(word);
        }

        if (node.middle != null) {
            printWords(node.middle, word);
        }
        if (node.right != null) {
            if (!node.right.start && word.length() > 0) {
                word = word.substring(0, word.length() - 1);
            }
            printWords(node.right, word);
        }
        if (node.start) {
            word = String.valueOf(node.letter);
        }
    }

}
