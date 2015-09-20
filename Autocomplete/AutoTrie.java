import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Prefix-Trie. Supports linear time find() and insert(). Should support
 * determining whether a word is a full word in the Trie or a prefix.
 * Implementation taken from the Alg 4th edition, Sedgewick and Wayne
 * 
 * @author Alexey Grigoryev aleksei.grigoryev@bekrley.edu
 */
public class AutoTrie {
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
        private double weight = 0;
        private double max;

        /**
         * TNode constructor
         * 
         * @param letter
         *            letter within the node
         * @param weight2
         *            how the letter stacks up agains others
         */
        public TNode(char letter, double weight) {
            this.letter = letter;
            max = weight;
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
            if (other.max == max) {
                return -1;
            }
            return (int) (other.max - max);
        }
    }

    TNode root;
    HashMap<String, Double> wordToWeight;

    /**
     * constructor
     */
    public AutoTrie() {
        root = null;
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
        TNode temp = new TNode(c, 0);
        if (node.letter > temp.letter) {
            return find(node.left, s, index);
        } else if (node.letter < temp.letter) {
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
    private TNode put(TNode node, String key, int index, double weight) {
        char c = key.charAt(index);
        TNode temp = new TNode(c, weight);
        if (index == 0) {
            temp.start = true;
        }
        if (node == null) {
            node = temp;
        } else if (node.max < weight) {
            node.max = weight;
        }
        if (node.letter > temp.letter) {
            node.left = put(node.left, key, index, weight);
        } else if (node.letter < temp.letter) {
            node.right = put(node.right, key, index, weight);
        } else if (index < key.length() - 1) {
            node.middle = put(node.middle, key, index + 1, weight);
        } else {
            node.weight = weight;
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
    public void insert(String s, Double weight) {

        TNode temp = put(root, s, 0, weight);
        if (temp != null) {
            root = put(root, s, 0, weight);
        }
    }

    /**
     * Gets the top word that matches the prefix
     * 
     * @param prefix
     * @return the most common word
     */
    public String getTop(String prefix) {
        Iterator<String> words = topWords(prefix, 1).iterator();
        if (words.hasNext()) {
            return words.next();
        } else {
            return null;
        }
    }

    public Iterable<String> topWords(String prefix, int k) {
        TNode node;
        Comparator<String> comparator = new WordWeightComparator();
        PriorityQueue<String> words = new PriorityQueue<String>(k, comparator);
        PriorityQueue<TNode> nodes = new PriorityQueue<TNode>();
        HashMap<TNode, String> nodeToPrefix = new HashMap<TNode, String>();
        HashSet<String> usedWords = new HashSet<String>();
        int counter = 0;
        if (prefix.length() == 0) {
            node = root;
            prefix = "";
        } else {
            if (find(root, prefix, 0) == null) {
                return words;
            }
            node = find(root, prefix, 0);
            if (node.weight == node.max) {
                words.add(prefix);
            } else if (node.weight != 0) {
                words.add(prefix);
            }
            node = node.middle;
        }
        String word = prefix;
        while (counter != k && node != null) {
            if (nodeToPrefix.containsKey(node)) {
                word = nodeToPrefix.get(node);
            }
            if (node.left != null) {
                nodes.add(node.left);
                nodeToPrefix.put(node.left, word);
            }
            if (node.middle != null) {
                nodes.add(node.middle);
                if (node.weight == node.max) {
                    nodeToPrefix.put(node.middle, word + node.letter);
                }
            }
            if (node.right != null) {
                nodes.add(node.right);
                nodeToPrefix.put(node.right, word);
            }
            if (node.max == node.weight) {
                words.add(word + node.letter);
                counter += 1;
            }
            if (node.left != null || node.right != null) {
                nodeToPrefix.put(node.middle, word + node.letter);
                if (node.weight != 0 && node.weight < node.max) {
                    Iterator<TNode> nodeIterator = nodes.iterator();
                    for (int i = 0; i < k - counter && nodeIterator.hasNext(); i += 1) {
                        if (node.max > nodeIterator.next().max
                                && !usedWords.contains(word + node.letter)) {
                            words.add(word + node.letter);
                            usedWords.add(word + node.letter);
                        }
                    }
                }
                node = nodes.poll();
                continue;
            }
            if (node != null && node.weight != 0 && node.weight < node.max
                    && !nodes.isEmpty()) {
                if (!usedWords.contains(word + node.letter)) {
                    words.add(word + node.letter);
                    usedWords.add(word + node.letter);
                }
            }
            word += node.letter;
            node = nodes.poll();

        }
        PriorityQueue<String> actualWords = new PriorityQueue<String>(k,
                comparator);
        usedWords = new HashSet<String>();
        for (int i = 0; i < k; i += 1) {
            if (words.isEmpty()) {
                break;
            }
            String wordToAdd = words.poll();
            if (!usedWords.contains(wordToAdd)) {
                actualWords.add(wordToAdd);
                usedWords.add(wordToAdd);
            } else {
                i -= 1;
                words.poll();
            }
        }
        return actualWords;
    }

    /**
     * sets the words and values for the priority queue of words
     * 
     * @param wordToWeight
     */
    public void setMap(HashMap<String, Double> wordToWeight) {
        this.wordToWeight = wordToWeight;
    }

    /**
     * custom comparator for the queue to have words in order
     * 
     * @author alexey
     *
     */
    public class WordWeightComparator implements Comparator<String> {
        /**
         * sorts the words by weight
         */
        @Override
        public int compare(String x, String y) {
            return (int) (wordToWeight.get(y) - wordToWeight.get(x));
        }
    }
}
